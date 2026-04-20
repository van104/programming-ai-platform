package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
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
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
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

    //登陆
    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser) {
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (user == null) {
            return new Result<>(401, "用户名或密码错误", null);
        }
        // 不返回密码给前端
        user.setPassword(null);
        return Result.success("登录成功", user);
    }

    //根据角色查询用户
    @GetMapping("/role/{role}")
    public Result<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        // 不返回密码给前端
        for (User user : users) {
             user.setPassword(null);
        }
        return Result.success("角色查询成功", users);
    }
}
