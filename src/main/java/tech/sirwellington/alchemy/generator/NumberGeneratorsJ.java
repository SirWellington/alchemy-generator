/*
 * Copyright 2025 Wellington Moreno<jwellington.moreno@gmail.com>.
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


import org.apache.commons.lang3.RandomUtils;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static java.lang.Integer.MIN_VALUE;
import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.generator.ChecksJ.checkThat;

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
class NumberGeneratorsJ {

    private NumberGeneratorsJ() throws IllegalAccessError {
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
    static AlchemyGenerator<Integer> integers(int inclusiveLowerBound, int exclusiveUpperBound) throws IllegalArgumentException {
        checkThat(inclusiveLowerBound < exclusiveUpperBound, "inclusiveLowerBound must be > exclusiveUpperBound");
        boolean isNegativeLowerBound = inclusiveLowerBound < 0;
        boolean isNegativeUpperBound = exclusiveUpperBound <= 0;
        
        return () -> {
          if (isNegativeLowerBound && isNegativeUpperBound) {
              int min = -exclusiveUpperBound;
              int max = -inclusiveLowerBound;
              if (inclusiveLowerBound == MIN_VALUE) {
                  max = Integer.MAX_VALUE;
              }
              int adjustedMin = safeIncrement(min);
              int adjustedMax = safeIncrement(max);
              return -RandomUtils.secure().randomInt(adjustedMin, adjustedMax);
          }
          else if (isNegativeLowerBound) {
              int seed= -RandomUtils.secure().randomInt(0, inclusiveLowerBound - exclusiveUpperBound);
              return exclusiveUpperBound + seed;
          } 
          else {
              return RandomUtils.secure().randomInt(inclusiveLowerBound, exclusiveUpperBound);
          }
        };
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
    static AlchemyGenerator<Integer> positiveInteger() {
        return integers(1, Integer.MAX_VALUE);
    }
    
    /**
     * Produces positive integers from {@code 1...1000}.
     * @see #positiveInteger()
     * @see #integers(int, int) 
     */
    static AlchemyGenerator<Integer> smallPositiveIntegers() {
        return integers(1, 1000);
    }
    
    /**
     * Produces a series of negative integers from {@code Integer.MIN_VALUE...0}.
     * @see #positiveInteger()
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
        boolean isNegativeLowerBound = inclusiveLowerBound < 0;
        boolean isNegativeUpperBound = exclusiveUpperBound <= 0;

        return () -> {
            if (isNegativeLowerBound && isNegativeUpperBound) {
                long min = -exclusiveUpperBound;
                long max = -inclusiveLowerBound;
                if (inclusiveLowerBound == Long.MIN_VALUE) {
                    max = Long.MAX_VALUE;
                }
                long adjustedMin = safeIncrement(min);
                long adjustedMax = safeIncrement(max);
                return -RandomUtils.secure().randomLong(adjustedMin, adjustedMax);
            }
            else if (isNegativeLowerBound) {
                long seed= -RandomUtils.secure().randomLong(0, inclusiveLowerBound - exclusiveUpperBound);
                return exclusiveUpperBound + seed;
            }
            else {
                return RandomUtils.secure().randomLong(inclusiveLowerBound, exclusiveUpperBound);
            }
        };
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
    static AlchemyGenerator<Double> doubles(double inclusiveLowerBound, double exclusiveUpperBound) {
        checkThat(inclusiveLowerBound <= exclusiveUpperBound, "upper bound must be > lower bound.");
        boolean isNegativeLowerBound = inclusiveLowerBound < 0.0;
        boolean isNegativeUpperBound = exclusiveUpperBound < 0.0;

        return () -> {
            if (isNegativeLowerBound && isNegativeUpperBound) {
                double min = -exclusiveUpperBound;
                double max = -inclusiveLowerBound;
                if (inclusiveLowerBound == -Double.MAX_VALUE) {
                    max = Double.MAX_VALUE;
                }
                double adjustedMin = safeIncrement(min);
                double adjustedMax = safeIncrement(max);
                return -RandomUtils.secure().randomDouble(adjustedMin, adjustedMax);
            }
            else if (isNegativeLowerBound) {
                double seed= -RandomUtils.secure().randomDouble(0, inclusiveLowerBound - exclusiveUpperBound);
                return exclusiveUpperBound + seed;
            }
            else {
                return RandomUtils.secure().randomDouble(inclusiveLowerBound, exclusiveUpperBound);
            }
        };
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
     * Produces positive integers from {@code 1...1000}.
     * @see #positiveDoubles() 
     * @see #doubles(double, double) 
     */
    static AlchemyGenerator<Double> smallPositiveDoubles() {
        return doubles(1.0, 1000.0);
    }

    /**
     * Produces a series of negative integers from {@code Long.MIN_VALUE...0}.
     * @see #positiveDoubles() ()
     * @see #doubles(double, double) 
     */
    static AlchemyGenerator<Double> negativeDoubles() {
        return doubles(-Double.MAX_VALUE, 0.0);
    }

    //===========================================
    // UTILITY FUNCTIONS
    //===========================================
    private static int safeIncrement(int num) {
        if (num == Integer.MAX_VALUE) {
            return num;
        } else {
            return num + 1;
        }
    }

    private static long safeIncrement(long num) {
        if (num == Long.MAX_VALUE) {
            return num;
        } else {
            return num + 1;
        }
    }

    private static double safeIncrement(double num) {
        if (num == Double.MAX_VALUE) {
            return num;
        } else {
            return num + 1.0;
        }
    }
}
