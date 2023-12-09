package org.example.useless.remote.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.useless.remote.codec.RpcMessageDecoder;
import org.example.useless.remote.codec.RpcMessageEncoder;

import java.util.concurrent.TimeUnit;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class UselessRpcServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast("encoder", new RpcMessageEncoder());
        pipeline.addLast("decoder", new RpcMessageDecoder());
        // TODO handler thread pool
        pipeline.addLast(new UselessRpcServerHandler());
    }
}
