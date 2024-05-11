package com.shing.shingrpc.registry;

import cn.hutool.json.JSONUtil;
import com.shing.shingrpc.config.RegistryConfig;
import com.shing.shingrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Etcd 注册中心实现类，用于服务的注册与发现。
 *
 * @author shing
 */
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    /**
     * 注册中心根路径
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";


    /**
     * 初始化 Etcd 注册中心。
     *
     * @param registryConfig 注册中心配置信息，包括地址和超时时间。
     */
    @Override
    public void init(RegistryConfig registryConfig) {
        // 创建 Etcd 客户端并连接到指定的地址
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
    }

    /**
     * 注册服务到 Etcd 注册中心。
     *
     * @param serviceMetaInfo 待注册的服务元数据信息。
     * @throws Exception 如果注册过程中遇到任何错误，则抛出异常。
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 获取 Lease 客户端
        Lease leaseClient = client.getLeaseClient();

        // 为服务注册创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将服务的键值对与租约关联，并设置租约过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
    }

    /**
     * 从 Etcd 注册中心注销服务。
     *
     * @param serviceMetaInfo 待注销的服务元数据信息。
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        // 直接删除对应的服务注册信息
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
    }

    /**
     * 实现服务发现功能，查询指定服务的所有提供者。
     *
     * @param serviceKey 需要查询的服务关键字。
     * @return 返回空列表，该方法暂未实现服务发现功能。
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 暂未实现服务发现，返回空列表
        return List.of();
    }

    /**
     * 销毁注册中心连接，释放资源。
     */
    @Override
    public void destroy() {
        // 输出节点下线信息并释放资源
        System.out.println("当前节点下线");
        // 关闭 KV 和 Client 客户端，释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
