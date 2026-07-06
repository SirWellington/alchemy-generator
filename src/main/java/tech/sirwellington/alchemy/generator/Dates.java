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
package tech.sirwellington.alchemy.generator;

import tech.sirwellington.alchemy.annotations.arguments.Positive;
import tech.sirwellington.alchemy.annotations.arguments.Required;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;

/**
 * {@summary Functions that help working with Dates.}
 *
 * {@snippet :
 * var tenDaysAgo = Dates.daysBeforeNow(10);
 * var isRightNow = Dates.isNow(tenDaysAgo) // false
 * }
 * @author SirWellington
 */
public final class Dates {

    private Dates() throws IllegalAccessException {
        throw new IllegalAccessException("cannot directly instantiate");
    }

    /**
     * @return A new {@link Date} representing "now", whenever this method is called.
     */
    public static Date now() {
        return new Date();
    }

    /**
     * Returns a date that is {@code days} in the past.
     * @param days The number of days before today the date should be.
     * @return {@code today - days}.
     * @see #daysAfterNow(int)
     */
    public static Date daysBeforeNow(@Positive int days) {
        checkThat(days > 0, "days must be positive");
        var instant = Instant.now().minus(days, ChronoUnit.DAYS);
        return new Date(instant.toEpochMilli());
    }

    /**
     * Returns a date that is {@code days} in the future.
     * @param days The number of days after today the date should be.
     * @return {@code today + days}.
     * @see #daysBeforeNow(int)
     */
    public static Date daysAfterNow(@Positive int days) {
        checkThat(days > 0, "days must be positive");
        var instant = Instant.now().plus(days, ChronoUnit.DAYS);
        return new Date(instant.toEpochMilli());
    }

    /**
     * Returns a date that is {@code hours} in the past.
     * @param hours The number of hours before now the date should be.
     * @return {@code now - hours}.
     * @see #hoursAfterNow(int)
     */
    public static Date hoursBeforeNow(@Positive int hours) {
        checkThat(hours > 0, "hours must be positive");
        var instant = Instant.now().minus(hours, ChronoUnit.HOURS);
        return new Date(instant.toEpochMilli());
    }

    /**
     * Returns a date that is {@code hours} ahead of right now.
     * @param hours The number of hours after right now the date should be.
     * @return {@code now + hours}.
     * @see #hoursBeforeNow(int)
     */
    public static Date hoursAfterNow(@Positive int hours) {
        checkThat(hours > 0, "hours must be positive");
        var instant = Instant.now().plus(hours, ChronoUnit.HOURS);
        return new Date(instant.toEpochMilli());
    }

    /**
     * Returns a date that is {@code minutes} in the past.
     * @param minutes The number of minutes before now the date should be.
     * @return {@code now - minutes}.
     * @see #minutesAfterNow(int)
     */
    public static Date minutesBeforeNow(@Positive int minutes) {
        checkThat(minutes > 0, "minutes must be positive");
        var instant = Instant.now().minus(minutes, ChronoUnit.MINUTES);
        return new Date(instant.toEpochMilli());
    }

    /**
     * Returns a date that is {@code minutes} ahead of right now.
     * @param minutes The number of minutes after right now the date should be.
     * @return {@code now + minutes}.
     * @see #minutesBeforeNow(int)
     */
    public static Date minutesAfterNow(@Positive int minutes) {
        checkThat(minutes > 0, "minutes must be positive");
        var instant = Instant.now().plus(Duration.ofMinutes(minutes));
        return new Date(instant.toEpochMilli());
    }

    /**
     * Convenience method of {@link #isNow(Date, long)} with a default margin of error of 5 milliseconds
     * (can be updated in a future release).
     * @param date The date to check, must be non-null.
     * @return Whether the date can be considered "now".
     */
    public static boolean isNow(@Required Date date) {
        return isNow(date, 5L);
    }

    /**
     * Checks to see if the date is now, up to the provided {@code marginOfErrorMillis}.
     * @param date The date to check, must be non-null.
     * @param marginOfErrorMillis The margin of error, in milliseconds.
     * @return Whether the date can be considered "now", using the specified margin of error.
     */
    public static boolean isNow(@Required Date date, long marginOfErrorMillis) {
        Date now = now();
        checkNotNull(date, "date cannot be null");
        checkThat(marginOfErrorMillis >= 0, "margin of error must be >= 0");

        var delta = marginOfErrorMillis;
        long timeOfDate = date.getTime();
        long timeOfNow = now.getTime();
        return timeOfDate >= timeOfNow - delta && timeOfDate <= timeOfNow + delta;
    }

    /**
     * Checks to see if the instant is considered now, up to the provided {@code marginOfErrorMillis}.
     * @param instant The instant to check, must be non-null.
     * @param marginOfErrorMillis The margin of error, in milliseconds.
     * @return Whether the instant can be considered "now", using the specified margin of error.
     */
    public static boolean isNow(@Required Instant instant, long marginOfErrorMillis) {
        var now = Instant.now();
        checkNotNull(instant, "instant cannot be null");
        checkThat(marginOfErrorMillis >= 0, "margin of error must be >= 0");

        var delta = marginOfErrorMillis;
        long timeOfDate = instant.toEpochMilli();
        long timeOfNow = now.toEpochMilli();
        return timeOfDate >= timeOfNow - delta && timeOfDate <= timeOfNow + delta;
    }

    /**
     * @return The current year.
     */
    public static int currentYear() {
        return ZonedDateTime.now().getYear();
    }
}
