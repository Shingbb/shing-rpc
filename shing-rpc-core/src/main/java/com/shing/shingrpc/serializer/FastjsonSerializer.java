package com.shing.shingrpc.serializer;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

/**
 * Fastjson JSON 序列化器
 * 实现了Serializer接口，使用阿里巴巴的Fastjson库进行对象的序列化和反序列化操作。
 *
 * @author shing
 */
public class FastjsonSerializer implements Serializer {
    /**
     * 将对象序列化为JSON字节数组
     *
     * @param object 需要被序列化的对象
     * @return 序列化后的JSON字节数组
     * @throws IOException 如果序列化过程中发生错误，则抛出IOException
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        String jsonStr = JSON.toJSONString(object); // 使用Fastjson将对象转换为JSON字符串
        return jsonStr.getBytes(); // 将JSON字符串转换为字节数组
    }

    /**
     * 将JSON字节数组反序列化为对象
     *
     * @param bytes 需要被反序列化的JSON字节数组
     * @param type  预期反序列化结果的类型
     * @return 反序列化后的对象实例
     * @throws IOException 如果反序列化过程中发生错误，则抛出IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        return JSON.parseObject(new String(bytes), type); // 将字节数组转换为JSON字符串，然后反序列化为指定类型
    }
}
