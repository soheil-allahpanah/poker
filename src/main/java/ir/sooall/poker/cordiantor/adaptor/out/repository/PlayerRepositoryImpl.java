package ir.sooall.poker.cordiantor.adaptor.out.repository;

import ir.sooall.poker.common.model.Player;
import ir.sooall.poker.common.model.ValueObjects;
import ir.sooall.poker.cordiantor.db.InMemoryDB;
import ir.sooall.poker.cordiantor.db.Table;
import ir.sooall.poker.cordiantor.domain.repository.PlayerRepository;

import java.util.Objects;

public class PlayerRepositoryImpl implements PlayerRepository {

    private final Table<ValueObjects.PlayerId, Player> playersTable;

    public PlayerRepositoryImpl() {
        playersTable = Objects.requireNonNull(InMemoryDB.table(ValueObjects.PlayerId.class, Player.class));
    }

    @Override
    public Player findById(ValueObjects.PlayerId id) {
        return playersTable.findById(id);
    }

    @Override
    public void save(Player player) {
        playersTable.save(player.id(), player);
    }
}
