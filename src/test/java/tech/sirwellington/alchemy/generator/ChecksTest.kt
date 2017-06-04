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

import org.apache.commons.lang3.RandomStringUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.Throwables.assertThrows

/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class ChecksTest
{

    private lateinit var message: String

    @Before
    fun setUp()
    {
        message = "some message"
    }

    @Test
    fun testCheckNotNull()
    {
        println("testCheckNotNull")

        val `object` = Any()
        checkNotNull(`object`)
        checkNotNull(`object`, message!!)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCheckNotNullExpecting()
    {
        println("testCheckNotNullExpecting")

        checkNotNull(null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCheckNotNullExpectingWithMessage()
    {
        println("testCheckNotNullExpectingWithMessage")

        checkNotNull(null, message!!)
    }

    @Test
    fun testCheckThat()
    {
        println("testCheckThat")

        checkThat(true)
        checkThat(true, message!!)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCheckThatExpecting()
    {
        println("testCheckThatExpecting")

        checkThat(false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCheckThatExpectingWithMessage()
    {
        println("testCheckThatExpectingWithMessage")

        checkThat(false, message!!)
    }

    @Test
    fun testCheckNotEmptyString()
    {
        println("testCheckNotEmptyString")

        val string = RandomStringUtils.randomAlphanumeric(10)
        checkNotEmpty(string)

        val nullString: String? = null
        assertThrows { checkNotEmpty(nullString) }
                .isInstanceOf(IllegalArgumentException::class.java)

        val emptyString = ""
        assertThrows { checkNotEmpty(emptyString) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testCheckNotEmptyStringWithMessage()
    {
        println("testCheckNotEmptyStringWithMessage")

        val message = RandomStringUtils.randomAlphabetic(100)
        val string = RandomStringUtils.randomAscii(10)
        checkNotEmpty(string, message)
        checkNotEmpty(string, null)
        checkNotEmpty(string, "")

        val nullString: String? = null
        assertThrows { checkNotEmpty(nullString, message) }
                .isInstanceOf(IllegalArgumentException::class.java)

        val emptyString = ""
        assertThrows { checkNotEmpty(emptyString, message) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }
}
