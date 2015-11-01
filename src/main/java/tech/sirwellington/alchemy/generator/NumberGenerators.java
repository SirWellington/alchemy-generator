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

import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.generator.BooleanGenerators.booleans;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;

/**
 * Common {@linkplain AlchemyGenerator Generators} for NumberGenerators.
 * <br>
 * Includes:
 * <br>
 * <pre>
 * + Integers
 * + Longs
 * + Doubles
 * </pre>
 *
 * @author SirWellington
 */
@StrategyPattern(role = CONCRETE_BEHAVIOR)
public final class NumberGenerators
{

    private final static Logger LOG = LoggerFactory.getLogger(NumberGenerators.class);

    private NumberGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate this class");
    }

    /**
     * Creates a series of integer values.
     *
     * @param inclusiveLowerBound The inclusive lower bound
     * @param exclusiveUpperBound The exclusive upper bound
     *
     * @return
     *
     * @throws IllegalArgumentException If {@code lowerBound >= upperBound}
     */
    public static AlchemyGenerator<Integer> integers(int inclusiveLowerBound, int exclusiveUpperBound) throws IllegalArgumentException
    {
        checkThat(inclusiveLowerBound <= exclusiveUpperBound, "Upper Bound must be greater than Lower Bound");
        final boolean isNegativeLowerBound = inclusiveLowerBound < 0;
        final boolean isNegativeUpperBound = exclusiveUpperBound < 0;

        return () ->
        {

            if (isNegativeLowerBound && isNegativeUpperBound)
            {
                int min = (-exclusiveUpperBound);
                int max = inclusiveLowerBound == Integer.MIN_VALUE ? Integer.MAX_VALUE : -inclusiveLowerBound;

                int dirtyValue = RandomUtils.nextInt(min, max);
                int valueAdjustedForInclusiveness = safeIncrement(dirtyValue);
                return -valueAdjustedForInclusiveness;
            }
            else if (isNegativeLowerBound)
            {
                boolean shouldProduceNegative = booleans().get();
                if (shouldProduceNegative)
                {
                    int dirtyMax = inclusiveLowerBound == Integer.MIN_VALUE ? Integer.MAX_VALUE : -inclusiveLowerBound;
                    int maxAdjustedForInclusiveness = safeIncrement(dirtyMax);
                    return -RandomUtils.nextInt(0, maxAdjustedForInclusiveness);
                }
                else
                {
                    return RandomUtils.nextInt(0, exclusiveUpperBound);
                }
            }
            else //Positive bounds
            {
                return RandomUtils.nextInt(inclusiveLowerBound, exclusiveUpperBound);
            }
        };

    }

    /**
     * Creates a series of positive integer values from 1 to Integer.MAX_VALUE
     *
     * @return
     * @see #smallPositiveIntegers()
     * @see #positiveLongs()
     */
    public static AlchemyGenerator<Integer> positiveIntegers()
    {
        return integers(1, Integer.MAX_VALUE);
    }

    /**
     * Creates a series of small positive integers from 1 to 1000.
     *
     * @return
     * @see #positiveIntegers()
     */
    public static AlchemyGenerator<Integer> smallPositiveIntegers()
    {
        return integers(1, 1000);
    }

    /**
     * Creates a series of negative integer values from Integer.MIN_VALUE to -1
     *
     * @return
     */
    public static AlchemyGenerator<Integer> negativeIntegers()
    {
        return () ->
        {
            int value = positiveIntegers().get();
            return value < 0 ? value : -value;
        };
    }

    /**
     * Produces long values within the specified Range
     *
     * @param inclusiveLowerBound inclusive lower bound
     * @param exclusiveUpperBound exclusive upper bound
     *
     * @return
     *
     * @throws IllegalArgumentException If {@code lowerBound >= upperBound}
     */
    public static AlchemyGenerator<Long> longs(long inclusiveLowerBound, long exclusiveUpperBound) throws IllegalArgumentException
    {
        checkThat(inclusiveLowerBound <= exclusiveUpperBound, "Upper Bound must be greater than Lower Bound");
        final boolean negativeLowerBound = inclusiveLowerBound < 0;
        final boolean negativeUpperBound = exclusiveUpperBound < 0;

        return () ->
        {

            if (negativeLowerBound && negativeUpperBound)
            {
                long min = (-exclusiveUpperBound);
                long max = inclusiveLowerBound == Long.MIN_VALUE ? Long.MAX_VALUE : -inclusiveLowerBound;

                long dirtyValue = RandomUtils.nextLong(min, max);
                long valueAdjustedForInclusiveness = safeIncrement(dirtyValue);
                return -valueAdjustedForInclusiveness;
            }
            else if (negativeLowerBound)
            {
                boolean shouldProduceNegative = booleans().get();

                if (shouldProduceNegative)
                {
                    long min = 0L;
                    long dirtyMax = inclusiveLowerBound == Long.MIN_VALUE ? Long.MAX_VALUE : -inclusiveLowerBound;
                    long maxAdjustedForInclusiveness = safeIncrement(dirtyMax);
                    return -RandomUtils.nextLong(min, maxAdjustedForInclusiveness);
                }
                else
                {
                    return RandomUtils.nextLong(0, exclusiveUpperBound);
                }
            }
            else //Positive bounds
            {
                return RandomUtils.nextLong(inclusiveLowerBound, exclusiveUpperBound);
            }
        };
    }

    /**
     * Produces a series of positive values from {@code 1} to {@code Long.MAX_VALUE}
     *
     * @return
     * @see #smallPositiveLongs()
     * @see #positiveIntegers()
     */
    public static AlchemyGenerator<Long> positiveLongs()
    {
        return longs(1L, Long.MAX_VALUE);
    }

    /**
     * Produces a series of positive values from 1 to 10,000
     *
     * @return
     *
     * @see #positiveLongs()
     * @see #positiveLongs()
     */
    public static AlchemyGenerator<Long> smallPositiveLongs()
    {
        return longs(1L, 10_000L);
    }

    /**
     * Creates a series of double values within the specified range
     *
     * @param inclusiveLowerBound The inclusive lower bound
     * @param inclusiveUpperBound The inclusive upper bound
     *
     * @return
     *
     * @throws IllegalArgumentException If {@code lowerBound >= upperBound}
     */
    public static AlchemyGenerator<Double> doubles(double inclusiveLowerBound, double inclusiveUpperBound) throws IllegalArgumentException
    {
        checkThat(inclusiveLowerBound <= inclusiveUpperBound, "Upper Bound must be greater than Lower Bound");
        final boolean negativeLowerBound = inclusiveLowerBound < 0;
        final boolean negativeUpperBound = inclusiveUpperBound < 0;

        return () ->
        {
            if (negativeLowerBound && negativeUpperBound)
            {
                return -RandomUtils.nextDouble(-inclusiveUpperBound, -inclusiveLowerBound);
            }
            else if (negativeLowerBound)
            {
                boolean shouldProduceNegative = booleans().get();
                if (shouldProduceNegative)
                {
                    return -RandomUtils.nextDouble(0, -inclusiveLowerBound);
                }
                else
                {
                    return RandomUtils.nextDouble(0, inclusiveUpperBound);
                }
            }
            else //Positive bounds
            {
                return RandomUtils.nextDouble(inclusiveLowerBound, inclusiveUpperBound);
            }
        };
    }

    /**
     * Creates a series of positive double values from 0 to Double.MAX_VALUE.
     *
     * @return
     * @see #smallPositiveDoubles()
     * @see #positiveIntegers()
     */
    public static AlchemyGenerator<Double> positiveDoubles()
    {
        return doubles(0.1, Double.MAX_VALUE);
    }

    /**
     * Creates a series of positive doubles from 0.1 to 1000.0
     *
     * @return
     *
     * @see #positiveDoubles()
     * @see #positiveIntegers()
     */
    public static AlchemyGenerator<Double> smallPositiveDoubles()
    {
        return doubles(0.1, 1000);
    }

    /**
     * Generates an integer value from the specified set.
     *
     * @param values
     *
     * @return
     */
    public static AlchemyGenerator<Integer> integersFromFixedList(List<Integer> values)
    {
        checkNotNull(values);
        checkThat(!values.isEmpty(), "No values specified");
        return () ->
        {
            int index = integers(0, values.size()).get();
            return values.get(index);
        };
    }

    /**
     * Generates a double value from the specified set.
     *
     * @param values
     *
     * @return
     */
    public static AlchemyGenerator<Double> doublesFromFixedList(List<Double> values)
    {
        checkNotNull(values);
        checkThat(!values.isEmpty(), "No values specified");
        return () ->
        {
            int index = integers(0, values.size()).get();
            return values.get(index);
        };
    }

    //==============================================================================================
    //Internal
    //==============================================================================================
    /**
     * Attempts to increment the value without potentially circling back to {@link Long#MIN_VALUE}
     *
     * @param value
     *
     * @return
     */
    @Internal
    static long safeIncrement(long value)
    {
        return value == Long.MAX_VALUE ? value : value + 1;
    }

    /**
     * Attempts to decrement the value without potentially circling back to {@link Long#MAX_VALUE}.
     *
     * @param value
     *
     * @return
     */
    @Internal
    static long safeDecrement(long value)
    {
        return value == Long.MIN_VALUE ? value : value - 1;
    }

    /**
     * Attempts to increment the value without potentially circling back to
     * {@link Integer#MIN_VALUE}
     *
     * @param value
     * @return
     */
    @Internal
    static int safeIncrement(int value)
    {
        return value == Integer.MAX_VALUE ? value : value + 1;
    }

    /**
     * Attempts to decrement the value without potentially circling back to
     * {@link Integer#MAX_VALUE}.
     *
     * @param value
     * @return
     */
    @Internal
    static int safeDecrement(int value)
    {
        return value == Integer.MIN_VALUE ? value : value - 1;
    }
}
