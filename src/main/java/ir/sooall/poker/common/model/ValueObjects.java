package ir.sooall.poker.common.model;

import java.util.UUID;

public interface ValueObjects {
    record PlayerId(UUID value) {
    }

    record PlayerName(String value) {
    }

    enum PlayerStatus {
        REGISTERED,
        ACTIVATED,
        DEACTIVATED,
        UNREGISTERED
    }

    record NetAddress(String ip, Integer port) {
    }

    record HandValue(Integer value) {
    }

    enum GameStatus {
        INITIATED,
        RUNNING,
        DONE,
        FAILED
    }

    record GameId(UUID value) {
    }


}
