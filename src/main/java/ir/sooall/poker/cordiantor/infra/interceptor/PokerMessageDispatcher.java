package ir.sooall.poker.cordiantor.infra.interceptor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ir.sooall.poker.cordiantor.adaptor.in.contorller.RegisterPlayerController;
import ir.sooall.poker.cordiantor.infra.config.ServiceRegistry;
import ir.sooall.poker.player.client.message.PokerRequest;
import ir.sooall.poker.player.client.message.RequestAction;

public class PokerMessageDispatcher extends SimpleChannelInboundHandler<PokerRequest> {

    private final RegisterPlayerController registerPlayerController = ServiceRegistry.getService(RegisterPlayerController.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PokerRequest request) throws Exception {
        System.out.println("PokerMessageDispatcher >> channelRead0 >> ");
        if (request.action() == RequestAction.REGISTER) {
            var response = registerPlayerController.register(request);
            System.out.println("PokerMessageDispatcher >> channelRead0 >> response : " + response);
            ctx.channel().writeAndFlush(response);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
