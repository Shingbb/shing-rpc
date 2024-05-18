package com.shing.shingrpc.protocol;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 协议消息的序列化器枚举
 *
 * @author shing
 */
@Getter
public enum ProtocolMessageSerializerEnum {
    JDK(0, "jdk"), // 使用JDK序列化
    JSON(1, "json"), // 使用FastJSON序列化
    JACKSON(2, "jackson"), // 使用Jackson序列化
    KRYO(3, "kryo"), // 使用Kryo序列化
    HESSIAN(4, "hessian"); // 使用Hessian序列化

    private final int key; // 序列化方式的键值
    private final String value; // 序列化方式的名称

    /**
     * 构造函数用于初始化枚举成员。
     *
     * @param key   序列化方式的键值。
     * @param value 序列化方式的名称。
     */
    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 获取所有序列化方式的值列表。
     *
     * @return 序列化方式名称的列表。
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据键值获取对应的枚举成员。
     *
     * @param key 序列化方式的键值。
     * @return 对应键值的枚举成员，如果找不到则返回null。
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 根据名称获取对应的枚举成员。
     *
     * @param value 序列化方式的名称。
     * @return 对应名称的枚举成员，如果找不到或名称为空则返回null。
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
