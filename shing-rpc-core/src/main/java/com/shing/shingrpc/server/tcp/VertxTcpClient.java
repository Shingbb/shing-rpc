package com.shing.shingrpc.server.tcp;

import com.shing.shingrpc.RpcApplication;
import com.shing.shingrpc.model.RpcRequest;
import com.shing.shingrpc.model.RpcResponse;
import com.shing.shingrpc.model.ServiceMetaInfo;
import com.shing.shingrpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Vertx TCP 请求客户端
 *
 * 用于通过TCP协议向指定的服务端发送RPC请求，并获取响应。
 * @author shing
 */
public class VertxTcpClient {

    /**
     * 发送RPC请求并获取响应
     * @param rpcRequest RPC请求对象
     * @param serviceMetaInfo 服务元信息，包含服务的主机地址和端口号
     * @return RPC响应对象
     * @throws InterruptedException 如果执行过程中线程被中断则抛出此异常
     * @throws ExecutionException 如果获取CompletableFuture结果时发生异常则抛出此异常
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        // 创建Vertx实例和NetClient客户端
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();

        // 尝试连接到服务端
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
            if (!result.succeeded()) {
                System.err.println("Failed to connect to TCP server");
                return;
            }
            NetSocket socket = result.result();

            // 构造协议消息并发送请求
            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
            ProtocolMessage.Header header = new ProtocolMessage.Header();
            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());

            // 编码请求并发送至服务端
            try {
                Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                socket.write(encodeBuffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }

            // 设置接收响应的处理逻辑
            TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                try {
                    // 解码接收到的响应
                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                } catch (IOException e) {
                    throw new RuntimeException("协议消息解码错误");
                }
            });
            socket.handler(bufferHandlerWrapper);
        });

        // 等待响应完成并返回
        RpcResponse rpcResponse = responseFuture.get();

        return rpcResponse;
    }

}
