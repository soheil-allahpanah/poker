package ir.sooall.poker.common.mapper;

import ir.sooall.poker.common.message.PokerMessage;

public interface Message2ObjectMapper<Message extends PokerMessage, Object> {
    Object map(Message message);
}
