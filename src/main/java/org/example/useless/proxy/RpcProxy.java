package org.example.useless.proxy;

import org.example.useless.model.RpcRequest;
import org.example.useless.model.RpcServiceConfig;
import org.example.useless.proxy.impl.NettyRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author ImBrooklyn
 * @since 21/08/2023
 */
public class RpcProxy implements InvocationHandler {

    private final RpcServiceConfig rpcServiceConfig;
    private final RpcClient client = NettyRpcClient.getInstance();

    public RpcProxy(RpcServiceConfig rpcServiceConfig) {
        this.rpcServiceConfig = rpcServiceConfig;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();
        // check
        return client.sendRpcRequest(request);
    }
}
