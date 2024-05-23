package com.shing.shingrpc.loadbalancer;

/**
 * 负载均衡器键名常量
 * 该接口定义了负载均衡器中使用的键名常量，包括轮询、随机和一致性哈希等策略的键名。
 *
 * @author shing
 */
public interface LoadBalancerKeys {
    /**
     * 轮询策略键名
     * 用于标识轮询负载均衡策略。
     */
    String ROUND_ROBIN = "roundRobin";

    /**
     * 随机策略键名
     * 用于标识随机负载均衡策略。
     */
    String RANDOM = "random";

    /**
     * 一致性哈希策略键名
     * 用于标识一致性哈希负载均衡策略。
     */
    String CONSISTENT_HASH = "consistentHash";
}
