package ir.sooall.poker.framwork.client;

import ir.sooall.poker.framwork.message.PokerRequest;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestInfo {
    final PokerRequest req;
    final AtomicBoolean cancelled;
    final ResponseFuture handle;
    final ResponseHandler<?> r;
    final Duration timeout;
    final ZonedDateTime startTime;
    TimerTask timer;

    RequestInfo(PokerRequest req, AtomicBoolean cancelled, ResponseFuture handle, ResponseHandler<?> r,
                Duration timeout, ZonedDateTime startTime, TimerTask timer) {
        this.req = req;
        this.cancelled = cancelled;
        this.handle = handle;
        this.r = r;
        this.timeout = timeout;
        this.startTime = startTime;
        this.timer = timer;
    }

    RequestInfo(PokerRequest req, AtomicBoolean cancelled, ResponseFuture handle, ResponseHandler<?> r, Duration timeout, TimerTask timer) {
        this(req, cancelled, handle, r, timeout, ZonedDateTime.now(), timer);
    }

    Duration age() {
        return Duration.between(startTime, ZonedDateTime.now());
    }

    Duration remaining() {
        return timeout == null ? null : timeout.minus(age());
    }

    boolean isExpired() {
        if (timeout != null) {
            return ZonedDateTime.now().isAfter(startTime.plus(timeout));
        }
        return false;
    }

    void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public String toString() {
        return "RequestInfo{ req=" + req + ", cancelled="
            + cancelled + ", handle=" + handle + ", r=" + r + ", timeout=" + timeout + '}';
    }
}
