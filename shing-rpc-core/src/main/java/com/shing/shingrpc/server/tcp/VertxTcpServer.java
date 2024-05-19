package com.shing.shingrpc.server.tcp;

import com.shing.shingrpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于Vert.x实现的TCP服务器，实现HttpServer接口。
 *
 * @author shing
 */
@Slf4j
public class VertxTcpServer implements HttpServer {

    /**
     * 启动服务器，监听指定端口。
     *
     * @param port 服务器监听的端口号。
     */
    @Override
    public void doStart(int port) {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();

        // 创建TCP服务器
        NetServer server = vertx.createNetServer();

        // 处理客户端连接请求
        server.connectHandler(socket -> {
            // 处理接收到的数据
            socket.handler(buffer -> {
                // 处理粘包和半包情况
                String testMessage = "Hello, server!Hello, server!Hello, server!Hello, server!";
                int messageLength = testMessage.getBytes().length;
                if (buffer.getBytes().length < messageLength) {
                    System.out.println("半包, length = " + buffer.getBytes().length);
                    return;
                }
                if (buffer.getBytes().length > messageLength) {
                    System.out.println("粘包, length = " + buffer.getBytes().length);
                    return;
                }
                // 解析接收到的数据
                String str = new String(buffer.getBytes(0, messageLength));
                System.out.println(str);
                if (testMessage.equals(str)) {
                    System.out.println("收到完整消息");
                }
            });
        });

        // 监听指定端口，成功或失败会打印相应日志
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port " + port);
            } else {
                log.info("Failed to start TCP server: " + result.cause());
            }
        });
    }

    /**
     * 程序入口点，创建服务器实例并启动。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
