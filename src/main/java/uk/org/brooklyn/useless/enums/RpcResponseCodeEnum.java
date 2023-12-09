package uk.org.brooklyn.useless.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@AllArgsConstructor
@Getter
public enum RpcResponseCodeEnum {
    SUCCESS(200, "remote call succeeded"),
    FAIL(500, "remote call failed")
    ;

    private final int code;
    private final String message;

}
