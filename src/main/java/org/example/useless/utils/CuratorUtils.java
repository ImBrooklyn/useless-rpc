package org.example.useless.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
@Slf4j
public class CuratorUtils {
    private CuratorUtils() {
    }

    public static final String ROOT_PATH = "/useless-rpc/";

    private static CuratorFramework zookeeperClient;

    public static String serviceRoot(String rpcServiceName) {
        return ROOT_PATH.concat(rpcServiceName);
    }

    public static String servicePath(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        return serviceRoot(rpcServiceName).concat(inetSocketAddress.toString());
    }

    public static CuratorFramework getZookeeperClient() {
        if (zookeeperClient != null && zookeeperClient.getState() == CuratorFrameworkState.STARTED) {
            return zookeeperClient;
        }

        String address = "127.0.0.1:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zookeeperClient = CuratorFrameworkFactory.builder()
                .connectString(address)
                .retryPolicy(retryPolicy)
                .build();
        zookeeperClient.start();
        try {
            // wait 30s until connect to the zookeeper
            if (!zookeeperClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Connecting to zookeeper timeout...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zookeeperClient;

    }


}
