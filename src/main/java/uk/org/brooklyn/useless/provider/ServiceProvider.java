package uk.org.brooklyn.useless.provider;

import uk.org.brooklyn.useless.model.RpcServiceConfig;

public interface ServiceProvider {
    Object getService(String rpcServiceName);
    void publishService(RpcServiceConfig rpcServiceConfig);

}
