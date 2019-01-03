/*
 * Copyright © 2019. Sir Wellington.
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
package tech.sirwellington.alchemy.generator

import org.apache.commons.lang3.RandomUtils
import org.hamcrest.Matchers.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.safeDecrement
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.safeIncrement
import tech.sirwellington.alchemy.generator.Throwables.assertThrows
import java.util.ArrayList


@RunWith(MockitoJUnitRunner::class)
class NumberGeneratorsTest
{

    @Test
    fun testCannotInstantiate()
    {
        println("testCannotInstantiate")

        assertThrows { NumberGenerators() }
                .isInstanceOf(IllegalAccessException::class.java)

        assertThrows { NumberGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }
}

@RunWith(MockitoJUnitRunner::class)
class IntegersTests
{

    @Test
    fun testIntegers()
    {
        println("testIntegers")

        val lowerBound = RandomUtils.nextInt(0, Integer.MAX_VALUE / 2)
        val upperBound = RandomUtils.nextInt(lowerBound, Integer.MAX_VALUE)
        val instance = NumberGenerators.integers(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }
    }

    @Test
    fun testIntegersWithMinAndMax()
    {
        println("testIntegersWithMinAndMax")

        val lowerBound = Integer.MIN_VALUE
        val upperBound = Integer.MAX_VALUE

        val instance = NumberGenerators.integers(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }
    }

    @Test
    fun testIntegersWithNegativeRange()
    {
        println("testIntegersWithNegativeRange")

        var lowerBound = -10
        var upperBound = 150
        var instance = NumberGenerators.integers(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }

        lowerBound = -4934
        upperBound = -500
        instance = NumberGenerators.integers(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }

        lowerBound = -5000
        upperBound = -1
        instance = NumberGenerators.integers(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }

        lowerBound = Integer.MIN_VALUE
        upperBound = -1
        instance = NumberGenerators.integers(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }

        lowerBound = Integer.MIN_VALUE
        upperBound = 0
        instance = NumberGenerators.integers(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }
    }

    @Test
    fun testPositiveIntegers()
    {
        println("testPositiveIntegers")

        val instance = NumberGenerators.positiveIntegers()
        assertNotNull(instance)

        doInLoop()
        {
            assertThat(instance.get(), greaterThan(0))
        }
    }

    @Test
    fun testSmallPositiveIntegers()
    {
        println("testSmallPositiveIntegers")

        val instance = NumberGenerators.smallPositiveIntegers()
        assertThat(instance, notNullValue())

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThan(0))
            assertThat(value, lessThanOrEqualTo(1000))
        }
    }

    @Test
    fun testAnyIntegers()
    {
        val generator = NumberGenerators.anyIntegers()
        val value = generator.get()

        assertThat(value, notNullValue())
    }

    @Test
    fun testIntegersFromFixedList()
    {
        println("testIntegersFromFixedList")
        val values = ArrayList<Int>()

        doInLoop()
        {
            values.add(RandomUtils.nextInt(4, 35))
        }

        val instance = NumberGenerators.integersFromFixedList(values)

        doInLoop()
        {
            val value = instance.get()
            assertTrue(values.contains(value))
        }
    }


    @Test
    fun testNegativeIntegers()
    {
        println("testNegativeIntegers")

        val instance = NumberGenerators.negativeIntegers()
        assertThat(instance, notNullValue())

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, lessThan(0))
        }
    }


    @Test
    fun testIntegersWithBadBounds()
    {
        println("testIntegersWithBadBounds")

        assertThrows { NumberGenerators.integers(7, 3) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.integers(-10, -100) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.integers(50, -600) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.integers(10, 10) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.integers(-10, -10) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

}

@RunWith(MockitoJUnitRunner::class)
class LongTests
{

    @Test
    fun testLongs()
    {
        println("testLongs")

        val lowerBound = RandomUtils.nextLong(0L, Long.MAX_VALUE / 2)
        val upperBound = RandomUtils.nextLong(lowerBound, Long.MAX_VALUE)
        val instance = NumberGenerators.longs(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }
    }

    @Test
    fun testLongsWithNegativeRange()
    {
        println("testLongsWithNegativeRange")
        var lowerBound: Long = -10
        var upperBound = 150_435_353_256_241L
        var instance = NumberGenerators.longs(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }

        lowerBound = -493_435_754_432_216_763L
        upperBound = -500000
        instance = NumberGenerators.longs(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }

        lowerBound = Long.MIN_VALUE
        upperBound = -1L
        instance = NumberGenerators.longs(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }

        lowerBound = Long.MIN_VALUE
        upperBound = 0L
        instance = NumberGenerators.longs(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }
    }

    @Test
    fun testLongsWithBadBounds()
    {
        println("testLongsWithBadBounds")

        assertThrows { NumberGenerators.longs(7_423_352_214L, 3) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.longs(-10L, -100L) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.longs(50L, -600L) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.longs(50L, 50L) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.longs(-50L, -50L) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testLongsWithMinAndMax()
    {
        println("testLongsWithMinAndMax")

        val lowerBound = Long.MIN_VALUE
        val upperBound = Long.MAX_VALUE

        val instance = NumberGenerators.longs(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThan(upperBound))
        }
    }

    @Test
    fun testPositiveLongs()
    {
        println("testPositiveLongs")

        val instance = NumberGenerators.positiveLongs()
        assertThat(instance, notNullValue())

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThan(0L))
        }
    }

    @Test
    fun testSmallPositiveLongs()
    {
        println("testSmallPositiveLongs")

        val instance = NumberGenerators.smallPositiveLongs()
        assertThat(instance, notNullValue())

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThan(0L))
            assertThat(value, lessThanOrEqualTo(10_000L))
        }
    }

    @Test
    fun testAnyLongs()
    {
        val generator = NumberGenerators.anyLongs()
        val value = generator.get()

        assertThat(value, notNullValue())
    }

}

@RunWith(MockitoJUnitRunner::class)
class DoubleTests
{

    @Test
    fun testDoubles()
    {
        println("testDoubles")

        val lowerBound = 80.0
        val upperBound = 190.0
        val instance = NumberGenerators.doubles(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThanOrEqualTo(upperBound))
        }
    }

    @Test
    fun testDoublesWithNegativeRange()
    {
        println("testDoublesWithNegativeRange")

        var lowerBound = -1343.0
        var upperBound = 2044532.3
        var instance = NumberGenerators.doubles(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThanOrEqualTo(upperBound))
        }

        lowerBound = -492425.0
        upperBound = -5945.0
        instance = NumberGenerators.doubles(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThanOrEqualTo(upperBound))
        }
    }

    @Test
    fun testDoublesWithBadBounds()
    {
        println("testDoublesWithBadBounds")

        assertThrows { NumberGenerators.doubles(50.0, 35.0) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.doubles(50.0, -35.0) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.doubles(-50.0, -350.0) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testDoublesFromFixedList()
    {
        println("testDoublesFromFixedList")

        val values = ArrayList<Double>()

        doInLoop()
        {
            values.add(RandomUtils.nextDouble(4.0, 365.0))
        }

        val instance = NumberGenerators.doublesFromFixedList(values)

        doInLoop()
        {
            val value = instance.get()
            assertTrue(values.contains(value))
        }
    }


    @Test
    fun testPositiveDoubles()
    {
        println("testPositiveDoubles")
        val instance = NumberGenerators.positiveDoubles()
        assertNotNull(instance)

        doInLoop()
        {
            assertThat(instance.get(), greaterThan(0.0))
        }

    }

    @Test
    fun testSmallPositiveDoubles()
    {
        println("testSmallPositiveDoubles")

        val instance = NumberGenerators.smallPositiveDoubles()
        assertThat(instance, notNullValue())

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThan(0.0))
            assertThat(value, lessThanOrEqualTo(1000.0))
        }
    }

    @Test
    fun testAnyDoubles()
    {
        val generator = NumberGenerators.anyDoubles()
        val value = generator.get()
    }

}

@RunWith(MockitoJUnitRunner::class)
class FloatTests
{

    @Test
    fun testFloats()
    {
        println("testFloats")

        val lowerBound = 80.0f
        val upperBound = 190.0f
        val instance = NumberGenerators.floats(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThanOrEqualTo(upperBound))
        }
    }

    @Test
    fun testFloatsWithNegativeRange()
    {
        println("testFloatsWithNegativeRange")

        var lowerBound = -1343.0f
        var upperBound = 2044532.3f
        var instance = NumberGenerators.floats(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThanOrEqualTo(upperBound))
        }

        lowerBound = -492425.0f
        upperBound = -5945.0f
        instance = NumberGenerators.floats(lowerBound, upperBound)

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThanOrEqualTo(lowerBound))
            assertThat(value, lessThanOrEqualTo(upperBound))
        }
    }

    @Test
    fun testFloatsWithBadBounds()
    {
        println("testFloatsWithBadBounds")

        assertThrows { NumberGenerators.floats(50.0f, 15.0f) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.floats(60.0f, -35.0f) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { NumberGenerators.floats(-50.0f, -550.0f) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testFloatsFromFixedList()
    {
        println("testFloatsFromFixedList")

        val values = ArrayList<Float>()

        doInLoop()
        {
            values.add(RandomUtils.nextFloat(1.0f, 365.0f))
        }

        val instance = NumberGenerators.floatsFromFixedList(values)

        doInLoop()
        {
            val value = instance.get()
            assertTrue(values.contains(value))
        }
    }


    @Test
    fun testPositiveFloats()
    {
        println("testPositiveFloats")

        val instance = NumberGenerators.positiveFloats()
        assertNotNull(instance)

        doInLoop()
        {
            assertThat(instance.get(), greaterThan(0.0f))
        }

    }

    @Test
    fun testSmallPositiveFloats()
    {
        println("testSmallPositiveFloats")

        val instance = NumberGenerators.smallPositiveFloats()
        assertThat(instance, notNullValue())

        doInLoop()
        {
            val value = instance.get()
            assertThat(value, greaterThan(0.0f))
            assertThat(value, lessThanOrEqualTo(1000.0f))
        }
    }

    @Test
    fun testAnyFloats()
    {
        val generator = NumberGenerators.anyFloats()

        doInLoop()
        {
            val value = generator.get()
            assertThat(value, notNullValue())
        }

    }

}

@RunWith(MockitoJUnitRunner::class)
class InternalFunctionsTests
{

    @Test
    fun testSafeIncrement_long()
    {
        println("testSafeIncrement_long")

        var value = one(NumberGenerators.longs(-10_000L, 10_000L))
        var result = safeIncrement(value)
        assertThat(result, `is`(value + 1))

        value = Long.MAX_VALUE
        result = safeIncrement(value)
        assertThat(result, `is`(value))

        value = Long.MIN_VALUE
        result = safeIncrement(value)
        assertThat(result, `is`(value + 1))
    }

    @Test
    fun testSafeDecrement_long()
    {
        println("testSafeDecrement_long")

        var value = one(NumberGenerators.longs(-10_000L, 10_000L))
        var result = safeDecrement(value)
        assertThat(result, `is`(value - 1))

        value = Long.MIN_VALUE
        result = safeDecrement(value)
        assertThat(result, `is`(value))

        value = Long.MAX_VALUE
        result = safeDecrement(value)
        assertThat(result, `is`(value - 1))
    }

    @Test
    fun testSafeIncrement_int()
    {
        println("testSafeIncrement_int")

        var value = one(NumberGenerators.integers(-10000, 10000))
        var result = safeIncrement(value)
        assertThat(result, `is`(value + 1))

        value = Integer.MAX_VALUE
        result = safeIncrement(value)
        assertThat(result, `is`(value))

        value = Integer.MIN_VALUE
        result = safeIncrement(value)
        assertThat(result, `is`(value + 1))
    }

    @Test
    fun testSafeDecrement_int()
    {
        println("testSafeDecrement_int")

        var value = one(NumberGenerators.integers(-10000, 10000))
        var result = safeDecrement(value)
        assertThat(result, `is`(value - 1))

        value = Integer.MIN_VALUE
        result = safeDecrement(value)
        assertThat(result, `is`(value))

        value = Integer.MAX_VALUE
        result = safeDecrement(value)
        assertThat(result, `is`(value - 1))
    }


}
