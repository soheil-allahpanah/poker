package ir.sooall.poker.cordiantor.config;

import ir.sooall.framwork.di.annotations.Bean;
import ir.sooall.framwork.di.annotations.Configuration;
import ir.sooall.poker.cordiantor.adaptor.in.contorller.RegisterPlayerController;
import ir.sooall.poker.cordiantor.adaptor.out.repository.PlayerRepositoryImpl;
import ir.sooall.poker.cordiantor.domain.repository.PlayerRepository;
import ir.sooall.poker.cordiantor.usecase.registerplayer.RegisterPlayerUseCase;
import ir.sooall.poker.cordiantor.usecase.registerplayer.RegisterPlayerUseCaseImpl;

@Configuration
public class Config {
    @Bean
    public PlayerRepository playerRepository() {
        return new PlayerRepositoryImpl();
    }

    @Bean
    public RegisterPlayerUseCase registerPlayerUseCase(PlayerRepository playerRepository) {
        return new RegisterPlayerUseCaseImpl(playerRepository);
    }

    @Bean
    public RegisterPlayerController registerPlayerController(RegisterPlayerUseCase registerPlayerUseCase) {
        return new RegisterPlayerController(registerPlayerUseCase);
    }
}
