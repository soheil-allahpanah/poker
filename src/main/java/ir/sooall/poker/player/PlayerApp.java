package ir.sooall.poker.player;

import ir.sooall.poker.player.client.PokerClient;
import ir.sooall.poker.player.client.ResponseHandler;
import ir.sooall.poker.player.client.message.PokerMessageHeader;
import ir.sooall.poker.player.client.message.PokerProtocolConstantKey;
import ir.sooall.poker.player.client.message.PokerResponse;
import ir.sooall.poker.player.client.message.ResponseAction;
import ir.sooall.poker.player.server.PlayerAppBootstrap;
import ir.sooall.poker.player.server.PlayerServerInitializer;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerApp {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8324"));

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws Exception {
        PokerClient client = PokerClient.builder().build();
        new PlayerAppBootstrap().config(new PlayerServerInitializer()).run(PORT);
        executorService.scheduleWithFixedDelay(() -> {
            try {
                client.register()
                    .ipAndPort("localhost", 8323)
                    .timeout(Duration.ofSeconds(30))
                    .header()
                    .protocolName(PokerMessageHeader.POKER_PROTOCOL_NAME)
                    .protocolVersion(PokerMessageHeader.POKER_PROTOCOL_VERSION)
                    .content()
                    .addItem(PokerProtocolConstantKey.PLAYER_ID.name(), UUID.randomUUID().toString())
                    .addItem(PokerProtocolConstantKey.PLAYER_NAME.name(), UUID.randomUUID().toString())
                    .addItem(PokerProtocolConstantKey.PLAYER_HOST.name(), "localhost")
                    .addItem(PokerProtocolConstantKey.PLAYER_PORT.name(), "8323")
                    .builder()
                    .execute(new ResponseHandler<>(PokerResponse.class) {
                        @Override
                        protected void receive(PokerResponse pokerMessage) {
                            if (pokerMessage.action().equals(ResponseAction.ACK_REGISTER)) {
                                executorService.shutdown();
                            }
                        }

                        @Override
                        protected void onError(Throwable err) {
                            err.printStackTrace();
                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }, 1, 10, TimeUnit.SECONDS);
    }
}
