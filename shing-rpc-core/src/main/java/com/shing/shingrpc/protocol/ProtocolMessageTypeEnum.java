package com.shing.shingrpc.protocol;

import lombok.Getter;

/**
 * 协议消息的类型枚举
 *
 * @author shing
 */
@Getter
public enum ProtocolMessageTypeEnum {
    /**
     * 请求消息类型
     */
    REQUEST(0),

    /**
     * 响应消息类型
     */
    RESPONSE(1),

    /**
     * 心跳消息类型
     */
    HEART_BEAT(2),

    /**
     * 其他消息类型
     */
    OTHERS(3);

    // 每个枚举值对应的键值
    private final int key;

    /**
     * 构造函数，初始化枚举值的键值
     *
     * @param key 枚举值的键
     */
    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    /**
     * 根据 key 获取枚举
     *
     * @param key 要查找的枚举键值
     * @return 对应键值的枚举项，如果找不到则返回null
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        // 遍历枚举值，查找匹配键值的枚举项
        for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }

}

