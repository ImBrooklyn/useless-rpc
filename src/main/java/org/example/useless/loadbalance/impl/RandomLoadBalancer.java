package org.example.useless.loadbalance.impl;

import org.example.useless.loadbalance.AbstractLoadBalancer;
import org.example.useless.model.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected String doSelect(List<String> serviceList, RpcRequest rpcRequest) {
        Random random = new Random(System.currentTimeMillis());
        return serviceList.get(random.nextInt(serviceList.size()));
    }
}
