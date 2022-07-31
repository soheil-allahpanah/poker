package ir.sooall.poker.common.message;

import java.util.UUID;

public final class PokerAckRegisterMessage implements PokerMessage {
    private final String protocolName;
    private final String version;
    private final String action;
    private UUID playerId;

    private PokerAckRegisterMessage(String protocolName, String version, String action) {
        this.protocolName = protocolName;
        this.version = version;
        this.action = action;

    }

    public static PokerAckRegisterMessage init(String protocolName, String version, String action, String content) {
        var ackRegisterMessage = new PokerAckRegisterMessage(protocolName, version, action);
        String[] contentValues = content.split("\n\r");
        assert contentValues.length == 1;
        ackRegisterMessage.playerId = UUID.fromString(contentValues[0]);
        return ackRegisterMessage;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public String getVersion() {
        return version;
    }

    public String getAction() {
        return action;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public String toString() {
        return protocolName + " " + version + " " + action + "\n\r"
            + playerId + "\n\r";
    }
}
