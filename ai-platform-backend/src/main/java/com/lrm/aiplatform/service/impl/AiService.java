package com.lrm.aiplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrm.aiplatform.config.DeepSeekProperties;
import com.lrm.aiplatform.entity.AiRecord;
import com.lrm.aiplatform.entity.Submission;
import com.lrm.aiplatform.mapper.AiRecordMapper;
import com.lrm.aiplatform.mapper.SubmissionMapper;
import com.lrm.aiplatform.service.IUserService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AiService {

    private final AiRecordMapper aiRecordMapper;
    private final SubmissionMapper submissionMapper;
    private final LearningProfileService learningProfileService;
    private final RestTemplate restTemplate;
    private final IUserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DeepSeekProperties deepSeekProperties;

    public AiService(AiRecordMapper aiRecordMapper,
            SubmissionMapper submissionMapper,
            LearningProfileService learningProfileService,
            RestTemplate restTemplate,
            IUserService userService,
            DeepSeekProperties deepSeekProperties) {
        this.aiRecordMapper = aiRecordMapper;
        this.submissionMapper = submissionMapper;
        this.learningProfileService = learningProfileService;
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.deepSeekProperties = deepSeekProperties;
    }

    public String askAi(Long userId, String question) {
        return askAi(userId, question, deepSeekProperties.getApi().isMock());
    }

    public String askAi(Long userId, String question, boolean mock) {
        return callAiWithPrompt(userId, question,
                "你是一个专业的计算机算法编程助教，擅长帮助学生解答编程问题、调试代码、解释算法和数据结构。请用简洁清晰的中文回答。",
                mock, false);
    }

    public String analyzeCode(Long userId, String code) {
        return analyzeCode(userId, code, deepSeekProperties.getApi().isMock());
    }

    public String analyzeCode(Long userId, String code, boolean mock) {
        String prompt = "请分析以下代码：\n1. 代码结构是否正确\n2. 是否存在潜在 bug\n3. 是否有改进建议\n4. 时间复杂度分析\n\n代码：\n" + code;
        return callAiWithPrompt(userId, prompt,
                "你是一个专业的代码审查专家，擅长分析代码质量、发现 bug 和提供优化建议。请用简洁清晰的中文回答，按点列出分析结果。",
                mock, true);
    }

    private String callAiWithPrompt(Long userId, String userInput, String systemPrompt, boolean mock, boolean saveAsSubmission) {
        String aiAnswer;

        if (mock) {
            aiAnswer = mockResponse(saveAsSubmission);
        } else {
            try {
                aiAnswer = callDeepSeekApi(userInput, systemPrompt);
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("connect timed out")) {
                    throw new RuntimeException("AI 服务连接超时，请检查网络或稍后重试");
                }
                if (msg != null && msg.contains("Read timed out")) {
                    throw new RuntimeException("AI 响应超时，请简化问题后重试");
                }
                throw new RuntimeException("AI 服务异常：" + msg, e);
            }
        }

        if (saveAsSubmission) {
            // AI 分析 → 保存为提交记录，增加提交次数
            Submission submission = new Submission();
            submission.setUserId(userId);
            submission.setExerciseId(0L);
            submission.setCode(userInput);
            submission.setSubmitTime(LocalDateTime.now());
            submissionMapper.insert(submission);
        } else {
            // AI 对话 → 保存为 AI 记录，增加 AI 使用次数
            AiRecord record = new AiRecord();
            record.setUserId(userId);
            record.setQuestion(userInput);
            record.setAiAnswer(aiAnswer);
            record.setCreateTime(LocalDateTime.now());
            aiRecordMapper.insert(record);
        }

        learningProfileService.generateProfile(userId);

        return aiAnswer;
    }

    /** 模拟响应：根据模式返回不同的固定结果 */
    private String mockResponse(boolean isAnalysis) {
        if (isAnalysis) {
            return "代码结构正确，无明显错误";
        }
        return "这是一个模拟的 AI 对话回复。";
    }

    private String callDeepSeekApi(String userInput, String systemPrompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + deepSeekProperties.getApi().getKey());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", deepSeekProperties.getApi().getModel());

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userInput);

        requestBody.put("messages", Arrays.asList(systemMessage, userMessage));
        requestBody.put("max_tokens", 8192);
        requestBody.put("temperature", 0.7);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
        ResponseEntity<String> response = restTemplate.exchange(
                Objects.requireNonNull(deepSeekProperties.getApi().getUrl()), Objects.requireNonNull(HttpMethod.POST), entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                return choices.get(0).path("message").path("content").asText();
            }
            return "AI 未返回有效内容";
        } else {
            throw new RuntimeException("API 调用失败，状态码：" + response.getStatusCode());
        }
    }

    /**
     * 获取用户最近一周的 AI 使用频率
     * 返回：总次数 + 每天的使用次数
     */
    public Map<String, Object> getWeeklyFrequency(Long userId) {
        if (userService.getById(userId) == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6); // 包含今天共7天
        LocalDateTime startTime = weekAgo.atStartOfDay();
        LocalDateTime endTime = today.atTime(LocalTime.MAX);

        // 查询最近一周的所有记录
        QueryWrapper<AiRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .ge("create_time", startTime)
               .le("create_time", endTime)
               .orderByAsc("create_time");

        List<AiRecord> records = aiRecordMapper.selectList(wrapper);

        // 按天统计
        Map<String, Integer> dailyCount = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            dailyCount.put(weekAgo.plusDays(i).toString(), 0);
        }
        for (AiRecord record : records) {
            if (record.getCreateTime() != null) {
                String day = record.getCreateTime().toLocalDate().toString();
                dailyCount.put(day, dailyCount.getOrDefault(day, 0) + 1);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", userId);
        result.put("totalCount", records.size());
        result.put("startDate", weekAgo.toString());
        result.put("endDate", today.toString());
        result.put("dailyCount", dailyCount);

        return result;
    }
}