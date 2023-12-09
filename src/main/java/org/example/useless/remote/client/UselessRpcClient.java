package org.example.useless.remote.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.useless.enums.CompressorEnum;
import org.example.useless.enums.MessageTypeEnum;
import org.example.useless.enums.SerializerEnum;
import org.example.useless.model.RpcMessage;
import org.example.useless.model.RpcRequest;
import org.example.useless.model.RpcResponse;
import org.example.useless.registry.ServiceDiscovery;
import org.example.useless.registry.impl.ZookeeperServiceDiscovery;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@Slf4j
public class UselessRpcClient {

    private final RequestQueue requestQueue;
    private final ChannelProvider channelProvider;
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;


    public UselessRpcClient() {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new UselessRpcClientInitializer());
        this.requestQueue = RequestQueue.getInstance();
        this.channelProvider = ChannelProvider.getInstance();
        this.serviceDiscovery = ZookeeperServiceDiscovery.getInstance();
    }

    public CompletableFuture<RpcResponse<?>> sendRpcRequest(RpcRequest request) {
        CompletableFuture<RpcResponse<?>> resultFuture = new CompletableFuture<>();
        InetSocketAddress remoteAddress = serviceDiscovery.lookupService(request);
        Channel channel = getChannel(remoteAddress);
        if (channel.isActive()) {
            requestQueue.put(request.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder()
                    .messageType(MessageTypeEnum.REQUEST.getCode())
                    .codec(SerializerEnum.HESSIAN.getCode())
                    .compress(CompressorEnum.GZIP.getCode())
                    .data(request)
                    .build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("Request sent failed.", future.cause());
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                }
            });
        } else {
            log.error("Inactive channel: {}", channel.remoteAddress());
            throw new IllegalStateException("Inactive channel");
        }

        return resultFuture;
    }

    private Channel getChannel(InetSocketAddress remoteAddress) {
        Channel channel = channelProvider.getChannel(remoteAddress);
        if (channel == null) {
            channel = doConnect(remoteAddress);
            channelProvider.setChannel(channel);
        }
        return channel;
    }

    @SneakyThrows
    private Channel doConnect(InetSocketAddress remoteAddress) {
        CompletableFuture<Channel> resultFuture = new CompletableFuture<>();
        log.info("Client is connecting to [{}]....", remoteAddress);
        bootstrap.connect(remoteAddress).addListener((ChannelFutureListener) future -> {
            log.info("future.toString(): {}", future.toString());
            if (future.isSuccess()) {
                log.info("Client has connected to [{}]", remoteAddress);
                resultFuture.complete(future.channel());
            } else {
                resultFuture.completeExceptionally(new IllegalStateException());
                throw new IllegalStateException();
            }
        });
        return resultFuture.get();
    }

    private void close() {
        group.shutdownGracefully();
    }
}
