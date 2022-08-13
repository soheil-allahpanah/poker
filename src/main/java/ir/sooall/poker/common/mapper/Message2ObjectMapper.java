package ir.sooall.poker.common.mapper;

import ir.sooall.poker.framwork.message.PokerObject;

public interface Message2ObjectMapper<Message extends PokerObject, Object> {
    Object map(Message message);
}
