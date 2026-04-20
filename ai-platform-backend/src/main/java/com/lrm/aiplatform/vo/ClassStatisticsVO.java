package com.lrm.aiplatform.vo;

import lombok.Data;

@Data
public class ClassStatisticsVO {

    private Integer totalStudentCount;

    private Integer totalAiUsageCount;

    private Integer totalSubmissionCount;

    private Double avgAiUsage;

    private Double avgSubmission;
}
