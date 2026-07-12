/*
 * Copyright © 2026. Sir Wellington.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.sirwellington.alchemy.generator;

import java.util.function.Function;
import java.util.function.Supplier;

import tech.sirwellington.alchemy.annotations.arguments.Required;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.INTERFACE;

/**
 * {@summary Generates Data or Objects, primarily used for testing scenarios.}
 * <br>
 * Common generators exist for:
 * <pre>
 * + Integers
 * + Longs
 * + Doubles
 * + Booleans
 * + Binary
 * + Strings
 *      + Alphabetics
 *      + Hexadecimal
 *      + UUIDs
 * + Enum Values
 * + Lists of the above
 * + Maps of the above
 * </pre>
 * <p>
 * Examples:
 *
 * <pre>
 * Get a positive integer:
 *
 * {@snippet :
 *  int positive = one(positiveIntegers());
 * }
 * </pre>
 *
 * @param <T> The type of the Object to generate.
 * @author SirWellington
 */
@StrategyPattern(role = INTERFACE)
public interface AlchemyGenerator<T> {
    /**
     * Generate a non-null value of type {@code T}.
     */
    @Required
    T get();

    /**
     * Creates a new generator by applying a function over the output of {@code this} {@link AlchemyGenerator}.
     *
     * @param function The mapping function.
     * @param <O>      The type of the output.
     * @return A new {@link AlchemyGenerator} that produces values of type {@code O}.
     */
    default <O> AlchemyGenerator<O> mapping(@Required Function<T, O> function) {
        return () -> function.apply(get());
    }

    /**
     * A bridge which allows turning a {@link Supplier} into an {@link AlchemyGenerator}.
     * It also conveniently allows a generator on the spot using a lambda:
     * {@snippet :
     * var idGenerators = AlchemyGenerator.of(
     *   () -> uuid() + ".myapplication"
     * );
     *
     * var keyId = idGenerators.get();
     *}
     *
     * @param supplier The supplier to wrap in a {@link AlchemyGenerator}.
     * @param <T>      The type of value being generated.
     * @return A new {@link AlchemyGenerator} that wraps the {@link Supplier}.
     */
    static <T> AlchemyGenerator<T> of(@Required Supplier<T> supplier) {
        return supplier::get;
    }

    /**
     * Calls the generator once to get the ones of its values.
     * This allows a shorthand way to get a single value.
     * <br>
     * For example:
     * {@snippet :
     * var userId = one(StringGenerators.uuids());
     * }
     *
     * @param <T>       The type being generated.
     * @param generator Provides the value to get.
     * @return Only one value from the generator.
     */
    static <T> T one(@Required AlchemyGenerator<T> generator) {
        Checks.checkNotNull(generator, "Generator cannot be null");
        return generator.get();
    }

}

