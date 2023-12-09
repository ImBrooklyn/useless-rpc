package uk.org.brooklyn.useless.loadbalance;

import uk.org.brooklyn.useless.model.RpcRequest;

import java.util.List;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
public interface LoadBalancer {
    String selectServiceAddress(List<String> serviceList, RpcRequest rpcRequest);
}
