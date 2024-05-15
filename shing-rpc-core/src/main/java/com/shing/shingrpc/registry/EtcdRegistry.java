package com.shing.shingrpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.shing.shingrpc.config.RegistryConfig;
import com.shing.shingrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Etcd 注册中心实现类，用于服务的注册与发现。
 *
 * @author shing
 */
public class EtcdRegistry implements Registry {

    // Etcd客户端，用于与Etcd服务器进行通信。
    private Client client;

    // KV客户端，用于进行键值对的存储和检索，实现服务的注册与发现逻辑。
    private KV kvClient;

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

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
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    /**
     * 注册服务到 Etcd 注册中心。
     *
     * @param serviceMetaInfo 待注册的服务元数据信息。
     * @throws Exception 如果注册过程中遇到任何错误，则抛出异常。
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {

        // 创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        // 为服务注册创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将服务的键值对与租约关联，并设置租约过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
    }

    /**
     * 从 Etcd 注册中心注销服务。
     *
     * @param serviceMetaInfo 待注销的服务元数据信息。
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        // 直接删除对应的服务注册信息
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));

        // 也要从本地缓存移除
        localRegisterNodeKeySet.remove(registerKey);
    }

    /**
     * 实现服务发现功能，查询指定服务的所有提供者。
     *
     * @param serviceKey 需要查询的服务关键字。
     * @return 返回空列表，该方法暂未实现服务发现功能。
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //前缀搜索，结尾一定要加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            return keyValues.stream()
                    .map(keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    /**
     * 心跳函数，用于定时对注册的节点进行续签操作。
     * 此函数不接受参数且无返回值。
     * 续签操作确保节点信息在注册中心保持活跃，防止因超时而被移除。
     */
    @Override
    public void heartBeat() {
        // 10 秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的 key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 该节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });

        // 设置Cron表达式支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        // 启动定时任务执行器
        CronUtil.start();
    }


    /**
     * 销毁当前节点资源的方法。
     * 此方法负责在当前节点下线时，释放相关的资源，包括KV客户端和普通客户端。
     */
    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        // 下线节点
        // 遍历本节点所有的 key
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }
        // 释放资源
        // 关闭KV客户端，如果它不为null。
        if (kvClient != null) {
            kvClient.close();
        }
        // 关闭普通客户端，如果它不为null。
        if (client != null) {
            client.close();
        }
    }
}