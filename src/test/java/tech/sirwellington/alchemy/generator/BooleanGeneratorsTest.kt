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
package tech.sirwellington.alchemy.generator

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.Throwables.assertThrows
import java.util.*

/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class BooleanGeneratorsTest
{

    @Before
    fun setUp()
    {
    }

    @Test
    fun testCannotInstantiate()
    {
        println("testCannotInstantiate")

        assertThrows { BooleanGenerators() }
                .isInstanceOf(IllegalAccessException::class.java)

        assertThrows { BooleanGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }

    @Test
    fun testBooleans()
    {
        println("testBooleans")

        val instance = BooleanGenerators.booleans()
        assertNotNull(instance)

        val values = HashSet<Boolean>()

        doInLoop {
            val value = instance.get()
            assertNotNull(value)
            values.add(value)
        }

        assertThat(values.size, `is`(2))
    }

    @Test
    fun testAlternatingBooleans()
    {
        println("testAlternatingBooleans")

        val instance = BooleanGenerators.alternatingBooleans()

        var value = instance.get()
        var previous: Boolean

        doInLoop {
            previous = value
            value = instance.get()

            assertThat(value, `is`(not(previous)))
        }
    }

}
