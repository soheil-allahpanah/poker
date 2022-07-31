package ir.sooall.poker.common.model;

import ir.sooall.poker.common.model.ValueObjects.*;

public record Player(PlayerId id, PlayerName name, NetAddress address, PlayerStatus status) {

}


