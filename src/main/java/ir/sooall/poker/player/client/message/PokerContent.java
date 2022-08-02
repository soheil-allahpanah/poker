package ir.sooall.poker.player.client.message;

import java.util.HashMap;

public class PokerContent<BuildType, Builder extends BuilderInterface<BuildType>> {
    private final HashMap<String, String> data = new HashMap<>();

    public HashMap<String, String> getData() {
        return data;
    }

    private final Builder builder;

    PokerContent(Builder builder) {
        this.builder = builder;
    }

    public BuildType build() {
        return builder.build();
    }

    public PokerContent<BuildType, Builder> addItem(String key, String value) {
        data.put(key, value);
        return this;
    }
}
