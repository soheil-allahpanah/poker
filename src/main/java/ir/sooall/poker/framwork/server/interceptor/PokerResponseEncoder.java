package ir.sooall.poker.framwork.server.interceptor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ir.sooall.poker.framwork.message.PokerResponse;

import java.nio.charset.StandardCharsets;

public class PokerResponseEncoder extends MessageToByteEncoder<PokerResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, PokerResponse msg, ByteBuf out) throws Exception {
        System.out.println("PokerResponseEncoder >> write ");
        var message = msg.toString();
        out.writeInt(message.length());
        out.writeBytes(message.getBytes(StandardCharsets.US_ASCII));
        System.out.println("---------------------------------- ");
    }

}
