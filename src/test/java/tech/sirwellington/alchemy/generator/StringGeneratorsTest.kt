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

import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.isEmptyOrNullString
import org.hamcrest.Matchers.isEmptyString
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.sameInstance
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.DateGenerators.Companion.anyTime
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.integers
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.negativeIntegers
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.smallPositiveIntegers
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.strings
import tech.sirwellington.alchemy.generator.Throwables.assertThrows
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class StringGeneratorsTest
{


    @Before
    fun setUp()
    {
    }

    @Test
    fun testCannotInstantiate()
    {
        println("testCannotInstantiate")

        assertThrows { StringGenerators() }
                .isInstanceOf(IllegalAccessException::class.java)

        assertThrows { StringGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }


    @Test
    fun testStrings()
    {
        println("testStrings")

        val instance = strings()
        assertThat<AlchemyGenerator<String>>(instance, notNullValue())

        doInLoop { assertThat(instance.get(), not(isEmptyOrNullString())) }
    }

    @Test
    fun testStringsWithLength()
    {
        println("testStringsWithLength")

        val length = one(smallPositiveIntegers())
        val instance = strings(length)
        assertNotNull(instance)

        doInLoop {
            val value = instance.get()
            assertTrue(value.length == length)
        }
    }

    @Test
    fun testStringsWithBadSize()
    {
        println("testStringsWithBadSize")

        val length = one(negativeIntegers())
        assertThrows { StringGenerators.strings(length) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testHexadecimalString()
    {
        println("testHexadecimalString")

        val length = 90
        val instance = StringGenerators.hexadecimalString(length)

        doInLoop {
            val value = instance.get()
            assertTrue(value.length == length)
        }
    }

    @Test
    fun testHexadecimalStringWithBadSize()
    {
        println("testHexadecimalStringWithBadSize")

        val length = -90
        assertThrows { StringGenerators.hexadecimalString(length) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testAlphabeticStringWithLength()
    {
        println("testAlphabeticStringWithLength")

        val length = one(integers(40, 100))

        val instance = StringGenerators.alphabeticStrings(length)

        doInLoop {
            val value = instance.get()
            assertThat(value.length, `is`(length))
        }
    }

    @Test
    fun testAlphabeticString()
    {
        println("testAlphabeticString")

        val instance = StringGenerators.alphabeticStrings()

        doInLoop {
            val value = instance.get()
            assertThat(value, not(isEmptyString()))
        }
    }

    @Test
    fun testAlphabeticStringWithBadSize()
    {
        println("testAlphabeticStringWithBadSize")

        val length = 0
        assertThrows { StringGenerators.alphabeticStrings(length) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testAlphanumericString()
    {
        println("testAlphanumericString")

        val instance = StringGenerators.alphanumericStrings()

        doInLoop {
            val value = instance.get()
            assertThat(value, not(isEmptyString()))
            assertThat(value.length, greaterThanOrEqualTo(10))
            assertThat(value.length, lessThanOrEqualTo(100))
        }
    }

    @Test
    fun testAlphanumericStringWithLength()
    {
        println("testAlphanumericStringWithLength")

        val length = one(integers(10, 100))
        val instance = StringGenerators.alphanumericStrings(length)

        doInLoop {
            val value = instance.get()
            assertThat(value.length, `is`(length))
        }

        //Edge cases
        assertThrows { StringGenerators.alphabeticStrings(one(negativeIntegers())) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }


    @Test
    fun testNumericString()
    {
        println("testNumericString")

        val instance = StringGenerators.numericStrings()
        assertThat(instance, notNullValue())

        doInLoop {
            val value = instance.get()
            assertThat(value, not(isEmptyOrNullString()))
            assertAllDigits(value)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testNumericStringWithLength()
    {
        println("testNumericStringWithLength")

        val length = one(integers(1, 100))
        val instance = StringGenerators.numericStrings(length)
        assertThat(instance, notNullValue())

        doInLoop {
            val value = instance.get()
            assertThat(value, not(isEmptyOrNullString()))
            assertThat(value.length, `is`(length))
            assertAllDigits(value)
        }
    }


    @Test
    fun testStringsFromFixedList()
    {
        println("testStringsFromFixedList")

        val values = ArrayList<String>()

        doInLoop { values.add(RandomStringUtils.randomAlphabetic(it + 1)) }
        val instance = StringGenerators.stringsFromFixedList(values)

        doInLoop {
            val value = instance.get()
            assertTrue(values.contains(value))
        }
    }

    @Test
    fun testStringsFromFixedList_List()
    {
        println("testStringsFromFixedList_List")

        val values = ArrayList<String>()
        val one = StringGenerators.strings(10).get()
        val two = strings(10).get()
        val three = strings(10).get()
        values.add(one)
        values.add(two)
        values.add(three)

        val instance = StringGenerators.stringsFromFixedList(one, two, three)

        doInLoop { assertThat(instance.get(), org.hamcrest.Matchers.isIn(values)) }
    }

    @Test
    fun testStringsFromFixedList_StringArr()
    {
        println("testStringsFromFixedList_StringArr")

        val alphabetic = StringGenerators.alphabeticStrings(10)
        val values = arrayOf(alphabetic.get(), alphabetic.get(), alphabetic.get())
        val instance = StringGenerators.stringsFromFixedList(*values)
        assertThat(instance, notNullValue())

        doInLoop {
            val result = instance.get()
            assertThat(result, Matchers.isIn(values))
        }
    }

    @Test
    fun testAlphabeticStringWithNoArgs()
    {
        println("testAlphabeticStringWithNoArgs")

        val instance = StringGenerators.alphabeticStrings()
        assertThat(instance, notNullValue())

        doInLoop {
            val value = instance.get()
            assertThat(value, notNullValue())
            assertThat(value.length, greaterThanOrEqualTo(10))
            assertThat(value.length, lessThanOrEqualTo(100))
        }

    }

    @Test
    fun testUuids()
    {
        println("testUuids")

        val uuids = HashSet<String>()

        val instance = StringGenerators.uuids

        val iterations = AtomicInteger()
        doInLoop {
            val value = instance.get()
            assertThat(value, notNullValue())
            assertThat(value.isEmpty(), `is`(false))
            uuids.add(value)
            iterations.incrementAndGet()
        }

        assertThat(uuids.size, `is`(iterations.get()))
    }

    @Test
    fun testUuidsFunction()
    {
        println("testUuidsFunction")

        assertThat(StringGenerators.uuids(), sameInstance(StringGenerators.uuids))
    }

    @Test
    fun testAsString()
    {
        println("testAsString")

        val generator = mock(AlchemyGenerator::class.java)

        whenever(generator.get())
                .thenReturn(one(anyTime()))

        val instance = StringGenerators.asString(generator)
        assertThat(instance, notNullValue())

        doInLoop {
            val result = instance.get()
            assertThat(result, not(isEmptyOrNullString()))
            verify(generator, times(it + 1)).get()
        }

    }

    private fun assertAllDigits(value: String)
    {
        for (c in value.toCharArray())
        {
            assertThat(Character.isDigit(c), `is`(true))
        }
    }

}
