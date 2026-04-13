package com.lrm.aiplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lrm.aiplatform.entity.AiRecord;
import com.lrm.aiplatform.entity.LearningProfile;
import com.lrm.aiplatform.entity.Submission;
import com.lrm.aiplatform.mapper.AiRecordMapper;
import com.lrm.aiplatform.mapper.LearningProfileMapper;
import com.lrm.aiplatform.mapper.SubmissionMapper;
import com.lrm.aiplatform.service.IUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;



@Service
public class LearningProfileService {

    private final LearningProfileMapper profileMapper;
    private final AiRecordMapper aiRecordMapper;
    private final SubmissionMapper submissionMapper;
    private final IUserService userService;

    public LearningProfileService(LearningProfileMapper profileMapper,
                                  AiRecordMapper aiRecordMapper,
                                  SubmissionMapper submissionMapper, IUserService userService) {
        this.profileMapper = profileMapper;
        this.aiRecordMapper = aiRecordMapper;
        this.submissionMapper = submissionMapper;
        this.userService = userService;
    }

    public LearningProfile generateProfile(Long userId) {
        // 1. 验证用户是否存在
        if (userService.getById(userId) == null) {
            throw new IllegalArgumentException("用户不存在，无法生成学习档案。");
        }

// 安全写法，避免溢出
        Long aiCountLong = aiRecordMapper.selectCount(new  QueryWrapper<AiRecord>().eq("user_id", userId));
        int aiCount = Math.toIntExact(aiCountLong); // 如果数量超 int 范围会抛异常

        Long submitCountLong = submissionMapper.selectCount(new QueryWrapper<Submission>().eq("user_id", userId));
        int submitCount = Math.toIntExact(submitCountLong);

        // 去重题目数（按 exercise_id 去重）
        List<Submission> submissions = submissionMapper.selectList(
                new QueryWrapper<Submission>().eq("user_id", userId).select("DISTINCT exercise_id")
        );
        int distinctExerciseCount = submissions.size();


        String level;
        if (aiCount + submitCount >= 10) {
            level = "高活跃";
        } else if (aiCount + submitCount >= 5) {
            level = "中活跃";
        } else {
            level = "低活跃";
        }

        // 2. 检查学习档案是否已存在
        LearningProfile profile = profileMapper.selectOne(new QueryWrapper<LearningProfile>().eq("user_id", userId));

        LocalDateTime now = LocalDateTime.now();

        if (profile == null) {
            // 不存在，则创建
            profile = new LearningProfile();
            profile.setUserId(userId);
            profile.setAiUsageCount(aiCount);
            profile.setSubmissionCount(submitCount);
            profile.setDistinctExerciseCount(distinctExerciseCount);
            profile.setActivityLevel(level);
            profile.setCreateTime(now);
            profile.setUpdateTime(now);
            profileMapper.insert(profile);
        } else {
            // 存在，判断是否有变化再决定是否更新
            boolean changed = !profile.getAiUsageCount().equals(aiCount)
                    || !profile.getSubmissionCount().equals(submitCount)
                    || !Integer.valueOf(distinctExerciseCount).equals(profile.getDistinctExerciseCount())
                    || !level.equals(profile.getActivityLevel());
            if (changed) {
                profile.setAiUsageCount(aiCount);
                profile.setSubmissionCount(submitCount);
                profile.setDistinctExerciseCount(distinctExerciseCount);
                profile.setActivityLevel(level);
                profile.setUpdateTime(now);
                profileMapper.updateById(profile);
            }
        }

        return profile;
    }

    public LearningProfile getProfile(Long userId) {
        // 1. 验证用户是否存在
        if (userService.getById(userId) == null) {
            throw new IllegalArgumentException("用户不存在，无法查询学习档案。");
        }
        return profileMapper.selectOne(new QueryWrapper<LearningProfile>().eq("user_id", userId));
    }
}