package ir.sooall.poker.common.mapper;

import ir.sooall.poker.common.message.PokerMessage;

public interface Object2MessageMapper<Object, Message extends PokerMessage> {
    Message map(Object request);
}
