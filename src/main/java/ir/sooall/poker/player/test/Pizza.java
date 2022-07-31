package ir.sooall.poker.player.test;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import static ir.sooall.poker.player.test.NyPizza.Size.SMALL;

public abstract class Pizza {
    public enum Topping {HAM, MUSHROOM, ONION, PEPPER, SAUSAGE}

    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {

        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();

        // Subclasses must override this method to return "this"
        protected abstract T self();
    }

    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone(); // See Item 50
    }

    NyPizza pizza = new NyPizza.Builder(SMALL)
        .addTopping(Topping.SAUSAGE).addTopping(Topping.ONION).build();
    Calzone calzone = new Calzone.Builder()
        .addTopping(Topping.HAM).sauceInside().build();
}

