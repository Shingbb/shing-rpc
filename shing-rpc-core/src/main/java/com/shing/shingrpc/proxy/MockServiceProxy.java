package com.shing.shingrpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理（JDK 动态代理）
 *
 * @author shing
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {


    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的返回值类型,生成特定的默认值对象
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * 生成指定类型的默认值对象（可自行完善默认值逻辑）
     * 根据指定的类型返回默认值。
     * 如果类型是基本类型，则返回对应的默认值；如果是对象类型，则返回null。
     *
     * @param type 类型参数，指定需要获取默认值的类型。
     * @return 返回对应类型的默认值。基本类型时，返回其默认值（如false、0等）；对象类型时，返回null。
     */
    private Object getDefaultObject(Class<?> type) {
        // 判断类型是否为基本类型
        if (type.isPrimitive()) {
            // 对基本类型做分类处理，返回对应默认值
            if (type == boolean.class) {
                return false;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            }
        }
        // 如果不是基本类型，则默认返回null
        return null;
    }
}