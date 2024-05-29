package com.shing.shingrpc.bootstrap;

import com.shing.shingrpc.RpcApplication;
import com.shing.shingrpc.config.RegistryConfig;
import com.shing.shingrpc.config.RpcConfig;
import com.shing.shingrpc.model.ServiceMetaInfo;
import com.shing.shingrpc.model.ServiceRegisterInfo;
import com.shing.shingrpc.registry.LocalRegistry;
import com.shing.shingrpc.registry.Registry;
import com.shing.shingrpc.registry.RegistryFactory;
import com.shing.shingrpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者初始化引导类。负责完成服务的注册和服务器的启动。
 *
 * @author shing
 */
public class ProviderBootstrap {

    /**
     * 初始化服务提供者。此方法会遍历给定的服务注册信息列表，将服务注册到本地及远程注册中心，并启动服务器。
     *
     * @param serviceRegisterInfoList 需要注册的服务信息列表，包含每个服务的名称和实现类。
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }

        // 启动服务器
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}