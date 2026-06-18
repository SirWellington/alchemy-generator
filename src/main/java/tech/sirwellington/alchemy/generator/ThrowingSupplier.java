package tech.sirwellington.alchemy.generator;

import tech.sirwellington.alchemy.annotations.access.Internal;

/**
 * A variation of a {@link java.util.function.Supplier} that allows throwing exceptions.
 *
 * @param <T> The type of value returned.
 * @param <E> The type of the exception thrown.
 */
@FunctionalInterface
@Internal
interface ThrowingSupplier<T, E extends Throwable> {
    T get() throws E;
}
