package com.czjtu.aiplatform.controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Backend is running!";
    }
}
