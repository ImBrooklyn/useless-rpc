package uk.org.brooklyn.useless.compress.impl;

import uk.org.brooklyn.useless.compress.Compressor;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class NonCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return bytes;
    }
}
