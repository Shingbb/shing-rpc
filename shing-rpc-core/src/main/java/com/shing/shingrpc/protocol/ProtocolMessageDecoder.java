package com.shing.shingrpc.protocol;


import com.shing.shingrpc.model.RpcRequest;
import com.shing.shingrpc.model.RpcResponse;
import com.shing.shingrpc.serializer.Serializer;
import com.shing.shingrpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息解码器
 *
 * @author shing
 */
public class ProtocolMessageDecoder {

    /**
     * 解码 --> 解码接收到的RPC协议消息。
     *
     * @param buffer 接收到的数据Buffer，包含完整的协议消息。
     * @return 解码后的ProtocolMessage对象，可能是RpcRequest或RpcResponse。
     * @throws IOException      当序列化或反序列化过程出错时抛出。
     * @throws RuntimeException 当消息的魔数不匹配、序列化协议未找到、消息类型不支持时抛出。
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        // 初始化协议消息头
        ProtocolMessage.Header header = new ProtocolMessage.Header();

        // 从Buffer中读取协议头信息
        byte magic = buffer.getByte(0);
        // 校验消息的魔数是否正确
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("消息 magic 非法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        // 从Buffer中读取协议消息体
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());

        // 根据头部信息中的序列化协议类型和消息类型，进行消息体的反序列化
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化消息的协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("序列化消息的类型不存在");
        }

        // 根据消息类型创建并返回相应的ProtocolMessage对象
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }
    }

}