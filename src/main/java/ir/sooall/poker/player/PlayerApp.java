package ir.sooall.poker.player;

import ir.sooall.poker.player.server.PlayerAppBootstrap;
import ir.sooall.poker.player.server.PlayerServerInitializer;

public class PlayerApp {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8324"));

    public static void main(String[] args) throws Exception {
        new PlayerAppBootstrap().config(new PlayerServerInitializer()).run(PORT);
    }
}
