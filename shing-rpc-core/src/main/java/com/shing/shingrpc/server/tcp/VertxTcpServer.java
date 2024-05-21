package com.shing.shingrpc.server.tcp;

import com.shing.shingrpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
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
            // 构造解析器，用于处理客户端发送的数据
            RecordParser parser = RecordParser.newFixed(8);
            // 设置解析器的处理逻辑
            parser.setOutput(new Handler<Buffer>() {
                // 初始化变量
                int size = -1;
                Buffer resultBuffer = Buffer.buffer();

                @Override
                public void handle(Buffer buffer) {
                    if (-1 == size) {
                        // 读取并设置消息体长度
                        size = buffer.getInt(4);
                        parser.fixedSizeMode(size);
                        // 将头信息添加到结果缓冲区
                        resultBuffer.appendBuffer(buffer);
                    } else {
                        // 将体信息添加到结果缓冲区
                        resultBuffer.appendBuffer(buffer);
                        System.out.println(resultBuffer.toString());
                        // 重置解析器以处理新的消息
                        parser.fixedSizeMode(8);
                        size = -1;
                        resultBuffer = Buffer.buffer();
                    }
                }
            });
            // 将数据流发送到解析器
            socket.handler(parser);
        });

        // 监听指定端口，并处理监听结果
        server.listen(port, result -> {
            if (result.succeeded()) {
                // 成功启动服务器，记录日志
                log.info("TCP server started on port " + port);
            } else {
                // 启动失败，记录错误日志
                log.info("Failed to start TCP server: " + result.cause());
            }
        });
    }

    // 程序入口点
    public static void main(String[] args) {
        // 实例化并启动服务器，监听8888端口
        new VertxTcpServer().doStart(8888);
    }
}
