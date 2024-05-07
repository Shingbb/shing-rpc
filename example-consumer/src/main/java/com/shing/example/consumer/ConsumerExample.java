package com.shing.example.consumer;

import com.shing.example.common.model.User;
import com.shing.example.common.service.UserService;
import com.shing.shingrpc.config.RpcConfig;
import com.shing.shingrpc.proxy.ServiceProxyFactory;
import com.shing.shingrpc.utils.ConfigUtils;

/**
 * 简易服务消费者示例
 *
 * @author shing
 */
public class ConsumerExample {
    public static void main(String[] args) {
        Object rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("alex");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
        long number = userService.getNumber();
        System.out.println(number);


    }
}
