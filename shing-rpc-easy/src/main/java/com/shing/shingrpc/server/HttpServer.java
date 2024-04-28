package com.shing.shingrpc.server;

/**
 * HTTP 服务器接口
 *
 * @author shing
 */
public interface HttpServer {

    /**
     * 启动服务器
     *
     * @param port
     */
    void doStart(int port);
}
