package com.shing.shingrpc;

import com.shing.shingrpc.config.RegistryConfig;
import com.shing.shingrpc.config.RpcConfig;
import com.shing.shingrpc.constant.RpcConstant;
import com.shing.shingrpc.registry.Registry;
import com.shing.shingrpc.registry.RegistryFactory;
import com.shing.shingrpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 框架应用
 * 相当于 holder，存放了项目全局用到的变量。双检锁单例模式实现
 *
 * @author shing
 */
@Slf4j
public class RpcApplication {

    /**
     * rpcConfig 是一个用于存储RPC配置的类变量。
     * 由于它是静态变量，所以这个配置在整个应用程序生命周期中只被初始化一次。
     * 使用 "volatile" 关键字确保了多线程环境下的可见性，即当一个线程修改了这个变量的值时，
     * 其他线程能够立即看到这个修改。
     */
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig 自定义的RPC配置
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init,config={}", newRpcConfig.toString());

        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

    }

    /**
     * 初始化框架，尝试从配置文件加载配置，失败则使用默认配置
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            // 尝试从配置文件加载RPC配置
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败,使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取RPC配置
     *
     * @return 返回当前RPC框架的配置对象
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    // 双检锁机制，确保线程安全地初始化rpcConfig
                    init();
                }
            }

        }
        return rpcConfig;
    }

}