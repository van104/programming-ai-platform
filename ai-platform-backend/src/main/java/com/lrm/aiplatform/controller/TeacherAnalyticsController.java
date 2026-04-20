package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.entity.LearningProfile;
import com.lrm.aiplatform.service.impl.TeacherAnalyticsService;
import com.lrm.aiplatform.vo.ClassStatisticsVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherAnalyticsController {

    private final TeacherAnalyticsService analyticsService;

    public TeacherAnalyticsController(TeacherAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * 获取班级整体统计数据
     */
    @GetMapping("/class/statistics")
    public Result<ClassStatisticsVO> classStatistics() {
        return Result.success(analyticsService.getClassStatistics());
    }

    /**
     * 查看指定学生的学习画像
     */
    @GetMapping("/student/profile/{userId}")
    public Result<LearningProfile> studentProfile(@PathVariable Long userId) {
        LearningProfile profile = analyticsService.getStudentProfile(userId);
        if (profile == null) {
            return new Result<>(404, "该学生暂无学习画像", null);
        }
        return Result.success("查询成功", profile);
    }

    /**
     * 获取所有学生的学习画像列表
     */
    @GetMapping("/student/profiles")
    public Result<List<LearningProfile>> allStudentProfiles() {
        return Result.success("查询成功", analyticsService.getAllStudentProfiles());
    }
}
