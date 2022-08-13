package ir.sooall.poker.cordiantor;

import ir.sooall.framwork.di.Configor;
import ir.sooall.poker.cordiantor.adaptor.in.contorller.RegisterPlayerController;
import ir.sooall.poker.cordiantor.adaptor.out.repository.PlayerRepositoryImpl;
import ir.sooall.poker.cordiantor.domain.repository.PlayerRepository;
import ir.sooall.poker.framwork.server.server.CoordinatorAppBootstrap;
import ir.sooall.poker.framwork.server.server.CoordinatorServerInitializer;
import ir.sooall.poker.framwork.server.config.ServiceRegistry;
import ir.sooall.poker.cordiantor.usecase.registerplayer.RegisterPlayerUseCase;
import ir.sooall.poker.cordiantor.usecase.registerplayer.RegisterPlayerUseCaseImpl;

public class CoordinatorApp {
    static final int PORT = Integer.parseInt(System.getProperty("port", "8323"));

    public static void main(String[] args) throws Exception {

        Configor.run(CoordinatorApp.class);
        new CoordinatorAppBootstrap().config(new CoordinatorServerInitializer()).run(PORT);

    }
}
