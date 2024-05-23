package com.shing.shingrpc.loadbalancer;

import com.shing.shingrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机负载均衡器类，实现了LoadBalancer接口，用于从服务列表中随机选择一个服务进行负载。
 *
 * @author shing
 */
public class RandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random(); // 使用Random类进行随机数生成

    /**
     * 从提供的服务元信息列表中随机选择一个服务。
     *
     * @param requestParams 请求参数，可用于负载均衡算法中（本实现未使用该参数）。
     * @param serviceMetaInfoList 服务元信息列表，包含可用的服务实例。
     * @return 返回随机选择的服务元信息，如果列表为空返回null。
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size(); // 获取服务列表的大小
        if (size == 0) {
            return null; // 如果列表为空，返回null
        }
        // 如果服务列表只有一个服务，直接返回该服务
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        // 从服务列表中随机选择一个服务并返回
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}

