package com.shing.shingrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.shing.shingrpc.RpcApplication;
import com.shing.shingrpc.config.RpcConfig;
import com.shing.shingrpc.constant.RpcConstant;
import com.shing.shingrpc.model.RpcRequest;
import com.shing.shingrpc.model.RpcResponse;
import com.shing.shingrpc.model.ServiceMetaInfo;
import com.shing.shingrpc.protocol.*;
import com.shing.shingrpc.registry.Registry;
import com.shing.shingrpc.registry.RegistryFactory;
import com.shing.shingrpc.serializer.JdkSerializer;
import com.shing.shingrpc.serializer.Serializer;
import com.shing.shingrpc.serializer.SerializerFactory;
import com.shing.shingrpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（JDK 动态代理）
 *
 * @author shing
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        /*Serializer serializer = new JdkSerializer();
        Serializer serializer = null;
        ServiceLoader<Serializer> serviceLoader = ServiceLoader.load(Serializer.class);
        for (Serializer service : serviceLoader) {
            serializer = service;
        }*/
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
        ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
        // 发送 TCP 请求
        RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
        return rpcResponse.getData();
    }
}
