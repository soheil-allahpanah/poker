package ir.sooall.poker.cordiantor.db;

import ir.sooall.poker.common.model.Game;
import ir.sooall.poker.common.model.Hand;
import ir.sooall.poker.common.model.Player;
import ir.sooall.poker.common.model.ValueObjects;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDB {

    private static final ConcurrentHashMap<ValueObjects.PlayerId, Player> players = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ValueObjects.GameId, Game> games = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ValueObjects.PlayerId, Hand> hands = new ConcurrentHashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, V> Table<T, V> table(Class<T> t, Class<V> v) {
        if (t.equals(ValueObjects.PlayerId.class) && v.equals(Player.class)) {
            return new Table(players);
        } else if (t.equals(ValueObjects.PlayerId.class) && v.equals(Hand.class)) {
            return new Table(hands);
        } else if (t.equals(ValueObjects.GameId.class) && v.equals(Game.class)) {
            return new Table(games);
        }
        return null;
    }

}
