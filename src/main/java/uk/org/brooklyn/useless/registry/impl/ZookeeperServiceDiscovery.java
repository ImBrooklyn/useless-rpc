package uk.org.brooklyn.useless.registry.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import uk.org.brooklyn.useless.exception.RpcException;
import uk.org.brooklyn.useless.loadbalance.LoadBalancer;
import uk.org.brooklyn.useless.loadbalance.impl.RandomLoadBalancer;
import uk.org.brooklyn.useless.model.RpcRequest;
import uk.org.brooklyn.useless.registry.ServiceDiscovery;
import uk.org.brooklyn.useless.utils.CuratorUtils;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private ZookeeperServiceDiscovery() {
    }

    private static final ZookeeperServiceDiscovery INSTANCE = new ZookeeperServiceDiscovery();

    public static ZookeeperServiceDiscovery getInstance() {
        return INSTANCE;
    }

    private final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    private final LoadBalancer loadBalancer = new RandomLoadBalancer();

    @Override
    public InetSocketAddress lookupService(RpcRequest request) {
        String rpcServiceName = request.rpcServiceName();
        List<String> serviceList = getServiceList(rpcServiceName);
        if (serviceList.isEmpty()) {
            log.error("Service [{}] unavailable.", rpcServiceName);
            throw new RpcException("Service unavailable");
        }
        String targetAddress = loadBalancer.selectServiceAddress(serviceList, request);
        log.debug("Service address is selected: [{}]", targetAddress);
        String[] socketAddressArray = targetAddress.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }

    private List<String> getServiceList(String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        String serviceRoot = CuratorUtils.serviceRoot(rpcServiceName);
        CuratorFramework client = CuratorUtils.getZookeeperClient();
        List<String> results = null;
        try {
            results = client.getChildren().forPath(serviceRoot);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, results);
            addListener(rpcServiceName);
        } catch (Exception e) {
            log.error("Get children nodes failed for path [{}]", serviceRoot);
        }

        return Optional.ofNullable(results).orElseGet(Collections::emptyList);
    }

    private void addListener(String rpcServiceName) {
        String serviceRoot = CuratorUtils.serviceRoot(rpcServiceName);
        CuratorFramework client = CuratorUtils.getZookeeperClient();
        CuratorCache cache = CuratorCache.build(client, serviceRoot);

        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forInitialized(() -> System.out.println("CuratorCacheListener initialized"))
                .forCreates(newNode -> System.out.printf("Node created: [%s]%n", new String(newNode.getData(), StandardCharsets.UTF_8)))
                .forDeletes(oldNode -> System.out.printf("Node deleted: [%s]%n", new String(oldNode.getData(), StandardCharsets.UTF_8)))
                .forChanges((oldNode, node) -> {
                    System.out.printf("Node change - creation: [%s]%n",
                            new String(node.getData(), StandardCharsets.UTF_8));
                    System.out.printf("Node change - deletion: [%s]%n",
                            new String(oldNode.getData(), StandardCharsets.UTF_8));
                })
                .build();

        cache.listenable().addListener(listener);
        cache.start();

    }
}
