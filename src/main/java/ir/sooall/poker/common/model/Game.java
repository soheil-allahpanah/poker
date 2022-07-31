package ir.sooall.poker.common.model;

import ir.sooall.poker.common.model.ValueObjects.*;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Game {
    private final GameId id;
    private final GameStatus status;
    private final PlayerHandMap playerHandsMap;

    private Game(GameId id, GameStatus status, PlayerHandMap playerHandMap) {
        this.id = id;
        this.status = status;
        this.playerHandsMap = playerHandMap;
    }

    public static GameBuilder builder() {
        return new GameBuilder();
    }

    public GameId id() {
        return id;
    }

    public GameStatus status() {
        return status;
    }

    public PlayerHandMap playerHandsMap() {
        return playerHandsMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Game) obj;
        return Objects.equals(this.id, that.id) &&
            Objects.equals(this.status, that.status) &&
            Objects.equals(this.playerHandsMap, that.playerHandsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, playerHandsMap);
    }

    @Override
    public String toString() {
        return "Game[" +
            "id=" + id + ", " +
            "status=" + status + ", " +
            "playerHandsMap=" + playerHandsMap + ']';
    }

    public static class GameBuilder {
        private GameId id;
        private GameStatus status;
        private PlayerHandMap playerHandsMap;

        public GameBuilder id(GameId id) {
            this.id = id;
            return this;
        }

        public GameBuilder status(GameStatus status) {
            this.status = status;
            return this;
        }

        public GameBuilder addPlayerHandsMap(Player player, Hand hand) {
            if (playerHandsMap == null) {
                playerHandsMap = new PlayerHandMap(new ConcurrentHashMap<>());
            }
            playerHandsMap.add(player.id(), hand);
            return this;
        }

        public Game game() {
            return new Game(id, status, playerHandsMap);
        }

        public GameBuilder init() {
            this.id = new GameId(UUID.randomUUID());
            this.status = GameStatus.INITIATED;
            this.playerHandsMap = new PlayerHandMap(new ConcurrentHashMap<>());
            return this;
        }

    }


}
