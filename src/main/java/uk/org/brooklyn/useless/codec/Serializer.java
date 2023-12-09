package uk.org.brooklyn.useless.codec;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public interface Serializer {
    byte[] serialize(Object obj);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
