package com.shing.shingrpc.server.tcp;

import com.shing.shingrpc.model.RpcRequest;
import com.shing.shingrpc.model.RpcResponse;
import com.shing.shingrpc.protocol.ProtocolMessage;
import com.shing.shingrpc.protocol.ProtocolMessageDecoder;
import com.shing.shingrpc.protocol.ProtocolMessageEncoder;
import com.shing.shingrpc.protocol.ProtocolMessageTypeEnum;
import com.shing.shingrpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 处理TCP服务器的连接请求，负责接收请求、处理请求并发送响应。
 * @author shing
 */
public class TcpServerHandler implements Handler<NetSocket> {
    /**
     * 处理接收到的NetSocket连接。
     * @param netSocket 与客户端建立的网络套接字。
     */
    @Override
    public void handle(NetSocket netSocket) {
        // 处理接收到的数据
        netSocket.handler(buffer -> {
            // 解码接收到的协议消息
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            // 处理RPC请求
            // 构建RPC响应对象
            RpcResponse rpcResponse = new RpcResponse();
            try {
                // 通过服务名称获取服务实现类，使用反射调用相应方法
                Class<?> implClass = LocalRegistry.getService(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 设置方法返回结果到响应对象
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                // 设置异常信息到响应对象
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // 编码RPC响应并发送给客户端
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });
    }
}