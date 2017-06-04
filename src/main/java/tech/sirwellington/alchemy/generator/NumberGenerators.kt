/*
 * Copyright 2017 SirWellington Tech.
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
package tech.sirwellington.alchemy.generator

import org.apache.commons.lang3.RandomUtils
import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.Internal
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR

/**
 * Common [Generators][AlchemyGenerator] for NumberGenerators.
 * <br></br>
 * Includes:
 * <br></br>
 * <pre>
 * + Integers
 * + Longs
 * + Doubles
</pre> *

 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
class NumberGenerators
@Throws(IllegalAccessException::class)
internal constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate this class")
    }

    companion object
    {

        private val LOG = LoggerFactory.getLogger(NumberGenerators::class.java)

        /**
         * Creates a series of integer values within the specified bounds.
         *
         * @param inclusiveLowerBound The inclusive lower bound
         *
         * @param exclusiveUpperBound The exclusive upper bound
         *
         *
         * @return
         *
         *
         * @throws IllegalArgumentException If `lowerBound >= upperBound`
         */
        @Throws(IllegalArgumentException::class)
        @JvmStatic
        fun integers(inclusiveLowerBound: Int, exclusiveUpperBound: Int): AlchemyGenerator<Int>
        {
            checkThat(inclusiveLowerBound < exclusiveUpperBound, "Lower Bound must be < Upper Bound")

            val isNegativeLowerBound = inclusiveLowerBound < 0
            // <= because of the fact that 0 would be the *exclusive* upper bound.
            val isNegativeUpperBound = exclusiveUpperBound <= 0

            return AlchemyGenerator result@ {

                if (isNegativeLowerBound && isNegativeUpperBound)
                {
                    val min = -exclusiveUpperBound
                    val max = if (inclusiveLowerBound == Integer.MIN_VALUE) Integer.MAX_VALUE else -inclusiveLowerBound

                    //Adjust by one, for inclusivity
                    val minAdjustedForInclusivity = safeIncrement(min)
                    val maxAdjustedForInclusivity = safeIncrement(max)

                    val value = RandomUtils.nextInt(minAdjustedForInclusivity, maxAdjustedForInclusivity)
                    return@result -value
                }
                else if (isNegativeLowerBound)
                {
                    val shouldProduceNegative = BooleanGenerators.booleans().get()

                    if (shouldProduceNegative)
                    {
                        val max = if (inclusiveLowerBound == Integer.MIN_VALUE) Integer.MAX_VALUE else -inclusiveLowerBound
                        val maxAdjustedForInclusivity = safeIncrement(max)
                        return@result -RandomUtils.nextInt(0, maxAdjustedForInclusivity)
                    }
                    else
                    {
                        return@result RandomUtils.nextInt(0, exclusiveUpperBound)
                    }
                }
                else
                //Positive bounds
                {
                    return@result RandomUtils.nextInt(inclusiveLowerBound, exclusiveUpperBound)
                }
            }

        }

        /**
         * Creates a series of integer values, negative and positive.
         * The range is `Integer.MIN_VALUE...Integer.MAX_VALUE`.
         *
         * @return
         */
        @JvmStatic
        fun anyIntegers(): AlchemyGenerator<Int>
        {
            return integers(Integer.MIN_VALUE, Integer.MAX_VALUE)
        }

        /**
         * Creates a series of positive integer values from 1 to Integer.MAX_VALUE
         *
         * @return
         *
         * @see .smallPositiveIntegers
         * @see .positiveLongs
         */
        @JvmStatic
        fun positiveIntegers(): AlchemyGenerator<Int>
        {
            return integers(1, Integer.MAX_VALUE)
        }

        /**
         * Creates a series of small positive integers from 1 to 1000.
         *
         * @return
         *
         * @see .positiveIntegers
         */
        @JvmStatic
        fun smallPositiveIntegers(): AlchemyGenerator<Int>
        {
            return integers(1, 1000)
        }

        /**
         * Creates a series of negative integer values from Integer.MIN_VALUE to -1
         *
         * @return
         */
        @JvmStatic
        fun negativeIntegers(): AlchemyGenerator<Int>
        {
            return AlchemyGenerator result@ {
                val value = positiveIntegers().get()

                return@result if (value < 0) value else -value
            }
        }

        /**
         * Produces long values within the specified Range
         *
         * @param inclusiveLowerBound inclusive lower bound
         *
         * @param exclusiveUpperBound exclusive upper bound
         *
         *
         * @return
         *
         *
         * @throws IllegalArgumentException If `lowerBound >= upperBound`
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun longs(inclusiveLowerBound: Long, exclusiveUpperBound: Long): AlchemyGenerator<Long>
        {
            checkThat(inclusiveLowerBound < exclusiveUpperBound, "Lower Bound must be < Upper Bound")

            val negativeLowerBound = inclusiveLowerBound < 0
            // <= because of the fact that 0 would be the *exclusive* upper bound.
            val negativeUpperBound = exclusiveUpperBound <= 0

            return AlchemyGenerator result@ {

                if (negativeLowerBound && negativeUpperBound)
                {
                    //Reverse the min and max
                    val min = -exclusiveUpperBound
                    val max = if (inclusiveLowerBound == java.lang.Long.MIN_VALUE) java.lang.Long.MAX_VALUE else -inclusiveLowerBound

                    //Adjust by one, for inclusivity
                    val minAdjustedForInclusivity = safeIncrement(min)
                    val maxAdjustedForInclusivity = safeIncrement(max)

                    val value = RandomUtils.nextLong(minAdjustedForInclusivity, maxAdjustedForInclusivity)
                    return@result -value
                }
                else if (negativeLowerBound)
                {
                    val shouldProduceNegative = BooleanGenerators.booleans().get()

                    if (shouldProduceNegative)
                    {
                        val min = 0L
                        val max = if (inclusiveLowerBound == java.lang.Long.MIN_VALUE) java.lang.Long.MAX_VALUE else -inclusiveLowerBound
                        val maxAdjustedForInclusivity = safeIncrement(max)

                        val value = -RandomUtils.nextLong(min, maxAdjustedForInclusivity)
                        return@result value
                    }
                    else
                    {
                        return@result RandomUtils.nextLong(0, exclusiveUpperBound)
                    }
                }
                else
                //Positive bounds
                {
                    return@result RandomUtils.nextLong(inclusiveLowerBound, exclusiveUpperBound)
                }
            }
        }

        /**
         * Creates a series of Longs, both negative and positive.
         * The range is `Long.MIN_VALUE...Long.MAX_VALUE`.
         *
         * @return
         */
        @JvmStatic
        fun anyLongs(): AlchemyGenerator<Long>
        {
            return longs(java.lang.Long.MIN_VALUE, java.lang.Long.MAX_VALUE)
        }

        /**
         * Produces a series of positive values from `1` to `Long.MAX_VALUE`
         *
         * @return
         *
         * @see .smallPositiveLongs
         * @see .positiveIntegers
         */
        @JvmStatic
        fun positiveLongs(): AlchemyGenerator<Long>
        {
            return longs(1L, java.lang.Long.MAX_VALUE)
        }

        /**
         * Produces a series of positive values from 1 to 10,000
         *
         * @return
         *
         *
         * @see .positiveLongs
         * @see .positiveLongs
         */
        @JvmStatic
        fun smallPositiveLongs(): AlchemyGenerator<Long>
        {
            return longs(1L, 10_000L)
        }

        /**
         * Creates a series of double values within the specified range
         *
         * @param inclusiveLowerBound The inclusive lower bound
         *
         * @param inclusiveUpperBound The inclusive upper bound
         *
         *
         * @return
         *
         *
         * @throws IllegalArgumentException If `lowerBound >= upperBound`
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun doubles(inclusiveLowerBound: Double, inclusiveUpperBound: Double): AlchemyGenerator<Double>
        {
            checkThat(inclusiveLowerBound <= inclusiveUpperBound, "Upper Bound must be greater than Lower Bound")
            val negativeLowerBound = inclusiveLowerBound < 0
            val negativeUpperBound = inclusiveUpperBound < 0

            return AlchemyGenerator result@ {
                if (negativeLowerBound && negativeUpperBound)
                {
                    return@result -RandomUtils.nextDouble(-inclusiveUpperBound, -inclusiveLowerBound)
                }
                else if (negativeLowerBound)
                {
                    val shouldProduceNegative = BooleanGenerators.booleans().get()

                    if (shouldProduceNegative)
                    {
                        return@result -RandomUtils.nextDouble(0.0, -inclusiveLowerBound)
                    }
                    else
                    {
                        return@result RandomUtils.nextDouble(0.0, inclusiveUpperBound)
                    }
                }
                else
                //Positive bounds
                {
                    return@result RandomUtils.nextDouble(inclusiveLowerBound, inclusiveUpperBound)
                }
            }
        }

        /**
         * Creates a series of doubles, both negative and positive.
         * The range is `-Double.MAX_VALUE...Double.MAX_VALUE`.
         *
         * @return
         */
        @JvmStatic
        fun anyDoubles(): AlchemyGenerator<Double>
        {
            return doubles(-java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        }

        /**
         * Creates a series of positive double values from 0 to Double.MAX_VALUE.
         *
         * @return
         *
         * @see .smallPositiveDoubles
         * @see .positiveIntegers
         */
        @JvmStatic
        fun positiveDoubles(): AlchemyGenerator<Double>
        {
            return doubles(0.1, java.lang.Double.MAX_VALUE)
        }

        /**
         * Creates a series of positive doubles from 0.1 to 1000.0
         *
         * @return
         *
         *
         * @see .positiveDoubles
         * @see .positiveIntegers
         */
        @JvmStatic
        fun smallPositiveDoubles(): AlchemyGenerator<Double>
        {
            return doubles(0.1, 1000.0)
        }

        /**
         * Generates an integer value from the specified set.
         *
         * @param values
         *
         *
         * @return
         */
        @JvmStatic
        fun integersFromFixedList(values: List<Int>): AlchemyGenerator<Int>
        {
            checkNotNull(values)
            checkThat(!values.isEmpty(), "No values specified")
            return AlchemyGenerator {
                val index = integers(0, values.size).get()
                values[index]
            }
        }

        /**
         * Generates a double value from the specified set.
         *
         * @param values
         *
         *
         * @return
         */
        @JvmStatic
        fun doublesFromFixedList(values: List<Double>): AlchemyGenerator<Double>
        {
            checkNotNull(values)
            checkThat(!values.isEmpty(), "No values specified")

            return AlchemyGenerator {
                val index = integers(0, values.size).get()
                values[index]
            }
        }

        //==============================================================================================
        //Internal
        //==============================================================================================
        /**
         * Attempts to increment the value without potentially circling back to [Long.MIN_VALUE]
         *
         * @param value
         *
         *
         * @return
         */
        @JvmStatic
        @Internal
        internal fun safeIncrement(value: Long): Long
        {
            return if (value == java.lang.Long.MAX_VALUE) value else value + 1
        }

        /**
         * Attempts to decrement the value without potentially circling back to [Long.MAX_VALUE].
         *
         * @param value
         *
         *
         * @return
         */
        @JvmStatic
        @Internal
        internal fun safeDecrement(value: Long): Long
        {
            return if (value == java.lang.Long.MIN_VALUE) value else value - 1
        }

        /**
         * Attempts to increment the value without potentially circling back to
         * [Integer.MIN_VALUE]
         *
         * @param value
         *
         * @return
         */
        @JvmStatic
        @Internal
        internal fun safeIncrement(value: Int): Int
        {
            return if (value == Integer.MAX_VALUE) value else value + 1
        }

        /**
         * Attempts to decrement the value without potentially circling back to
         * [Integer.MAX_VALUE].
         *
         * @param value
         *
         * @return
         */
        @JvmStatic
        @Internal
        internal fun safeDecrement(value: Int): Int
        {
            return if (value == Integer.MIN_VALUE) value else value - 1
        }
    }
}
