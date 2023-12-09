package uk.org.brooklyn.useless.remote.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import uk.org.brooklyn.useless.enums.CompressorEnum;
import uk.org.brooklyn.useless.enums.MessageTypeEnum;
import uk.org.brooklyn.useless.enums.SerializerEnum;
import uk.org.brooklyn.useless.handler.RpcRequestHandler;
import uk.org.brooklyn.useless.model.RpcMessage;
import uk.org.brooklyn.useless.model.RpcRequest;
import uk.org.brooklyn.useless.model.RpcResponse;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@Slf4j
public class UselessRpcServerHandler extends SimpleChannelInboundHandler<RpcMessage> {
    // TODO
    private final RpcRequestHandler requestHandler = RpcRequestHandler.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        MessageTypeEnum messageType = MessageTypeEnum.getOrThrow(msg.getMessageType());
        RpcMessage rpcMessage = switch (messageType) {
            case PING -> RpcMessage.builder()
                    .messageType(MessageTypeEnum.PONG.getCode())
                    .codec(SerializerEnum.HESSIAN.getCode())
                    .compress(CompressorEnum.NON.getCode())
                    .build();
            case REQUEST -> {
                log.info("request: {}", msg.getData());
                RpcRequest request = (RpcRequest) msg.getData();
                Object result = requestHandler.process(request);
                RpcResponse<?> response = RpcResponse.success(result, request.getRequestId());
                yield RpcMessage.builder()
                        .messageType(MessageTypeEnum.RESPONSE.getCode())
                        .codec(SerializerEnum.HESSIAN.getCode())
                        .compress(CompressorEnum.GZIP.getCode())
                        .data(response)
                        .build();
            }
            default -> {
                log.warn("Illegal message type [{}], msg discarded.", msg.getMessageType());
                yield null;
            }
        };
        if (rpcMessage != null && ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            IdleState state = event.state();
            if (state == IdleState.READER_IDLE) {
                log.info("reader idle, channel closing...");
                ctx.close();
                log.info("reader idle, channel closed...");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server exception caught", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
