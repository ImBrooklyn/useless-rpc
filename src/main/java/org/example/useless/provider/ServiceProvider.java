package org.example.useless.provider;

import org.example.useless.model.RpcServiceConfig;

public interface ServiceProvider {
    Object getService(String rpcServiceName);
    void publishService(RpcServiceConfig rpcServiceConfig);

}
