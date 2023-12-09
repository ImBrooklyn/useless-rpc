package org.example.useless.registry;

import org.example.useless.model.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest request);
}
