package com.czjtu.aiplatform.service;

import org.springframework.stereotype.Service;

@Service
public class DemoService {
    public String getWelcomeMessage() {
        return "AI Programming Teaching Platform Backend";
    }
}