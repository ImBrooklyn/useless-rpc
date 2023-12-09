package org.example.useless.codec.impl;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.example.useless.codec.Serializer;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class ProtostuffSerializer implements Serializer {

    private final ThreadLocal<LinkedBuffer> BUFFER = ThreadLocal.withInitial(() ->
            LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public byte[] serialize(Object obj) {
        Class<?> clazz = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        LinkedBuffer buffer = BUFFER.get();
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
