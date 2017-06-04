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

import java.time.Instant
import java.util.Date

import tech.sirwellington.alchemy.annotations.access.Internal
import tech.sirwellington.alchemy.annotations.access.NonInstantiable

import java.time.temporal.ChronoUnit.*

/*
 * TODO: This belongs in a separate project.
 */

/**
 * Generation methods for `Date`s
 *
 * @author SirWellington
 */
@Internal
@NonInstantiable
internal class Dates
@Throws(IllegalAccessException::class)
constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate class")
    }

    companion object
    {

        /**
         * A Date representing now, when this method is called.
         *
         * @return
         */
        @JvmStatic
        fun now(): Date
        {
            return Date()
        }

        /**
         * A date representing an instant in the past
         *
         * @param days The days ago from now
         *
         * @return
         */
        @JvmStatic
        fun daysAgo(days: Int): Date
        {
            val instant = Instant.now().minus(days.toLong(), DAYS)
            return Date(instant.toEpochMilli())
        }

        /**
         * A date representing an instant in the future
         *
         * @param days The days ahead from now
         *
         * @return
         */
        @JvmStatic
        fun daysAhead(days: Int): Date
        {
            val instant = Instant.now().plus(days.toLong(), DAYS)
            return Date(instant.toEpochMilli())
        }

        /**
         * A date representing an instant in the past
         *
         * @param hours The hours ago from now
         *
         * @return
         */
        @JvmStatic
        fun hoursAgo(hours: Int): Date
        {
            val instant = Instant.now().minus(hours.toLong(), HOURS)
            return Date(instant.toEpochMilli())
        }

        /**
         * A date representing an instant in the future
         *
         * @param hours The hours ahead from now
         *
         * @return
         */
        @JvmStatic
        fun hoursAhead(hours: Int): Date
        {
            val instant = Instant.now().plus(hours.toLong(), HOURS)

            return Date(instant.toEpochMilli())
        }

        /**
         * A date representing an instant in the past
         *
         * @param minutes The minutes ago from now
         *
         * @return
         */
        @JvmStatic
        fun minutesAgo(minutes: Int): Date
        {
            val instant = Instant.now().minus(minutes.toLong(), MINUTES)

            return Date(instant.toEpochMilli())
        }

        /**
         * A date representing an instant in the future
         *
         * @param minutes the minutes ago ahead from now
         *
         * @return
         */
        @JvmStatic
        fun minutesAhead(minutes: Int): Date
        {
            val instant = Instant.now().plus(minutes.toLong(), MINUTES)

            return Date(instant.toEpochMilli())
        }

        /**
         * Checks to see if the date is now, up to the provided marginOfError in milliseconds.
         * The date is now if:
         * <br></br>
         * `now - marginOfError <= date <= now + marginOfError
         *
         *
         * @param date                The date to check.
         * *
         * @param marginOfErrorMillis The margin of error in milliseconds.
         * *
         * @return
         */
        @JvmStatic
        @JvmOverloads
        fun isNow(date: Date, marginOfErrorMillis: Long = 5): Boolean
        {
            val now = now()
            checkNotNull(date)
            checkThat(marginOfErrorMillis >= 0, "margin of error must be >= 0")

            val delta = marginOfErrorMillis
            val timeOfDate = date.time
            val timeOfNow = now.time

            return timeOfDate >= timeOfNow - delta && timeOfDate <= timeOfNow + delta
        }

        @JvmStatic
        @JvmOverloads
        fun isNow(instant: Instant, marginOfErrorMillis: Long = 5): Boolean
        {
            val now = Instant.now()

            checkNotNull(instant)
            checkNotNull(now)
            checkThat(marginOfErrorMillis >= 0, "margin of error must be >= 0 ")

            val delta = marginOfErrorMillis
            val timeOfDate = instant.toEpochMilli()
            val timeOfNow = now.toEpochMilli()

            return timeOfDate >= timeOfNow - delta && timeOfDate <= timeOfNow + delta
        }
    }
}
