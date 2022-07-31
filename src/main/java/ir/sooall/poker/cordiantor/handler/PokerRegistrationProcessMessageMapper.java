package ir.sooall.poker.cordiantor.handler;

import ir.sooall.poker.common.mapper.Message2ObjectMapper;
import ir.sooall.poker.common.mapper.Object2MessageMapper;
import ir.sooall.poker.common.message.PokerAckRegisterMessage;
import ir.sooall.poker.common.message.PokerNAckRegisterMessage;
import ir.sooall.poker.common.message.PokerRegisterMessage;
import ir.sooall.poker.common.model.Player;
import ir.sooall.poker.common.model.ValueObjects;

public interface PokerRegistrationProcessMessageMapper {
    class RegisterMessage2ObjectMapper implements Message2ObjectMapper<PokerRegisterMessage, Player> {
        @Override
        public Player map(PokerRegisterMessage message) {
            return new Player(new ValueObjects.PlayerId(message.getPlayerId())
                , new ValueObjects.PlayerName(message.getPlayerName())
                , new ValueObjects.NetAddress(message.getPlayerHost(), Integer.valueOf(message.getPlayerPort()))
                , ValueObjects.PlayerStatus.REGISTERED);
        }
    }

    class Player2AckRegisterMessageMapper implements Object2MessageMapper<Player, PokerAckRegisterMessage> {
        @Override
        public PokerAckRegisterMessage map(Player obj) {
            return PokerAckRegisterMessage.init("POKER", "1.0", "ACK-REGISTER", obj.id().value().toString() + "\n\r");
        }
    }

    class Player2NAckRegisterMessageMapper implements Object2MessageMapper<Player, PokerNAckRegisterMessage> {
        @Override
        public PokerNAckRegisterMessage map(Player obj) {
            return PokerNAckRegisterMessage.init("POKER", "1.0", "ACK-REGISTER"
                , obj.id().value().toString() + "\n\r" + "409" + "\n\r" + "Duplicate Resource");
        }
    }
}
