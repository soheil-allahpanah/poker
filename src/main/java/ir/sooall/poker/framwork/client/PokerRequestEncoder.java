package ir.sooall.poker.framwork.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ir.sooall.poker.framwork.message.PokerRequest;

public class PokerRequestEncoder extends MessageToByteEncoder<PokerRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, PokerRequest msg, ByteBuf out) throws Exception {
        System.out.println("PokerRequestEncoder >> write ");
        var message = msg.toString();
        out.writeInt(message.length());
        out.writeBytes(message.getBytes());
        System.out.println("---------------------------------- ");
    }

}
