package ir.sooall.poker.framwork.client;

public enum PokerAction {
    REGISTER("REGISTER"),
    ACK_REGISTER("ACK-REGISTER"),
    NACK_REGISTER("NACK-REGISTER");

    private final String valueStr;

    PokerAction(String valueStr) {
        this.valueStr = valueStr;
    }

    public String valueStr() {
        return valueStr;
    }
}
