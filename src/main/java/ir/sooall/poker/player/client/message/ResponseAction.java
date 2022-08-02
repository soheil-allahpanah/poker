package ir.sooall.poker.player.client.message;

public enum ResponseAction {
    ACK_REGISTER("ACK-REGISTER"),

    NACK_REGISTER("NACK-REGISTER");

    private final String constant;

    ResponseAction(String constant) {
        this.constant = constant;
    }

    public String constant() {
        return constant;
    }
}
