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

import tech.sirwellington.alchemy.annotations.arguments.Required;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import java.util.function.Function;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.INTERFACE;

/**
 * {@summary Generates Data or Objects, primarily used for testint scenarios.}
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
 *
 * Examples:
 *
 * <pre>
 * Get a positive integer:
 *
 * {@code
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

    final class Get {

        /**
         * Calls the generator once to get the ones of its values.
         *
         * @param <T>       The type being generated.
         * @param generator Provides the value to get.
         * @return Only one value from the generator.
         */
        public static <T> T one(@Required AlchemyGenerator<T> generator) {
            if (generator == null) {
                throw new IllegalArgumentException("Generator cannot be null");
            }

            return generator.get();
        }
    }

    /**
     * Creates a new generator by applying a function over the output of {@code this} {@link AlchemyGenerator}.
     * @param function The mapping function.
     * @return A new {@link AlchemyGenerator} that produces values of type {@code O}.
     * @param <O> The type of the output.
     */
    default <O> AlchemyGenerator<O> map(@Required Function<T, O> function) {
        return () -> function.apply(get());
    }
}

