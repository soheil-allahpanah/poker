package ir.sooall.poker.framwork.client;

import io.netty.channel.Channel;
import ir.sooall.poker.framwork.message.PokerRequest;
import ir.sooall.poker.framwork.message.PokerResponse;

import java.time.Duration;

public abstract class State<T> {

    /**
     * State event when a connection is being attempted.  No payload.
     */
    static final class Connecting extends State<Void> {

        Connecting() {
            super(Void.class, StateType.Connecting, null);
        }
    }

    /**
     * State event when a request has been sent, before the response header
     * has completely arrived.  No payload.
     */
    static final class AwaitingResponse extends State<Void> {

        AwaitingResponse() {
            super(Void.class, StateType.AwaitingResponse, null);
        }
    }

    /**
     * State event when a connection has been achieved;  payload is the
     * Channel;  invoking close() on it will abort.
     */
    public static final class Connected extends State<Channel> {

        Connected(Channel channel) {
            super(Channel.class, StateType.Connected, channel);
        }
    }
    /**
     * State event when the HTTP request is about to be sent;  payload
     * is the HTTP request (you can still modify headers, etc at this point).
     */
    public static final class SendRequest extends State<PokerRequest> {

        SendRequest(PokerRequest req) {
            super(PokerRequest.class, StateType.SendRequest, req);
        }
    }


    /**
     * State event triggered when one chunk of content has arrived;  if the
     * server is using chunked transfer encoding, this state will be fired
     * once for each chunk;  when the FullContentReceived event is fired,
     * there will be no more ContentReceived events.
     */
    public static final class ContentReceived extends State<PokerResponse> {

        ContentReceived(PokerResponse message) {
            super(PokerResponse.class, StateType.ContentReceived, message);
        }
    }

    /**
     * Final state event triggered when the channel is unregistered.
     */
    static final class Closed extends State<Void> {

        Closed() {
            super(Void.class, StateType.Closed, null);
        }
    }

    /**
     * Convenience state event providing the entire response and its body
     * as a FullHttpResponse.
     */
    public static final class Finished extends State<PokerResponse> {

        Finished(PokerResponse message) {
            super(PokerResponse.class, StateType.Finished, message);
        }
    }

    /**
     * State event triggered when an exception is thrown somewhere in
     * processing of the request or response.  Does not indicate that processing
     * is aborted (close the channel for that), or that further errors will
     * not be thrown.
     */
    public static final class Error extends State<Throwable> {

        Error(Throwable t) {
            super(Throwable.class, StateType.Error, t);
        }
    }

    /**
     * State event triggered when a timeout occurs.
     */
    public static final class Timeout extends State<Duration> {
        Timeout(Duration d) {
            super(Duration.class, StateType.Timeout, d);
        }
    }

    /**
     * State event triggered by someone invoking cancel() on the ResponseFuture
     * for this request.
     */
    static final class Cancelled extends State<Void> {

        Cancelled() {
            super(Void.class, StateType.Cancelled, null);
        }
    }

    private final Class<T> type;
    private final StateType name;
    private final T state;

    State(Class<T> type, StateType name, T state) {
        this.type = type;
        this.name = name;
        this.state = state;
    }

    public Class<T> type() {
        return type;
    }

    public String name() {
        return name.name();
    }

    public StateType stateType() {
        return name;
    }

    public T get() {
        return state;
    }

    @Override
    public String toString() {
        return name();
    }
}
