package ir.sooall.poker.cordiantor.domain.repository;

import ir.sooall.poker.common.model.Player;
import ir.sooall.poker.common.model.ValueObjects;

public interface PlayerRepository {
    Player findById(ValueObjects.PlayerId id);

    void save(Player player);
}
