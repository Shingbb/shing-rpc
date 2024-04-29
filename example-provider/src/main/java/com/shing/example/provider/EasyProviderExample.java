package com.shing.example.provider;

import com.shing.example.common.service.UserService;
import com.shing.shingrpc.registry.LocalRegistry;
import com.shing.shingrpc.server.HttpServer;
import com.shing.shingrpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 *
 * @author shing
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8081);
    }
}
