/*
 * Copyright 2025 Wellington Moreno<jwellington.moreno@gmail.com>.
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.arguments.Required;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static java.time.temporal.ChronoUnit.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.ChecksJ.checkNotNull;
import static tech.sirwellington.alchemy.generator.ChecksJ.checkThat;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.longs;

/**
 *
 * @author SirWellington
 */
class TimeGenerators {
    /**
     * Produces [Instants][Instant] representing the *present*, i.e *now*. Note that
     * the 'present'
     * depends on when the Generator is [called][AlchemyGenerator.get].
     */
    static AlchemyGenerator<Instant> presentInstants() {
        return Instant::now;
    }

    /**
     * Produces [Instants][Instant] that are always in the past, i.e. before the present.
     */
    static AlchemyGenerator<Instant> pastInstants() {
        /*
         * There is no need to recalculate the present instant per-call. We simply capture the present Instant, and
         * supply dates before that reference point. They will always be in the past.
         */
        return before(Instant.now());
    }

    /**
     * Produces [Instants][Instant] that are always in the future, i.e. after the present.
     */
    static AlchemyGenerator<Instant> futureInstants() {
        // In order to stay in the future, the "present" must be continuously recalculated.
        return () -> {
            Instant present = Instant.now();
            return after(present).get();
        };
    }

    /**
     * Produces [Instants][Instant] that are always before the specified time.
     * @param instant Dates produced will be before this instant.
     */
    static AlchemyGenerator<Instant> before(@Required Instant instant) {
        checkNotNull(instant, "instant cannot be null");

        return () -> {
        long daysBefore = one(longs(1L, 1000L));
        long hoursBefore = one(longs(0L, 100L));
        long minutesBefore = one(longs(0L, 60L));
        long secondsBefore = one(longs(0L, 60L));
        long millisecondsBefore = one(longs(0L, 1000L));

        return instant.minus(daysBefore, DAYS)
                .minus(hoursBefore, HOURS)
                .minus(minutesBefore, MINUTES)
                .minus(secondsBefore, SECONDS)
                .minus(millisecondsBefore, MILLIS);
        };

    }

    /**
     * Produces [Instants][Instant] that are always after the specified time.
     *
     * @param instant Dates produced will be after this instant.
     */
    static AlchemyGenerator<Instant> after(@Required Instant instant) {
        checkNotNull(instant, "instant cannot be null");

        return () -> {

        long daysAhead = one(longs(1L, 11000L));
        long hoursAhead = one(longs(0L, 100L));
        long minutesAhead = one(longs(0L, 60L));
        long secondsAhead = one(longs(0L, 60L));
        long millisecondsAhead = one(longs(0L, 1000L));

        return instant.plus(daysAhead, DAYS)
                .plus(hoursAhead, HOURS)
                .plus(minutesAhead, MINUTES)
                .plus(secondsAhead, SECONDS)
                .plus(millisecondsAhead, MILLIS);

        };
    }

    /**
     * Produces [Instants][Instant] from any time, past, present, or future.
     */
    static AlchemyGenerator<Instant> anytime() {
        return () -> {
            int choice = one(integers(0, 3));
            switch(choice) {
                case 0: return pastInstants().get();
                case 1: return futureInstants().get();
                default: return presentInstants().get();
            }
        };
    }

    /**
     * Generates [Instants][Instant] between the specified times.
     * @param startTime Times produced will come at or after this time.
     * @param endTime Times produced will come before this time.
     * @throws IllegalArgumentException If either time is null, or if the startTime is not before the endTime.
     */
    static AlchemyGenerator<Instant> timesBetween(
        @Required Instant startTime,
        @Required Instant endTime
    ) {
        checkNotNull(startTime, "startTime is null");
        checkNotNull(endTime, "endTime is null");
        checkThat(startTime.isBefore(endTime), "startTime must be before endTime");

        long epochOfStart = startTime.toEpochMilli();
        long epochOfEnd = endTime.toEpochMilli();
        AlchemyGenerator<Long> timestampGenerator = longs(epochOfStart, epochOfEnd);

        return () -> {
            long timestamp = timestampGenerator.get();
            return Instant.ofEpochMilli(timestamp);
        };
    }
    /**
     * Converts this [Instant] generator into a [ZonedDateTime] generator. Zone Id Defaults to UTC.
     * @param generator The underlying generator to convert.
     */
    static AlchemyGenerator<ZonedDateTime> toZonedDateTimeGenerator(
        @Required  AlchemyGenerator<Instant> generator
    ) {
       return toZonedDateTimeGenerator(generator, ZoneOffset.UTC);
    }
    /**
     * Converts this [Instant] generator into a [ZonedDateTime] generator.
     * @param generator The underlying generator to convert.
     * @param zone  The [ZoneId] to the generate times in. Defaults to [ZoneOffset.UTC].
     */
    static AlchemyGenerator<ZonedDateTime> toZonedDateTimeGenerator(
        @Required  AlchemyGenerator<Instant> generator,
        @Required  ZoneId zone
    ) {
        checkNotNull(generator, "generator cannot be null");
        checkNotNull(zone, "zone is missing");

        return () -> {
            Instant instant = generator.get();
            checkNotNull(instant, "Instant produced is null");
            return ZonedDateTime.ofInstant(instant, zone);
        };
    }
}
