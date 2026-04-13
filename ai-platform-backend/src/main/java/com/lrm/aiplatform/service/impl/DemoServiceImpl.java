package com.lrm.aiplatform.service.impl;

import com.lrm.aiplatform.service.DemoService;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {

    @Override
    public String getWelcomeMessage() {
        return "Welcome to AI Platform!";
    }
}
