package com.shing.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.shing.example.common.model.User;
import com.shing.example.common.service.UserService;
import com.shing.shingrpc.model.RpcRequest;
import com.shing.shingrpc.model.RpcResponse;
import com.shing.shingrpc.serializer.JdkSerializer;
import com.shing.shingrpc.serializer.Serializer;

import java.io.IOException;

/**
 * UserService的静态代理实现类，用于通过远程过程调用（RPC）获取用户信息。
 *
 * @author shing
 */
public class UserServiceProxy implements UserService {
    /**
     * 通过RPC调用远程服务获取用户信息。
     *
     * @param user 请求中包含的用户对象，通常用于指定要获取信息的用户ID或其他标识。
     * @return 返回从远程服务端获取的用户信息对象。
     */
    @Override
    public User getUser(User user) {
        // 使用JDK序列化器进行数据序列化
        Serializer serializer = new JdkSerializer();

        // 构建RPC请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            // 将RPC请求序列化为字节数组并发送HTTP POST请求
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8081")
                    .body(bodyBytes)
                    .execute()) {
                // 接收响应并反序列化为RPC响应对象
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            // 从RPC响应中提取用户对象并返回
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            // 打印异常堆栈信息
            e.printStackTrace();
        }

        // 异常情况下返回null
        return null;
    }
}
