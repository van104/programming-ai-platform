package com.lrm.aiplatform.service.impl;


import com.lrm.aiplatform.entity.Exercise;
import com.lrm.aiplatform.mapper.ExerciseMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExerciseService {

    private final ExerciseMapper exerciseMapper;

    public ExerciseService(ExerciseMapper exerciseMapper) {
        this.exerciseMapper = exerciseMapper;
    }

    public List<Exercise> listExercises() {
        return exerciseMapper.selectList(null);
    }
}
