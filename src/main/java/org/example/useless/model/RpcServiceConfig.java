package org.example.useless.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RpcServiceConfig {
    private String version = "";
    private String group = "";
    private Object service;

    public String rpcServiceName() {
        return String.join(":", this.serviceName(), this.getGroup(), this.getVersion());
    }

    private String serviceName() {
        return Optional.ofNullable(this.service)
                .map(Object::getClass)
                .map(Class::getInterfaces)
                .map(a -> a[0])
                .map(Class::getCanonicalName)
                .orElse(null);
    }
}
