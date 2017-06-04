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
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.integers
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.longs
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS
import java.time.temporal.ChronoUnit.MILLIS
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.SECONDS

/**
 * Generators for [Java Instants][Instant].

 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
class TimeGenerators
@Throws(IllegalAccessException::class)
internal constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate")
    }

    companion object
    {

        private val LOG = LoggerFactory.getLogger(TimeGenerators::class.java)

        /**
         * Produces [Instants][Instant] representing the *present*, i.e *now*. Note that
         * the 'present'
         * depends on when the Generator is [called][AlchemyGenerator.get].

         * @return
         */
        fun presentInstants(): AlchemyGenerator<Instant>
        {
            return AlchemyGenerator { Instant.now() }
        }

        /**
         * Produces [Instants][Instant] that are always in the past, i.e. before the present.

         * @return
         */
        fun pastInstants(): AlchemyGenerator<Instant>
        {
            /*
         * There is no need to recalculate the present instant per-call. We simply capture the present Instant, and
         * supply dates before that reference point. They will always be in the past.
         */
            return before(Instant.now())
        }

        /**
         * Produces [Instants][Instant] that are always in the future, i.e. after the present.

         * @return
         */
        fun futureInstants(): AlchemyGenerator<Instant>
        {
            // In order to stay in the future, the "present" must be continuously recalculated.
            return AlchemyGenerator {
                val present = Instant.now()
                after(present).get()
            }
        }

        /**
         * Produces [Instants][Instant] that are always before the specified time.

         * @param instant
         * *
         * *
         * @return
         * *
         * @throws IllegalArgumentException
         */
        @Throws(IllegalArgumentException::class)
        fun before(@NonNull instant: Instant): AlchemyGenerator<Instant>
        {
            checkNotNull(instant, "instant cannot be null")

            return AlchemyGenerator {
                val daysBefore = one(integers(1, 1000))
                val hoursBefore = one(integers(0, 100))
                val minutesBefore = one(integers(0, 60))
                val secondsBefore = one(integers(0, 60))
                val millisecondsBefore = one(integers(0, 1000))

                instant.minus(daysBefore.toLong(), DAYS)
                       .minus(hoursBefore.toLong(), HOURS)
                       .minus(minutesBefore.toLong(), MINUTES)
                       .minus(secondsBefore.toLong(), SECONDS)
                       .minus(millisecondsBefore.toLong(), MILLIS)
            }

        }

        /**
         * Produces [Instants][Instant] that are always after the specified time.

         * @param instant
         * *
         * *
         * @return
         * *
         * @throws IllegalArgumentException
         */
        @Throws(IllegalArgumentException::class)
        fun after(@NonNull instant: Instant): AlchemyGenerator<Instant>
        {
            checkNotNull(instant, "instant cannot be null")

            return AlchemyGenerator {

                val daysAhead = one(integers(1, 11000))
                val hoursAhead = one(integers(0, 100))
                val minutesAhead = one(integers(0, 60))
                val secondsAhead = one(integers(0, 60))
                val millisecondsAhead = one(integers(0, 1000))

                instant.plus(daysAhead.toLong(), DAYS)
                       .plus(hoursAhead.toLong(), HOURS)
                       .plus(minutesAhead.toLong(), MINUTES)
                       .plus(secondsAhead.toLong(), SECONDS)
                       .plus(millisecondsAhead.toLong(), MILLIS)

            }
        }

        /**
         * Produces [Instants][Instant] from any time, past, present, or future.

         * @return
         */
        fun anytime(): AlchemyGenerator<Instant>
        {
            return AlchemyGenerator {

                val choice = one(integers(0, 3))

                when (choice)
                {
                    0    ->  pastInstants().get()
                    1    ->  futureInstants().get()
                    else ->  presentInstants().get()
                }
            }
        }

        /**
         * Generates [Instants][Instant] between the specified times.

         * @param startTime Times produced will come at or after this time.
         * *
         * @param endTime Times produced will come before this time.
         * *
         * *
         * @return
         * *
         * @throws IllegalArgumentException If either time is null, or if the startTime is not before the endTime.
         */
        @Throws(IllegalArgumentException::class)
        fun timesBetween(@NonNull startTime: Instant, @NonNull endTime: Instant): AlchemyGenerator<Instant>
        {
            checkNotNull(startTime, "startTime is null")
            checkNotNull(endTime, "endTime is null")
            checkThat(startTime.isBefore(endTime), "startTime must be before endTime")

            val epochOfStart = startTime.toEpochMilli()
            val epochOfEnd = endTime.toEpochMilli()
            val timestampGenerator = longs(epochOfStart, epochOfEnd)

            return AlchemyGenerator {
                val timestamp = timestampGenerator.get()
                Instant.ofEpochMilli(timestamp)
            }

        }
    }

}
