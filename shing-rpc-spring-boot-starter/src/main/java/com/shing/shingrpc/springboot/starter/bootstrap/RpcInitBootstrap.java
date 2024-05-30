package com.shing.shingrpc.springboot.starter.bootstrap;

import com.shing.shingrpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import com.shing.shingrpc.RpcApplication;
import com.shing.shingrpc.config.RpcConfig;
import com.shing.shingrpc.server.tcp.VertxTcpServer;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Rpc 框架启动器类，用于在Spring Boot应用启动时初始化Rpc框架。
 *
 * @author shing
 * @slf4j 日志注解，用于记录日志信息。
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * 注册Rpc相关的Bean定义到Spring的BeanDefinitionRegistry中。
     *
     * @param importingClassMetadata 包含了引入RpcInitBootstrap的类的元数据，用于读取EnableRpc注解的属性值。
     * @param registry Spring的BeanDefinitionRegistry，用于注册Bean定义。
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 读取EnableRpc注解的needServer属性值，判断是否需要启动服务器
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // 初始化Rpc框架，进行配置和注册中心的设置
        RpcApplication.init();

        // 获取全局Rpc配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 如果需要启动服务器，则实例化并启动VertxTcpServer
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            // 如果不需要启动服务器，记录日志信息
            log.info("不启动 server");
        }
    }
}

