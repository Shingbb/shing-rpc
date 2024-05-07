package com.shing.example.common.service;

import com.shing.example.common.model.User;

/**
 * 用户服务
 *
 * @author shing
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 新方法 - 获取数字
     *
     * @return
     */
    default short getNumber() {
        return 123;
    }
}
