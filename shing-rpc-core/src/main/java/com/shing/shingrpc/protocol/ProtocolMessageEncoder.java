package com.shing.shingrpc.protocol;

import com.shing.shingrpc.serializer.Serializer;
import com.shing.shingrpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 用于编码协议消息的工具类。
 * @author shing
 */
public class ProtocolMessageEncoder {

    /**
     * 编码 --> 将协议消息对象编码为Buffer格式。
     * @param protocolMessage 需要被编码的协议消息对象。不能为null且消息头也不能为null。
     * @return 编码后的消息Buffer。
     * @throws IOException 当序列化消息体时发生IO异常。
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        // 检查消息对象和消息头是否为空，若为空则返回空的Buffer
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();

        // 初始化Buffer并写入消息头信息
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        // 根据消息头中的序列化协议类型获取对应的序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            // 如果找不到对应的序列化器则抛出运行时异常
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        // 序列化消息体，并将序列化后的字节数组写入Buffer
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        buffer.appendInt(bodyBytes.length); // 先写入消息体长度
        buffer.appendBytes(bodyBytes); // 再写入消息体数据

        return buffer;
    }
}
