package ir.sooall.poker.cordiantor.handler;

import ir.sooall.poker.common.mapper.Message2ObjectMapper;
import ir.sooall.poker.common.mapper.Object2MessageMapper;
import ir.sooall.poker.common.model.Player;
import ir.sooall.poker.common.model.ValueObjects;
import ir.sooall.poker.framwork.client.message.*;

import java.util.UUID;

public interface PokerRegistrationProcessMessageMapper {
    class RegisterMessage2ObjectMapper implements Message2ObjectMapper<PokerRequest, Player> {
        @Override
        public Player map(PokerRequest message) {
            return new Player(new ValueObjects.PlayerId(UUID.fromString(message.content().get(PokerProtocolConstantKey.PLAYER_ID.name())))
                , new ValueObjects.PlayerName(message.content().get(PokerProtocolConstantKey.PLAYER_NAME.name()))
                , new ValueObjects.NetAddress(message.content().get(PokerProtocolConstantKey.PLAYER_HOST.name())
                , Integer.valueOf(message.content().get(PokerProtocolConstantKey.PLAYER_PORT.name())))
                , ValueObjects.PlayerStatus.REGISTERED);
        }
    }

    class Player2AckRegisterMessageMapper implements Object2MessageMapper<Player, PokerResponse> {
        @Override
        public PokerResponse map(Player obj) {
            return PokerResponse.builder()
                .header()
                .protocolName(PokerMessageHeader.POKER_PROTOCOL_NAME)
                .protocolVersion(PokerMessageHeader.POKER_PROTOCOL_VERSION)
                .action(ResponseAction.ACK_REGISTER)
                .content()
                .addItem(PokerProtocolConstantKey.PLAYER_ID.name(), obj.id().value().toString())
                .addItem(PokerProtocolConstantKey.PLAYER_NAME.name(), obj.name().value())
                .build();
        }
    }

    class Player2NAckRegisterMessageMapper implements Object2MessageMapper<Player, PokerResponse> {
        @Override
        public PokerResponse map(Player obj) {
            return PokerResponse.builder()
                .header()
                .protocolName(PokerMessageHeader.POKER_PROTOCOL_NAME)
                .protocolVersion(PokerMessageHeader.POKER_PROTOCOL_VERSION)
                .action(ResponseAction.NACK_REGISTER)
                .content()
                .addItem(PokerProtocolConstantKey.ERROR_CODE.name(), "409")
                .addItem(PokerProtocolConstantKey.ERROR_MESSAGE.name(), "Duplicate Resource")
                .build();
        }
    }
}
