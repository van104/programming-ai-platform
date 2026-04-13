package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.entity.LearningProfile;
import com.lrm.aiplatform.service.impl.AiService;
import com.lrm.aiplatform.service.impl.LearningProfileService;
import com.lrm.aiplatform.service.impl.SubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class LearningProfileController {

    private final LearningProfileService profileService;
    private final AiService aiService;
    private final SubmissionService submissionService;

    public LearningProfileController(LearningProfileService profileService,
                                     AiService aiService,
                                     SubmissionService submissionService) {
        this.profileService = profileService;
        this.aiService = aiService;
        this.submissionService = submissionService;
    }

    @GetMapping("/profile/generate")
    public Result<LearningProfile> generate(@RequestParam Long userId) {
        return Result.success(profileService.generateProfile(userId));
    }

    /**
     * 获取学生完整画像（聚合所有维度数据）
     */
    @GetMapping("/profile/full/{userId}")
    public Result<Map<String, Object>> getFullProfile(@PathVariable Long userId) {
        // 1. 基础画像
        LearningProfile profile = profileService.generateProfile(userId);

        // 2. AI 周使用频率
        Map<String, Object> aiFrequency = aiService.getWeeklyFrequency(userId);

        // 3. 练习题数量
        Map<String, Object> exerciseCount = submissionService.getExerciseCount(userId);

        // 聚合
        Map<String, Object> fullProfile = new LinkedHashMap<>();
        fullProfile.put("profile", profile);
        fullProfile.put("weeklyAiFrequency", aiFrequency);
        fullProfile.put("exerciseStats", exerciseCount);

        return Result.success("查询成功", fullProfile);
    }
}