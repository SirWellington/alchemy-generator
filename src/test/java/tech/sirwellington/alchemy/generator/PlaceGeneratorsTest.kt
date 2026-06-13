/*
 * Copyright © 2026. Sir Wellington.
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
import com.natpryce.hamkrest.equalTo
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

        repeatTest()
        {
            val city = instance.get()
            assertThat(city, !isNullOrBlank)
            assertThat(city.length, greaterThanOrEqualTo(2))
        }
    }

    @Test
    fun testStates()
    {
        println("testStates")

        val instance = PlaceGenerators.states()
        assertNotNull(instance)

        repeatTest()
        {
            val state = instance.get()
            assertThat(state, !isNullOrBlank)
            assertThat(state.length, greaterThanOrEqualTo(2))
        }
    }

    @Test
    fun testStatesShortCodes()
    {
        println("testStatesShortCodes")

        val instance = PlaceGenerators.stateShortCodes()
        assertNotNull(instance)

        repeatTest()
        {
            val stateShortCode = instance.get()
            assertThat(stateShortCode, !isNullOrBlank)
            assertThat(stateShortCode.length, equalTo(2))
        }
    }

    @Test
    fun testCountries()
    {
        println("testCountries")

        val instance = PlaceGenerators.countries()
        assertNotNull(instance)

        repeatTest()
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

        repeatTest()
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

        repeatTest()
        {
            val address = instance.get()
            assertThat(address, !isNullOrBlank)
            assertThat(address.length, greaterThanOrEqualTo(5))
        }
    }

}