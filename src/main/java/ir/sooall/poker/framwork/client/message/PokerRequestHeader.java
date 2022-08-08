package ir.sooall.poker.framwork.client.message;

public class PokerRequestHeader extends PokerMessageHeader {
    private RequestAction action;

    public RequestAction action() {
        return action;
    }

    PokerRequestHeader(Builder<?> builder) {
        super(builder);
        this.action = builder.action;
    }

    public static class Builder<ContentBuilder> extends PokerMessageHeader.Builder<Builder<ContentBuilder>> {
        private RequestAction action;
        private final ContentBuilder contentBuilder;

        public Builder(ContentBuilder contentBuilder) {
            this.contentBuilder = contentBuilder;
        }

        public Builder<ContentBuilder> action(RequestAction action) {
            this.action = action;
            return self();
        }

        public ContentBuilder content() {
            return contentBuilder;
        }

        PokerRequestHeader build() {
            return new PokerRequestHeader(this);
        }

        Builder<ContentBuilder> self() {
            return this;
        }

    }
}
