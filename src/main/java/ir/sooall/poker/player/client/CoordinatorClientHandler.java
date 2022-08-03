package ir.sooall.poker.player.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import ir.sooall.poker.player.client.message.PokerResponse;
import io.netty.channel.ChannelHandler.Sharable;

import java.util.List;


@Sharable
public final class CoordinatorClientHandler extends ByteToMessageDecoder {

    private final PokerClient client;

    public CoordinatorClientHandler(PokerClient client) {
        this.client = client;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RequestInfo info = ctx.channel().attr(PokerClient.KEY).get();
        if (!info.cancelled.get()) {
            info.handle.event(new State.Closed());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final RequestInfo info = ctx.channel().attr(PokerClient.KEY).get();
        if (checkCancelled(ctx)) {
            return;
        }
        try {
            System.out.println("PokerMessageDecoder >> channelRead0  >> in.readableBytes() : " + in.readableBytes());
            System.out.println("PokerMessageDecoder >> channelRead0  >> in.readerIndex() : " + in.readerIndex());
            System.out.println("PokerMessageDecoder >> channelRead0  >> in.capacity() : " + in.capacity());
            byte[] decoded = new byte[in.readableBytes()];
            in.readBytes(decoded);
            System.out.println("PokerMessageDecoder >> channelRead0  >> new String(decoded) : " + new String(decoded));
            var response = PokerResponse.fromString(new String(decoded));
            System.out.println("PokerMessageDecoder >> channelRead0  >> response : " + response);
            info.handle.event(new State.ContentReceived(response));
            info.cancelTimer();
            out.add(response);
            if (info.r != null) {
                info.r.internalReceive(response);
            }
            info.handle.event(new State.Finished(response));
            info.handle.trigger();
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
