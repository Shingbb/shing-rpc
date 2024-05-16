package com.shing.shingrpc.registry;

import com.shing.shingrpc.config.RegistryConfig;
import com.shing.shingrpc.model.ServiceMetaInfo;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * zookeeper 注册中心
 *
 * @author shing
 */
@Slf4j
public class ZooKeeperRegistry implements Registry {

    /**
     * CuratorFramework客户端。用于与ZooKeeper进行交互。
     */
    private CuratorFramework client;

    /**
     * 服务发现器。用于发现和查询分布式系统中的服务。
     */
    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String ZK_ROOT_PATH = "/rpc/zk";


    /**
     * 初始化函数，用于创建并启动 Curator 客户端和服务发现。
     *
     * @param registryConfig 注册中心配置，包含注册中心地址和超时时间等配置信息。
     *                       这些信息将用于构建 Curator 客户端。
     */
    @Override
    public void init(RegistryConfig registryConfig) {
        // 使用提供的注册中心配置构建 Curator 客户端
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress()) // 注册中心地址
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3)) // 重试策略
                .build();

        // 使用 Curator 客户端构建服务发现实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client) // Curator 客户端
                .basePath(ZK_ROOT_PATH) // ZooKeeper 根路径
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class)) // 序列化器
                .build();

        try {
            // 启动 Curator 客户端和服务发现实例
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            // 启动过程中遇到异常，抛出运行时异常
            throw new RuntimeException(e);
        }
    }


    /**
     * 注册服务到服务发现组件。
     *
     * @param serviceMetaInfo 包含服务元数据信息的对象，用于构建服务实例和注册到ZooKeeper。
     * @throws Exception 抛出异常的条件不明确，需要具体化。
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 将服务实例注册到服务发现组件
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        // 将服务节点的键值添加到本地缓存以供后续使用
        String registerKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.add(registerKey);
    }

    /**
     * 取消注册指定的服务。
     * 该方法会将给定的服务元信息从服务发现机制中注销。
     *
     * @param serviceMetaInfo 服务的元信息，包含服务的名称、地址等标识信息。
     *                        用于构建服务实例以进行注销操作。
     * @throws RuntimeException 如果注销服务时发生异常，则抛出运行时异常。
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            // 使用服务元信息构建服务实例，然后注销该服务实例
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            // 如果在注销过程中遇到任何异常，将其封装并抛出为运行时异常
            throw new RuntimeException(e);
        }
    }


    /**
     * 进行服务发现，优先从缓存中获取服务信息，如果缓存中不存在，则通过查询服务实例来获取，并更新缓存。
     *
     * @param serviceKey 用于查询特定服务的关键字。
     * @return 返回一个服务元信息列表，包含多个服务的详细信息。
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存中读取服务信息
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }
        try {
            // 通过服务发现框架查询服务实例
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList = serviceDiscovery.queryForInstances(serviceKey);

            // 将服务实例转换为服务元信息列表
            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstanceList.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());

            // 将获取到的服务信息写入缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            // 查询服务失败，抛出运行时异常
            throw new RuntimeException("获取服务列表失败", e);
        }
    }


    @Override
    public void heartBeat() {
        // 不需要心跳机制，建立了临时节点，如果服务器故障，则临时节点直接丢失

    }

    /**
     * 监听（消费端）
     *
     * @param serviceNodeKey 服务节点 key
     */
    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean newWatch = watchingKeySet.add(watchKey);
        if (newWatch) {
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener
                            .builder()
                            .forDeletes(childData -> registryServiceCache.clearCache())
                            .forChanges(((oldNode, node) -> registryServiceCache.clearCache()))
                            .build()
            );
        }
    }

    /**
     * 销毁函数，用于清理资源和下线当前节点。
     * 此方法不接受参数，也不返回任何值。
     * 主要执行以下两个主要操作：
     * 1. 下线节点：从注册中心删除当前节点的所有临时节点。
     * 2. 释放资源：关闭与注册中心的连接。
     */
    @Override
    public void destroy() {
        log.info("当前节点下线");
        // 下线节点，删除当前节点的所有临时节点
        for (String key : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                // 如果删除节点失败，抛出运行时异常
                throw new RuntimeException(key + "节点下线失败");
            }
        }

        // 释放与注册中心的连接资源
        if (client != null) {
            client.close();
        }
    }

    /**
     * 构建一个服务实例。
     *
     * @param serviceMetaInfo 包含服务元数据信息的对象，如服务主机、端口和键等。
     * @return 返回一个配置了服务地址、名称和元数据的服务实例对象。
     * @throws RuntimeException 如果构建过程中发生异常。
     */
    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        // 构建服务的完整地址
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
        try {
            // 使用服务的元数据信息构建服务实例
            return ServiceInstance
                    .<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        } catch (Exception e) {
            // 如果构建过程中发生异常，抛出运行时异常
            throw new RuntimeException(e);
        }
    }

}
