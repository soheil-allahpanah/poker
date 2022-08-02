package ir.sooall.poker.player.client.message;

public class PokerResponseHeader extends PokerMessageHeader {
    private ResponseAction action;

    PokerResponseHeader(Builder<?> builder) {
        super(builder);
        this.action = builder.action;
    }

    public ResponseAction getAction() {
        return action;
    }

    public  static class Builder<ContentBuilder> extends PokerMessageHeader.Builder<Builder<ContentBuilder>>  {
        private ResponseAction action;
        private final ContentBuilder contentBuilder;

        public Builder(ContentBuilder contentBuilder) {
            this.contentBuilder = contentBuilder;
        }

        public Builder<ContentBuilder> action(ResponseAction action) {
            this.action = action;
            return self();
        }

        public ContentBuilder content() {
            return contentBuilder;
        }

        PokerResponseHeader build() {
            return new PokerResponseHeader(this);
        }

        Builder<ContentBuilder> self() {
            return this;
        }
    }
}
