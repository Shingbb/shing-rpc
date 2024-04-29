package com.shing.example.consumer;

import com.shing.example.common.model.User;
import com.shing.example.common.service.UserService;
import com.shing.shingrpc.proxy.ServiceProxyFactory;

/**
 * 简易服务消费者示例
 * 这个类展示了如何简单地作为一个服务的消费者，去调用UserService来获取用户信息。
 *
 * @author shing
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        // 静态代理
//        UserService userService = new UserServiceProxy();

        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User(); // 创建一个用户实例，设置用户名
        user.setName("shing");

        // 调用userService的getUser方法，尝试获取用户信息
        User newUser = userService.getUser(user);

        // 检查返回的用户信息是否为空，打印用户名称或提示用户信息为空
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
