package ir.sooall.poker.cordiantor;

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

        ServiceRegistry.addService(PlayerRepository.class, new PlayerRepositoryImpl());
        ServiceRegistry.addService(RegisterPlayerUseCase.class, new RegisterPlayerUseCaseImpl(ServiceRegistry.getService(PlayerRepository.class)));
        ServiceRegistry.addService(RegisterPlayerController.class, new RegisterPlayerController(ServiceRegistry.getService(RegisterPlayerUseCase.class)));
        new CoordinatorAppBootstrap().config(new CoordinatorServerInitializer()).run(PORT);

    }
}
