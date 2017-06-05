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

import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.arguments.Required
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR
import java.time.Instant
import java.util.*

/**
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
class DateGenerators
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
         * Always returns the current time, i.e. the present.
         * <br></br>
         * <pre>
         * Note that the current time depends on when it is called.
         * </pre>
         *
         *
         * @return
         */
        @JvmStatic
        fun presentDates(): AlchemyGenerator<Date>
        {
            return AlchemyGenerator { Date() }
        }

        /**
         * Returns Dates from the past, i.e. before now.
         * <br></br>
         * <pre>
         * Note that the current time depends on when it is called.
         * </pre>
         *
         *
         * @return
         */
        @JvmStatic
        fun pastDates(): AlchemyGenerator<Date>
        {
            return toDate(TimeGenerators.pastInstants())
        }

        /**
         * Returns Dates in the future, i.e. after now.
         * <br></br>
         * <pre>
         * Note that the current time depends on when it is called.
         * </pre>
         *
         * @return
         */
        @JvmStatic
        fun futureDates(): AlchemyGenerator<Date>
        {
            return toDate(TimeGenerators.futureInstants())
        }

        /**
         * Returns dates before the specified reference date.
         *
         * @param referenceDate
         *
         * @return
         *
         * @throws IllegalArgumentException
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun before(@Required referenceDate: Date): AlchemyGenerator<Date>
        {
            val instant = referenceDate.toInstant()
            return toDate(TimeGenerators.before(instant))
        }

        /**
         * Returns dates after the specified reference date.
         *
         * @param referenceDate
         *
         * @return
         *
         * @throws IllegalArgumentException
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun after(@Required referenceDate: Date): AlchemyGenerator<Date>
        {
            val instant = referenceDate.toInstant()
            return toDate(TimeGenerators.after(instant))
        }

        /**
         * Returns any date, can be in the futureInstants, pastInstants, or presentDate.
         *
         * @return
         */
        @JvmStatic
        fun anyTime(): AlchemyGenerator<Date>
        {
            return toDate(TimeGenerators.anytime())
        }

        /**
         * Converts [Instants][Instant] to [Dates][Date] using the supplied [AlchemyGenerator].
         *
         * @param generator
         *
         * @return
         *
         * @throws IllegalArgumentException
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun toDate(@Required generator: AlchemyGenerator<Instant>): AlchemyGenerator<Date>
        {
            checkNotNull(generator.get(), "generator produced null")

            return AlchemyGenerator { Date.from(generator.get()) }
        }


        /**
         * Generates Dates between the specified Times.
         *
         * @param startDate Dates produced will be at or after this date.
         * @param endDate   Dates produced will be before this date.
         *
         * @return
         *
         * @throws IllegalArgumentException If either date is null, or startDate is not before endDate
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun datesBetween(@Required startDate: Date, @Required endDate: Date): AlchemyGenerator<Date>
        {
            checkThat(startDate.before(endDate), "endDate must be after startDate")

            val startTime = startDate.time
            val endTime = endDate.time
            val timestampGenerator = NumberGenerators.longs(startTime, endTime)

            return AlchemyGenerator {
                val timestamp = timestampGenerator.get()
                Date(timestamp)
            }
        }
    }

}
