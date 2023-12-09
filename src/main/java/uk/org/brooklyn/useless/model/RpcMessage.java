package uk.org.brooklyn.useless.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RpcMessage {
    /**
     * message type
     */
    private byte messageType;
    /**
     * codec type
     */
    private byte codec;
    /**
     * compress type
     */
    private byte compress;
    /**
     * serial number
     */
    private int serialNumber;
    /**
     * message data
     */
    private Object data;
}
