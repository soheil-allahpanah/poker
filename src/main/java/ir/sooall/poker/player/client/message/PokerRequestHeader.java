package ir.sooall.poker.player.client.message;

class PokerRequestHeader extends PokerMessageHeader {
    private RequestAction action;

    RequestAction getAction() {
        return action;
    }

    PokerRequestHeader(Builder<?> builder) {
        super(builder);
        this.action = builder.action;
    }

    static class Builder<ContentBuilder> extends PokerMessageHeader.Builder<Builder<ContentBuilder>> {
        private RequestAction action;
        private final ContentBuilder contentBuilder;

        public Builder(ContentBuilder contentBuilder) {
            this.contentBuilder = contentBuilder;
        }

        Builder<ContentBuilder> action(RequestAction action) {
            this.action = action;
            return self();
        }

        ContentBuilder content() {
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
