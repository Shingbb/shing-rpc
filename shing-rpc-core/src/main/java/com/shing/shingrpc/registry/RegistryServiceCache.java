package com.shing.shingrpc.registry;

import com.shing.shingrpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心服务本地缓存类，用于缓存服务的元数据信息。
 *
 * @author shing
 */
public class RegistryServiceCache {

    /**
     * 保存服务元数据信息的缓存列表。
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 将新的服务缓存写入本地缓存。
     *
     * @param newServiceCache 新的服务缓存列表，包含需要缓存的服务的元数据信息。
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    /**
     * 从本地缓存读取服务信息。
     *
     * @return 返回当前缓存中保存的所有服务的元数据信息列表。
     */
    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    /**
     * 清空本地缓存，将服务缓存置为null。
     */
    void clearCache() {
        this.serviceCache = null;
    }

}
