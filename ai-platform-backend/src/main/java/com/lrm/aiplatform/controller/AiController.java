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

    @PostMapping("/ai/ask")
    public Result<String> askAi(@RequestParam Long userId,
                                @RequestParam String question) {
        String answer = aiService.askAi(userId, question);
        return Result.success("AI响应成功", answer);
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
