package uk.org.brooklyn.useless.proxy.impl;

import uk.org.brooklyn.useless.exception.RpcException;
import uk.org.brooklyn.useless.model.RpcRequest;
import uk.org.brooklyn.useless.proxy.RpcClient;
import uk.org.brooklyn.useless.remote.client.UselessRpcClient;

import java.util.concurrent.ExecutionException;

/**
 * @author ImBrooklyn
 * @since 21/08/2023
 */
public class NettyRpcClient implements RpcClient {
    private NettyRpcClient() {
        this.client = new UselessRpcClient();
    }

    private static final NettyRpcClient INSTANCE = new NettyRpcClient();

    private final UselessRpcClient client;

    public static NettyRpcClient getInstance() {
        return INSTANCE;
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        try {
            return client.sendRpcRequest(rpcRequest).get().getData();
        } catch (InterruptedException | ExecutionException e) {
            // TODO
            throw new RpcException("sendRpcRequest failed...", e);
        }
    }
}
