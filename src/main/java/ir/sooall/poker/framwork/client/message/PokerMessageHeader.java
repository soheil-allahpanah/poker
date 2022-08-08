package ir.sooall.poker.framwork.client.message;

public abstract class PokerMessageHeader {

    public final static String POKER_PROTOCOL_NAME = "POKER";
    public final static String POKER_PROTOCOL_VERSION = "1.0";
    private Integer contentLength;
    private String protocolName;
    private String protocolVersion;

    void updateContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }

    Integer getContentLength() {
        return contentLength;
    }

    String getProtocolName() {
        return protocolName;
    }

    String getProtocolVersion() {
        return protocolVersion;
    }

    PokerMessageHeader(Builder<?> builder) {
        this.protocolName = builder.protocolName;
        this.protocolVersion = builder.protocolVersion;
    }

    public static abstract class Builder<T extends Builder<T>> {
        private String protocolName;
        private String protocolVersion;

        public T protocolName(String protocolName) {
            this.protocolName = protocolName;
            return self();
        }

        public T protocolVersion(String protocolVersion) {
            this.protocolVersion = protocolVersion;
            return self();
        }

        abstract T self();

        abstract PokerMessageHeader build();

    }

}
