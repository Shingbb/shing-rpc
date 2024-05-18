package com.shing.shingrpc.server.tcp;

import com.shing.shingrpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

/**
 * Vert.x TCP服务器实现，用于提供基于TCP协议的HTTP服务。
 * @author shing
 */
public class VertxTcpServer implements HttpServer {

    /**
     * 处理客户端请求，构造并返回响应数据。
     * @param requestData 客户端发送的请求数据（字节数组）。
     * @return 应答数据的字节数组。
     */
    private byte[] handleRequest(byte[] requestData) {
        // 根据接收到的requestData构造响应数据
        return "Hello, client!".getBytes();
    }

    /**
     * 启动服务器，监听指定端口，接收并处理TCP请求。
     * @param port 服务器监听的端口号。
     */
    @Override
    public void doStart(int port) {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();

        // 创建TCP服务器
        NetServer server = vertx.createNetServer();

        // 处理客户端连接
        server.connectHandler(socket -> {
            // 处理接收到的数据
            socket.handler(buffer -> {
                // 将Buffer转换为字节数组，以处理请求
                byte[] requestData = buffer.getBytes();
                // 调用handleRequest处理请求，并发送响应
                byte[] responseData = handleRequest(requestData);
                // 将响应数据发送回客户端
                socket.write(Buffer.buffer(responseData));
            });
        });

        // 监听指定端口，成功或失败进行相应处理
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP server started on port " + port);
            } else {
                System.err.println("Failed to start TCP server: " + result.cause());
            }
        });
    }

    /**
     * 程序入口点，创建服务器实例并启动。
     * @param args 命令行参数（未使用）。
     */
    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
