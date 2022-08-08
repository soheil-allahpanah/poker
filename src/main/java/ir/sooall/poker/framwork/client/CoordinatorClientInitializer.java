package ir.sooall.poker.framwork.client;

import io.netty.channel.*;

public class CoordinatorClientInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast("encoder", new StringEncoder(StandardCharsets.US_ASCII));
        pipeline.addLast("encoder", new PokerRequestEncoder());
        pipeline.addLast("handler", new PokerResponseDecoder());
    }


}
