package com.shing.example.provider;

import com.shing.shingrpc.RpcApplication;
import com.shing.example.common.service.UserService;
import com.shing.shingrpc.config.RegistryConfig;
import com.shing.shingrpc.config.RpcConfig;
import com.shing.shingrpc.model.ServiceMetaInfo;
import com.shing.shingrpc.registry.LocalRegistry;
import com.shing.shingrpc.registry.Registry;
import com.shing.shingrpc.registry.RegistryFactory;
import com.shing.shingrpc.server.HttpServer;
import com.shing.shingrpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 *
 * @author shing
 */
public class ProviderExample {
    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
