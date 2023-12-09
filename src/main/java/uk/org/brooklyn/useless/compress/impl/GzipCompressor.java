package uk.org.brooklyn.useless.compress.impl;

import uk.org.brooklyn.useless.compress.Compressor;
import uk.org.brooklyn.useless.exception.CompressException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class GzipCompressor implements Compressor {
    private static final int BUFFER_SIZE = 1024 * 4;
    @Override
    public byte[] compress(byte[] bytes) {
        Objects.requireNonNull(bytes);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CompressException("gzip compress failed", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream gunzip = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = gunzip.read(buffer)) > -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new CompressException("gzip decompress failed", e);
        }
    }
}
