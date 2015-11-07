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

package tech.sirwellington.alchemy.generator;

import java.time.Instant;
import java.util.Date;
import tech.sirwellington.alchemy.annotations.access.Internal;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;

/*
 * TODO: This belongs in a seperate project.
 */
/**
 * Generation methods for {@code Date}s
 *
 * @author SirWellington
 */
@Internal
@NonInstantiable
final class Dates
{

    Dates() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate class");
    }

    /**
     * A Date representing now, when this method is called.
     *
     * @return
     */
    public static Date now()
    {
        return new Date();
    }

    /**
     * A date representing an instant in the past
     *
     * @param days The days ago from now
     *
     * @return
     */
    public static Date daysAgo(int days)
    {
        Instant instant = Instant.now().minus(days, DAYS);
        return new Date(instant.toEpochMilli());
    }

    /**
     * A date representing an instant in the future
     *
     * @param days The days ahead from now
     *
     * @return
     */
    public static Date daysAhead(int days)
    {
        Instant instant = Instant.now().plus(days, DAYS);
        return new Date(instant.toEpochMilli());
    }

    /**
     * A date representing an instant in the past
     *
     * @param hours The hours ago from now
     *
     * @return
     */
    public static Date hoursAgo(int hours)
    {
        Instant instant = Instant.now().minus(hours, HOURS);
        return new Date(instant.toEpochMilli());
    }

    /**
     * A date representing an instant in the future
     *
     * @param hours The hours ahead from now
     *
     * @return
     */
    public static Date hoursAhead(int hours)
    {
        Instant instant = Instant.now().plus(hours, HOURS);
        return new Date(instant.toEpochMilli());
    }

    /**
     * A date representing an instant in the past
     *
     * @param minutes The minutes ago from now
     *
     * @return
     */
    public static Date minutesAgo(int minutes)
    {
        Instant instant = Instant.now().minus(minutes, MINUTES);
        return new Date(instant.toEpochMilli());
    }

    /**
     * A date representing an instant in the future
     *
     * @param minutes the minutes ago ahead from now
     *
     * @return
     */
    public static Date minutesAhead(int minutes)
    {
        Instant instant = Instant.now().plus(minutes, MINUTES);
        return new Date(instant.toEpochMilli());
    }

    /**
     * Checks to see if the date is now, up to {@code 5 milliseconds} of delta.
     *
     * @param date
     *
     * @see #isNow(java.util.Date, long)
     *
     * @return True if the date is considered to be {@link #now() }, False otherwise.
     */
    public static boolean isNow(Date date)
    {
        return isNow(date, 5);
    }

    /**
     * Checks to see if the date is now, up to the provided marginOfError in milliseconds.
     * The date is now if:
     * <br>
     * {@code
     *      now - marginOfError <= date <= now + marginOfError
     * }
     * @param date                The date to check.
     * @param marginOfErrorMillis The margin of error in milliseconds.
     *
     * @return
     */
    public static boolean isNow(Date date, long marginOfErrorMillis)
    {
        Date now = now();
        checkNotNull(date);
        checkThat(marginOfErrorMillis >= 0, "margin of error must be >= 0");

        long delta = marginOfErrorMillis;
        long timeOfDate = date.getTime();
        long timeOfNow = now.getTime();

        return (timeOfDate >= timeOfNow - delta) &&
               (timeOfDate <= timeOfNow + delta);
    }
}
