package ir.sooall.poker.cordiantor.infra.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ir.sooall.poker.cordiantor.infra.interceptor.PokerMessageDecoder;
import ir.sooall.poker.cordiantor.infra.interceptor.PokerMessageDispatcher;
import ir.sooall.poker.cordiantor.infra.interceptor.PokerMessageEncoder;

import java.nio.charset.StandardCharsets;

public class CoordinatorServerInitializer extends ChannelInitializer<SocketChannel> {

    public CoordinatorServerInitializer() {
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        var pipeLine = ch.pipeline();
        pipeLine.addLast(new PokerMessageEncoder());
        pipeLine.addLast(new PokerMessageDecoder(), new PokerMessageDispatcher());
    }
}
