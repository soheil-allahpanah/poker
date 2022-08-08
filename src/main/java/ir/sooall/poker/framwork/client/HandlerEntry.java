package ir.sooall.poker.framwork.client;

import java.util.LinkedHashSet;
import java.util.Set;

public final class HandlerEntry<T> {

    final Class<? extends State<T>> state;

    public Class<? extends State<T>> state() {
        return state;
    }

    private final Set<Receiver<T>> receivers = new LinkedHashSet<>();

    public HandlerEntry(Class<? extends State<T>> state) {
        this.state = state;
    }

    public void add(Receiver<T> r) {
        receivers.add(r);
    }

    public void onEvent(State<T> state) {
        for (Receiver<T> r : receivers) {
            r.receive(state.get());
        }
    }
}
