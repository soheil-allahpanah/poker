package ir.sooall.poker.cordiantor.infra.interceptor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ir.sooall.poker.player.client.message.PokerResponse;

import java.nio.charset.StandardCharsets;

public class PokerMessageEncoder extends MessageToByteEncoder<PokerResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, PokerResponse msg, ByteBuf out) throws Exception {
        System.out.println("PokerMessageEncoder >> write ");
        out.writeBytes(msg.toString().getBytes(StandardCharsets.US_ASCII));
        System.out.println("---------------------------------- ");
    }

}
