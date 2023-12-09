package uk.org.brooklyn.useless.provider.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import uk.org.brooklyn.useless.exception.RpcException;
import uk.org.brooklyn.useless.model.RpcServiceConfig;
import uk.org.brooklyn.useless.provider.ServiceProvider;
import uk.org.brooklyn.useless.registry.ServiceRegistry;
import uk.org.brooklyn.useless.registry.impl.ZookeeperServiceRegistry;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
@Slf4j
public class ZookeeperServiceProvider implements ServiceProvider {

    private ZookeeperServiceProvider() {
    }

    private static final ZookeeperServiceProvider INSTANCE = new ZookeeperServiceProvider();

    public static ZookeeperServiceProvider getInstance() {
        return INSTANCE;
    }

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private final ServiceRegistry serviceRegistry = ZookeeperServiceRegistry.getInstance();

    @Override
    public Object getService(String rpcServiceName) {
        return Optional.ofNullable(rpcServiceName)
                .map(serviceMap::get)
                .orElseThrow(() -> new RpcException("Service cannot be found."));
    }

    @Override
    @SneakyThrows
    public void publishService(RpcServiceConfig rpcServiceConfig) {
//        String localAddress = InetAddress.getLocalHost().getHostAddress();
        String localAddress = "127.0.0.1";
        log.info("publishService.localAddress is: [{}]", localAddress);
        String rpcServiceName = rpcServiceConfig.rpcServiceName();
        if (!serviceMap.containsKey(rpcServiceName)) {
            serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
            log.info("Service added: {}", rpcServiceName);
        }
        serviceRegistry.registerService(rpcServiceConfig.rpcServiceName(),
                new InetSocketAddress(localAddress, 9418));

    }
}
