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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.sirwellington.alchemy.generator.NumberGenerators.*;

/**
 * Tests for {@link NumberGenerators}
 */
@DisplayName("Number Generators Tests")
class NumberGeneratorsTest extends BaseGeneratorTest {

    @Test
    @DisplayName("cannot instantiate utility class")
    void testCannotInstantiate() {
        assertThrows(
            IllegalAccessException.class, () ->
                NumberGenerators.class.getDeclaredConstructor().newInstance()
        );
    }

    // ===================== Integers =====================

    @Nested
    @DisplayName("Integers Tests")
    class IntegersTests {

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("generates integers within bounds (exclusive upper)")
        void testIntegers() {
            int lowerBound = RANDOM.nextInt(0, Integer.MAX_VALUE / 2);
            int upperBound = RANDOM.nextInt(lowerBound + 1, Integer.MAX_VALUE);

            var instance = integers(lowerBound, upperBound);

            repeatBlock(() -> {
                var value = instance.get();
                assertThat(value, greaterThanOrEqualTo(lowerBound));
                assertThat(value, lessThan(upperBound));
            });
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("generates with MIN/MAX bounds (exclusive upper)")
        void testIntegersWithMinAndMax() {
            int lowerBound = Integer.MIN_VALUE;
            int upperBound = Integer.MAX_VALUE;

            var instance = integers(lowerBound, upperBound);

            repeatBlock(() -> {
                var value = instance.get();
                assertThat(value, greaterThanOrEqualTo(lowerBound));
                assertThat(value, lessThan(upperBound));
            });
        }

        @Test
        @DisplayName("handles negative ranges")
        void testIntegersWithNegativeRange() {
            // Subtest 1: -10 to 150
            {
                int lowerBound = -10;
                int upperBound = 150;
                var instance = integers(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThan(upperBound));
                });
            }

            // Subtest 2: -4934 to -500
            {
                int lowerBound = -4934;
                int upperBound = -500;
                var instance = integers(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThan(upperBound));
                });
            }

            // Subtest 3: -5000 to -1
            {
                int lowerBound = -5000;
                int upperBound = -1;
                var instance = integers(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThan(upperBound));
                });
            }

            // Subtest 4: MIN_VALUE to -1
            {
                int lowerBound = Integer.MIN_VALUE;
                int upperBound = -1;
                var instance = integers(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThan(upperBound));
                });
            }

            // Subtest 5: MIN_VALUE to 0
            {
                int lowerBound = Integer.MIN_VALUE;
                int upperBound = 0;
                var instance = integers(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThan(upperBound));
                });
            }
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("positiveIntegers() returns values > 0")
        void testPositiveIntegers() {
            var instance = positiveIntegers();
            assertNotNull(instance);

            var value = instance.get();
            assertThat(value, greaterThan(0));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("smallPositiveIntegers() returns values in (0, 1000]")
        void testSmallPositiveIntegers() {
            var instance = smallPositiveIntegers();
            assertNotNull(instance);

            var value = instance.get();
            assertThat(value, greaterThan(0));
            assertThat(value, lessThanOrEqualTo(1000));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("anyIntegers() returns non-null value")
        void testAnyIntegers() {
            AlchemyGenerator<Integer> generator = anyIntegers();
            var value = generator.get();
            assertNotNull(value);
        }

        @RepeatedTest(DEFAULT_ITERATIONS / 5)
        @DisplayName("integersFromFixedList() only generates values from the list")
        void testIntegersFromFixedList() {
            var values = new ArrayList<Integer>();

            for (int i = 0; i < 15; i++) {
                values.add(RANDOM.nextInt(4, 35));
            }

            var instance = integersFromFixedList(values);
            var value = instance.get();
            assertThat(value, isIn(values));
        }

        @RepeatedTest(20)
        @DisplayName("negativeIntegers() returns values < 0")
        void testNegativeIntegers() {
            var instance = negativeIntegers();
            assertNotNull(instance);

            var value = instance.get();
            assertThat(value, lessThan(0));
        }

        @Test
        @DisplayName("throws IllegalArgumentException for bad bounds")
        void testIntegersWithBadBounds() {
            assertThrows(IllegalArgumentException.class, () -> integers(7, 3));
            assertThrows(IllegalArgumentException.class, () -> integers(-10, -100));
            assertThrows(IllegalArgumentException.class, () -> integers(50, -600));
            assertThrows(IllegalArgumentException.class, () -> integers(10, 10));
            assertThrows(IllegalArgumentException.class, () -> integers(-10, -10));
        }
    }

    // ===================== Longs =====================

    @Nested
    @DisplayName("Long Tests")
    class LongTests {

        @RepeatedTest(10)
        @DisplayName("generates longs within bounds (exclusive upper)")
        void testLongs() {
            long lowerBound = RANDOM.nextLong(0L, Long.MAX_VALUE / 2);
            long upperBound = RANDOM.nextLong(lowerBound + 1, Long.MAX_VALUE);

            var instance = longs(lowerBound, upperBound);

            var value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }

        @Test
        @DisplayName("handles negative ranges")
        void testLongsWithNegativeRange() {
            // Subtest 1: -10 to ~150T
            {
                long lowerBound = -10;
                long upperBound = 150_435_353_256_241L;
                var instance = longs(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThan(upperBound));
                });
            }

            // Subtest 2: -493T... to -500K
            {
                long lowerBound = -493_435_754_432_216_763L;
                long upperBound = -500_000L;
                var instance = longs(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThan(upperBound));
                });
            }

            // Subtest 3: MIN_VALUE to -1
            {
                long lowerBound = Long.MIN_VALUE;
                long upperBound = -1L;
                var instance = longs(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThan(upperBound));
                });
            }

            // Subtest 4: MIN_VALUE to 0
            {
                long lowerBound = Long.MIN_VALUE;
                long upperBound = 0L;
                var instance = longs(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThan(upperBound));
                });
            }
        }

        @Test
        @DisplayName("throws IllegalArgumentException for bad bounds")
        void testLongsWithBadBounds() {
            assertThrows(IllegalArgumentException.class, () -> longs(7_423_352_214L, 3L));
            assertThrows(IllegalArgumentException.class, () -> longs(-10L, -100L));
            assertThrows(IllegalArgumentException.class, () -> longs(50L, -600L));
            assertThrows(IllegalArgumentException.class, () -> longs(50L, 50L));
            assertThrows(IllegalArgumentException.class, () -> longs(-50L, -50L));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("generates with MIN/MAX bounds (exclusive upper)")
        void testLongsWithMinAndMax() {
            long lowerBound = Long.MIN_VALUE;
            long upperBound = Long.MAX_VALUE;

            var instance = longs(lowerBound, upperBound);

            var value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }

        @RepeatedTest(10)
        @DisplayName("positiveLongs() returns values > 0L")
        void testPositiveLongs() {
            var instance = positiveLongs();
            assertNotNull(instance);

            var value = instance.get();
            assertThat(value, greaterThan(0L));
        }

        @RepeatedTest(10)
        @DisplayName("smallPositiveLongs() returns values in (0, 10_000]")
        void testSmallPositiveLongs() {
            var instance = smallPositiveLongs();
            assertNotNull(instance);

            var value = instance.get();
            assertThat(value, greaterThan(0L));
            assertThat(value, lessThanOrEqualTo(10_000L));
        }

        @Test
        @DisplayName("anyLongs() returns non-null value")
        void testAnyLongs() {
            var generator = anyLongs();
            var value = generator.get();
            assertNotNull(value);
        }
    }

    // ===================== Doubles =====================

    @Nested
    @DisplayName("Double Tests")
    class DoubleTests {

        @RepeatedTest(10)
        @DisplayName("generates doubles within bounds (inclusive upper)")
        void testDoubles() {
            double lowerBound = 80.0;
            double upperBound = 190.0;

            var instance = doubles(lowerBound, upperBound);

            var value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThanOrEqualTo(upperBound));
        }

        @Test
        @DisplayName("handles negative ranges")
        void testDoublesWithNegativeRange() {
            // Subtest 1: -1343 to ~2M
            {
                double lowerBound = -1343.0;
                double upperBound = 2_044_532.3;
                var generator = doubles(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = generator.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThanOrEqualTo(upperBound));
                });
            }

            // Subtest 2: -492K to -5K
            {
                double lowerBound = -492_425.0;
                double upperBound = -5_945.0;
                var instance = doubles(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThanOrEqualTo(upperBound));
                });
            }
        }

        @Test
        @DisplayName("throws IllegalArgumentException for bad bounds")
        void testDoublesWithBadBounds() {
            assertThrows(IllegalArgumentException.class, () -> doubles(50.0, 35.0));
            assertThrows(IllegalArgumentException.class, () -> doubles(50.0, -35.0));
            assertThrows(IllegalArgumentException.class, () -> doubles(-50.0, -350.0));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("doublesFromFixedList() only generates values from list")
        void testDoublesFromFixedList() {
            var values = new ArrayList<Double>();

            for (int i = 0; i < 15; i++) {
                values.add(RANDOM.nextDouble(4.0, 365.0));
            }

            var generator = doublesFromFixedList(values);
            var value = generator.get();
            assertThat(value, isIn(values));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("positiveDoubles() returns values > 0.0")
        void testPositiveDoubles() {
            var generator = positiveDoubles();
            assertNotNull(generator);

            var value = generator.get();
            assertThat(value, greaterThan(0.0));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("smallPositiveDoubles() returns values in (0, 1000]")
        void testSmallPositiveDoubles() {
            var generator = smallPositiveDoubles();
            assertNotNull(generator);

            var value = generator.get();
            assertThat(value, greaterThan(0.0));
            assertThat(value, lessThanOrEqualTo(1000.0));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("negativeDoubles() returns values in (-Double.MAX, 0]")
        void testNegativeDoubles() {
            var generator = negativeDoubles();
            assertNotNull(generator);
            var value = generator.get();
            assertNotNull(value);
            assertThat(value, lessThan(0.0));
        }

        @RepeatedTest(5)
        @DisplayName("anyDoubles() returns non-null value")
        void testAnyDoubles() {
            var generator = anyDoubles();
            var value = generator.get();
            assertNotNull(value);
        }
    }

    // ===================== Floats =====================

    @Nested
    @DisplayName("Float Tests")
    class FloatTests {

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("generates floats within bounds (inclusive upper)")
        void testFloats() {
            float lowerBound = 80.0f;
            float upperBound = 190.0f;

            var instance = floats(lowerBound, upperBound);

            var value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThanOrEqualTo(upperBound));
        }

        @Test
        @DisplayName("handles negative ranges")
        void testFloatsWithNegativeRange() {
            // Subtest 1: -1343 to ~2M
            {
                float lowerBound = -1343.0f;
                float upperBound = 2_044_532.3f;
                var instance = floats(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThanOrEqualTo(upperBound));
                });
            }

            // Subtest 2: -492K to -5K
            {
                float lowerBound = -492_425.0f;
                float upperBound = -5_945.0f;
                var instance = floats(lowerBound, upperBound);

                repeatBlock(() -> {
                    var value = instance.get();
                    assertThat(value, greaterThanOrEqualTo(lowerBound));
                    assertThat(value, lessThanOrEqualTo(upperBound));
                });
            }
        }

        @Test
        @DisplayName("throws IllegalArgumentException for bad bounds")
        void testFloatsWithBadBounds() {
            assertThrows(IllegalArgumentException.class, () -> floats(50.0f, 35.0f));
            assertThrows(IllegalArgumentException.class, () -> floats(50.0f, -35.0f));
            assertThrows(IllegalArgumentException.class, () -> floats(-50.0f, -350.0f));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("floatsFromFixedList() only generates values from list")
        void testFloatsFromFixedList() {
            var values = new ArrayList<Float>();
            for (int i = 0; i < 15; i++) {
                values.add(RANDOM.nextFloat(4.0f, 365.0f));
            }

            var instance = floatsFromFixedList(values);
            var value = instance.get();
            assertThat(value, isIn(values));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("positiveFloats() returns values > 0.0f")
        void testPositiveFloats() {
            var instance = positiveFloats();
            assertNotNull(instance);

            var value = instance.get();
            assertThat(value, greaterThan(0.0f));
        }

        @RepeatedTest(DEFAULT_ITERATIONS)
        @DisplayName("smallPositiveFloats() returns values in (0, 1000]")
        void testSmallPositiveFloats() {
            var instance = smallPositiveFloats();
            assertNotNull(instance);

            var value = instance.get();
            assertThat(value, greaterThan(0.0f));
            assertThat(value, lessThanOrEqualTo(1000.0f));
        }

        @RepeatedTest(10)
        @DisplayName("anyFloats() returns non-null value")
        void testAnyFloats() {
            var generator = anyFloats();
            var value = generator.get();
            assertNotNull(value);
        }
    }
}
