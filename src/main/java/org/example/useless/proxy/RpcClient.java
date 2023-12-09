package org.example.useless.proxy;

import org.example.useless.model.RpcRequest;

/**
 * @author ImBrooklyn
 * @since 21/08/2023
 */
public interface RpcClient {
    public Object sendRpcRequest(RpcRequest rpcRequest);
}
