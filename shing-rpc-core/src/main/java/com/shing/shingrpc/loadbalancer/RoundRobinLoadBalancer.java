package com.shing.shingrpc.loadbalancer;

import com.shing.shingrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器。该负载均衡器采用轮询算法来选择服务提供者。
 *
 * @author shing
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    /**
     * 当前轮询的下标，使用AtomicInteger保证线程安全。
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     * 从服务列表中选择一个服务提供者。
     *
     * @param requestParams 请求参数，用于负载均衡器的选路逻辑（本实现未使用该参数）。
     * @param serviceMetaInfoList 可用的服务提供者列表。
     * @return 选中的服务提供者的元数据信息。如果列表为空，返回null；如果列表只有一个元素，直接返回该元素。
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        // 检查服务列表是否为空
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 如果只有一个服务，无需轮询，直接返回
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }

        // 使用取模算法进行轮询，并更新当前索引
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}
