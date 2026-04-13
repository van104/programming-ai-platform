package com.lrm.aiplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LearningProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer aiUsageCount;

    private Integer submissionCount;

    private String activityLevel;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
