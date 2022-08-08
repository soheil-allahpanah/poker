package ir.sooall.poker.framwork.server.interceptor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import ir.sooall.poker.framwork.client.message.PokerRequest;

import java.util.List;

public class PokerRequestDecoder extends ByteToMessageDecoder {

    private boolean isRead =  false;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (isRead) {
            return;
        }
        try {
            System.out.println("PokerRequestDecoder >> channelRead0  >> in.readableBytes() : " + in.readableBytes());
            System.out.println("PokerRequestDecoder >> channelRead0  >> in.readerIndex() : " + in.readerIndex());
            System.out.println("PokerRequestDecoder >> channelRead0  >> in.capacity() : " + in.capacity());
            if (in.readableBytes() >= 4) {
                int contentLength = in.readInt();
                if (in.readableBytes() < contentLength) {
                    in.resetReaderIndex();
                    return;
                }
                System.out.println("PokerRequestDecoder >> channelRead0  >> contentLength : " + contentLength);
                System.out.println("PokerRequestDecoder >> channelRead0  >> in.readerIndex : " + in.readerIndex());
                byte[] decoded = new byte[contentLength - 4];
                in.readBytes(decoded);
                var result = new String(decoded);
                System.out.println("PokerRequestDecoder >> channelRead0  String(decoded) : " + result);
                out.add(PokerRequest.fromString(result));
                isRead = true;
                ReferenceCountUtil.release(in);
            }
        } catch (Exception e) {
            in.resetReaderIndex();
            e.printStackTrace();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
