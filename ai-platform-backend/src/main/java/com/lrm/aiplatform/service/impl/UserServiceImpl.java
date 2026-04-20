package com.lrm.aiplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lrm.aiplatform.entity.User;
import com.lrm.aiplatform.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrm.aiplatform.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lrm
 * @since 2026-03-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username).last("LIMIT 1");
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    @Override
    public List<User> getUsersByRole(String role) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role", role);
        return this.list(wrapper);
    }

    @Override
    public String register(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return "密码不能为空";
        }

        // 检查用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (this.count(wrapper) > 0) {
            return "用户名已存在";
        }

        // 设置默认值
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("student"); // 默认角色
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(java.time.LocalDateTime.now());
        }

        this.save(user);
        return null; // null 表示成功，没有错误信息
    }
}
