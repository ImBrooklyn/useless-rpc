package uk.org.brooklyn.useless.registry.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import uk.org.brooklyn.useless.registry.ServiceRegistry;
import uk.org.brooklyn.useless.utils.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
@Slf4j
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private ZookeeperServiceRegistry() {
    }

    private static final ZookeeperServiceRegistry INSTANCE = new ZookeeperServiceRegistry();

    public static ZookeeperServiceRegistry getInstance() {
        return INSTANCE;
    }

    private final Set<String> REGISTERED_PATH = ConcurrentHashMap.newKeySet();

    @Override
    public void registerService(String serviceName, InetSocketAddress remoteAddress) {

        CuratorFramework client = CuratorUtils.getZookeeperClient();
        String servicePath = CuratorUtils.servicePath(serviceName, remoteAddress);
        try {
            if (REGISTERED_PATH.contains(servicePath) || client.checkExists().forPath(servicePath) != null) {
                log.info("Node already registered: [{}]", servicePath);
            } else {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
                log.info("Node created: [{}]", servicePath);
            }
            REGISTERED_PATH.add(servicePath);
        } catch (Exception e) {
            log.error("Register failed for path [{}].", servicePath);
        }
    }

    @Override
    public void unregisterService(String serviceName, InetSocketAddress remoteAddress) {
        CuratorFramework client = CuratorUtils.getZookeeperClient();
        String servicePath = CuratorUtils.servicePath(serviceName, remoteAddress);
        try {
            client.delete().forPath(servicePath);
        } catch (Exception e) {
            log.error("Unregister failed for path [{}].", servicePath);
        }
    }

    @Override
    public void clearRegisteredService() {
        // TODO shutdown hook
        CuratorFramework client = CuratorUtils.getZookeeperClient();
        REGISTERED_PATH.stream().parallel().forEach(path -> {
            try {
                client.delete().forPath(path);
            } catch (Exception e) {
                log.error("Clear registry failed.");
            }
        });
    }
}
