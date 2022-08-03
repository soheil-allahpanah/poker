package ir.sooall.poker.player.client;

import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;

import java.time.Duration;
import java.util.*;

public class PokerClientBuilder {
    private static final int DEFAULT_THREAD_COUNT = 4;

    private NioEventLoopGroup group;
    private Duration timeout;
    private Integer threadCount = -1;

    private final List<PokerClient.ChannelOptionSetting<?>> settings = new LinkedList<>();

    public PokerClientBuilder threadCount(int count) {
        if (group != null) {
            throw new IllegalStateException("Cannot set threadCount if you are"
                + " providing the NioEventLoopGroup");
        }
        this.threadCount = count;
        return this;
    }

    public PokerClientBuilder eventLoopGroup(NioEventLoopGroup group) {
        if (threadCount != -1) {
            throw new IllegalStateException("Thread count already set. If you want to provide "
                + "your own NioEventLoopGroup, don't also set that - these options are "
                + "mutually exclusive.");
        }
        this.group = group;
        return this;
    }

    public PokerClientBuilder timeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    public <T> PokerClientBuilder channelOption(ChannelOption<T> option, T value) {
        settings.removeIf(setting -> setting.option().equals(option));
        settings.add(new PokerClient.ChannelOptionSetting<>(option, value));
        return this;
    }

    public PokerClient build() {
        return new PokerClient(group, settings, timeout, threadCount == -1 ? DEFAULT_THREAD_COUNT : threadCount);
    }
}
