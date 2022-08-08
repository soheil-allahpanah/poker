
package ir.sooall.poker.framwork.client;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple utilities for dealing with exceptions.
 *
 * @author Tim Boudreau
 */
public final class Exceptions {

    private Exceptions() {
    }

    private static final class DefaultExceptionHandler implements ExceptionHandler {
    }

    static final ExceptionHandler HANDLER;

    static {
        Iterator<ExceptionHandler> e = ServiceLoader.load(ExceptionHandler.class).iterator();
        HANDLER = e.hasNext() ? e.next() : new DefaultExceptionHandler();
    }

    public static void printStackTrace(String msg, Throwable t) {
        HANDLER.printStackTrace(msg, t);
    }

    public static void printStackTrace(Class<?> caller, String msg, Throwable t) {
        HANDLER.printStackTrace(caller, msg, t);
    }

    public static void printStackTrace(Class<?> caller, Throwable t) {
        HANDLER.printStackTrace(caller, t);
    }

    public static void printStackTrace(Throwable t) {
        HANDLER.printStackTrace(t);
    }

    /**
     * Dirty trick to rethrow a checked exception. Makes it possible to
     * implement an interface such as Iterable (which cannot throw exceptions)
     * without the useless re-wrapping of exceptions in RuntimeException.
     *
     * @param t A throwable. This method will throw it without requiring a catch
     * block.
     */
    public static <ReturnType> ReturnType chuck(Throwable t) {
        chuck(RuntimeException.class, t);
        throw new AssertionError(t); //should not get here
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void chuck(Class<T> type, Throwable t) throws T {
        throw (T) t;
    }

    public static <ReturnType> ReturnType chuckUnless(Throwable t, ReturnType what, BooleanSupplier predicate) {
        if (!predicate.getAsBoolean()) {
            return chuck(t);
        }
        return what;
    }

    /**
     * Unwinds any causing-exception and chucks the root cause throwable.
     *
     * @param <ReturnType> The type to pretend to return (this method always
     * exits abnormally), so methods can be written cleanly, e.g.
     * <code>return Exceptions.chuck(someException).
     * @param t
     * @return
     */
    @SuppressWarnings("ThrowableResultIgnored")
    public static <ReturnType> ReturnType chuckOriginal(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        chuck(RuntimeException.class, t);
        throw new AssertionError(t); //should not get here
    }

    /**
     * Service provider which logs exceptions; the default implementation uses
     * the JDK's logger.
     */
    public interface ExceptionHandler {

        default void printStackTrace(String msg, Throwable t) {
            Logger.getLogger(Exceptions.class.getName()).log(Level.SEVERE, msg, t);
        }

        default void printStackTrace(Class<?> caller, String msg, Throwable t) {
            Logger.getLogger(caller.getName()).log(Level.SEVERE, msg, t);
        }

        default void printStackTrace(Class<?> caller, Throwable t) {
            Logger.getLogger(caller.getName()).log(Level.SEVERE, null, t);
        }

        default void printStackTrace(Throwable t) {
            Logger.getLogger(Exceptions.class.getName()).log(Level.SEVERE, null, t);
        }
    }
}