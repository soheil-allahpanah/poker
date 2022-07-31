package ir.sooall.poker.player.client;

import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;


final class NioChannelFactory implements ChannelFactory<NioSocketChannel> {

    private final boolean debug;
    NioChannelFactory(boolean debug) {
        this.debug = debug;
    }

    @Override
    public NioSocketChannel newChannel() {
        try {
            return debug ? new DebugNioSocketChannel(SocketChannel.open()) : new NioSocketChannel(SocketChannel.open());
        } catch (IOException ioe) {
            return Exceptions.chuck(ioe);
        }
    }

    public Channel newChannel(EventLoop eventLoop) {
        return newChannel();
    }

    private static final class DebugNioSocketChannel extends NioSocketChannel {

        public DebugNioSocketChannel() {
        }

        public DebugNioSocketChannel(SelectorProvider provider) {
            super(provider);
        }

        public DebugNioSocketChannel(SocketChannel socket) {
            super(socket);
        }

        public DebugNioSocketChannel(Channel parent, SocketChannel socket) {
            super(parent, socket);
        }

        @Override
        protected void doClose() throws Exception {
            new Exception("client doClose").printStackTrace();
            super.doClose(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void doDeregister() throws Exception {
            new Exception("client doDeregister").printStackTrace();
            super.doDeregister(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ChannelFuture close(ChannelPromise promise) {
            new Exception("client Explicit close w/ promise").printStackTrace();
            return super.close(promise); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ChannelFuture close() {
            new Exception("client Explicit close").printStackTrace();
            return super.close(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ChannelFuture disconnect() {
            new Exception("client Explicit disconnect").printStackTrace();
            return super.disconnect(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void doDisconnect() throws Exception {
            new Exception("client doDisconnect").printStackTrace();
            super.doDisconnect(); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
