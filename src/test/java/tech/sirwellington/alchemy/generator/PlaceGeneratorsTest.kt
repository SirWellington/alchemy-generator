/*
 * Copyright © 2018. Sir Wellington.
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

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.greaterThanOrEqualTo
import com.natpryce.hamkrest.isNullOrBlank
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertNotNull

@RunWith(MockitoJUnitRunner::class)
class PlaceGeneratorsTest
{

    @Test
    fun testCities()
    {
        println("testCities")

        val instance = PlaceGenerators.cities()
        assertNotNull(instance)

        doInLoop()
        {
            val city = instance.get()
            assertThat(city, !isNullOrBlank)
            assertThat(city.length, greaterThanOrEqualTo(2))
        }
    }

    @Test
    fun testCountries()
    {
        println("testCountries")

        val instance = PlaceGenerators.countries()
        assertNotNull(instance)

        doInLoop()
        {
            val country = instance.get()
            assertThat(country, !isNullOrBlank)
            assertThat(country.length, greaterThanOrEqualTo(2))
        }
    }

    @Test
    fun testStreetAddresses()
    {
        println("testStreetAddresses")

        val instance = PlaceGenerators.streetAddresses()
        assertNotNull(instance)

        doInLoop()
        {
            val address = instance.get()
            assertThat(address, !isNullOrBlank)
            assertThat(address.length, greaterThanOrEqualTo(5))
        }
    }

    @Test
    fun testFullAddresses()
    {
        println("testFullAddresses")

        val instance = PlaceGenerators.fullAddresses()
        assertNotNull(instance)

        doInLoop()
        {
            val address = instance.get()
            assertThat(address, !isNullOrBlank)
            assertThat(address.length, greaterThanOrEqualTo(5))
        }
    }

}