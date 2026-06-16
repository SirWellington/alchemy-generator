/*
 *Copyright © 2026 Wellington Moreno<jwellington.moreno@gmail.com>.
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


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;

import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.Required;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static java.lang.Integer.MIN_VALUE;
import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.generator.Checks.checkNotEmpty;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;

/**
 * Common {@link AlchemyGenerator Alchemy Generators} for Number Generators.
 * <p>
 * <b>Includes</b>:
 * <pre>
 * + Integers
 * + Longs
 * + Doubles
 * </pre>
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
public final class NumberGenerators {
    
    private static final SecureRandom RANDOM = new SecureRandom();

    private NumberGenerators() throws IllegalAccessError {
        throw new IllegalAccessError("cannot directly instantiate.");
    }
    //===========================================
    // INTEGERS
    //===========================================

    /**
     * Creates a series of integer values within the specified bounds. 
     * @param inclusiveLowerBound Can be negative, must be {@code < exclusiveUpperBound}.
     * @param exclusiveUpperBound Can be negative, must be {@code > inclusiveLowerBound}.
     * @throws IllegalArgumentException If {@code inclusiveLowerBound >= exclusiveUpperBound}.
     */
    static AlchemyGenerator<Integer> integers(
        int inclusiveLowerBound,
        int exclusiveUpperBound
    ) throws IllegalArgumentException {
        checkThat(
            inclusiveLowerBound < exclusiveUpperBound,
            "inclusiveLowerBound must be > exclusiveUpperBound"
        );
        return () -> RANDOM.nextInt(inclusiveLowerBound, exclusiveUpperBound);
    }
    
    /**
     * Creates a series of integer values, negative and positive.
     * The range is {@code Integer.MIN_VALUE...Integer.MAX_VALUE}.
     */
    static AlchemyGenerator<Integer> anyIntegers() {
        return integers(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    /**
     * Produces positive integers from {@code 1...Integer.MAX_VALUE}.
     * @see #smallPositiveIntegers()
     * @see #integers(int, int) 
     * @see #negativeIntegers() 
     */
    static AlchemyGenerator<Integer> positiveIntegers() {
        return integers(1, Integer.MAX_VALUE);
    }
    
    /**
     * Produces positive integers from {@code 1...1000}.
     * @see #positiveIntegers()
     * @see #integers(int, int) 
     */
    static AlchemyGenerator<Integer> smallPositiveIntegers() {
        return integers(1, 1000);
    }
    
    /**
     * Produces a series of negative integers from {@code Integer.MIN_VALUE...0}.
     * @see #positiveIntegers()
     * @see #integers(int, int) 
     */
    static AlchemyGenerator<Integer> negativeIntegers() {
        return integers(Integer.MIN_VALUE, 0);
    }

    //===========================================
    // LONGS
    //===========================================
    
    /**
     * Creates a series of long values within the specified bounds.
     * @param inclusiveLowerBound Can be negative, must be {@code < exclusiveUpperBound}.
     * @param exclusiveUpperBound Can be negative, must be {@code > inclusiveLowerBound}.
     * @throws IllegalArgumentException If {@code inclusiveLowerBound >= exclusiveUpperBound}.
     */
    static AlchemyGenerator<Long> longs(long inclusiveLowerBound, long exclusiveUpperBound) throws IllegalArgumentException {
        checkThat(inclusiveLowerBound < exclusiveUpperBound, "inclusiveLowerBound must be > exclusiveUpperBound");

        return () -> RANDOM.nextLong(inclusiveLowerBound, exclusiveUpperBound);
    }

    /**
     * Creates a series of Long values, negative and positive.
     * The range is {@code Long.MIN_VALUE...Long.MAX_VALUE}.
     * @see #longs(long, long) 
     */
    static AlchemyGenerator<Long> anyLongs() {
        return longs(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Produces positive longs from {@code 1...Long.MAX_VALUE}.
     * @see #smallPositiveLongs() ()
     * @see #longs(long, long)
     * @see #negativeLongs() 
     */
    static AlchemyGenerator<Long> positiveLongs() {
        return longs(1L, Long.MAX_VALUE);
    }

    /**
     * Produces positive longs from {@code 1...1000}.
     * @see #positiveLongs() ()
     * @see #longs(long, long) 
     */
    static AlchemyGenerator<Long> smallPositiveLongs() {
        return longs(1L, 1000L);
    }

    /**
     * Produces a series of negative longs from {@code Long.MIN_VALUE...0}.
     * @see #positiveLongs() 
     * @see #longs(long, long) 
     */
    static AlchemyGenerator<Long> negativeLongs() {
        return longs(Long.MIN_VALUE, 0L);
    }

    //===========================================
    // DOUBLES
    //===========================================
    /**
     * Creates a series of double values within the specified bounds.
     * @param inclusiveLowerBound Can be negative, must be {@code < exclusiveUpperBound}.
     * @param exclusiveUpperBound Can be negative, must be {@code > inclusiveLowerBound}.
     * @throws IllegalArgumentException If {@code inclusiveLowerBound >= exclusiveUpperBound}.
     */
    static AlchemyGenerator<Double> doubles(
        double inclusiveLowerBound,
        double exclusiveUpperBound
    ) {
        checkThat(inclusiveLowerBound <= exclusiveUpperBound, "upper bound must be > lower bound.");
        return () -> RANDOM.nextDouble(inclusiveLowerBound, exclusiveUpperBound);
    }

    /**
     * Creates a series of Double values, negative and positive.
     * The range is {@code -Double.MAX_VALUE...Double.MAX_VALUE}.
     * @see #doubles(double, double)
     */
    static AlchemyGenerator<Double> anyDoubles() {
        return doubles(-Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Produces positive doubles from {@code 1...Double.MAX_VALUE}.
     * @see #smallPositiveDoubles()
     * @see #doubles(double, double) 
     * @see #negativeDoubles() 
     */
    static AlchemyGenerator<Double> positiveDoubles() {
        return doubles(1.0, Double.MAX_VALUE);
    }

    /**
     * Produces positive doubles from {@code 1...1000}.
     * @see #positiveDoubles() 
     * @see #doubles(double, double) 
     */
    static AlchemyGenerator<Double> smallPositiveDoubles() {
        return doubles(1.0, 1000.0);
    }

    /**
     * Produces a series of negative doubles from {@code Long.MIN_VALUE...0}.
     * @see #positiveDoubles()
     * @see #doubles(double, double) 
     */
    @Deprecated
    private static AlchemyGenerator<Double> negativeDoubles() {
        return doubles(-Double.MAX_VALUE, 0.0);
    }

    //===========================================
    // FLOATS
    //===========================================
    /**
     * Creates a series of float values within the specified bounds.
     * @param inclusiveLowerBound Can be negative, must be {@code < exclusiveUpperBound}.
     * @param exclusiveUpperBound Can be negative, must be {@code > inclusiveLowerBound}.
     * @throws IllegalArgumentException If {@code inclusiveLowerBound >= exclusiveUpperBound}.
     */
    static AlchemyGenerator<Float> floats(float inclusiveLowerBound, float exclusiveUpperBound) {
        var doubles = doubles(inclusiveLowerBound, exclusiveUpperBound);
        return () -> doubles.get().floatValue();
    }

    /**
     * Creates a series of Float values, negative and positive.
     * The range is {@code -Float.MAX_VALUE...Float.MAX_VALUE}.
     * @see #floats(float, float)
     */
    static AlchemyGenerator<Float> anyFloats() {
        return floats(-Float.MAX_VALUE, Float.MAX_VALUE);
    }

    /**
     * Produces positive floats from {@code 1...Float.MAX_VALUE}.
     * @see #smallPositiveFloats()
     * @see #floats(float, float)
     * @see #negativeFloats()
     */
    static AlchemyGenerator<Float> positiveFloats() {
        return floats(1.0f, Float.MAX_VALUE);
    }

    /**
     * Produces positive floats from {@code 1...1000}.
     * @see #positiveFloats()
     * @see #floats(float, float)
     */
    static AlchemyGenerator<Float> smallPositiveFloats() {
        return floats(1.0f, 1000.0f);
    }

    /**
     * Produces a series of negative integers from {@code Long.MIN_VALUE...0}.
     * @see #positiveFloats()
     * @see #floats(float, float)
     */
    static AlchemyGenerator<Float> negativeFloats() {
        return floats(-Float.MAX_VALUE, 0.0f);
    }

    //===========================================
    // LISTS
    //===========================================
    /**
     * Generates an integer value from the specified set.
     * @param values The list to pull the values from.
     */
    static AlchemyGenerator<Integer> integersFromFixedList(@Required List<Integer> values) {
        checkNotEmpty(values, "No values specified");

        return () -> {
            var index = integers(0, values.size()).get();
            return values.get(index);
        };
    }

    /**
     * Generates a double value from the specified List.
     * @param values The list to pull values from.
     */
    static AlchemyGenerator<Double> doublesFromFixedList(List<Double> values) {
        checkNotEmpty(values, "No values specified");

        return () -> {
            var index = integers(0, values.size()).get();
            return values.get(index);
        };
    }

    /**
     * Generates a float value from the specified set.
     * @param values The list to pull values from.
     */
    static AlchemyGenerator<Float> floatsFromFixedList(List<Float> values) {
        checkNotEmpty(values, "No values specified");

        return () -> {
            var index = integers(0, values.size()).get();
            return values.get(index);
        };
    }

    //===========================================
    // UTILITY FUNCTIONS
    //===========================================
    @Internal
    static int safeIncrement(int num) {
        if (num == Integer.MAX_VALUE) {
            return num;
        } else {
            return num + 1;
        }
    }

    @Internal
    static long safeIncrement(long num) {
        if (num == Long.MAX_VALUE) {
            return num;
        } else {
            return num + 1;
        }
    }

    @Internal
    static double safeIncrement(double num) {
        if (num == Double.MAX_VALUE) {
            return num;
        } else {
            return num + 1.0;
        }
    }
}
