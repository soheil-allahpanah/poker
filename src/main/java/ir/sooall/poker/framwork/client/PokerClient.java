package ir.sooall.poker.framwork.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.AttributeKey;
import ir.sooall.poker.framwork.client.message.PokerRequest;
import ir.sooall.poker.framwork.client.message.RequestAction;

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

    private synchronized Bootstrap start() {
        if (bootstrap == null) {
            bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.handler(new CoordinatorClientInitializer());
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

    public PokerRequest.Builder register() {
        var pr = PokerRequest.builder(this);
        pr.header().action(RequestAction.REGISTER);
        return pr;
    }

    public void submit(PokerRequest rq, final AtomicBoolean cancelled, final ResponseFuture handle,
                        final ResponseHandler<?> r, Duration timeout) {
        // Ensure the cancelled event is sent
        System.out.println("Poker Client >> submit >> ");
        if (cancelled.get()) {
            handle.event(new State.Cancelled());
            return;
        }
        // Assign a reference to the channel as soon as it is available,
        // so that we can close it in case of an exception
        final AtomicReference<Channel> theChannel = new AtomicReference<>();

        final PokerRequest req = rq;
        try {
            Bootstrap bootstrap = start();
            System.out.println("Poker Client >> submit >> bootstrap is given" + bootstrap);
            TimeoutTimerTask timerTask = null;
            RequestInfo info = new RequestInfo(req, cancelled, handle, r, timeout, null);
            System.out.println("Poker Client >> submit >> bootstrap is created");

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
            System.out.println("Poker Client >> submit >> handle connecting");

            //XXX who is escaping this?
            ChannelFuture fut = bootstrap.connect(req.ipAndPort().ip(), req.ipAndPort().port());
            theChannel.set(fut.channel());
            System.out.println("Poker Client >> submit >> bootstrap connected");
            if (timerTask != null) {
                fut.channel().closeFuture().addListener(timerTask);
            }
            fut.channel().attr(KEY).set(info);
            System.out.println("Poker Client >> submit >> KEY attribute is set");

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
                    System.out.println("Poker Client >> submit >> fut listener >> failed");

                    Throwable cause = future.cause();
                    if (cause == null) {
                        cause = new ConnectException("Unknown problem connecting to " + req.ipAndPort().ip() + ":" + req.ipAndPort().port());
                    }
                    handle.event(new State.Error(cause));
                    cancelled.set(true);
                }
                if (cancelled.get()) {
                    System.out.println("Poker Client >> submit >> fut listener >> canceled");

                    future.cancel(true);
                    if (future.channel().isOpen()) {
                        future.channel().close();
                    }
                    return;
                }

                handle.event(new State.Connected(future.channel()));
                System.out.println("Poker Client >> submit >> fut listener >> connected");
                handle.event(new State.SendRequest(req));
                System.out.println("Poker Client >> submit >> fut listener >> sent");
//                var message = req.toString();
                future = future.channel().writeAndFlush(req);
                System.out.println("Poker Client >> submit >> fut listener >> wrote and flushed >> req : "+ req);
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


}
