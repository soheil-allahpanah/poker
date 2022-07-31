package ir.sooall.poker.common.message;



import java.util.UUID;

public final class PokerRegisterMessage implements PokerMessage {

    private final String protocolName;
    private final String version;
    private final String action;
    private UUID playerId;
    private String playerName;
    private String playerHost;
    private String playerPort;

    private PokerRegisterMessage(String protocolName, String version, String action) {
        this.protocolName = protocolName;
        this.version = version;
        this.action = action;
    }

    public static PokerRegisterMessage init(String protocolName, String version, String action, String content) {
        PokerRegisterMessage registerMessage = new PokerRegisterMessage(protocolName, version, action);
        String[] contentValues = content.split("\n\r");
        assert contentValues.length == 3;
        registerMessage.playerId = UUID.fromString(contentValues[0]);
        registerMessage.playerName = contentValues[1];
        registerMessage.playerHost = contentValues[2];
        registerMessage.playerPort = contentValues[3];
        return registerMessage;
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

    public String getPlayerHost() {
        return playerHost;
    }

    public String getPlayerPort() {
        return playerPort;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public String toString() {
        return protocolName + " " + version + " " + action + "\n\r"
            + playerId.toString() + "\n\r"
            + playerName + "\n\r"
            + playerHost + "\n\r"
            + playerPort + "\n\r"
            ;
    }
}
