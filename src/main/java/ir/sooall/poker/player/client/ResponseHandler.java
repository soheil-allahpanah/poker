package ir.sooall.poker.player.client;

import ir.sooall.poker.player.client.message.PokerResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResponseHandler<T> {

    private final Class<T> type;
    private final CountDownLatch latch = new CountDownLatch(1);

    public ResponseHandler(Class<T> type) {
        this.type = type;
    }

    public void await() throws InterruptedException {
        latch.await();
    }

    public boolean await(long l, TimeUnit tu) throws InterruptedException {
        return latch.await(l, tu);
    }

    protected void internalReceive(PokerResponse pokerMessage) {
        try {
            _doReceive(pokerMessage);
            // todo : must be implemented
        } finally {
            latch.countDown();
        }
    }

    void _doReceive(PokerResponse pokerMessage) {
        receive(pokerMessage);
    }

    protected void receive(PokerResponse pokerMessage) {
    }

    protected void onErrorResponse(String content) {
    }

    protected void onError(Throwable err) {
//        err.printStackTrace();
    }

    public Class<T> type() {
        return type;
    }
}
