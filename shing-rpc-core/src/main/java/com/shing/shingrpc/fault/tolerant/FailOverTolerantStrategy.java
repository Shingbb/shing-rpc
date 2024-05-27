package com.shing.shingrpc.fault.tolerant;

import com.shing.shingrpc.model.RpcResponse;

import java.util.Map;

/**
 * 转移到其他服务节点 - 容错策略
 *
 * @author shing
 */
public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 可自行扩展，获取其他服务节点并调用
        return null;
    }
}
