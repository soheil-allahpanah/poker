package ir.sooall.poker.cordiantor.infra.interceptor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import ir.sooall.poker.player.client.message.PokerRequest;

import java.util.List;

public class PokerMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            System.out.println("PokerMessageDecoder >> channelRead0  >> in.readableBytes() : " + in.readableBytes());
            System.out.println("PokerMessageDecoder >> channelRead0  >> in.readerIndex() : " + in.readerIndex());
            System.out.println("PokerMessageDecoder >> channelRead0  >> in.capacity() : " + in.capacity());
            byte[] decoded = new byte[in.readableBytes()];
            in.readBytes(decoded);
            System.out.println("PokerMessageDecoder >> channelRead0  String(decoded) : " + new String(decoded));
            out.add(PokerRequest.fromString(new String(decoded)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
