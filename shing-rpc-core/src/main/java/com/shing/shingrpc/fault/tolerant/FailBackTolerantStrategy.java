package com.shing.shingrpc.fault.tolerant;

import com.shing.shingrpc.model.RpcResponse;

import java.util.Map;

/**
 * 降级到其他服务 - 容错策略
 *
 * @author shing
 */
public class FailBackTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 可自行扩展，获取降级的服务并调用
        return null;
    }
}
