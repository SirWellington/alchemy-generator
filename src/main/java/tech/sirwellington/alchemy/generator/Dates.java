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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

import tech.sirwellington.alchemy.annotations.arguments.Required;

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

    /**
     * Converts the {@link Instant} to a {@link Date} using {@link Instant#toEpochMilli()};
     */
    @Required
    public static Date from(@Required Instant instant) {
        checkNotNull(instant, "instant cannot be null");
        return new Date(instant.toEpochMilli());
    }
}
