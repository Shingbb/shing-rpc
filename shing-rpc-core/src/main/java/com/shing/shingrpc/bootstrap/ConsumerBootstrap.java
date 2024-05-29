package com.shing.shingrpc.bootstrap;

import com.shing.shingrpc.RpcApplication;

/**
 * 服务消费者启动类（初始化）
 *
 * @author shing
 */
public class ConsumerBootstrap {

    /**
     * 初始化
     */
    public static void init() {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
    }
}
