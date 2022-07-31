package ir.sooall.poker.player;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import ir.sooall.poker.common.message.PokerRegisterMessage;
import ir.sooall.poker.player.server.PlayerAppBootstrap;
import ir.sooall.poker.player.server.PlayerServerInitializer;

import java.util.UUID;
import java.util.concurrent.*;

public class PlayerApp {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8324"));

    private static CoordinatorClient coordinatorClient;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws Exception {
        new PlayerAppBootstrap().config(new PlayerServerInitializer()).run(PORT);
        coordinatorClient = new CoordinatorClient().config("localhost", 8323);
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    coordinatorClient.sendMessage(PokerRegisterMessage.init("POKER"
                        , "1.0"
                        , "REGISTER"
                        , UUID.randomUUID() + "\n\r" + "localhost" + "\n\r" + "8324" + "\n\r")).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isSuccess()) {

                            }
                        }
                    })
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 60 , TimeUnit.SECONDS);

    }
}
