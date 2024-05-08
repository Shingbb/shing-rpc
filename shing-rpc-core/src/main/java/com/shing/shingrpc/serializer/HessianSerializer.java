package com.shing.shingrpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 序列化器
 * 用于实现对象的序列化和反序列化，使用Hessian协议。
 *
 * @author shing
 */
public class HessianSerializer implements Serializer {
    /**
     * 序列化对象为字节数组
     *
     * @param object 需要被序列化的对象
     * @return object 的字节表示
     * @throws IOException 如果序列化过程中发生IO错误
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(bos);
        ho.writeObject(object);
        // 将对象写入字节数组输出流后，返回其字节表示
        return bos.toByteArray();
    }

    /**
     * 反序列化字节数组为对象
     *
     * @param bytes 待反序列化的字节数组
     * @param tClass 需要反序列化的对象类型
     * @return 反序列化后的对象
     * @throws IOException 如果反序列化过程中发生IO错误
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(bis);
        // 从字节数组输入流中读取对象，返回反序列化后的实例
        return (T) hi.readObject(tClass);
    }
}
