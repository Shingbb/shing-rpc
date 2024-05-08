package com.shing.shingrpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shing.shingrpc.model.RpcRequest;
import com.shing.shingrpc.model.RpcResponse;

import java.io.IOException;


/**
 * Jackson JSON 序列化器
 * 提供序列化和反序列化方法，用于将对象转换为JSON字节数据和从JSON字节数据中还原对象。
 *
 * @author shing
 */
public class JacksonSerializer implements Serializer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 序列化对象为JSON字节数据。
     *
     * @param obj 需要被序列化的对象。
     * @param <T> 对象的类型。
     * @return obj的JSON字节数据表示。
     * @throws IOException 如果序列化过程中发生错误。
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    /**
     * 反序列化JSON字节数据为对象。
     * 对于特定类型（RpcRequest, RpcResponse）的对象进行额外处理。
     *
     * @param bytes     JSON字节数据。
     * @param classType 需要反序列化的对象类型。
     * @param <T>       对象的类型。
     * @return 反序列化后的对象。
     * @throws IOException 如果反序列化过程中发生错误。
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, classType);
        if (obj instanceof RpcRequest) {
            // 对RpcRequest特殊处理，解决对象擦除问题
            return handleRequest((RpcRequest) obj, classType);
        }
        if (obj instanceof RpcResponse) {
            // 对RpcResponse特殊处理，解决对象擦除问题
            return handleResponse((RpcResponse) obj, classType);
        }
        return obj;
    }

    /**
     * RpcRequest反序列化特殊处理函数。
     * 由于Java对象擦除，反序列化时可能无法正确恢复原始类型。此方法对RpcRequest内的参数进行重新处理，
     * 确保类型正确无误。
     *
     * @param rpcRequest rpc 请求对象。
     * @param type       需要转换的目标类型。
     * @param <T>        类型参数。
     * @return 处理后的RpcRequest对象。
     * @throws IOException 如果处理过程中发生序列化或反序列化错误。
     */
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();

        // 循环检查并重新处理每个参数的类型，确保类型匹配
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            // 如果参数类型不匹配，则重新进行类型转换
            if (!type.isAssignableFrom(args[i].getClass())) {
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, type);
            }
        }
        return type.cast(rpcRequest);
    }

    /**
     * RpcResponse反序列化特殊处理函数。
     * 用于处理RpcResponse中的数据字段，确保其类型正确无误。
     *
     * @param rpcResponse rpc 响应对象。
     * @param type        需要转换的目标类型。
     * @param <T>         类型参数。
     * @return 处理后的RpcResponse对象。
     * @throws IOException 如果处理过程中发生序列化或反序列化错误。
     */
    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
        // 重新处理响应数据的类型，确保数据类型正确
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes, rpcResponse.getDataType()));
        return type.cast(rpcResponse);
    }
}
