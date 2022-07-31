package ir.sooall.poker.player.client;

public record ContentLine(String key, String value) {
    @Override
    public String toString() {
        return key + ":" + value + "\n\r";
    }
}
