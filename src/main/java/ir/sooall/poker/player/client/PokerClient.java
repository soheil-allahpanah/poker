package ir.sooall.poker.player.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class PokerClient {

    private Bootstrap bootstrap;
    private final NioEventLoopGroup group;
    private final NioChannelFactory channelFactory = new NioChannelFactory(Boolean.getBoolean("httpclient.debug"));
    private final Duration timeout;
    private final Iterable<ChannelOptionSetting<?>> settings;

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
            bootstrap.handler(new CoordinatorClientInitializer(ipAndPort, new CoordinatorClientHandler()));
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

    public static PokerClientBuilder builder() {
        return new PokerClientBuilder();
    }

    public PokerRequestBuilder register() {
        return new PRB(PokerAction.REGISTER);
    }

    private static class PRB implements PokerRequestBuilder {

        private String protocolName;
        private String protocolVersion;
        private PokerAction action;
        private List<ContentLine> contentLineList;

        public PRB(PokerAction action) {
            this.action = action;
            this.initHeader();
        }

        @Override
        public PokerRequestBuilder initHeader() {
            protocolName = "POKER";
            protocolVersion = "1.0";
            return this;
        }

        @Override
        public PokerRequestBuilder setAction(PokerAction action) {
            this.action = action;
            return this;
        }

        @Override
        public PokerRequestBuilder addContentLine(ContentLine contentLine) {
            if (contentLineList == null) {
                contentLineList = new ArrayList<>();
            }
            contentLineList.add(contentLine);
            return null;
        }

        @Override
        public PokerRequest build() {
            return new PokerRequest(protocolName, protocolVersion, action, contentLineList);
        }


    }

}
