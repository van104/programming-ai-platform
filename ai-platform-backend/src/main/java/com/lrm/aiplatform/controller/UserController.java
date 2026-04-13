package com.lrm.aiplatform.controller;

import com.lrm.aiplatform.common.Result;
import com.lrm.aiplatform.entity.User;
import org.springframework.web.bind.annotation.*;

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

    private final com.lrm.aiplatform.service.IUserService userService;

    public UserController(com.lrm.aiplatform.service.IUserService userService) {
        this.userService = userService;
    }

    //添加用户
    @GetMapping("/add")
    public Result<String> addUser() {
        User user = new User();
        user.setUsername("student1");
        user.setPassword("123456");
        user.setRole("student");
        userService.save(user);
        return Result.success("用户添加成功", null);
    }
    //全局删除
    @GetMapping("/delete/")
    public Result<String> deleteUser() {
        userService.remove(null);
        return Result.success("用户删除成功", null);
    }

    //删除用户，根据id删除
    @GetMapping("/delete/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("用户删除成功", null);
    }

    //更新用户
    @GetMapping("/update")
    public Result<String> updateUser() {
        User user = new User();
        user.setId(16L);
        user.setUsername("student2");
        user.setPassword("123456");
        user.setRole("student");
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
     public Result<java.util.List<User>> queryAllUser() {
        java.util.List<User> userList = userService.list();
        return Result.success("用户查询成功", userList);
    }
    //登陆
    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser){
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (user == null) {
            return new Result<>(401, "用户名或密码错误", null);
        }
        return Result.success("登录成功", user);
    }
}
