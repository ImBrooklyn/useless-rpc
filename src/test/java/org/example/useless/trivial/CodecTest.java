package org.example.useless.trivial;

import org.example.useless.codec.Serializer;
import org.example.useless.codec.impl.HessianSerializer;
import org.example.useless.codec.impl.KryoSerializer;
import org.example.useless.model.RpcRequest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author ImBrooklyn
 * @since 22/08/2023
 */
public class CodecTest {
    @Test
    public void testCodec() {
        RpcRequest request = RpcRequest.builder()
                .group("12")
                .requestId(UUID.randomUUID().toString())
                .interfaceName("name")
                .paramTypes(new Class[]{String.class})
                .methodName("a")
                .build();
        // Serializer codec = new KryoSerializer();
        Serializer codec = new HessianSerializer();

        byte[] bytes = codec.serialize(request);
        RpcRequest copy = codec.deserialize(bytes, RpcRequest.class);
        System.out.println("copy = " + copy);

    }
}
