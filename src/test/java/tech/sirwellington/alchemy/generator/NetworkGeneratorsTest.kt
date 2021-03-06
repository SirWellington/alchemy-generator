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

import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class NetworkGeneratorsTest
{

    @Before
    fun setUp()
    {
    }

    @Test
    fun testHttpUrls()
    {
        val generator = NetworkGenerators.httpUrls()
        assertThat(generator, notNullValue())

        doInLoop()
        {
            val result = generator.get()
            assertThat(result, notNullValue())
        }
    }

    @Test
    fun testHttpsUrls()
    {
        val generator = NetworkGenerators.httpsUrls()
        assertThat(generator, notNullValue())

        doInLoop()
        {
            val result = generator.get()
            assertThat(result, notNullValue())
            assertThat(result.toString(), startsWith("https://"))
        }
    }

    @Test
    fun testUrlsWithProtocol()
    {
        doInLoop()
        {
            val scheme = StringGenerators.stringsFromFixedList("http", "https", "file", "ftp").get()
            val generator = NetworkGenerators.urlsWithProtocol(scheme)
            assertThat(generator, notNullValue())

            val result = generator.get()
            assertThat(result, notNullValue())
            assertThat(result.toString(), startsWith(scheme))
        }
    }


    @Test
    fun testPorts()
    {
        val generator = NetworkGenerators.ports()
        assertThat(generator, notNullValue())

        doInLoop()
        {
            val port = generator.get()
            assertThat(port, greaterThanOrEqualTo(22))
            assertThat(port, lessThan(java.lang.Short.MAX_VALUE.toInt()))
        }

    }

    @Test
    fun testIp4Addresses()
    {
        val generator = NetworkGenerators.ip4Addresses()
        assertThat(generator, notNullValue())

        val max = "999.999.999.999"
        val min = "1.1.1.1"
        val expectedPeriods = 3

        doInLoop()
        {
            val address = generator.get()

            val periodAppearances = numberOfAppearancesOfCharInString('.', address)
            assertThat(periodAppearances, `is`(expectedPeriods))

            assertThat(address.length, `is`(lessThanOrEqualTo(max.length)))
            assertThat(address.length, `is`(greaterThanOrEqualTo(min.length)))
        }
    }

    private fun numberOfAppearancesOfCharInString(character: Char, string: String): Int
    {
        return string.toCharArray()
                .count { it == character }

    }

}