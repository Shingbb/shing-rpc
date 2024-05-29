package com.shing.shingrpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 服务注册信息类，用于存储服务的注册信息。
 *
 * @param <T> 服务接口的类型
 * @author shing
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegisterInfo<T> {

    /**
     * 服务名称，用于标识一个服务。
     */
    private String serviceName;

    /**
     * 实现类，指定提供服务的具体实现。
     *
     * @param implClass 服务实现类的Class对象，必须是参数类型T的子类或实现类。
     */
    private Class<? extends T> implClass;
}