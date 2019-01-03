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

import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class GeolocationGeneratorsTest
{

    @Before
    @Throws(Exception::class)
    fun setUp()
    {
    }

    @Test
    fun testLatitudes()
    {
        val generator = GeolocationGenerators.latitudes()
        assertThat(generator, notNullValue())

        doInLoop { i ->
            val latitude = generator.get()

            assertThat(latitude, greaterThanOrEqualTo(-90.0))
            assertThat(latitude, lessThanOrEqualTo(90.0))
        }
    }

    @Test
    fun testLongitudes()
    {
        val generator = GeolocationGenerators.longitudes()
        assertThat(generator, notNullValue())

        doInLoop { i ->
            val latitude = generator.get()

            assertThat(latitude, greaterThanOrEqualTo(-180.0))
            assertThat(latitude, lessThanOrEqualTo(180.0))
        }
    }

}