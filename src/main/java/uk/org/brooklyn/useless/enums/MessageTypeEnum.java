package uk.org.brooklyn.useless.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum {
    PING((byte) 0x01),
    PONG((byte) 0x02),
    REQUEST((byte) 0x03),
    RESPONSE((byte) 0x04),
    ;

    private final byte code;

    public static MessageTypeEnum get(byte code) {
        for (MessageTypeEnum value : MessageTypeEnum.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }

    public static MessageTypeEnum getOrThrow(byte code) {
        return Optional.ofNullable(get(code))
                .orElseThrow(() -> new IllegalArgumentException("Illegal message type"));
    }
}
