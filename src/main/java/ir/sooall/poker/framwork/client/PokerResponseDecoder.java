package ir.sooall.poker.framwork.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import ir.sooall.poker.framwork.message.PokerResponse;

import java.util.List;


public final class PokerResponseDecoder extends ByteToMessageDecoder {


    private boolean isRead =  false;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RequestInfo info = ctx.channel().attr(PokerClient.KEY).get();
        if (!info.cancelled.get()) {
            info.handle.event(new State.Closed());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (isRead) {
            return;
        }
        final RequestInfo info = ctx.channel().attr(PokerClient.KEY).get();
        if (checkCancelled(ctx)) {
            return;
        }
        try {
            System.out.println("PokerResponseDecoder >> channelRead0  >> in.readableBytes() : " + in.readableBytes());
            System.out.println("PokerResponseDecoder >> channelRead0  >> in.readerIndex() : " + in.readerIndex());
            System.out.println("PokerResponseDecoder >> channelRead0  >> in.capacity() : " + in.capacity());
            if (in.readableBytes() >= 4) {
                int contentLength = in.readInt();
                if (in.readableBytes() < contentLength) {
                    in.resetReaderIndex();
                    return;
                }
                System.out.println("PokerResponseDecoder >> channelRead0  >> contentLength : " + contentLength);
                System.out.println("PokerResponseDecoder >> channelRead0  >> in.readerIndex : " + in.readerIndex());
                byte[] decoded = new byte[contentLength - 4];
                in.readBytes(decoded);
                var result = new String(decoded);
                System.out.println("PokerResponseDecoder >> channelRead0  String(decoded) : " + result);
                var response = PokerResponse.fromString(result);
                info.handle.event(new State.ContentReceived(response));
                info.cancelTimer();
                out.add(response);
                if (info.r != null) {
                    info.r.internalReceive(response);
                }
                info.handle.event(new State.Finished(response));
                info.handle.trigger();
                isRead = true;
                ReferenceCountUtil.release(in);

            }

        } catch (IllegalStateException e) {
            info.handle.event(new State.Error(e));
            e.printStackTrace();

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        RequestInfo info = ctx.channel().attr(PokerClient.KEY).get();
        info.handle.event(new State.Error(cause));
    }

    private boolean checkCancelled(ChannelHandlerContext ctx) {
        RequestInfo info = ctx.channel().attr(PokerClient.KEY).get();
        boolean result = info != null && info.cancelled.get();
        if (result) {
            Channel ch = ctx.channel();
            if (ch.isOpen()) {
                ch.close();
            }
        }
        return result;
    }


}
