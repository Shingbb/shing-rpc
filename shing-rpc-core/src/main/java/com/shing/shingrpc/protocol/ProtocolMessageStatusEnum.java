package com.shing.shingrpc.protocol;

import lombok.Getter;

/**
 * 协议消息的状态枚举
 *
 * @author shing
 */
@Getter
public enum ProtocolMessageStatusEnum {

    // 状态OK，对应文本"ok"，值为20
    OK("ok", 20),
    // 请求错误，对应文本"badRequest"，值为40
    BAD_REQUEST("badRequest", 40),
    // 响应错误，对应文本"badResponse"，值为50
    BAD_RESPONSE("badResponse", 50);

    // 状态文本
    private final String text;
    // 状态值
    private final int value;

    /**
     * 构造函数用于创建一个新的枚举常量。
     *
     * @param text  状态的文本表示。
     * @param value 状态的整数值表示。
     */
    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据值获取枚举常量。
     *
     * @param value 要查找的枚举常量的值。
     * @return 对应于给定值的枚举常量，如果找不到则返回null。
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        return null;
    }

}