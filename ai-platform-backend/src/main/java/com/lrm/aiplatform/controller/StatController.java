package com.lrm.aiplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.mapper.AiRecordMapper;
import com.lrm.aiplatform.mapper.SubmissionMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StatController {

    private final AiRecordMapper aiRecordMapper;
    private final SubmissionMapper submissionMapper;

    public StatController(AiRecordMapper aiRecordMapper, SubmissionMapper submissionMapper) {
        this.aiRecordMapper = aiRecordMapper;
        this.submissionMapper = submissionMapper;
    }

    @GetMapping("/stat")
    public Result<Map<String, Long>> getStat(@RequestParam Long userId) {
        QueryWrapper<com.lrm.aiplatform.entity.AiRecord> aiWrapper = new QueryWrapper<>();
        aiWrapper.eq("user_id", userId);
        QueryWrapper<com.lrm.aiplatform.entity.Submission> subWrapper = new QueryWrapper<>();
        subWrapper.eq("user_id", userId);
        Map<String, Long> result = new HashMap<>();
        result.put("aiCount", aiRecordMapper.selectCount(aiWrapper));
        result.put("submitCount", submissionMapper.selectCount(subWrapper));
        return Result.success(result);
    }
}
