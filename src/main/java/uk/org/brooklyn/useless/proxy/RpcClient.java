package uk.org.brooklyn.useless.proxy;

import uk.org.brooklyn.useless.model.RpcRequest;

/**
 * @author ImBrooklyn
 * @since 21/08/2023
 */
public interface RpcClient {
    public Object sendRpcRequest(RpcRequest rpcRequest);
}
