package uk.org.brooklyn.useless.remote.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import uk.org.brooklyn.useless.codec.Serializer;
import uk.org.brooklyn.useless.compress.Compressor;
import uk.org.brooklyn.useless.enums.CompressorEnum;
import uk.org.brooklyn.useless.enums.MessageTypeEnum;
import uk.org.brooklyn.useless.enums.SerializerEnum;
import uk.org.brooklyn.useless.model.RpcMessage;
import uk.org.brooklyn.useless.protocol.ProtocolConstants;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger SERIAL_NUMBER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {

        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getOrThrow(msg.getMessageType());

        out.writeBytes(ProtocolConstants.MAGIC_NUMBER);
        out.writeByte(ProtocolConstants.VERSION);
        // full length
        out.writerIndex(out.writerIndex() + 4);
        out.writeByte(msg.getMessageType());
        out.writeByte(msg.getCodec());
        out.writeByte(msg.getCompress());
        out.writeInt(SERIAL_NUMBER.getAndIncrement());

        byte[] body = null;
        int fullLength = ProtocolConstants.HEADER_LENGTH;
        switch (messageTypeEnum) {
            case PING, PONG -> {}
            case REQUEST, RESPONSE -> {
                Serializer serializer = SerializerEnum.getOrThrow(msg.getCodec()).getInstance();
                Compressor compressor = CompressorEnum.getOrThrow(msg.getCompress()).getInstance();
                body = serializer.serialize(msg.getData());
                body = compressor.compress(body);
                fullLength += body.length;
            }
        }

        if (body != null) {
            out.writeBytes(body);
        }

        int writeIndex = out.writerIndex();
        out.writerIndex(writeIndex - fullLength + ProtocolConstants.MAGIC_NUMBER.length + 1);
        out.writeInt(fullLength);
        out.writerIndex(writeIndex);
    }
}
