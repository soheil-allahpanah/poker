package ir.sooall.poker.player.client.message;

import java.util.Arrays;
import java.util.Objects;

public enum RequestAction {
    REGISTER("REGISTER");
    private final String constant;

    RequestAction(String constant) {
        this.constant = constant;
    }

    public String constant() {
        return constant;
    }

    public static RequestAction fromConstant(String constant) {
        return Arrays.stream(RequestAction.values())
            .filter(a -> Objects.equals(a.constant, constant)).findFirst().get();
    }
}
