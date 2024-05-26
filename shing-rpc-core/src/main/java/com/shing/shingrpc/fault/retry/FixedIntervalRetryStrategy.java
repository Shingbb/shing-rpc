package com.shing.shingrpc.fault.retry;

import com.github.rholder.retry.*;
import com.shing.shingrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔重试策略类。该类实现了RetryStrategy接口，提供了一个固定时间间隔的重试机制。
 *
 * @author shing
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {

    /**
     * 执行重试操作。该方法会尝试调用提供的Callable对象，如果遇到异常则按照预设的重试策略进行重试。
     *
     * @param callable 要执行并可能需要重试的callable任务。
     * @return RpcResponse 调用成功时返回的响应对象。
     * @throws ExecutionException 如果在执行callable任务时发生异常。
     * @throws RetryException     如果重试策略达到最大重试次数且仍然失败。
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        // 构建重试器，设置重试条件、等待策略、停止策略及重试监听器
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 重试条件：遇到任何异常都重试
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS)) // 等待策略：固定间隔3秒重试
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 停止策略：最多重试3次
                .withRetryListener(new RetryListener() { // 重试监听器：记录每次重试的信息
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}", attempt.getAttemptNumber());
                    }
                })
                .build();
        // 执行callable任务，如有异常则根据重试策略进行重试
        return retryer.call(callable);
    }
}