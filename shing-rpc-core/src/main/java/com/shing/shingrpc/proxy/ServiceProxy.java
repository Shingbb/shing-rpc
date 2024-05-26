package com.shing.shingrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.shing.shingrpc.RpcApplication;
import com.shing.shingrpc.config.RpcConfig;
import com.shing.shingrpc.constant.RpcConstant;
import com.shing.shingrpc.fault.retry.RetryStrategy;
import com.shing.shingrpc.fault.retry.RetryStrategyFactory;
import com.shing.shingrpc.loadbalancer.LoadBalancer;
import com.shing.shingrpc.loadbalancer.LoadBalancerFactory;
import com.shing.shingrpc.model.RpcRequest;
import com.shing.shingrpc.model.RpcResponse;
import com.shing.shingrpc.model.ServiceMetaInfo;
import com.shing.shingrpc.registry.Registry;
import com.shing.shingrpc.registry.RegistryFactory;
import com.shing.shingrpc.serializer.Serializer;
import com.shing.shingrpc.serializer.SerializerFactory;
import com.shing.shingrpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * 服务代理（JDK 动态代理）类，用于动态生成目标服务的代理对象，实现远程过程调用。
 *
 * @author shing
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 当调用代理对象的方法时，实际执行的逻辑在此方法中实现。
     *
     * @param proxy  代理对象本身，即被代理的对象。
     * @param method 被调用的方法，包含了方法的信息，如方法名、参数类型等。
     * @param args   方法调用时传入的参数数组。
     * @return 调用方法的返回结果，根据实际调用情况动态决定。
     * @throws Throwable 方法执行中抛出的异常。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 初始化序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构建 RPC 请求对象
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            // 从注册中心获取服务提供者列表
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }

            // 使用负载均衡策略选择一个服务提供者
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            HashMap<Object, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            // 应用重试机制发送 TCP 请求并获取响应
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            // 发送请求并处理可能的重试逻辑
            RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
            // 返回方法调用结果
            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("调用失败");
        }
    }
}

