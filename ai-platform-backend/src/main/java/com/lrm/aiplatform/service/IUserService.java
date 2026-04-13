package com.lrm.aiplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrm.aiplatform.entity.User;

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
}