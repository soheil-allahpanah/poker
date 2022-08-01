package ir.sooall.poker.player.client;

import java.time.Duration;

public interface PokerRequestBuilder {

    PokerRequestBuilder initHeader();

    PokerRequestBuilder action(PokerAction action);

    PokerRequestBuilder ipAndPort(IpAndPort ipAndPort);

    PokerRequestBuilder timeout(Duration timeout);

    PokerRequestBuilder addContentLine(ContentLine contentLIie);

    <T> PokerRequestBuilder on(Class<? extends State<T>> event, Receiver<T> r);

    <T> PokerRequestBuilder on(StateType event, Receiver<T> r);

    PokerRequestBuilder onEvent(Receiver<State<?>> r);

    PokerRequest build();

    ResponseFuture execute(ResponseHandler<?> r);

    ResponseFuture execute();
}
