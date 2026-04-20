package com.lrm.aiplatform.service.impl;

import com.lrm.aiplatform.entity.Exercise;
import com.lrm.aiplatform.mapper.ExerciseMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseMapper exerciseMapper;

    public ExerciseService(ExerciseMapper exerciseMapper) {
        this.exerciseMapper = exerciseMapper;
    }

    /**
     * 查询所有题目
     */
    public List<Exercise> listExercises() {
        return exerciseMapper.selectList(null);
    }

    /**
     * 根据 ID 查询题目
     */
    public Exercise getExerciseById(Long id) {
        return exerciseMapper.selectById(id);
    }

    /**
     * 添加题目
     */
    public void addExercise(Exercise exercise) {
        if (exercise.getCreateTime() == null) {
            exercise.setCreateTime(LocalDateTime.now());
        }
        exerciseMapper.insert(exercise);
    }

    /**
     * 更新题目
     */
    public void updateExercise(Exercise exercise) {
        exerciseMapper.updateById(exercise);
    }

    /**
     * 删除题目
     */
    public void deleteExercise(Long id) {
        exerciseMapper.deleteById(id);
    }
}
