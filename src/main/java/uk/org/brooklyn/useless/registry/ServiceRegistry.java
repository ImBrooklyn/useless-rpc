package uk.org.brooklyn.useless.registry;

import java.net.InetSocketAddress;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
public interface ServiceRegistry {
    void registerService(String serviceName, InetSocketAddress remoteAddress);
    void unregisterService(String serviceName, InetSocketAddress remoteAddress);
    void clearRegisteredService();
}
