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

import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.arguments.NonNull
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

        private val LOG = LoggerFactory.getLogger(DateGenerators::class.java)

        /**
         * Always returns the current time, i.e. the present.
         * <br></br>
         * <pre>
         * Note that the current time depends on when it is called.
        </pre> *

         * @return
         */
        fun presentDates(): AlchemyGenerator<Date>
        {
            return AlchemyGenerator { Date() }
        }

        /**
         * Returns Dates from the past, i.e. before now.
         * <br></br>
         * <pre>
         * Note that the current time depends on when it is called.
        </pre> *

         * @return
         */
        fun pastDates(): AlchemyGenerator<Date>
        {
            return toDate(TimeGenerators.pastInstants())
        }

        /**
         * Returns Dates in the future, i.e. after now.
         * <br></br>
         * <pre>
         * Note that the current time depends on when it is called.
        </pre> *

         * @return
         */
        fun futureDates(): AlchemyGenerator<Date>
        {
            return toDate(TimeGenerators.futureInstants())
        }

        /**
         * Returns dates before the specified reference date.

         * @param referenceDate
         * *
         * @return
         * *
         * @throws IllegalArgumentException
         */
        @Throws(IllegalArgumentException::class)
        fun before(@NonNull referenceDate: Date): AlchemyGenerator<Date>
        {
            checkNotNull(referenceDate, "referenceDate cannot be null")

            val instant = referenceDate.toInstant()
            return toDate(TimeGenerators.before(instant))
        }

        /**
         * Returns dates after the specified reference date.

         * @param referenceDate
         * *
         * @return
         * *
         * @throws IllegalArgumentException
         */
        @Throws(IllegalArgumentException::class)
        fun after(@NonNull referenceDate: Date): AlchemyGenerator<Date>
        {
            checkNotNull(referenceDate, "referenceDate cannot be null")

            val instant = referenceDate.toInstant()
            return toDate(TimeGenerators.after(instant))
        }

        /**
         * Returns any date, can be in the futureInstants, pastInstants, or presentDate.

         * @return
         */
        fun anyTime(): AlchemyGenerator<Date>
        {
            return toDate(TimeGenerators.anytime())
        }

        /**
         * Converts [Instants][Instant] to [Dates][Date] using the supplied [AlchemyGenerator].

         * @param generator
         * *
         * @return
         * *
         * @throws IllegalArgumentException
         */
        @Throws(IllegalArgumentException::class)
        fun toDate(@NonNull generator: AlchemyGenerator<Instant>): AlchemyGenerator<Date>
        {
            checkNotNull(generator, "generator cannot be null")
            checkNotNull(generator.get(), "generator produced null")

            return AlchemyGenerator { Date.from(generator.get()) }
        }


        /**
         * Generates Dates between the specified Times.

         * @param startDate Dates produced will be at or after this date.
         * *
         * @param endDate   Dates produced will be before this date.
         * *
         * @return
         * *
         * @throws IllegalArgumentException If either date is null, or startDate is not before endDate
         */
        @Throws(IllegalArgumentException::class)
        fun datesBetween(@NonNull startDate: Date, @NonNull endDate: Date): AlchemyGenerator<Date>
        {
            checkNotNull(startDate, "startDate is null")
            checkNotNull(endDate, "endDate is null")
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
