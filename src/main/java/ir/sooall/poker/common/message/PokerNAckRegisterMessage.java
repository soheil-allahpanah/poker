package ir.sooall.poker.common.message;

import java.util.UUID;

public final class PokerNAckRegisterMessage implements PokerMessage {
    private final String protocolName;
    private final String version;
    private final String action;
    private UUID offeredPlayerId;
    private String errorCode;
    private String errorMessage;

    private PokerNAckRegisterMessage(String protocolName, String version, String action) {
        this.protocolName = protocolName;
        this.version = version;
        this.action = action;

    }

    public static PokerNAckRegisterMessage init(String protocolName, String version, String action, String content) {
        var nAkRegisterMsg = new PokerNAckRegisterMessage(protocolName, version, action);
        String[] contentValues = content.split("\n\r");
        assert contentValues.length == 3;
        nAkRegisterMsg.offeredPlayerId = UUID.fromString(contentValues[0]);
        nAkRegisterMsg.errorCode = contentValues[1];
        nAkRegisterMsg.errorMessage = contentValues[2];
        return nAkRegisterMsg;
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

    public UUID getOfferedPlayerId() {
        return offeredPlayerId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
