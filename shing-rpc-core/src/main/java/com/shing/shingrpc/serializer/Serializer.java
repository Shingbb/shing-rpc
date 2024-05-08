package com.shing.shingrpc.serializer;

import java.io.IOException;

/**
 * 序列化器接口，提供序列化和反序列化方法。
 *
 * @author shing
 */
public interface Serializer {
    /**
     * 将对象序列化为字节数组。
     *
     * @param object 需要被序列化的对象
     * @param <T> 对象的类型
     * @return 序列化后的字节数组
     * @throws IOException 序列化过程中发生的IO异常
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 将字节数组反序列化为指定类型的对象。
     *
     * @param bytes 需要被反序列化的字节数组
     * @param tClass 需要反序列化的对象的类类型
     * @param <T> 对象的类型
     * @return 反序列化后的对象
     * @throws IOException 反序列化过程中发生的IO异常
     */
    <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException;
}
