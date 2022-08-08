package ir.sooall.poker.framwork.client;

import java.util.function.Consumer;

public abstract class Receiver<T> implements Consumer<T> {

    public abstract void receive(T object);

    public final void accept(T object) {
        receive(object);
    }

    public <E extends Throwable> void onFail(E exception) throws E {
        throw exception;
    }

    public void onFail() {
        //do nothing
        System.err.println(this + " failed");
    }

    public static <T> Receiver<T> of(Callback<T> callback) {
        return new CallbackReceiver<>(callback);
    }

    public static <T> Receiver<T> of(Consumer<T> cons) {
        return new Receiver<T>() {
            @Override
            public void receive(T object) {
                cons.accept(object);
            }
        };
    }

    private static final class CallbackReceiver<T> extends Receiver<T> {

        private final Callback<T> callback;

        public CallbackReceiver(Callback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void receive(T object) {
            callback.receive(null, object);
        }

        @Override
        public <E extends Throwable> void onFail(E exception) throws E {
            callback.receive(exception, null);
        }

        @Override
        public void onFail() {
            callback.receive(new Exception("Unknown failure"), null);
        }
    }
}
