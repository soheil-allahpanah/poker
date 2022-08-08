package ir.sooall.poker.framwork.server.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import ir.sooall.poker.framwork.server.interceptor.PokerRequestDecoder;
import ir.sooall.poker.framwork.server.interceptor.PokerMessageDispatcher;
import ir.sooall.poker.framwork.server.interceptor.PokerResponseEncoder;

public class CoordinatorServerInitializer extends ChannelInitializer<SocketChannel> {

    public CoordinatorServerInitializer() {
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        var pipeLine = ch.pipeline();
        pipeLine.addLast(new PokerResponseEncoder());
        pipeLine.addLast(new PokerRequestDecoder(), new PokerMessageDispatcher());
    }
}
