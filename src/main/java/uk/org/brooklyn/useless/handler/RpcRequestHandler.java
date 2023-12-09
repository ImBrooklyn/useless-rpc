package uk.org.brooklyn.useless.handler;

import lombok.extern.slf4j.Slf4j;
import uk.org.brooklyn.useless.exception.RpcException;
import uk.org.brooklyn.useless.model.RpcRequest;
import uk.org.brooklyn.useless.provider.ServiceProvider;
import uk.org.brooklyn.useless.provider.impl.ZookeeperServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
@Slf4j
public class RpcRequestHandler {

    private RpcRequestHandler() {
    }

    private static final RpcRequestHandler INSTANCE = new RpcRequestHandler();

    public static RpcRequestHandler getInstance() {
        return INSTANCE;
    }

    private final ServiceProvider serviceProvider = ZookeeperServiceProvider.getInstance();

    public Object process(RpcRequest request) {
        Object service = serviceProvider.getService(request.rpcServiceName());
        return invokeTargetMethod(request, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
