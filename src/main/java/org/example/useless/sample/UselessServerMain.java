package org.example.useless.sample;

import org.example.useless.remote.server.UselessRpcServer;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class UselessServerMain {
    public static void main(String[] args) {
        UselessRpcServer server = new UselessRpcServer(9418);
        server.start();
    }
}
