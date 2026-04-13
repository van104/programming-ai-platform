package com.lrm.aiplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lrm.aiplatform.entity.Submission;
import com.lrm.aiplatform.mapper.SubmissionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final SubmissionMapper submissionMapper;
    private final LearningProfileService learningProfileService;

    public SubmissionService(SubmissionMapper submissionMapper, LearningProfileService learningProfileService) {
        this.submissionMapper = submissionMapper;
        this.learningProfileService = learningProfileService;
    }

    public void submitCode(Submission submission) {
        if (submission == null) {
            throw new IllegalArgumentException("提交参数不能为空");
        }
        if (submission.getUserId() == null) {
            throw new IllegalArgumentException("userId 不能为空");
        }
        if (submission.getExerciseId() == null) {
            throw new IllegalArgumentException("exerciseId 不能为空");
        }
        if (submission.getCode() == null || submission.getCode().isBlank()) {
            throw new IllegalArgumentException("code 不能为空");
        }
        if (submission.getSubmitTime() == null) {
            submission.setSubmitTime(LocalDateTime.now());
        }
        submissionMapper.insert(submission);

        // 实时更新学习档案
        learningProfileService.generateProfile(submission.getUserId());
    }

    /**
     * 获取学生做练习题的数量
     */
    public Map<String, Object> getExerciseCount(Long userId) {
        QueryWrapper<Submission> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        List<Submission> submissions = submissionMapper.selectList(wrapper);

        // 不重复的题目数（按 exercise_id 去重）
        long distinctExerciseCount = submissions.stream()
                .map(Submission::getExerciseId)
                .distinct()
                .count();

        // 每道题的提交次数
        Map<Long, Long> exerciseSubmitCount = submissions.stream()
                .collect(Collectors.groupingBy(Submission::getExerciseId, Collectors.counting()));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", userId);
        result.put("totalSubmissions", submissions.size());
        result.put("distinctExerciseCount", distinctExerciseCount);
        result.put("exerciseDetail", exerciseSubmitCount);

        return result;
    }
}