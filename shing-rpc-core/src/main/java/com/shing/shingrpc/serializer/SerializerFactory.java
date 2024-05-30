package com.shing.shingrpc.serializer;

import com.shing.shingrpc.spi.SpiLoader;

/**
 * 序列化器工厂（用于获取序列化器对象）
 *
 * @author shing
 */
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();


    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

}