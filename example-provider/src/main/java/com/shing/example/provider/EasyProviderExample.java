package com.shing.example.provider;

import com.shing.shingrpc.server.HttpServer;
import com.shing.shingrpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 *
 * @author shing
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8081);
    }
}
