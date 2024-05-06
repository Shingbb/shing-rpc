package com.shing.shingrpc.server;

import io.vertx.core.Vertx;

/**
 * 实现HttpServer接口的Vert.x HTTP服务器类。
 *
 * @author shing
 */
public class VertxHttpServer implements HttpServer {

    /**
     * 启动服务器，监听指定端口。
     *
     * @param port 服务器监听的端口号。
     */
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 HTTP 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求
       /* server.requestHandler(request -> {
            // 处理 HTTP 请求
            System.out.println("Received request: " + request.method() + " " + request.uri());

            // 发送 HTTP 响应
            request.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x HTTP server!");
        });*/
        server.requestHandler(new HttpServerHandler());
        // 启动 HTTP 服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.err.println("Failed to start server: " + result.cause());
            }
        });
    }

}
