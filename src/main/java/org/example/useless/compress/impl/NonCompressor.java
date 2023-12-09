package org.example.useless.compress.impl;

import org.example.useless.compress.Compressor;

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
