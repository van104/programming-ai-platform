package com.lrm.aiplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Submission {

    @TableId(type = IdType.AUTO)
    private Long id;

    @JsonAlias("user_id")
    private Long userId;

    @JsonAlias("exercise_id")
    private Long exerciseId;

    private String code;

    @JsonAlias("submit_time")
    private LocalDateTime submitTime;
}

