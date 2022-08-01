package ir.sooall.poker.player.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface Callback<T> extends BiConsumer<Throwable, T> {

    void receive(Throwable err, T obj);

    @Override
    default void accept(Throwable err, T obj) {
        receive(err, obj);
    }

    static <T> Callback<T> fromBiConsumer(BiConsumer<Throwable, T> cons) {
        return cons::accept;
    }

    default Callback<T> attachTo(CompletionStage<T> fut) {
        fut.whenComplete((T t, Throwable u) -> {
            receive(u, t);
        });
        return this;
    }

    static <T> Callback<T> fromCompletableFuture(CompletableFuture<T> fut) {
        return (Throwable err, T obj) -> {
            if (err != null) {
                fut.completeExceptionally(err);
            } else {
                fut.complete(obj);
            }
        };
    }

    static <T> Callback<T> fromCompletableFuture(CompletableFuture<T> fut, Supplier<T> ifNullResult) {
        return (Throwable err, T obj) -> {
            if (err != null) {
                fut.completeExceptionally(err);
            } else {
                if (obj == null) {
                    try {
                        fut.complete(ifNullResult.get());
                    } catch (Exception ex) {
                        fut.completeExceptionally(ex);
                    }
                } else {
                    fut.complete(obj);
                }
            }
        };
    }
}