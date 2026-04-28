package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.utils.JwtUtil;
import com.lrm.aiplatform.utils.MD5Util;
import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.dto.LoginDTO;
import com.lrm.aiplatform.entity.User;
import com.lrm.aiplatform.service.IUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lrm
 * @since 2026-03-16
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    public UserController(IUserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //添加用户
    @PostMapping("/add")
    public Result<String> addUser(@RequestBody User user) {
        String errorMsg = userService.register(user);
        if (errorMsg != null) {
            return new Result<>(400, errorMsg, null);
        }
        return Result.success("用户添加成功", null);
    }

    //删除用户，根据id删除
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("用户删除成功", null);
    }

    //更新用户
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user) {
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            user.setPassword(MD5Util.encrypt(user.getPassword()));
        }
        userService.updateById(user);
        return Result.success("用户更新成功", null);
    }

    //查询用户，根据id查询
    @GetMapping("/query/{id}")
    public Result<User> queryUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success("用户查询成功", user);
    }

    //查询所有用户
    @GetMapping("/query/all")
    public Result<List<User>> queryAllUser() {
        List<User> userList = userService.list();
        return Result.success("用户查询成功", userList);
    }

    //登陆RequestBody（JSON 请求体）
    @PostMapping("/login")
    public Result<LoginDTO> login(@RequestBody User loginUser) {
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (user == null) {
            return new Result<>(401, "用户名或密码错误", null);
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginDTO loginDTO = new LoginDTO(user.getId(), user.getUsername(), token);
        return Result.success("登录成功", loginDTO);
    }
//    //路径参数（RESTful 风格）
//    @PostMapping("/login1/{username}/{password}")
//    public Result<User> login1(@PathVariable String username, @PathVariable String password) {
//        User user = userService.login(username, password);
//        if (user == null) {
//            return new Result<>(401, "用户名或密码错误", null);
//        }
//        // MD5加密返回
//        user.setPassword(MD5Util.encrypt(user.getPassword()));
//        return Result.success("登录成功", user);
//    }
//
//    //传统查询参数（Query String）
//    @PostMapping("/login2")
//    public Result<User> login2(@RequestParam String username, @RequestParam String password) {
//        User user = userService.login(username, password);
//        if (user == null) {
//            return new Result<>(401, "用户名或密码错误", null);
//        }
//        user.setPassword(MD5Util.encrypt(user.getPassword()));
//        return Result.success("登录成功", user);
//    }

    //根据角色查询用户
    @GetMapping("/role/{role}")
    public Result<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        // MD5加密返回
        for (User user : users) {
             user.setPassword(MD5Util.encrypt(user.getPassword()));
        }
        return Result.success("角色查询成功", users);
    }

    /**
     * 获取用户列表 (第2天新增)
     */
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        List<User> users = userService.getAllUsers();
        return Result.success("获取成功", users);
    }
}
