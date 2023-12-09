package uk.org.brooklyn.useless.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.org.brooklyn.useless.compress.impl.GzipCompressor;
import uk.org.brooklyn.useless.compress.Compressor;
import uk.org.brooklyn.useless.compress.impl.NonCompressor;

import java.util.Optional;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@AllArgsConstructor
@Getter
public enum CompressorEnum {
    NON((byte) 0x00, new NonCompressor()),
    GZIP((byte) 0x01, new GzipCompressor()),
    ;

    private final byte code;
    private final Compressor instance;

    public static CompressorEnum get(byte code) {
        for (CompressorEnum value : CompressorEnum.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }

        return null;
    }

    public static CompressorEnum getOrThrow(byte code) {
        return Optional.ofNullable(get(code))
                .orElseThrow(() -> new IllegalArgumentException("Illegal compress type"));
    }
}
