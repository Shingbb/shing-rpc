package com.shing.shingrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议消息结构，封装了消息的头和体部分。
 * 消息头包含消息的基本信息，如魔数、版本号、序列化器标识、消息类型、状态以及请求ID等。
 * 消息体则用于封装实际的请求或响应数据。
 *
 * @param <T> 消息体的类型
 * @author shing
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    /**
     * 消息头，包含消息的基本信息。
     */
    private Header header;

    /**
     * 消息体，可以是请求或响应对象。
     */
    private T body;

    /**
     * 协议消息头，包含消息的基本信息。
     * 这是一个静态内部类，用于详细描述消息头的各个属性。
     */
    @Data
    public static class Header {

        /**
         * 魔数，用于确保消息的合法性与安全性。
         */
        private byte magic;

        /**
         * 消息版本号，用于处理向前兼容性。
         */
        private byte version;

        /**
         * 序列化器标识，用于指定消息体的序列化与反序列化方式。
         */
        private byte serializer;

        /**
         * 消息类型，标识这是请求还是响应消息。
         */
        private byte type;

        /**
         * 消息状态，用于表示响应消息的成功或失败。
         */
        private byte status;

        /**
         * 请求ID，用于唯一标识一个请求，便于响应匹配。
         */
        private long requestId;

        /**
         * 消息体长度，用于指示消息体的实际大小。
         */
        private int bodyLength;
    }

}
