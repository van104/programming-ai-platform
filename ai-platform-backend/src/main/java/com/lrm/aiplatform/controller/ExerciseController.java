package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.entity.Exercise;
import com.lrm.aiplatform.service.impl.ExerciseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// RestController：标记这是接口控制层；创建该类对象
// RequestMapping:请求映射，统一接口

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    //http://localhost:8080/exercise/list
    @GetMapping("/list")
    public Result<List<Exercise>> list() {
        return Result.success(exerciseService.listExercises());
    }
}
