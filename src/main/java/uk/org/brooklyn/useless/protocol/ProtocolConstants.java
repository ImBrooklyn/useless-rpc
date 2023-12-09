package uk.org.brooklyn.useless.protocol;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class ProtocolConstants {
    private ProtocolConstants() {
    }

    public static final byte[] MAGIC_NUMBER = {(byte) 'j', (byte) 'u', (byte) 'n', (byte) 'k'};

    public static final byte VERSION = 1;

    public static final int HEADER_LENGTH = 16;
}
