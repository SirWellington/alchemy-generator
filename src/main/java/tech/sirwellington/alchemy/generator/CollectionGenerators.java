/*
 * Copyright © 2026 Wellington Moreno<jwellington.moreno@gmail.com>.
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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;
import tech.sirwellington.alchemy.annotations.arguments.Positive;
import tech.sirwellington.alchemy.annotations.arguments.Required;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.Checks.*;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.smallPositiveIntegers;

/**
 * {@summary Alchemy Generators for Java Collections.}
 * <p>
 * These generators are useful in conjunction with other {@link AlchemyGenerator},
 * such as those in {@link StringGenerators} and {@link NumberGenerators}.
 *
 * {@snippet :
 * var list = List.of(1, 2, 3, 4);
 * var generator = CollectionGenerators.fromList(list);
 * var value = generator.get(); // One of [1, 2, 3, 4]
 *
 * var uniqueIds = CollectionGenerators.listOf(StringGenerators.UUIDS, 25);
 * }
 *
 * @author SirWellington
 * @see StringGenerators
 * @see NumberGenerators
 * @see BinaryGenerators
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
public final class CollectionGenerators {

    private CollectionGenerators() throws IllegalAccessException {
        throw new IllegalAccessException("Cannot instantiate this class");
    }

    /**
     * Returns a list of Objects of varying size, using the supplied generator.
     *
     * @param <T>       The underlying type generated.
     * @param generator The underlying generator that produces the values for the list.
     * @return A generator -> A list of random values, the length of which will vary.
     */
    public static <T> List<T> listOf(@Required AlchemyGenerator<T> generator) {
        int size = one(integers(5, 200));
        return listOf(generator, size);
    }

    /**
     * Returns a list of Objects of the given size, using the supplied generator.
     *
     * @param <T>       The underlying type generated.
     * @param generator The underlying generator that produces the values for the list.
     * @param size      The size of the lists produced by the generator.
     * @return A generator -> A list of random values, the length of which will vary.
     */
    public static <T> List<T> listOf(
        @Required AlchemyGenerator<T> generator,
        int size
    ) {
        checkThat(size >= 0, "Size must be at least 0");
        checkNotNull(generator, "generator is null");

        return IntStream.range(0, size)
                        .mapToObj(_ -> generator.get())
                        .collect(Collectors.toList());
    }

    /**
     * An {@link AlchemyGenerator} that returns values from a fixed list at random.
     *
     * @param <T>  The underlying type.
     * @param list Must be nonempty.
     * @return A generator -> A of one of the values provided.
     */
    public static <T> AlchemyGenerator<T> fromList(
        @Required @NonEmpty List<T> list
    ) {
        checkNotEmpty(list);
        return () -> {
            int index = one(integers(0, list.size()));
            return list.get(index);
        };
    }

    /**
     * Creates an {@link AlchemyGenerator} that produces {@link Map Maps}
     * using the Keys and Values generators by the supplied generators.
     * Convenience method for {@link #mapOf(tech.sirwellington.alchemy.generator.AlchemyGenerator, tech.sirwellington.alchemy.generator.AlchemyGenerator, int) }.
     *
     * @param <K>    Type of the Key values.
     * @param <V>    Type of the Value values.
     * @param keys   Generates the keys for the Map.
     * @param values Generates the values for the Map.
     * @return A {@link Map} generated from the parameters specified.
     */
    public static <K, V> AlchemyGenerator<Map<K, V>> mapGeneratorOf(
        @Required AlchemyGenerator<K> keys,
        @Required AlchemyGenerator<V> values,
        @Positive int size
    ) {
        checkThat(size > 0, "size must be > 0");
        checkNotNull(keys, "keys cannot be null");
        checkNotNull(values, "values cannot be null");

        return () -> {
            var map = new HashMap<K, V>();
            IntStream.range(0, size)
                     .forEach(i -> {
                         var key = keys.get();
                         var value = values.get();
                         map.put(key, value);
                     });

            return map;
        };
    }

    /**
     * Creates a {@link Map} using the Keys and Values generated by the supplied generators.
     * Convenience method for {@link #mapOf(tech.sirwellington.alchemy.generator.AlchemyGenerator, tech.sirwellington.alchemy.generator.AlchemyGenerator, int) }.
     *
     * @param <K>    Type of the Key values.
     * @param <V>    Type of the Value values.
     * @param keys   Generates the keys for the Map.
     * @param values Generates the values for the Map.
     * @return A {@link Map} generated from the parameters specified.
     */
    public static <K, V> Map<K, V> mapOf(
        @Required AlchemyGenerator<K> keys,
        @Required AlchemyGenerator<V> values
    ) {
        int size = one(smallPositiveIntegers());
        return mapOf(keys, values, size);
    }

    /**
     * Creates a {@link Map} using the Keys and Values generated by the supplied generators.
     *
     * @param <K>    Type of the Key values.
     * @param <V>    Type of the Value values.
     * @param keys   Generates the keys for the Map.
     * @param values Generates the values for the Map.
     * @param size   The exact size of the created Map.
     * @return A {@link Map} generated from the parameters specified.
     */
    public static <K, V> Map<K, V> mapOf(
        @Required AlchemyGenerator<K> keys,
        @Required AlchemyGenerator<V> values,
        int size
    ) {
        checkThat(size > 0, "size must be at least 1");
        checkNotNull(keys);
        checkNotNull(values);

        Function<Integer, K> keyMapper = (_) -> keys.get();
        Function<Integer, V> valueMapper = (_) -> values.get();

        return IntStream.range(0, size)
                        .boxed()
                        .collect(Collectors.toMap(keyMapper, valueMapper));
    }
}
