package com.lrm.aiplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrm.aiplatform.entity.AiRecord;
import com.lrm.aiplatform.mapper.AiRecordMapper;
import org.springframework.beans.factory.annotation.Value;
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
    private final LearningProfileService learningProfileService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${zhipu.api.key}")
    private String apiKey;

    @Value("${zhipu.api.url}")
    private String apiUrl;

    @Value("${zhipu.api.model}")
    private String model;

    public AiService(AiRecordMapper aiRecordMapper,
            LearningProfileService learningProfileService,
            RestTemplate restTemplate) {
        this.aiRecordMapper = aiRecordMapper;
        this.learningProfileService = learningProfileService;
        this.restTemplate = restTemplate;
    }

    public String askAi(Long userId, String question) {
        String aiAnswer;
        try {
            aiAnswer = callZhipuApi(question);
        } catch (Exception e) {
            aiAnswer = "【AI服务异常】" + e.getMessage();
        }

        // 保存记录
        AiRecord record = new AiRecord();
        record.setUserId(userId);
        record.setQuestion(question);
        record.setAiAnswer(aiAnswer);
        aiRecordMapper.insert(record);

        // 实时更新学习档案
        learningProfileService.generateProfile(userId);

        return aiAnswer;
    }

    private String callZhipuApi(String question) throws Exception {
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        // system prompt：定义 AI 角色为编程助教
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的计算机算法编程助教，擅长帮助学生解答编程问题、调试代码、解释算法和数据结构。请用简洁清晰的中文回答。");

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", question);

        requestBody.put("messages", Arrays.asList(systemMessage, userMessage));
        requestBody.put("max_tokens", 4096);
        requestBody.put("temperature", 0.7);

        // 发送请求
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        // 解析响应
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