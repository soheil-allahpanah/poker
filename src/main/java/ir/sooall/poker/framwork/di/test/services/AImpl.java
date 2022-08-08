package ir.sooall.poker.framwork.di.test.services;

import ir.sooall.poker.framwork.di.test.A;
import ir.sooall.poker.framwork.di.test.B;

public class AImpl implements A {
    public final B b;

    public AImpl(B b) {
        this.b = b;
    }
}
