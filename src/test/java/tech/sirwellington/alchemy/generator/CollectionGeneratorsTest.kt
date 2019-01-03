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

import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.lang3.RandomUtils
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.atLeast
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.integers
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.negativeIntegers
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.positiveIntegers
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.hexadecimalString
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.strings
import tech.sirwellington.alchemy.generator.Throwables.assertThrows
import java.util.ArrayList
import java.util.UUID

/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class CollectionGeneratorsTest
{

    private var iterations: Int = 0

    @Before
    fun setUp()
    {
        iterations = RandomUtils.nextInt(500, 5000)
    }

    @Test
    fun testCannotInstantiate()
    {
        println("testCannotInstantiate")

        assertThrows { CollectionGenerators() }
                .isInstanceOf(IllegalAccessException::class.java)

        assertThrows { CollectionGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }

    @Test
    fun testListOf_AlchemyGenerator()
    {
        println("testListOf_AlchemyGenerator")

        val value = Any()
        val generator = mock(AlchemyGenerator::class.java)
        whenever(generator.get()).thenReturn(value)

        val result = CollectionGenerators.listOf(generator)
        assertThat(result.isEmpty(), `is`(false))
        result.forEach { assertThat(it, `is`(value)) }
    }

    @Test
    fun testListOf_AlchemyGenerator_int()
    {
        println("testListOf_AlchemyGenerator_int")

        val value = Any()
        val size = 50
        val generator = mock(AlchemyGenerator::class.java)
        whenever(generator.get()).thenReturn(value)

        val result = CollectionGenerators.listOf(generator, 50)

        assertThat(result, notNullValue())
        assertThat(result.size, `is`(size))

        result.forEach { assertThat(it, `is`(value)) }
    }

    @Test
    fun testMapOfWithInt()
    {
        println("testMapOfWithInt")

        val string = strings(50).get()
        val valueGenerator = AlchemyGenerator { string }
        val size = integers(5, 100).get()

        val result = CollectionGenerators.mapOf<String, String>(StringGenerators.uuids, valueGenerator, size)
        assertThat(result, notNullValue())
        assertThat(result.size, `is`(size))

        for ((key, value) in result)
        {
            UUID.fromString(key)
            assertThat(value, `is`(string))
        }

    }

    @Test
    fun testConvenienceMapOf()
    {
        println("testConvenienceMapOf")

        val keyGenerator = mock(AlchemyGenerator::class.java)
        val valueGenerator = mock(AlchemyGenerator::class.java)

        whenever(keyGenerator.get())
                .thenAnswer { one(strings()) }

        whenever(valueGenerator.get())
                .thenAnswer { one(positiveIntegers()) }

        val result = CollectionGenerators.mapOf(keyGenerator, valueGenerator)
        assertThat(result, notNullValue())
        assertThat(result.isEmpty(), `is`(false))

        //'At least', in case duplicate entries were generated
        verify(keyGenerator, atLeast(result.size)).get()
        verify(valueGenerator, atLeast(result.size)).get()
    }

    @Test
    fun testFromList()
    {
        println("testFromList")

        val list = ArrayList<String>()

        val size = one(integers(1, 100))
        for (i in 0..size - 1)
        {
            list.add(one(hexadecimalString(15)))
        }

        val instance = CollectionGenerators.fromList(list)
        assertThat(instance, notNullValue())

        doInLoop {
            val value = instance.get()
            assertThat(list.contains(value), `is`(true))
        }
    }


    @Test
    fun testListOfEdgeCases()
    {
        println("testListOfEdgeCases")

        val badSize = one(negativeIntegers())
        assertThrows { CollectionGenerators.listOf(StringGenerators.uuids(), badSize) }
                .isInstanceOf(IllegalArgumentException::class.java)

        val result = CollectionGenerators.listOf(StringGenerators.uuids, 0)
        assertThat(result, notNullValue())
        assertThat(result, `is`(empty<String>()))

    }
}
