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
import org.hamcrest.Matchers
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.Throwables.assertThrows

/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class EnumGeneratorsTest
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

        assertThrows { EnumGenerators() }
                .isInstanceOf(IllegalAccessException::class.java)

        assertThrows { EnumGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }

    @Test
    fun testEnumValueOf()
    {
        println("testEnumValueOf")
        val fruits = EnumGenerators.enumValueOf(Fruit::class.java)

        assertThat(fruits, notNullValue())

        for (i in 0..iterations - 1)
        {
            val fruit = fruits.get()
            assertThat(fruit, notNullValue())
            assertThat(fruit, Matchers.isA(Fruit::class.java))
        }
    }

    internal enum class Fruit
    {

        Apple, Orange, Pear
    }

}
