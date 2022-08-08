package ir.sooall.poker.framwork.di.test.confg;

import ir.sooall.poker.framwork.di.annotations.Bean;
import ir.sooall.poker.framwork.di.annotations.Configuration;
import ir.sooall.poker.framwork.di.test.B;
import ir.sooall.poker.framwork.di.test.C;
import ir.sooall.poker.framwork.di.test.D;
import ir.sooall.poker.framwork.di.test.services.BImpl;
import ir.sooall.poker.framwork.di.test.services.CImpl;
import ir.sooall.poker.framwork.di.test.services.DImpl;

@Configuration
public class ConfigB {

    @Bean
    public B returnB(C c, D d) {
        return new BImpl(c, d);
    }

}
