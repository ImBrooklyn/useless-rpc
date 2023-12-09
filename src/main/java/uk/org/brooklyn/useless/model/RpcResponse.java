package uk.org.brooklyn.useless.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.org.brooklyn.useless.enums.RpcResponseCodeEnum;

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
public class RpcResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * request id
     */
    private String requestId;

    /**
     * response code
     */
    private Integer code;

    /**
     * response message
     */
    private String message;

    /**
     * response body
     */
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }
}
