package com.shing.example.provider;

import com.shing.example.common.model.User;
import com.shing.example.common.service.UserService;

/**
 * 用户服务实现类
 *
 * @author shing
 */

public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
