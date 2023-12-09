package uk.org.brooklyn.useless.trivial;

import org.apache.curator.framework.CuratorFramework;
import uk.org.brooklyn.useless.utils.CuratorUtils;
import org.junit.jupiter.api.Test;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
public class CuratorTest {
    @Test
    public void getClientTest() {
        CuratorFramework client = CuratorUtils.getZookeeperClient();
        System.out.println("client.getState() = " + client.getState());
    }
}
