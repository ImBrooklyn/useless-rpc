package org.example.useless.remote.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@Slf4j
public class UselessRpcServer {
    private final InetSocketAddress socketAddress;

    public UselessRpcServer(int port) {
        this.socketAddress = new InetSocketAddress(port);
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new UselessRpcServerInitializer());

            ChannelFuture channelFuture = bootstrap.bind(socketAddress).sync();
            log.info("Server has started on [{}]", socketAddress);
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("server boot interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
