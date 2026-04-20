package com.lrm.aiplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrm.aiplatform.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lrm
 * @since 2026-03-16
 */
public interface IUserService extends IService<User> {

    User login(String username, String password);

    List<User> getUsersByRole(String role);

    String register(User user);

    List<User> getAllUsers();
}