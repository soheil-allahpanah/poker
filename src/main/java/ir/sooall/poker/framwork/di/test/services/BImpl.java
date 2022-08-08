package ir.sooall.poker.framwork.di.test.services;

import ir.sooall.poker.framwork.di.test.A;
import ir.sooall.poker.framwork.di.test.B;
import ir.sooall.poker.framwork.di.test.C;
import ir.sooall.poker.framwork.di.test.D;

public class BImpl implements B {
    public final C c;
    public final D d;

    public BImpl(C c, D d) {
        this.c = c;
        this.d = d;
    }
}
