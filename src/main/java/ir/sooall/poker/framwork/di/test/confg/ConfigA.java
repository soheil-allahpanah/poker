package ir.sooall.poker.framwork.di.test.confg;

import ir.sooall.poker.framwork.di.annotations.Bean;
import ir.sooall.poker.framwork.di.annotations.Configuration;
import ir.sooall.poker.framwork.di.test.A;
import ir.sooall.poker.framwork.di.test.B;
import ir.sooall.poker.framwork.di.test.C;
import ir.sooall.poker.framwork.di.test.D;
import ir.sooall.poker.framwork.di.test.services.AImpl;
import ir.sooall.poker.framwork.di.test.services.BImpl;

@Configuration
public class ConfigA {

    @Bean
    public A returnA(B b) {
        return new AImpl(b);
    }

}
