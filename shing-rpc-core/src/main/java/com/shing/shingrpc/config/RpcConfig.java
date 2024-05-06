package com.shing.shingrpc.config;

import lombok.Data;

/**
 * RPC 框架配置
 *
 * @author shing
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "shing-rpc";

    /**
     * 版本号
     */
    private String version = "1.0.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

}
