package com.shing.shingrpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo 序列化器。实现 Serializer 接口，提供使用 Kryo 库的序列化和反序列化功能。
 *
 * @author shing
 */
public class KryoSerializer implements Serializer {
    /**
     * 使用 ThreadLocal 来为每个线程提供一个独立的 Kryo 实例，以保证线程安全。
     * Kryo 实例初始化时，设置为不强制要求注册所有类，以提高灵活性和安全性。
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    /**
     * 序列化方法。将对象转换为字节数组。
     *
     * @param object 需要被序列化的对象。
     * @return object 的字节表示。
     * @throws IOException 如果序列化过程中发生 IO 错误。
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        KRYO_THREAD_LOCAL.get().writeObject(output, object); // 使用 Kryo 序列化对象到输出流
        output.close();
        return byteArrayOutputStream.toByteArray(); // 返回序列化后的字节数组
    }

    /**
     * 反序列化方法。将字节数组转换为指定类型的对象。
     *
     * @param bytes   需要被反序列化的字节数组。
     * @param classType 反序列化目标对象的类型。
     * @return 反序列化后的对象。
     * @throws IOException 如果反序列化过程中发生 IO 错误。
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T result = KRYO_THREAD_LOCAL.get().readObject(input, classType); // 使用 Kryo 从输入流读取对象
        input.close();
        return result;
    }
}
