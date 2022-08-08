package ir.sooall.poker.framwork.di.test;

import com.sun.tools.javac.Main;
import ir.sooall.poker.framwork.di.Configor;
import ir.sooall.poker.framwork.di.test.confg.ConfigB;
import ir.sooall.poker.framwork.di.test.confg.ConfigCD;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MainTest {
    public static void main(String[] args) throws Throwable {
        Configor.run(MainTest.class);

        A a = Configor.getService(A.class);

//        try {
//            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
//            var confCD = new ConfigCD();
//            var confB = new ConfigB();
//
//            MethodType methodTypeC = MethodType.methodType(C.class);
//            var mc = lookup.findVirtual(ConfigCD.class, "returnC", methodTypeC);
//            MethodType methodTypeD = MethodType.methodType(D.class);
//            var md = lookup.findVirtual(ConfigCD.class, "returnD", methodTypeD);
//            MethodType methodTypeB = MethodType.methodType(B.class, C.class, D.class);
//            var mb = lookup.findVirtual(ConfigB.class, "returnB", methodTypeB);
//
////            mc.bindTo(confCD);
////            md.bindTo(confCD);
////            mb.bindTo(confB);
//            var c = (C) mc.invoke(confCD);
//            var d = (D) md.invoke(confCD);
//            var b = (B) mb.invoke(confB, c, d);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
    }
}
