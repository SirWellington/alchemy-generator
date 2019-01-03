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

import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR


/**
 *
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
object PlaceGenerators
{

    private val LOG = LoggerFactory.getLogger(this::class.java)

    private val cities = AlchemyResources.readLinesFromResource("places/cities.txt")
    private val states = AlchemyResources.readLinesFromResource("places/states.txt")
    private val places = AlchemyResources.readLinesFromResource("places/places.txt")
    private val country = AlchemyResources.readLinesFromResource("places/countries.txt")

    /**
     * Returns a list of valid U.S. cities.
     */
    @JvmStatic
    fun cities(): AlchemyGenerator<String>
    {
        return StringGenerators.stringsFromFixedList(cities)
    }

    /**
     * Returns a valid U.S. State. This can be:
     * 1. One of the official 50 states.
     * 2. One of United States properties (Puerto Rico, Guam, etc).
     */
    @JvmStatic
    fun states(): AlchemyGenerator<String>
    {
        return StringGenerators.stringsFromFixedList(states)
    }

    /**
     * Returns the name of a real country.
     *
     * For example: `Mexico`.
     */
    @JvmStatic
    fun countries(): AlchemyGenerator<String> = StringGenerators.stringsFromFixedList(country)


    /**
     * Returns a street address.
     *
     * > There are no guarantees as the validity of the addresses generated
     * > or whether they actually exist in the real world.
     *
     * For example: `145 N Longwood Blvd`
     */
    @JvmStatic
    fun streetAddresses(): AlchemyGenerator<String>
    {
        val booleans = BooleanGenerators.booleans()
        val streetNames = PeopleGenerators.names()
        val streetNumbers = NumberGenerators.integers(100, 10_000)
        val blockNumbers = NumberGenerators.integers(1, 300)
        val compasses = listOf("N", "S", "E", "W")
        val endings = listOf("Blvd", "St", "Ave", "Pl", "Rd")

        return AlchemyGenerator()
        {
            val streetNumber = one(streetNumbers)
            val blockNumber = one(blockNumbers)
            val streetName = one(streetNames)
            val direction = compasses.shuffled().first()
            val ending = endings.shuffled().first()
            val useCityBlockNumber = one(booleans)
            val useDirection = one(booleans)

            when
            {
                useCityBlockNumber && useDirection -> "$streetNumber $direction $blockNumber St"
                useCityBlockNumber                 -> "$streetNumber $blockNumber St"
                useDirection                       -> "$streetNumber $direction $streetName $ending"
                else                               -> "$streetNumber $streetName $ending"
            }
        }
    }

    /**
     * Return a full address, which includes a street address, city, and country.
     *
     * For example: `4592 E 2 St New York, United States`
     *
     * @param isUSAddress Whether to return a U.S based address which includes a [state][states].
     */
    @JvmStatic
    @JvmOverloads
    fun fullAddresses(isUSAddress: Boolean = true): AlchemyGenerator<String>
    {
        val streetAddresses = streetAddresses()
        val cities = cities()
        val states = states()
        val countries = countries()

        return AlchemyGenerator()
        {
            val streetAddress = one(streetAddresses)
            val city = one(cities)
            val state = one(states)
            val country = one(countries)

            when
            {
                isUSAddress -> "$streetAddress $city, $state United States"
                else        -> "$streetAddress $city, $country"
            }
        }
    }

}