package ir.sooall.poker.cordiantor.usecase.registerplayer;

import ir.sooall.poker.common.model.Player;
import ir.sooall.poker.cordiantor.domain.repository.PlayerRepository;

import java.util.Objects;

public class RegisterPlayerUseCaseImpl implements RegisterPlayerUseCase {

    private final PlayerRepository playerRepository;

    public RegisterPlayerUseCaseImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player register(Player request) {
        var storedPlayer = playerRepository.findById(request.id());
        if(Objects.nonNull(storedPlayer)) {
            throw new IllegalStateException("Player already exists");
        }
        return playerRepository.save(request);
    }
}
