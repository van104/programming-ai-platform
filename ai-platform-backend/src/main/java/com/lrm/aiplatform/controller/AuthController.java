package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.utils.JwtUtil;
import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.dto.LoginDTO;
import com.lrm.aiplatform.entity.User;
import com.lrm.aiplatform.service.IUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AuthController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(IUserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<LoginDTO> login(@RequestParam String username,
                                  @RequestParam String password) {
        User user = userService.login(username, password);
        if (user == null) {
            return new Result<>(401, "用户名或密码错误", null);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginDTO loginDTO = new LoginDTO(user.getId(), user.getUsername(), token);
        return Result.success("登录成功", loginDTO);
    }

    @GetMapping("/debug/token")
    public String debugToken() {
        return jwtUtil.generateToken(1L, "debug", "admin");
    }
}
