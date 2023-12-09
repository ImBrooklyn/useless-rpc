package org.example.useless.loadbalance;

import org.example.useless.model.RpcRequest;

import java.util.List;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {


    @Override
    public String selectServiceAddress(List<String> serviceList, RpcRequest rpcRequest) {
        if (serviceList == null || serviceList.isEmpty()) {
            return null;
        }
        return serviceList.size() == 1 ? serviceList.get(0) : doSelect(serviceList, rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceList, RpcRequest rpcRequest);
}
