package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.entity.Submission;
import com.lrm.aiplatform.service.impl.SubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/submission")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Submission submission) {
        submissionService.submitCode(submission);
        return Result.success("提交成功");
    }

    /**
     * 获取学生做练习题的数量
     */
    @GetMapping("/count/{userId}")
    public Result<Map<String, Object>> getExerciseCount(@PathVariable Long userId) {
        Map<String, Object> count = submissionService.getExerciseCount(userId);
        return Result.success("查询成功", count);
    }
}
