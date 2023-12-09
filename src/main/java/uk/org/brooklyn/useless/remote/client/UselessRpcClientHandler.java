package uk.org.brooklyn.useless.remote.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import uk.org.brooklyn.useless.enums.CompressorEnum;
import uk.org.brooklyn.useless.enums.MessageTypeEnum;
import uk.org.brooklyn.useless.enums.SerializerEnum;
import uk.org.brooklyn.useless.model.RpcMessage;
import uk.org.brooklyn.useless.model.RpcResponse;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@Slf4j
public class UselessRpcClientHandler extends SimpleChannelInboundHandler<RpcMessage> {

    private final RequestQueue requestQueue = RequestQueue.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        MessageTypeEnum messageType = MessageTypeEnum.getOrThrow(msg.getMessageType());
        switch (messageType) {
            case PONG -> log.info("Pong from server [{}]", ctx.channel().remoteAddress());
            case RESPONSE -> {
                log.debug("msg is : [{}]", msg);
                RpcResponse<?> response = (RpcResponse<?>) msg.getData();
                requestQueue.complete(response);
            }
            default -> log.warn("Illegal message type [{}], msg discarded.", msg.getMessageType());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            IdleState state = event.state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("Writer idle, sending ping...");
                RpcMessage rpcMessage = RpcMessage.builder()
                        .messageType(MessageTypeEnum.PING.getCode())
                        .codec(SerializerEnum.HESSIAN.getCode())
                        .compress(CompressorEnum.NON.getCode())
                        .build();
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Client exception caught", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
