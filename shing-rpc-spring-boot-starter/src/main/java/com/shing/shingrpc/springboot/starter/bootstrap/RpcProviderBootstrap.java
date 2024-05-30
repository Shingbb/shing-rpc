package com.shing.shingrpc.springboot.starter.bootstrap;

import com.shing.shingrpc.RpcApplication;
import com.shing.shingrpc.config.RegistryConfig;
import com.shing.shingrpc.config.RpcConfig;
import com.shing.shingrpc.model.ServiceMetaInfo;
import com.shing.shingrpc.registry.LocalRegistry;
import com.shing.shingrpc.registry.Registry;
import com.shing.shingrpc.registry.RegistryFactory;
import com.shing.shingrpc.springboot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Rpc服务提供者启动器。该类实现了Spring BeanPostProcessor接口，
 * 用于在Spring应用程序启动时，扫描标记了@RpcService注解的服务类，
 * 并将这些服务注册到本地及远程的服务注册中心。
 *
 * @author shing
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {

    /**
     * 处理Spring Bean初始化后的逻辑，扫描并注册Rpc服务。
     *
     * @param bean       初始化后的Spring Bean实例。
     * @param beanName   Spring Bean的名字。
     * @return 处理后的Bean实例，此处返回原Bean实例。
     * @throws BeansException 如果处理中发生错误。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 发现@RpcService注解，开始注册服务
            // 1. 解析服务的基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 如果没有指定接口类，则使用实现类的第一个接口作为服务接口
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 2. 执行服务注册
            // 首先在本地注册表中注册服务
            LocalRegistry.register(serviceName, beanClass);

            // 然后根据全局配置，将服务注册到指定的服务注册中心
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                // 注册失败时抛出运行时异常
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}

