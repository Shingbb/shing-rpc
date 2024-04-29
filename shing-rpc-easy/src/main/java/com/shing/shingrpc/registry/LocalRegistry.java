package com.shing.shingrpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心，用于注册和发现服务。
 *
 * @author shing
 */
public class LocalRegistry {

    // 使用ConcurrentHashMap来存储服务名称与对应类的映射，以支持线程安全的读写操作。
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务。
     *
     * @param serviceName 服务名称，作为映射的键。
     * @param serviceClass 服务的类，作为映射的值。
     */
    public static void register(String serviceName, Class<?> serviceClass) {
        map.put(serviceName, serviceClass);
    }

    /**
     * 获取服务的类。
     *
     * @param serviceName 服务名称，查找映射的键。
     * @return 返回与服务名称对应的服务类，如果不存在，则返回null。
     */
    public static Class<?> getService(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 移除服务。
     *
     * @param serviceName 要移除的服务名称。
     */
    public static void removeService(String serviceName) {
        map.remove(serviceName);
    }

}

