package ir.sooall.poker.player.client.message;

import ir.sooall.poker.player.client.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class PokerRequest implements PokerObject {

    public static class Message {
        private PokerRequestHeader header;
        private PokerContent<?, ?> content;

        public Message(PokerRequestHeader header, PokerContent<?, ?> content) {
            this.header = header;
            this.content = content;
        }

        public PokerRequestHeader getHeader() {
            return header;
        }

        public PokerContent<?, ?> getContent() {
            return content;
        }
    }

    private final Message message;
    private final List<HandlerEntry<?>> handlers;
    private final List<Receiver<State<?>>> any;
    private final IpAndPort ipAndPort;
    private final Duration timeout;

    public RequestAction action() {
        return message.header.action();
    }

    public Integer contentLength() {
        return message.header.getContentLength();
    }

    public String protocolName() {
        return message.header.getProtocolName();
    }

    public String protocolVersion() {
        return message.header.getProtocolName();
    }

    public HashMap<String, String> content() {
        return message.content.getData();
    }

    public List<HandlerEntry<?>> handlers() {
        return handlers;
    }

    public List<Receiver<State<?>>> any() {
        return any;
    }

    public IpAndPort ipAndPort() {
        return ipAndPort;
    }

    public Duration timeout() {
        return timeout;
    }

    public static Builder builder(PokerClient pokerClient) {
        return new Builder(pokerClient);
    }

    PokerRequest(PokerRequestHeader header
        , PokerContent<?, ?> content
        , List<HandlerEntry<?>> handlers
        , List<Receiver<State<?>>> any
        , IpAndPort ipAndPort
        , Duration timeout) {
        this.handlers = handlers;
        this.any = any;
        this.ipAndPort = ipAndPort;
        this.timeout = timeout;
        message = new Message(header, content);
    }

    public static class Builder implements BuilderInterface<PokerRequest> {

        private final PokerRequestHeader.Builder<PokerContent<PokerRequest, Builder>> headerBuilder;
        private final PokerContent<PokerRequest, Builder> content;
        private List<HandlerEntry<?>> handlers;
        private List<Receiver<State<?>>> any;
        private IpAndPort ipAndPort;
        private Duration timeout;
        private final PokerClient pokerClient;

        public Builder(PokerClient pokerClient) {
            content = new PokerContent<>(this);
            headerBuilder = new PokerRequestHeader.Builder<>(content);
            this.pokerClient = pokerClient;
        }

        public PokerRequestHeader.Builder<PokerContent<PokerRequest, Builder>> header() {
            return headerBuilder;
        }

        public Builder ipAndPort(String ip, int port) {
            this.ipAndPort = new IpAndPort(ip, port);
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> Builder on(Class<? extends State<T>> event, Receiver<T> r) {
            HandlerEntry<T> h = null;
            for (HandlerEntry<?> e : handlers) {
                if (e.state().equals(event)) {
                    h = (HandlerEntry<T>) e;
                    break;
                }
            }
            if (h == null) {
                h = new HandlerEntry<>(event);
                handlers.add(h);
            }
            h.add(r);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> Builder on(StateType event, Receiver<T> r) {
            this.on((Class<? extends State<T>>) event.type(), event.wrapperReceiver(r));
            return this;
        }

        public Builder onEvent(Receiver<State<?>> r) {
            any.add(r);
            return this;
        }

        public PokerRequest build() {
            var request = new PokerRequest(headerBuilder.build(), content, handlers, any, ipAndPort, timeout);
            request.message.header.updateContentLength(request.toString().getBytes().length);
            return request;
        }

        public PokerRequest.Message buildMessage() {
            var request = new PokerRequest(headerBuilder.build(), content, handlers, any, ipAndPort, timeout);
            request.message.header.updateContentLength(request.toString().getBytes().length);
            return request.message;
        }

        public ResponseFuture execute(ResponseHandler<?> r) {
            PokerRequest req = build();
            AtomicBoolean cancelled = new AtomicBoolean();
            ResponseFuture handle = new ResponseFuture(cancelled);
            handle.addAllHandler(this.handlers);
            handle.addAllAny(this.any);
            pokerClient.submit(req, cancelled, handle, r, timeout);
            return handle;
        }

        public ResponseFuture execute() {
            return execute(null);
        }

    }

    public static PokerRequest fromString(String str) {
        String[] lines = str.split("\n\r");
        var builder = new Builder(null);
        var headers = lines[1].split(" ");

        var protocolName = headers[0];
        var protocolVersion = headers[1];
        var action = headers[2];
        var contentBuilder = builder.header()
            .protocolName(protocolName)
            .protocolVersion(protocolVersion)
            .action(RequestAction.valueOf(action)).content();
        for (int i = 2; i < lines.length; i++) {
            var keyValue = lines[i].split(":");
            contentBuilder.addItem(keyValue[0], keyValue[1]);
        }
        return contentBuilder.build();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(message.header.getProtocolName()).append(" ")
            .append(message.header.getProtocolVersion()).append(" ")
            .append(message.header.action().constant()).append("\n\r");
        for (Map.Entry<String, String> entry : message.content.getData().entrySet()) {
            result.append(entry.getKey()).append(":").append(entry.getValue()).append("\n\r");
        }
        return result.toString().getBytes().length + "\n\r" + result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokerRequest that = (PokerRequest) o;
        return message.equals(that.message) && ipAndPort.equals(that.ipAndPort) && timeout.equals(that.timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, ipAndPort, timeout);
    }
}
