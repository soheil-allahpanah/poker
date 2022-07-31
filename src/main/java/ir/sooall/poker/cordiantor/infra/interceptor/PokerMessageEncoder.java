package ir.sooall.poker.cordiantor.infra.interceptor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToByteEncoder;
import ir.sooall.poker.common.message.PokerMessage;

import java.nio.charset.StandardCharsets;

public class PokerMessageEncoder extends MessageToByteEncoder<PokerMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, PokerMessage msg, ByteBuf out) throws Exception {
        System.out.println("PokerMessageEncoder >> write ");
        var message = PokerMessage.encode(msg);
        byte[] data = message.getBytes(StandardCharsets.US_ASCII);
        out.writeBytes(data);
        System.out.println("---------------------------------- ");
    }

}
