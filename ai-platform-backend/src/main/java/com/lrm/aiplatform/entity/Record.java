package com.lrm.aiplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author lrm
 * @since 2026-03-16
 */
@Getter
@Setter
@ToString
@TableName("code_record")
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 提交的代码内容
     */
    @TableField("code_content")
    private String codeContent;

    /**
     * 运行结果
     */
    @TableField("result")
    private String result;

    /**
     * 提交时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
