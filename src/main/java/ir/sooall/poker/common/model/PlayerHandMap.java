package ir.sooall.poker.common.model;

import java.util.concurrent.ConcurrentHashMap;

import ir.sooall.poker.common.model.ValueObjects.*;

public record PlayerHandMap(ConcurrentHashMap<PlayerId, Hand> value) {

    public void add(PlayerId playerId, Hand hand) {
        value.put(playerId, hand);
    }

    public Hand get(PlayerId playerId) {
        return value.get(playerId);
    }
}
