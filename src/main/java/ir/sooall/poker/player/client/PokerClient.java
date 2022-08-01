package ir.sooall.poker.player.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.AttributeKey;

import java.net.ConnectException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PokerClient {

    private Bootstrap bootstrap;
    private final NioEventLoopGroup group;
    private final NioChannelFactory channelFactory = new NioChannelFactory(Boolean.getBoolean("httpclient.debug"));
    private final Duration timeout;
    private final Iterable<ChannelOptionSetting<?>> settings;
    private final Timer timer = new Timer("HttpClient timeout for HttpClient@" + System.identityHashCode(this));
    static final AttributeKey<RequestInfo> KEY = AttributeKey.<RequestInfo>valueOf("info");

    public PokerClient() {
        this(null, Collections.emptyList(), null, 12);
    }

    PokerClient(NioEventLoopGroup threadPool, Iterable<ChannelOptionSetting<?>> settings, Duration timeout, int threads) {
        group = threadPool == null ? new NioEventLoopGroup(threads, new TF()) : threadPool;
        this.timeout = timeout;
        this.settings = settings == null ? Collections.emptySet() : settings;
    }

    private synchronized Bootstrap start(IpAndPort ipAndPort) {
        if (bootstrap == null) {
            bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.handler(new CoordinatorClientInitializer(new CoordinatorClientHandler(this)));
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.option(ChannelOption.SO_REUSEADDR, false);
            if (timeout != null) {
                bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeout.toMillis());
            }
            for (ChannelOptionSetting<?> setting : settings) {
                setting.apply(bootstrap);
            }
            bootstrap.channelFactory(channelFactory);
        }
        return bootstrap;
    }

    private static class TF implements ThreadFactory {

        private int threadsCreated = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "PokerClient event loop " + ++threadsCreated);
            t.setDaemon(true);
            return t;
        }
    }

    protected static final class ChannelOptionSetting<T> {

        private final ChannelOption<T> option;
        private final T value;

        ChannelOptionSetting(ChannelOption<T> option, T value) {
            this.option = option;
            this.value = value;
        }

        public ChannelOption<T> option() {
            return option;
        }

        public T value() {
            return value;
        }

        void apply(Bootstrap bootstrap) {
            bootstrap.option(option, value);
        }
    }

    static final class TimeoutTimerTask extends TimerTask implements ChannelFutureListener {

        private final AtomicBoolean cancelled;
        private final ResponseFuture handle;
        private final ResponseHandler<?> r;
        private final RequestInfo in;

        TimeoutTimerTask(AtomicBoolean cancelled, ResponseFuture handle, ResponseHandler<?> r, RequestInfo in) {
            this.cancelled = cancelled;
            this.handle = handle;
            this.r = r;
            this.in = in;
        }

        @Override
        public void run() {
            if (!cancelled.get()) {
                if (r != null) {
                    r.onError(new NoStackTimeoutException(in.timeout.toString()));
                }
                if (handle != null) {
                    handle.onTimeout(in.age());
                }
            }
            super.cancel();
        }

        @Override
        public void operationComplete(ChannelFuture f) throws Exception {
            cancelled.set(true);
            super.cancel();
        }
    }

    private static class NoStackTimeoutException extends TimeoutException {
        NoStackTimeoutException(String msg) {
            super(msg);
        }

        @Override
        public StackTraceElement[] getStackTrace() {
            return new StackTraceElement[0];
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    public static PokerClientBuilder builder() {
        return new PokerClientBuilder();
    }

    public PokerRequestBuilder register() {
        return new PRB(PokerAction.REGISTER);
    }

    private void submit(PokerRequest rq, final AtomicBoolean cancelled, final ResponseFuture handle,
                        final ResponseHandler<?> r, Duration timeout) {
        // Ensure the cancelled event is sent
        if (cancelled.get()) {
            handle.event(new State.Cancelled());
            return;
        }
        // Assign a reference to the channel as soon as it is available,
        // so that we can close it in case of an exception
        final AtomicReference<Channel> theChannel = new AtomicReference<>();

        final PokerRequest req = rq;
        try {
            Bootstrap bootstrap = start(req.ipAndPort());
            TimeoutTimerTask timerTask = null;
            RequestInfo info = new RequestInfo(req, cancelled, handle, r, timeout, null);
            if (timeout != null) {
                timerTask = new TimeoutTimerTask(cancelled, handle, r, info);
                timer.schedule(timerTask, timeout.toMillis());
            }
            info.timer = timerTask;
            if (info.isExpired()) {
                handle.event(new State.Timeout(info.age()));
                return;
            }
            handle.event(new State.Connecting());
            //XXX who is escaping this?
            ChannelFuture fut = bootstrap.connect(req.ipAndPort().ip(), req.ipAndPort().port());
            theChannel.set(fut.channel());
            if (timerTask != null) {
                fut.channel().closeFuture().addListener(timerTask);
            }
            fut.channel().attr(KEY).set(info);
            handle.setFuture(fut);
            if (r != null) {
                handle.on(State.Error.class, new Receiver<>() {
                    @Override
                    public void receive(Throwable object) {
                        r.onError(object);
                    }
                });
                handle.on(StateType.Cancelled, new Receiver<Void>() {
                    @Override
                    public void receive(Void object) {
                        r.onError(new CancellationException("Cancelled"));
                    }
                });
            }

            fut.addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    if (cause == null) {
                        cause = new ConnectException("Unknown problem connecting to " + req.ipAndPort().ip() + ":" + req.ipAndPort().port());
                    }
                    handle.event(new State.Error(cause));
                    cancelled.set(true);
                }
                if (cancelled.get()) {
                    future.cancel(true);
                    if (future.channel().isOpen()) {
                        future.channel().close();
                    }
                    return;
                }
                handle.event(new State.Connected(future.channel()));
                handle.event(new State.SendRequest(req));
                future = future.channel().writeAndFlush(req);
                future.addListener((ChannelFutureListener) future1 -> {
                    if (cancelled.get()) {
                        future1.cancel(true);
                        future1.channel().close();
                    }
                    handle.event(new State.AwaitingResponse());
                });
            });
        } catch (Exception ex) {
            Channel ch = theChannel.get();
            cancelled.set(true);
            if (ch != null && ch.isRegistered() && ch.isOpen()) {
                ch.close();
            }
            Exceptions.chuck(ex);
        }
    }


    private class PRB implements PokerRequestBuilder {

        private final List<HandlerEntry<?>> handlers = new LinkedList<>();
        private final List<Receiver<State<?>>> any = new LinkedList<>();

        private String protocolName;
        private String protocolVersion;
        private PokerAction action;
        private List<ContentLine> contentLineList;
        private IpAndPort ipAndPort;
        private Duration timeout;


        public PRB(PokerAction action) {
            this.action = action;
            this.initHeader();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> PokerRequestBuilder on(Class<? extends State<T>> event, Receiver<T> r) {
            HandlerEntry<T> h = null;
            for (HandlerEntry<?> e : handlers) {
                if (e.state.equals(event)) {
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

        @Override
        @SuppressWarnings("unchecked")
        public <T> PokerRequestBuilder on(StateType event, Receiver<T> r) {
            this.on((Class<? extends State<T>>) event.type(), event.wrapperReceiver(r));
            return this;
        }

        @Override
        public PokerRequestBuilder onEvent(Receiver<State<?>> r) {
            any.add(r);
            return this;
        }

        @Override
        public PokerRequestBuilder initHeader() {
            protocolName = "POKER";
            protocolVersion = "1.0";
            return this;
        }

        @Override
        public PokerRequestBuilder action(PokerAction action) {
            this.action = action;
            return this;
        }

        @Override
        public PokerRequestBuilder addContentLine(ContentLine contentLine) {
            if (contentLineList == null) {
                contentLineList = new ArrayList<>();
            }
            contentLineList.add(contentLine);
            return this;
        }

        @Override
        public PokerRequestBuilder ipAndPort(IpAndPort ipAndPort) {
            this.ipAndPort = ipAndPort;
            return this;
        }

        @Override
        public PokerRequestBuilder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public PokerRequest build() {
            return new PokerRequest(ipAndPort, protocolName, protocolVersion, action, contentLineList);
        }

        @Override
        public ResponseFuture execute(ResponseHandler<?> r) {
            PokerRequest req = build();
            AtomicBoolean cancelled = new AtomicBoolean();
            ResponseFuture handle = new ResponseFuture(cancelled);
            handle.handlers.addAll(this.handlers);
            handle.any.addAll(this.any);
            submit(req, cancelled, handle, r, timeout);
            return handle;
        }

        @Override
        public ResponseFuture execute() {
            return execute(null);
        }

    }

}
