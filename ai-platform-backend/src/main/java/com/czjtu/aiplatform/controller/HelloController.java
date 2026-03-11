package com.czjtu.aiplatform.controller;

import com.czjtu.aiplatform.common.Result;
import com.czjtu.aiplatform.service.DemoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class HelloController {
    private final DemoService demoService;

    public HelloController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success(demoService.getWelcomeMessage());
    }
}