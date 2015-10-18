/*
 * Copyright 2015 SirWellington Tech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.sirwellington.alchemy.generator;

import java.util.function.Supplier;
import tech.sirwellington.alchemy.annotations.arguments.NonNull;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;
import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.INTERFACE;
import static tech.sirwellington.alchemy.generator.Checks.Internal.checkNotNull;

/**
 * A {@link AlchemyGenerator} generates Data or Objects for use in testing scenarios.
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
 *  int positive = oneOf(positiveIntegers());
 * }
 * </pre>
 *
 * @author SirWellington
 *
 * @param <T> The type of the Object to generate.
 */
@FunctionalInterface
@StrategyPattern(role = INTERFACE)
public interface AlchemyGenerator<T> extends Supplier<T>
{

    /**
     * Generate a non-null value of type {@code T}
     * <p>
     * @return
     */
    @Override
    @NonNull
    T get();

    /**
     * Calls the generator once to get the ones of its values.
     *
     * @param <T>
     * @param generator
     *
     * @return Only one value from the generator.
     */
    static <T> T one(@NonNull AlchemyGenerator<T> generator)
    {
        checkNotNull(generator);
        return generator.get();
    }
}
