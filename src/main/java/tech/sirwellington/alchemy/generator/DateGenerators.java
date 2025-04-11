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

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import tech.sirwellington.alchemy.annotations.arguments.Required;

import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;

/**
 * Generators for {@link Date} types.
 *
 * @author SirWellington
 */
public final class DateGenerators {

    private DateGenerators() {
        throw new IllegalAccessError("cannot instantiated");
    }

    /**
     * Always returns the current time, i.e. the present.
     * <br></br>
     * <pre>
     * Note that the current time depends on when it is called.
     * </pre>
     */
    static AlchemyGenerator<Date> presentDates() {
        return Date::new;
    }

    /**
     * Returns Dates from the past, i.e. before now.
     * <br></br>
     * <pre>
     * Note that the current time depends on when it is called.
     * </pre>
     */
    static AlchemyGenerator<Date> pastDates() {
        return toDate(TimeGenerators.pastInstants());
    }

    /**
     * Returns Dates in the future, i.e. after now.
     * <br></br>
     * <pre>
     * Note that the current time depends on when it is called.
     * </pre>
     */
    static AlchemyGenerator<Date> futureDates() {
        return toDate(TimeGenerators.futureInstants());
    }

    /**
     * Returns dates before the specified reference date.
     *
     * @param referenceDate Dates produced will be before this date.
     */
    static AlchemyGenerator<Date> before(@Required Date referenceDate) {
        checkNotNull(referenceDate);
        Instant instant = referenceDate.toInstant();
        return toDate(TimeGenerators.before(instant));
    }

    /**
     * Returns dates after the specified reference date.
     *
     * @param referenceDate Dates produced will be after this date.
     */
    static AlchemyGenerator<Date> after(@Required Date referenceDate) {
        Instant instant = referenceDate.toInstant();
        return toDate(TimeGenerators.after(instant));
    }

    /**
     * Returns any date, can be in the futureInstants, pastInstants, or presentDate.
     */
    static AlchemyGenerator<Date> anyTime() {
        return toDate(TimeGenerators.anyTime());
    }

    /**
     * Converts {@link Instant Instants} to {@link Date Dates} using the supplied [AlchemyGenerator].
     *
     * @param generator The underlying generator.
     */
    static AlchemyGenerator<Date> toDate(
        @Required AlchemyGenerator<Instant> generator
    ) {
        checkNotNull(generator);
        checkNotNull(generator.get(), "generator produced null");
        return () -> Date.from(generator.get());
    }

    /**
     * Generates {@link Date Dates} between the specified Times.
     *
     * @param startDate Dates produced will be at or after this date.
     * @param endDate   Dates produced will be before this date.
     * @throws IllegalArgumentException If either date is null, or startDate is not before endDate
     */
    static AlchemyGenerator<Date> datesBetween(
        @Required Date startDate,
        @Required Date endDate
    ) {
        checkNotNull(startDate, "startDate null");
        checkNotNull(endDate, "endDate null");
        checkThat(startDate.before(endDate), "endDate must be after startDate");

        long startTimeMillis = startDate.getTime();
        long endTimeMillis = endDate.getTime();
        AlchemyGenerator<Long> timestampGenerator = NumberGenerators.longs(startTimeMillis, endTimeMillis);

        return () -> new Date(timestampGenerator.get());
    }


    /**
     * Converts a {@link AlchemyGenerator Generator} of {@link Date} objects to a {@link AlchemyGenerator Generator}
     * of {@link java.sql.Date} objects.
     */
    static AlchemyGenerator<java.sql.Date> toSqlDateGenerator(AlchemyGenerator<java.util.Date> generator) {
        checkNotNull(generator);
        return () -> new java.sql.Date(generator.get().getTime());
    }

    /**
     * Converts a {@link AlchemyGenerator Generator} of {@link Date} objects to a {@link AlchemyGenerator Generator}
     * of {@link java.sql.Timestamp} objects.
     */
    static AlchemyGenerator<java.sql.Timestamp> toSqlTimestampGenerator(
        AlchemyGenerator<java.util.Date> generator
    ) {
        checkNotNull(generator);
        return () -> {
            long time = generator.get().getTime();
            return new java.sql.Timestamp(time);
        };
    }

    /**
     * Converts a {@link AlchemyGenerator Generator} of {@link Date} objects to a {@link AlchemyGenerator Generator}
     * of {@link java.time.LocalDateTime} objects.
     * @param generator Date generator to be converted.
     * @return Converted generator.
     */
    static AlchemyGenerator<LocalDate> toLocalDateGenerator(AlchemyGenerator<Date> generator) {
        checkNotNull(generator);
        AlchemyGenerator<java.sql.Date> _generator = toSqlDateGenerator(generator);
        return () -> _generator.get().toLocalDate();
    }
}
