package com.lrm.aiplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Exercise {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private String difficulty;

    private LocalDateTime createTime;
}
