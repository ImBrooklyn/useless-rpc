package uk.org.brooklyn.useless.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RpcRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * request id
     */
    private String requestId;

    /**
     * interface name
     */
    private String interfaceName;

    /**
     * method name
     */
    private String methodName;

    /**
     * parameters
     */
    private Object[] parameters;

    /**
     * parameter types
     */
    private Class<?>[] paramTypes;

    /**
     * service version
     */
    private String version;

    /**
     * service group
     */
    private String group;

    public String rpcServiceName() {
        return String.join(":", this.getInterfaceName(), this.getGroup(), this.getVersion());
    }
}
