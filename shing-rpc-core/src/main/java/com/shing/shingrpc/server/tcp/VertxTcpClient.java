package com.shing.shingrpc.server.tcp;

import io.vertx.core.Vertx;

/**
 * Vert.x TCP 客户端示例。
 * 使用 Vert.x 的 NetClient 来连接 TCP 服务器，发送数据，并接收响应。
 *
 * @author shing
 */
public class VertxTcpClient {

    /**
     * 启动客户端，创建 Vert.x 实例，并尝试连接到指定的 TCP 服务器。
     * 一旦连接成功，将向服务器发送消息，并处理来自服务器的响应。
     */
    public void start() {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 尝试连接到本地主机的 8888 端口
        vertx.createNetClient().connect(8888, "localhost", result -> {
            if (result.succeeded()) {
                // 连接成功时的操作
                System.out.println("Connected to TCP server");
                io.vertx.core.net.NetSocket socket = result.result();

                // 向服务器发送消息
                socket.write("Hello, server!");

                // 设置处理服务器响应的回调函数
                socket.handler(buffer -> {
                    System.out.println("Received response from server: " + buffer.toString());
                });
            } else {
                // 连接失败时的处理
                System.err.println("Failed to connect to TCP server");
            }
        });
    }

    /**
     * 程序的入口点。创建 VertxTcpClient 实例并启动客户端。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
