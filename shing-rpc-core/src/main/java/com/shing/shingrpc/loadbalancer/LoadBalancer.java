package com.shing.shingrpc.loadbalancer;

import com.shing.shingrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器接口，用于消费端选择服务提供者的策略。
 *
 * @author shing
 */
public interface LoadBalancer {

    /**
     * 根据请求参数和可用的服务元信息选择一个服务提供者。
     *
     * @param requestParams       请求参数，可能用于影响选择逻辑。
     * @param serviceMetaInfoList 可用的服务提供者列表。
     * @return 选择出的服务提供者的元信息。
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
