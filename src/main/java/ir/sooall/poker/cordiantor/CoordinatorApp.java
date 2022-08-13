package ir.sooall.poker.cordiantor;

import ir.sooall.framwork.di.Configor;
import ir.sooall.poker.framwork.server.server.CoordinatorAppBootstrap;
import ir.sooall.poker.framwork.server.server.CoordinatorServerInitializer;

public class CoordinatorApp {
    static final int PORT = Integer.parseInt(System.getProperty("port", "8323"));

    public static void main(String[] args) throws Exception {

        Configor.run(CoordinatorApp.class);
        new CoordinatorAppBootstrap().config(new CoordinatorServerInitializer()).run(PORT);

    }
}
