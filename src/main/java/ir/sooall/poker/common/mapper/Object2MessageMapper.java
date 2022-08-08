package ir.sooall.poker.common.mapper;

import ir.sooall.poker.framwork.client.message.PokerObject;

public interface Object2MessageMapper<Object, Message extends PokerObject> {
    Message map(Object request);
}
