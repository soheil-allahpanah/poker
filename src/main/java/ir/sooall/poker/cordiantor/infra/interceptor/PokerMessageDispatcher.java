package ir.sooall.poker.cordiantor.infra.interceptor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ir.sooall.poker.common.message.PokerMessage;
import ir.sooall.poker.common.message.PokerRegisterMessage;
import ir.sooall.poker.cordiantor.adaptor.in.contorller.RegisterPlayerController;
import ir.sooall.poker.cordiantor.infra.config.ServiceRegistry;

public class PokerMessageDispatcher extends SimpleChannelInboundHandler<PokerMessage> {

    private final RegisterPlayerController registerPlayerController = ServiceRegistry.getService(RegisterPlayerController.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PokerMessage pokerMessage) throws Exception {
        System.out.println("PokerMessageDispatcher >> channelRead0 >> ");
        if (pokerMessage instanceof PokerRegisterMessage prm) {
            System.out.println("PokerMessageDispatcher >> channelRead0 >>  PokerRegisterMessage ");
            ctx.channel().writeAndFlush(registerPlayerController.register(prm));
        } else {
            throw new UnsupportedOperationException("Unsupported Poker Message");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
