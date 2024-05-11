package com.shing.shingrpc.registry;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * EtcdRegistry 类用于演示如何使用 etcd 的 Java 客户端进行键值对的存取操作。
 *
 * @author shing
 */
public class EtcdRegistryDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 创建 Etcd 客户端连接，指定服务端地址
        Client client = Client.builder().endpoints("http://localhost:2379")
                .build();

        // 获取 KV 客户端用于键值对操作
        KV kvClient = client.getKVClient();
        // 定义要存储的键和值
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        // 将键值对存入 etcd
        kvClient.put(key, value).get();

        // 异步获取键对应的值
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);

        // 等待异步操作完成，获取响应结果
        GetResponse response = getFuture.get();

        // 删除已存储的键
        kvClient.delete(key).get();
    }
}

