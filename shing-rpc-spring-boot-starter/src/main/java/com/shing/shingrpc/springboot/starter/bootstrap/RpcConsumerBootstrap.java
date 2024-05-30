package com.shing.shingrpc.springboot.starter.bootstrap;

import cn.hutool.core.bean.BeanException;
import com.shing.shingrpc.proxy.ServiceProxyFactory;
import com.shing.shingrpc.springboot.starter.annotation.RpcReference;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * Rpc服务消费者启动器。该类实现了Spring BeanPostProcessor接口，
 * 用于在Spring Bean初始化后自动扫描并注入Rpc服务的代理对象。
 *
 * @author shing
 */
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * 在Spring Bean初始化后对Bean进行处理。
     * 遍历当前Bean的所有字段，如果字段上注解了@RpcReference，则为其生成代理对象并注入。
     *
     * @param bean     当前正在创建的Spring Bean实例。
     * @param beanName 当前Spring Bean的名称。
     * @return 返回处理后的Bean实例。
     * @throws BeanException 如果处理过程中发生异常。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
        Class<?> beanClass = bean.getClass(); // 获取当前Bean的类对象

        // 遍历当前Bean的所有声明字段
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            // 如果字段上注解了@RpcReference，则处理
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // 根据注解获取接口类，如果未指定则使用字段类型
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true); // 允许访问私有字段
                // 生成接口类的代理对象
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    // 将代理对象注入字段
                    field.set(bean, proxyObject);
                    field.setAccessible(false); // 恢复字段访问性
                } catch (IllegalAccessException e) {
                    // 抛出运行时异常，如果注入失败
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }

        }
        // 返回处理后的Bean实例
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
