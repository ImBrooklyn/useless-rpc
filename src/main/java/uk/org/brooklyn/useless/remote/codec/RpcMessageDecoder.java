package uk.org.brooklyn.useless.remote.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import uk.org.brooklyn.useless.codec.Serializer;
import uk.org.brooklyn.useless.compress.Compressor;
import uk.org.brooklyn.useless.enums.CompressorEnum;
import uk.org.brooklyn.useless.enums.MessageTypeEnum;
import uk.org.brooklyn.useless.enums.SerializerEnum;
import uk.org.brooklyn.useless.model.RpcMessage;
import uk.org.brooklyn.useless.model.RpcRequest;
import uk.org.brooklyn.useless.model.RpcResponse;
import uk.org.brooklyn.useless.protocol.ProtocolConstants;

import java.util.Arrays;

/**
 * <pre>
 *   0     1     2     3     4     5     6     7     8     9     10    11    12    13    14    15    16
 *   +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
 *   |      magic number     | ver |          len          |type |codec|comp |     serial number     |
 *   +-----------------------+-----+-----------------------+-----+-----+-----+-----------------------+
 *   |                                                                                               |
 *   |                                         body                                                  |
 *   |                                                                                               |
 *   |                                        ... ...                                                |
 *   +-----------------------------------------------------------------------------------------------+
 * 4B  magic number（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 *
 * @author ImBrooklyn
 * @since 19/08/2023
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        super(16 * 1024 * 1024, 5, 4, -9, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);

        if (decoded instanceof ByteBuf frame) {
            if (frame.readableBytes() >= ProtocolConstants.HEADER_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("decode frame error.", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }

        return null;
    }

    private Object decodeFrame(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();

        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();

        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getOrThrow(messageType);

        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .compress(compressType)
                .serialNumber(requestId)
                .messageType(messageType).build();

        int bodyLength = fullLength - ProtocolConstants.HEADER_LENGTH;
        if (messageTypeEnum.equals(MessageTypeEnum.PING)
                || messageTypeEnum.equals(MessageTypeEnum.PONG)
                || bodyLength <= 0) {
            return rpcMessage;
        }

        Serializer serializer = SerializerEnum.getOrThrow(codecType).getInstance();
        Compressor compressor = CompressorEnum.getOrThrow(compressType).getInstance();

        byte[] bytes = new byte[bodyLength];
        in.readBytes(bytes);
        bytes = compressor.decompress(bytes);

        if (MessageTypeEnum.REQUEST.equals(messageTypeEnum)) {
            RpcRequest request = serializer.deserialize(bytes, RpcRequest.class);
            rpcMessage.setData(request);
        } else if (MessageTypeEnum.RESPONSE.equals(messageTypeEnum)) {
            RpcResponse<?> response = serializer.deserialize(bytes, RpcResponse.class);
            rpcMessage.setData(response);
        }

        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        // read the version and compare
        byte version = in.readByte();
        if (version != ProtocolConstants.VERSION) {
            throw new IllegalStateException("Incompatible version: " + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // check the first 4 bit magic number
        int len = ProtocolConstants.MAGIC_NUMBER.length;
        byte[] magicNum = new byte[len];
        in.readBytes(magicNum);
        for (int i = 0; i < len; i++) {
            if (magicNum[i] != ProtocolConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic number: " + Arrays.toString(magicNum));
            }
        }
    }
}
