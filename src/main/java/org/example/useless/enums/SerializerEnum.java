package org.example.useless.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.useless.codec.Serializer;
import org.example.useless.codec.impl.HessianSerializer;
import org.example.useless.codec.impl.ProtostuffSerializer;
import org.example.useless.codec.impl.KryoSerializer;

import java.util.Optional;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@AllArgsConstructor
@Getter
public enum SerializerEnum {
    KRYO((byte) 0x01, new KryoSerializer()),
    PROTOSTUFF((byte) 0x02, new ProtostuffSerializer()),
    HESSIAN((byte) 0x03, new HessianSerializer()),
    ;
    private final byte code;
    private final Serializer instance;

    public static SerializerEnum get(byte code) {
        for (SerializerEnum value : SerializerEnum.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }

    public static SerializerEnum getOrThrow(byte code) {
        return Optional.ofNullable(get(code))
                .orElseThrow(() -> new IllegalArgumentException("Illegal codec type"));
    }
}
