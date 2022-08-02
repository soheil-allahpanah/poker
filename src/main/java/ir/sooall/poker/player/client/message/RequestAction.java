package ir.sooall.poker.player.client.message;

public enum RequestAction {
    REGISTER("REGISTER");
    private final String constant;

    RequestAction(String constant) {
        this.constant = constant;
    }

    public String constant() {
        return constant;
    }
}
