package org.example.useless.proxy.impl;

import org.example.useless.exception.RpcException;
import org.example.useless.model.RpcRequest;
import org.example.useless.model.RpcResponse;
import org.example.useless.proxy.RpcClient;
import org.example.useless.remote.client.UselessRpcClient;

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
