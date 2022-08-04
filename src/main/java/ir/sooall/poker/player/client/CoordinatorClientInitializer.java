package ir.sooall.poker.player.client;

import io.netty.channel.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

public class CoordinatorClientInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("encoder", new StringEncoder(StandardCharsets.US_ASCII));
        pipeline.addLast("handler", new CoordinatorClientHandler());
    }


}
