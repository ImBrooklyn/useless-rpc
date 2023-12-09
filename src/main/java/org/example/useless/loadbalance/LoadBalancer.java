package org.example.useless.loadbalance;

import org.example.useless.model.RpcRequest;

import java.util.List;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
public interface LoadBalancer {
    String selectServiceAddress(List<String> serviceList, RpcRequest rpcRequest);
}
