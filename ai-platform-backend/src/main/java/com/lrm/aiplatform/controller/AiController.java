package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.service.impl.AiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    /** 真实接口：AI 对话*/
    @PostMapping("/ai/ask")
    public Result<String> askAi(@RequestParam Long userId,
                                @RequestParam String question) {
        String answer = aiService.askAi(userId, question);
        return Result.success("AI响应成功", answer);
    }
    /** 模拟接口：AI 对话 mock */
    @PostMapping("/ai/ask/mock")
    public Result<String> askAiMock(@RequestParam Long userId,
                                    @RequestParam String question) {
        String answer = aiService.askAi(userId, question, true);
        return Result.success("AI响应成功", answer);
    }
    /** 真实ai分析接口：调用 DeepSeek 分析代码 */
    @PostMapping("/ai/analyze")
    public Result<String> analyzeCode(@RequestParam Long userId,
                                      @RequestParam String code) {
        String result = aiService.analyzeCode(userId, code);
        return Result.success("代码分析完成", result);
    }

    /** 真实ai分析接口：不调 AI，直接返回固定结果 */
    @PostMapping("/ai/analyze/mock")
    public Result<String> analyzeCodeMock(@RequestParam Long userId,
                                          @RequestParam String code) {
        String result = aiService.analyzeCode(userId, code, true);
        return Result.success("模拟分析完成", result);
    }



    /**
     * 获取学生最近一周的 AI 使用频率
     */
    @GetMapping("/ai/frequency/{userId}")
    public Result<Map<String, Object>> getWeeklyFrequency(@PathVariable Long userId) {
        Map<String, Object> frequency = aiService.getWeeklyFrequency(userId);
        return Result.success("查询成功", frequency);
    }
}
