package org.example.useless.codec.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.example.useless.codec.Serializer;
import org.example.useless.exception.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializationException("serialization failed", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(byteArrayInputStream);
            Object o = hessianInput.readObject();
            return clazz.cast(o);
        } catch (Exception e) {
            throw new SerializationException("deserialization failed", e);
        }
    }
}
