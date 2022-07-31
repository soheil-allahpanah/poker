package ir.sooall.poker.player.client;

public interface PokerRequestBuilder {

    PokerRequestBuilder initHeader();

    PokerRequestBuilder setAction(PokerAction action);

    PokerRequestBuilder addContentLine(ContentLine contentLIie);

    PokerRequest build();

}
