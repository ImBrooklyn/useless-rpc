package uk.org.brooklyn.useless.remote.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import uk.org.brooklyn.useless.remote.codec.RpcMessageDecoder;
import uk.org.brooklyn.useless.remote.codec.RpcMessageEncoder;

import java.util.concurrent.TimeUnit;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class UselessRpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
        pipeline.addLast("encoder", new RpcMessageEncoder());
        pipeline.addLast("decoder", new RpcMessageDecoder());
        // TODO handler thread pool
        pipeline.addLast(new UselessRpcClientHandler());
    }
}
