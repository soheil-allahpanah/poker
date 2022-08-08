package ir.sooall.poker.framwork.client.message;

import java.util.Arrays;
import java.util.Objects;

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

    public static ResponseAction fromConstant(String constant) {
        return Arrays.stream(ResponseAction.values())
            .filter(a -> Objects.equals(a.constant, constant)).findFirst().get();
    }
}
