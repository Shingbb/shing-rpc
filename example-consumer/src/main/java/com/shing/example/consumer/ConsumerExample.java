package com.shing.example.consumer;

import com.shing.shingrpc.config.RpcConfig;
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

    }
}
