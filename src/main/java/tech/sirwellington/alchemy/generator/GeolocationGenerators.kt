/*
 * Copyright 2016 RedRoma, Inc..
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


import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.doubles


/**
 * Generators for creating Geo-Coordinates.

 * @author SirWellington
 */
@NonInstantiable
class GeolocationGenerators
@Throws(IllegalAccessException::class)
internal constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate")
    }

    companion object
    {

        /**
         * Creates valid latitudes from -90...90 degrees.
         * @return
         */
        fun latitudes(): AlchemyGenerator<Double>
        {
            return doubles(-90.0, 90.0)
        }

        /**
         * Creates valid longitudes from -180...180 degrees.

         * @return
         */
        fun longitudes(): AlchemyGenerator<Double>
        {
            return doubles(-180.0, 180.0)
        }
    }
}
