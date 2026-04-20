package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.service.DemoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
