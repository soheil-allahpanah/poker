package ir.sooall.poker.player.client;

import java.util.LinkedHashSet;
import java.util.Set;

final class HandlerEntry<T> {

    final Class<? extends State<T>> state;
    private final Set<Receiver<T>> receivers = new LinkedHashSet<>();

    HandlerEntry(Class<? extends State<T>> state) {
        this.state = state;
    }

    void add(Receiver<T> r) {
        receivers.add(r);
    }

    void onEvent(State<T> state) {
        for (Receiver<T> r : receivers) {
            r.receive(state.get());
        }
    }
}
