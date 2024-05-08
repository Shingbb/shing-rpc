package com.shing.shingrpc.serializer;

/**
 * 序列化器键名类，用于定义不同序列化器的键名常量。
 * 这个类提供了多个序列化器的名称，以便在需要的时候可以方便地引用它们。
 *
 * @author shing
 */
public interface SerializerKeys {

    // JDK序列化器的键名
    String JDK = "jdk";
    // FastJSON序列化器的键名
    String FASTJSON = "fastjson";
    // Jackson序列化器的键名
    String JACKSON = "jackson";
    // Kryo序列化器的键名
    String KRYO = "kryo";
    // Hessian序列化器的键名
    String HESSIAN = "hessian";
}
