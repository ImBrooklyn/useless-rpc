package uk.org.brooklyn.useless.compress;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public interface Compressor {
    byte[] compress(byte[] bytes);
    byte[] decompress(byte[] bytes);
}
