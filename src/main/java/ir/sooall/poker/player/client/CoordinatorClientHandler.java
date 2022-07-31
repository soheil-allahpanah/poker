package ir.sooall.poker.player.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class CoordinatorClientHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("message received form coordinator server : " + msg);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("Channel Inactivated");
    }


}
