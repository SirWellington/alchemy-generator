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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.safeDecrement;
import static tech.sirwellington.alchemy.generator.NumberGenerators.safeIncrement;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;


/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class NumberGeneratorsTest
{

    private int iterations;

    @Before
    public void setUp()
    {
        iterations = RandomUtils.nextInt(5000, 50_000);
    }

    @Test
    public void testCannotInstantiate()
    {
        System.out.println("testCannotInstantiate");

        assertThrows(() -> new NumberGenerators())
                .isInstanceOf(IllegalAccessException.class);

        assertThrows(() -> NumberGenerators.class.newInstance())
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void testIntegers()
    {
        System.out.println("testIntegers");

        int lowerBound = RandomUtils.nextInt(0, Integer.MAX_VALUE / 2);
        int upperBound = RandomUtils.nextInt(lowerBound, Integer.MAX_VALUE);
        AlchemyGenerator<Integer> instance = NumberGenerators.integers(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }
    }

    @Test
    public void testIntegersWithMinAndMax()
    {
        System.out.println("testIntegersWithMinAndMax");

        int lowerBound = Integer.MIN_VALUE;
        int upperBound = Integer.MAX_VALUE;

        AlchemyGenerator<Integer> instance = NumberGenerators.integers(lowerBound, upperBound);

        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }
    }

    @Test
    public void testIntegersWithNegativeRange()
    {
        System.out.println("testIntegersWithNegativeRange");
        int lowerBound = -10;
        int upperBound = 150;
        AlchemyGenerator<Integer> instance = NumberGenerators.integers(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }

        lowerBound = -4934;
        upperBound = -500;
        instance = NumberGenerators.integers(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }

        lowerBound = -5000;
        upperBound = -1;
        instance = NumberGenerators.integers(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }

        lowerBound = Integer.MIN_VALUE;
        upperBound = -1;
        instance = NumberGenerators.integers(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }

        lowerBound = Integer.MIN_VALUE;
        upperBound = 0;
        instance = NumberGenerators.integers(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }
    }

    @Test
    public void testIntegersWithBadBounds()
    {
        System.out.println("testIntegersWithBadBounds");
        
        assertThrows(() -> NumberGenerators.integers(7, 3))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> NumberGenerators.integers(-10, -100))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> NumberGenerators.integers(50, -600))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> NumberGenerators.integers(10, 10))
            .isInstanceOf(IllegalArgumentException.class);

         assertThrows(() -> NumberGenerators.integers(-10, -10))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testLongs()
    {
        System.out.println("testLongs");
        
        long lowerBound = RandomUtils.nextLong(0L, Long.MAX_VALUE / 2);
        long upperBound = RandomUtils.nextLong(lowerBound, Long.MAX_VALUE);
        AlchemyGenerator<Long> instance = NumberGenerators.longs(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            long value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }
    }

    @Test
    public void testLongsWithNegativeRange()
    {
        System.out.println("testLongsWithNegativeRange");
        long lowerBound = -10;
        long upperBound = 150_435_353_256_241L;
        AlchemyGenerator<Long> instance = NumberGenerators.longs(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            long value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }

        lowerBound = -493_435_754_432_216_763L;
        upperBound = -500_000;
        instance = NumberGenerators.longs(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            long value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }

        lowerBound = Long.MIN_VALUE;
        upperBound = -1L;
        instance = NumberGenerators.longs(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            long value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }

        lowerBound = Long.MIN_VALUE;
        upperBound = 0L;
        instance = NumberGenerators.longs(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            long value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }
    }

    @Test
    public void testLongsWithBadBounds()
    {
        System.out.println("testLongsWithBadBounds");
        assertThrows(() -> NumberGenerators.longs(7_423_352_214L, 3))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> NumberGenerators.longs(-10L, -100L))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> NumberGenerators.longs(50L, -600L))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> NumberGenerators.longs(50L, 50L))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> NumberGenerators.longs(-50L, -50L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testLongsWithMinAndMax()
    {
        System.out.println("testLongsWithMinAndMax");

        long lowerBound = Long.MIN_VALUE;
        long upperBound = Long.MAX_VALUE;

        AlchemyGenerator<Long> instance = NumberGenerators.longs(lowerBound, upperBound);

        for (int i = 0; i < iterations; ++i)
        {
            long value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThan(upperBound));
        }
    }

    @Test
    public void testDoubles()
    {
        System.out.println("testDoubles");
        double lowerBound = 80.0;
        double upperBound = 190.0;
        AlchemyGenerator<Double> instance = NumberGenerators.doubles(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            double value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThanOrEqualTo(upperBound));
        }
    }

    @Test
    public void testDoublesWithNegativeRange()
    {
        System.out.println("testDoublesWithNegativeRange");
        double lowerBound = -1343.0;
        double upperBound = 2044532.3;
        AlchemyGenerator<Double> instance = NumberGenerators.doubles(lowerBound, upperBound);

        for (int i = 0; i < iterations; ++i)
        {
            double value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThanOrEqualTo(upperBound));
        }

        lowerBound = -492425;
        upperBound = -5945;
        instance = NumberGenerators.doubles(lowerBound, upperBound);
        for (int i = 0; i < iterations; ++i)
        {
            double value = instance.get();
            assertThat(value, greaterThanOrEqualTo(lowerBound));
            assertThat(value, lessThanOrEqualTo(upperBound));
        }
    }

    @Test
    public void testDoublesWithBadBounds()
    {
        System.out.println("testDoublesWithBadBounds");

        assertThrows(() -> NumberGenerators.doubles(50, 35))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> NumberGenerators.doubles(50, -35))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> NumberGenerators.doubles(-50, -350))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testDoublesFromFixedList()
    {
        System.out.println("testDoublesFromFixedList");
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < iterations; ++i)
        {
            values.add(RandomUtils.nextDouble(4, 365));
        }

        AlchemyGenerator<Double> instance = NumberGenerators.doublesFromFixedList(values);

        for (int i = 0; i < iterations; ++i)
        {
            double value = instance.get();
            assertTrue(values.contains(value));
        }
    }

    @Test
    public void testIntegersFromFixedList()
    {
        System.out.println("testIntegersFromFixedList");
        List<Integer> values = new ArrayList<>();

        for (int i = 0; i < iterations; ++i)
        {
            values.add(RandomUtils.nextInt(4, 35));
        }

        AlchemyGenerator<Integer> instance = NumberGenerators.integersFromFixedList(values);

        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertTrue(values.contains(value));
        }
    }

    @Test
    public void testPositiveIntegers()
    {
        System.out.println("testPositiveIntegers");
        AlchemyGenerator<Integer> instance = NumberGenerators.positiveIntegers();
        assertNotNull(instance);

        for (int i = 0; i < iterations; ++i)
        {
            assertThat(instance.get(), greaterThan(0));
        }
    }

    @Test
    public void testPositiveDoubles()
    {
        System.out.println("testPositiveDoubles");
        AlchemyGenerator<Double> instance = NumberGenerators.positiveDoubles();
        assertNotNull(instance);

        for (int i = 0; i < iterations; ++i)
        {
            assertThat(instance.get(), greaterThan(0.0));
        }

    }

    @Test
    public void testSmallPositiveIntegers()
    {
        System.out.println("testSmallPositiveIntegers");

        AlchemyGenerator<Integer> instance = NumberGenerators.smallPositiveIntegers();
        assertThat(instance, notNullValue());

        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertThat(value, greaterThan(0));
            assertThat(value, lessThanOrEqualTo(1000));
        }
    }

    @Test
    public void testNegativeIntegers()
    {
        System.out.println("testNegativeIntegers");

        AlchemyGenerator<Integer> instance = NumberGenerators.negativeIntegers();
        assertThat(instance, notNullValue());

        for (int i = 0; i < iterations; ++i)
        {
            int value = instance.get();
            assertThat(value, lessThan(0));
        }
    }

    @Test
    public void testPositiveLongs()
    {
        System.out.println("testPositiveLongs");

        AlchemyGenerator<Long> instance = NumberGenerators.positiveLongs();
        assertThat(instance, notNullValue());

        for (int i = 0; i < iterations; ++i)
        {
            long value = instance.get();
            assertThat(value, greaterThan(0L));
        }
    }

    @Test
    public void testSmallPositiveLongs()
    {
        System.out.println("testSmallPositiveLongs");

        AlchemyGenerator<Long> instance = NumberGenerators.smallPositiveLongs();
        assertThat(instance, notNullValue());

        for (int i = 0; i < iterations; ++i)
        {
            long value = instance.get();
            assertThat(value, greaterThan(0L));
            assertThat(value, lessThanOrEqualTo(10_000L));
        }
    }

    @Test
    public void testSmallPositiveDoubles()
    {
        System.out.println("testSmallPositiveDoubles");

        AlchemyGenerator<Double> instance = NumberGenerators.smallPositiveDoubles();
        assertThat(instance, notNullValue());

        for (int i = 0; i < iterations; ++i)
        {
            double value = instance.get();
            assertThat(value, greaterThan(0.0));
            assertThat(value, lessThanOrEqualTo(1000.0));
        }
    }

    @Test
    public void testSafeIncrement_long()
    {
        System.out.println("testSafeIncrement_long");

        long value = one(NumberGenerators.longs(-10_000L, 10_000L));
        long result = safeIncrement(value);
        assertThat(result, is(value + 1));

        value = Long.MAX_VALUE;
        result = safeIncrement(value);
        assertThat(result, is(value));

        value = Long.MIN_VALUE;
        result = safeIncrement(value);
        assertThat(result, is(value + 1));
    }

    @Test
    public void testSafeDecrement_long()
    {
        System.out.println("testSafeDecrement_long");

        long value = one(NumberGenerators.longs(-10_000L, 10_000L));
        long result = safeDecrement(value);
        assertThat(result, is(value - 1));

        value = Long.MIN_VALUE;
        result = safeDecrement(value);
        assertThat(result, is(value));

        value = Long.MAX_VALUE;
        result = safeDecrement(value);
        assertThat(result, is(value - 1));
    }

    @Test
    public void testSafeIncrement_int()
    {
        System.out.println("testSafeIncrement_int");

        int value = one(NumberGenerators.integers(-10_000, 10_000));
        int result = safeIncrement(value);
        assertThat(result, is(value + 1));

        value = Integer.MAX_VALUE;
        result = safeIncrement(value);
        assertThat(result, is(value));

        value = Integer.MIN_VALUE;
        result = safeIncrement(value);
        assertThat(result, is(value + 1));
    }

    @Test
    public void testSafeDecrement_int()
    {
        System.out.println("testSafeDecrement_int");

        int value = one(NumberGenerators.integers(-10_000, 10_000));
        int result = safeDecrement(value);
        assertThat(result, is(value - 1));

        value = Integer.MIN_VALUE;
        result = safeDecrement(value);
        assertThat(result, is(value));

        value = Integer.MAX_VALUE;
        result = safeDecrement(value);
        assertThat(result, is(value - 1));
    }

}
