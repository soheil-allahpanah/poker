package ir.sooall.poker.framwork.di.test.confg;

import ir.sooall.poker.framwork.di.annotations.Bean;
import ir.sooall.poker.framwork.di.annotations.Configuration;
import ir.sooall.poker.framwork.di.test.C;
import ir.sooall.poker.framwork.di.test.D;
import ir.sooall.poker.framwork.di.test.services.CImpl;
import ir.sooall.poker.framwork.di.test.services.DImpl;

@Configuration
public class ConfigCD {

    @Bean
    public C returnC() {
        return new CImpl();
    }

    @Bean
    public D returnD() {
        return new DImpl();
    }
}
