package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.entity.Exercise;
import com.lrm.aiplatform.service.impl.ExerciseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    /**
     * 查询所有题目列表
     */
    @GetMapping("/list")
    public Result<List<Exercise>> list() {
        return Result.success("查询成功", exerciseService.listExercises());
    }

    /**
     * 根据 ID 获取题目详情
     */
    @GetMapping("/{id}")
    public Result<Exercise> getById(@PathVariable Long id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        if (exercise == null) {
            return new Result<>(404, "题目不存在", null);
        }
        return Result.success("查询成功", exercise);
    }

    /**
     * 添加新题目
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Exercise exercise) {
        exerciseService.addExercise(exercise);
        return Result.success("添加成功", null);
    }

    /**
     * 更新题目信息
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Exercise exercise) {
        if (exercise.getId() == null) {
            return new Result<>(400, "题目 ID 不能为空", null);
        }
        if (exerciseService.getExerciseById(exercise.getId()) == null) {
            return new Result<>(404, "题目不存在", null);
        }
        exerciseService.updateExercise(exercise);
        return Result.success("更新成功", null);
    }

    /**
     * 根据 ID 删除题目
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (exerciseService.getExerciseById(id) == null) {
            return new Result<>(404, "题目不存在", null);
        }
        exerciseService.deleteExercise(id);
        return Result.success("删除成功", null);
    }
}
