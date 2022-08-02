package ir.sooall.poker.cordiantor.handler;

import ir.sooall.poker.common.mapper.Message2ObjectMapper;
import ir.sooall.poker.common.mapper.Object2MessageMapper;
import ir.sooall.poker.common.model.Player;
import ir.sooall.poker.common.model.ValueObjects;
import ir.sooall.poker.player.client.message.PokerMessageHeader;
import ir.sooall.poker.player.client.message.PokerRequest;
import ir.sooall.poker.player.client.message.PokerResponse;
import ir.sooall.poker.player.client.message.ResponseAction;

import java.util.UUID;

public interface PokerRegistrationProcessMessageMapper {
    class RegisterMessage2ObjectMapper implements Message2ObjectMapper<PokerRequest, Player> {
        @Override
        public Player map(PokerRequest message) {
            return new Player(new ValueObjects.PlayerId(UUID.fromString(message.getContent().get("PLAYER_ID")))
                , new ValueObjects.PlayerName(message.getContent().get("PLAYER_NAME"))
                , new ValueObjects.NetAddress(message.getContent().get("PLAYER_IP"), Integer.valueOf(message.getContent().get("PLAYER_PORT")))
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
                .addItem("PLAYER_ID",obj.id().value().toString())
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
                .addItem("ERROR_CODE", "409")
                .addItem("ERROR_MESSAGE", "Duplicate Resource")
                .build();
        }
    }
}
