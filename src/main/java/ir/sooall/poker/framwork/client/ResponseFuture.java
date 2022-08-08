package ir.sooall.poker.framwork.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.ReferenceCounted;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class ResponseFuture implements Comparable<ResponseFuture> {

    AtomicBoolean cancelled;
    final List<HandlerEntry<?>> handlers = new CopyOnWriteArrayList<>();
    final List<Receiver<State<?>>> any = new CopyOnWriteArrayList<>();
    private volatile ChannelFuture future;
    private final CountDownLatch latch = new CountDownLatch(1);
    private final ZonedDateTime start = ZonedDateTime.now();
    private Map<StateType, List<Object>> queuedToSend;
    private final EnumSet<StateType> seenStates = EnumSet.noneOf(StateType.class);


    public ResponseFuture(AtomicBoolean cancelled) {
        this.cancelled = cancelled;
    }

    public void addAllHandler(List<HandlerEntry<?>> handlers) {
        if (handlers != null) {
            this.handlers.addAll(handlers);
        }
    }

    public void addAllAny(List<Receiver<State<?>>> any) {
        if (any != null) {
            this.any.addAll(any);
        }
    }

    void setFuture(ChannelFuture fut) {
        future = fut;
    }

    void trigger() {
        latch.countDown();
    }

    private void sendQueued() {
        if (queuedToSend != null && future != null && future.channel().isWritable()) {
            List<Object> toSend = new LinkedList<>();
            Set<StateType> toRemove = EnumSet.noneOf(StateType.class);
            for (Map.Entry<StateType, List<Object>> e : queuedToSend.entrySet()) {
                if (seenStates.contains(e.getKey()) && !e.getValue().isEmpty()) {
                    toSend.addAll(e.getValue());
                    e.getValue().clear();
                    toRemove.add(e.getKey());
                }
            }
            for (StateType st : toRemove) {
                queuedToSend.remove(st);
            }
            if (!toSend.isEmpty()) {
                new SendObjs(toSend).operationComplete(future.channel().newSucceededFuture());
            }
        }
    }

    final class SendObjs implements ChannelFutureListener {

        private final Iterator<Object> objs;

        SendObjs(List<Object> objs) {
            this.objs = objs.iterator();
        }

        @Override
        public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) {
                if (objs.hasNext()) {
                    Object o = objs.next();
                    future = future.channel().writeAndFlush(o);
                    if (objs.hasNext()) {
                        future.addListener(this);
                    }
                }
            } else {
                event(new State.Error(future.cause()));
            }
        }
    }

    public ResponseFuture await(long l, TimeUnit tu) throws InterruptedException {
        latch.await(l, tu);
        return this;
    }

    void onTimeout(Duration dur) {
//        System.out.println("onTimeout");
        cancel(dur);
    }

    /**
     * Cancel the associated request. This will make a best-effort, but cannot
     * guarantee, that no state changes will be fired after the final Cancelled.
     *
     * @return true if it succeeded, false if it was already canceled
     */
    public boolean cancel() {
        return cancel(null);
    }

    boolean cancel(Duration forTimeout) {
        // We need to send the timeout event before setting the cancelled flag
        if (forTimeout != null && !cancelled.get()) {
            event(new State.Timeout(forTimeout));
        }
        boolean result = cancelled.compareAndSet(false, true);
        if (result) {
            try {
                ChannelFuture fut = future;
                if (fut != null) {
                    fut.cancel(true);
                }
                if (fut != null && fut.channel() != null && fut.channel().isOpen()) {
                    fut.channel().close();
                }
            } finally {
                if (forTimeout == null) {
                    event(new State.Cancelled());
                }
            }
            latch.countDown();
        }
        return result;
    }

    private volatile Throwable error;

    /**
     * If an error was encountered, throw it
     *
     * @return this
     * @throws Throwable a throwable
     */
    public ResponseFuture throwIfError() throws Throwable {
        if (error != null) {
            throw error;
        }
        return this;
    }

    public final StateType lastState() {
        return lastState.get();
    }

    private final AtomicReference<StateType> lastState = new AtomicReference<>();

    @SuppressWarnings("unchecked")
    <T> void event(State<T> state) {
        if (state.stateType().isFailure()) {
            queuedToSend = null;
        }
        seenStates.add(state.stateType());
        lastState.set(state.stateType());
        if (state.get() instanceof ReferenceCounted) {
            ((ReferenceCounted) state.get()).touch("response-future-state-" + state.name());
        }
        try {
            if (state instanceof State.Error) {
                error = ((State.Error) state).get();
            }
            for (HandlerEntry<?> h : handlers) {
                if (h.state.isInstance(state)) {
                    HandlerEntry<T> hh = (HandlerEntry<T>) h;
                    hh.onEvent(state);
                }
            }
            for (Receiver<State<?>> r : any) {
                r.receive(state);
            }
        } finally {
            if (state instanceof State.Closed) {
                latch.countDown();
            }
            sendQueued();
        }
    }

    public ResponseFuture onAnyEvent(Receiver<State<?>> r) {
        any.add(r);
        return this;
    }

    boolean has(Class<? extends State<?>> state) {
        if (!any.isEmpty()) {
            return true;
        }
        for (HandlerEntry<?> h : handlers) {
            if (state == h.state) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> ResponseFuture on(StateType state, Receiver<T> receiver) {
        StateType s = this.lastState.get();
        if (s == StateType.Closed && state == StateType.Closed) {
            receiver.receive(null);
        }
        Class<? extends State<T>> type = (Class<? extends State<T>>) state.type();
        return on(type, state.wrapperReceiver(receiver));
    }

    @SuppressWarnings("unchecked")
    public <T> ResponseFuture on(Class<? extends State<T>> state, Receiver<T> receiver) {
        HandlerEntry<T> handler = null;
        for (HandlerEntry<?> h : handlers) {
            if (state.equals(h.state)) {
                handler = (HandlerEntry<T>) h;
                break;
            }
        }
        if (handler == null) {
            handler = new HandlerEntry<>(state);
            handlers.add(handler);
        }
        handler.add(receiver);
        return this;
    }

    @Override
    public int compareTo(ResponseFuture t) {
        ZonedDateTime mine = this.start;
        ZonedDateTime other = t.start;
        return mine.compareTo(other);
    }
}