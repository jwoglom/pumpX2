package com.jwoglom.pumpx2.shared;

import java.util.Objects;

@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {
    void accept(T t, U u, V v, W w);

    default QuadConsumer<T, U, V, W> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
        Objects.requireNonNull(after);

        return (l, r, s, t) -> {
            accept(l, r, s, t);
            after.accept(l, r, s, t);
        };
    }
}
