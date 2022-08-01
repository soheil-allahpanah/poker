package ir.sooall.poker.player.client;

public enum StateType {
    Connecting,
    Connected,
    SendRequest,
    AwaitingResponse,
    ContentReceived,
    Closed,
    Finished,
    Error,
    Timeout,
    Cancelled;

    public boolean isResponseComplete() {
        return switch (this) {
            case AwaitingResponse, Connected, Connecting, ContentReceived, SendRequest -> false;
            default -> true;
        };
    }

    public boolean isFailure() {
        return switch (this) {
            case Cancelled, Closed, Error, Timeout -> true;
            default -> false;
        };
    }

    <T> Receiver<T> wrapperReceiver(final Receiver<?> orig) {
        return new Receiver<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public void receive(T object) {
                Receiver r = orig;
                try {
                    r.receive(object);
                } catch (ClassCastException e) {
                    orig.receive(null);
                }
            }
        };
    }

    public Class<? extends State<?>> type() {
        return switch (this) {
            case Connecting -> State.Connecting.class;
            case Connected -> State.Connected.class;
            case SendRequest -> State.SendRequest.class;
            case AwaitingResponse -> State.AwaitingResponse.class;
            case ContentReceived -> State.ContentReceived.class;
            case Closed -> State.Closed.class;
            case Finished -> State.Finished.class;
            case Error -> State.Error.class;
            case Cancelled -> State.Cancelled.class;
            case Timeout -> State.Timeout.class;
        };
    }

}
