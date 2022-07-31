package ir.sooall.poker.common.fn;

import java.util.Optional;

public class Either<Left, Right> {
    private Optional<Left> left;
    private Optional<Right> right;

    private Either(Optional<Left> l, Optional<Right> r) {
        left = l;
        right = r;
    }

    public static <Left, Right> Either<Left, Right> left(Left left) {
        return new Either<Left, Right>(Optional.of(left), Optional.empty());
    }

    public static <Left, Right> Either<Left, Right> right(Right right) {
        return new Either<Left, Right>(Optional.empty(), Optional.of(right));
    }

    public Optional<Left> getLeft() {
        return left;
    }

    public Optional<Right> getRight() {
        return right;
    }

}
