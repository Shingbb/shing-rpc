package com.shing.shingrpc.loadbalancer;

import com.shing.shingrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器。该负载均衡器使用一致性哈希算法来选择服务提供者，
 * 这样的选择方式能够在服务提供者数量变化时最小化服务请求重新分发的数量。
 *
 * @author shing
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 一致性 Hash 环，存放虚拟节点。使用TreeMap以保持节点的有序性。
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数。每个真实节点在环上会被表示为这个数量的虚拟节点。
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    /**
     * 根据请求参数选择一个服务提供者。
     *
     * @param requestParams 请求参数，用于计算请求的哈希值。
     * @param serviceMetaInfoList 可用的服务提供者列表。
     * @return 选择的服务提供者信息。如果列表为空，返回null。
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        // 构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }
        // 获取调用请求的 hash 值
        int hash = getHash(requestParams);

        // 选择最接近且大于等于调用请求 hash 值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            // 如果没有大于等于调用请求 hash 值的虚拟节点，则返回环首部的节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * 计算给定键的哈希值。这里简单地使用键的hashCode方法。
     * 根据实际需要，可以使用更复杂的哈希算法。
     *
     * @param key 要计算哈希值的键。
     * @return 键的哈希值。
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}
