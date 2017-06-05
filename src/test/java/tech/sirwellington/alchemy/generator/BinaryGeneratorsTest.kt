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

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.integers
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.negativeIntegers
import tech.sirwellington.alchemy.generator.Throwables.assertThrows

/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class BinaryGeneratorsTest
{

    @Before
    fun setUp()
    {
    }

    @Test
    fun testCannotInstantiate()
    {
        println("testCannotInstantiate")

        assertThrows { BinaryGenerators() }
                .isInstanceOf(IllegalAccessException::class.java)

        assertThrows { BinaryGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }

    @Test
    fun testBinary()
    {
        println("testBinary")

        val bytes = integers(50, 5000).get()
        val instance = BinaryGenerators.binary(bytes)

        assertNotNull(instance)

        doInLoop {
            val value = instance.get()
            assertThat(value, notNullValue())
            assertThat(value.size, `is`(bytes))
        }
    }

    @Test
    fun testBinaryEdgeCases()
    {
        println("testBinaryGeneratorEdgeCases")


        val instance = BinaryGenerators.binary(0)
        assertThat(instance, notNullValue())

        val result = instance.get()
        assertThat(result, notNullValue())
        assertThat(result.size, `is`(0))

        val length = one(negativeIntegers())
        assertThrows { BinaryGenerators.binary(length) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testByteBuffers()
    {
        println("testByteBuffers")

        val size = one(integers(10, 1000))
        val instance = BinaryGenerators.byteBuffers(size)
        assertThat(instance, notNullValue())

        doInLoop {
            val result = instance.get()
            assertThat(result, notNullValue())
            assertThat(result.limit(), `is`(size))
            assertThat(result.array().size, `is`(size))
        }
    }

    @Test
    fun testByteBuffersEdgeCases()
    {
        println("testByteBuffersEdgeCases")

        val size = one(negativeIntegers())
        assertThrows { BinaryGenerators.byteBuffers(size) }
                .isInstanceOf(IllegalArgumentException::class.java)

        val result = BinaryGenerators.byteBuffers(0).get()
        assertThat(result, notNullValue())
        assertThat(result.limit(), `is`(0))
    }

    @Test
    fun testBytes()
    {
        println("testBytes")

        val generator = BinaryGenerators.bytes()
        assertThat(generator, notNullValue())

        doInLoop {
            val result = generator.get()
            assertThat(result, notNullValue())
        }
    }

}
