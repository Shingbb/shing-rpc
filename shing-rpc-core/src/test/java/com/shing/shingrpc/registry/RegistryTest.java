package com.shing.shingrpc.registry;

import com.shing.shingrpc.config.RegistryConfig;
import com.shing.shingrpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * 注册中心测试类，用于测试注册中心的各种功能。
 *
 * @author shing
 */
public class RegistryTest {
    final Registry registry = new EtcdRegistry(); // 使用EtcdRegistry作为注册中心实现

    /**
     * 初始化注册中心，配置注册中心地址。
     */
    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379"); // 设置Etcd注册中心的地址
        registry.init(registryConfig); // 初始化注册中心
    }

    /**
     * 测试服务注册功能。
     * 注册了三个不同配置的服务实例。
     */
    @Test
    public void register() throws Exception {
        // 注册第一个服务实例
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);

        // 注册第二个服务实例
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);
        registry.register(serviceMetaInfo);

        // 注册第三个服务实例
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
    }

    /**
     * 测试服务注销功能。
     * 注销了之前注册的第一个服务实例。
     */
    @Test
    public void unRegister() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.unRegister(serviceMetaInfo);
    }

    /**
     * 测试服务发现功能。
     * 发现了之前注册的全部服务实例。
     */
    @Test
    public void serviceDiscovery() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(serviceMetaInfoList); // 确保服务发现结果不为空
    }

    /**
     * 测试心跳检测功能。
     * 该测试方法实际上没有具体的测试逻辑，主要用于配合其他测试方法模拟真实环境下的心跳维持。
     */
    @Test
    public void heartBeat() throws Exception {
        // 先执行注册，init方法中已经包含了心跳初始化
        register();
        // 阻塞1分钟模拟心跳维持过程
        Thread.sleep(60 * 1000L);
    }

}