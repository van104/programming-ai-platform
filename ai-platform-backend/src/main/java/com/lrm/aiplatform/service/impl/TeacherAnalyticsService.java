package com.lrm.aiplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lrm.aiplatform.entity.AiRecord;
import com.lrm.aiplatform.entity.LearningProfile;
import com.lrm.aiplatform.entity.Submission;
import com.lrm.aiplatform.entity.User;
import com.lrm.aiplatform.mapper.AiRecordMapper;
import com.lrm.aiplatform.mapper.LearningProfileMapper;
import com.lrm.aiplatform.mapper.SubmissionMapper;
import com.lrm.aiplatform.mapper.UserMapper;
import com.lrm.aiplatform.vo.ClassStatisticsVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherAnalyticsService {

    private final UserMapper userMapper;
    private final AiRecordMapper aiRecordMapper;
    private final SubmissionMapper submissionMapper;
    private final LearningProfileMapper learningProfileMapper;

    public TeacherAnalyticsService(UserMapper userMapper,
            AiRecordMapper aiRecordMapper,
            SubmissionMapper submissionMapper,
            LearningProfileMapper learningProfileMapper) {
        this.userMapper = userMapper;
        this.aiRecordMapper = aiRecordMapper;
        this.submissionMapper = submissionMapper;
        this.learningProfileMapper = learningProfileMapper;
    }

    /**
     * 获取班级整体统计数据
     */
    public ClassStatisticsVO getClassStatistics() {
        // 查询学生数量
        List<User> students = userMapper.selectList(
                new QueryWrapper<User>().eq("role", "student"));
        int studentCount = students.size();

        ClassStatisticsVO vo = new ClassStatisticsVO();
        vo.setTotalStudentCount(studentCount);

        if (studentCount == 0) {
            vo.setTotalAiUsageCount(0);
            vo.setTotalSubmissionCount(0);
            vo.setAvgAiUsage(0.0);
            vo.setAvgSubmission(0.0);
            return vo;
        }

        // 只统计学生的数据
        List<Long> studentIds = students.stream().map(User::getId).toList();

        int aiTotal = Math.toIntExact(
                aiRecordMapper.selectCount(new QueryWrapper<AiRecord>().in("user_id", studentIds)));

        int submitTotal = Math.toIntExact(
                submissionMapper.selectCount(new QueryWrapper<Submission>().in("user_id", studentIds)));

        vo.setTotalAiUsageCount(aiTotal);
        vo.setTotalSubmissionCount(submitTotal);
        vo.setAvgAiUsage((double) Math.round(aiTotal * 1.0 / studentCount));
        vo.setAvgSubmission((double) Math.round(submitTotal * 1.0 / studentCount));

        return vo;
    }
    /*
     * 获取班级整体统计数据（根据学习画像）
     */
    // public ClassStatisticsVO getClassStatisticsByProfile() {
    // List<User> students = userMapper.selectList(
    // new QueryWrapper<User>().eq("role", "student")
    // );
    // int studentCount = students.size();
    //
    // List<LearningProfile> profiles = learningProfileMapper.selectList(null);
    //
    // int aiTotal = 0;
    // int submitTotal = 0;
    // for (LearningProfile p : profiles) {
    // if (p.getAiUsageCount() != null) {
    // aiTotal += p.getAiUsageCount();
    // }
    // if (p.getSubmissionCount() != null) {
    // submitTotal += p.getSubmissionCount();
    // }
    // }
    //
    // ClassStatisticsVO vo = new ClassStatisticsVO();
    // vo.setTotalStudentCount(studentCount);
    // vo.setTotalAiUsageCount(aiTotal);
    // vo.setTotalSubmissionCount(submitTotal);
    //
    // if (studentCount > 0) {
    // vo.setAvgAiUsage(aiTotal * 1.0 / studentCount);
    // vo.setAvgSubmission(submitTotal * 1.0 / studentCount);
    // } else {
    // vo.setAvgAiUsage(0.0);
    // vo.setAvgSubmission(0.0);
    // }
    //
    // return vo;
    // }

    /**
     * 查看指定学生的学习画像
     */
    public LearningProfile getStudentProfile(Long userId) {
        return learningProfileMapper.selectOne(
                new QueryWrapper<LearningProfile>().eq("user_id", userId).last("LIMIT 1"));
    }

    /**
     * 获取所有学生的学习画像列表
     */
    public List<LearningProfile> getAllStudentProfiles() {
        return learningProfileMapper.selectList(
                new QueryWrapper<LearningProfile>().orderByAsc("user_id"));
    }
}
