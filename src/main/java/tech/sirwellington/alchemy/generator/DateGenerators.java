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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonNull;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;

/**
 *
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
public final class DateGenerators
{

    private final static Logger LOG = LoggerFactory.getLogger(DateGenerators.class);

    DateGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
    }

    /**
     * Always returns the current time, i.e. the present.
     * <br>
     * <pre>
     * Note that the current time depends on when it is called.
     * </pre>
     *
     * @return
     */
    public static AlchemyGenerator<Date> presentDates()
    {
        return Dates::now;
    }

    /**
     * Returns Dates from the past, i.e. before now.
     * <br>
     * <pre>
     * Note that the current time depends on when it is called.
     * </pre>
     *
     * @return
     */
    public static AlchemyGenerator<Date> pastDates()
    {
        return toDate(TimeGenerators.pastInstants());
    }

    /**
     * Returns Dates in the future, i.e. after now.
     * <br>
     * <pre>
     * Note that the current time depends on when it is called.
     * </pre>
     *
     * @return
     */
    public static AlchemyGenerator<Date> futureDates()
    {
        return toDate(TimeGenerators.futureInstants());
    }

    /**
     * Returns dates before the specified reference date.
     *
     * @param referenceDate
     * 
     * @return 
     * @throws IllegalArgumentException
     */
    public static AlchemyGenerator<Date> before(@NonNull Date referenceDate) throws IllegalArgumentException
    {
        checkNotNull(referenceDate, "referenceDate cannot be null");

        Instant instant = referenceDate.toInstant();
        return toDate(TimeGenerators.before(instant));
    }

    /**
     * Returns dates after the specified reference date.
     *
     * @param referenceDate
     *
     * @return
     * @throws IllegalArgumentException
     */
    public static AlchemyGenerator<Date> after(@NonNull Date referenceDate) throws IllegalArgumentException
    {
        checkNotNull(referenceDate, "referenceDate cannot be null");

        Instant instant = referenceDate.toInstant();
        return toDate(TimeGenerators.after(instant));
    }

    /**
     * Returns any date, can be in the futureInstants, pastInstants, or presentDate.
     *
     * @return
     */
    public static AlchemyGenerator<Date> anyTime()
    {
        return toDate(TimeGenerators.anytime());
    }

    /**
     * Converts {@linkplain Instant Instants} to {@linkplain Date Dates} using the supplied {@link  AlchemyGenerator}.
     *
     * @param generator
     *
     * @return
     * @throws IllegalArgumentException
     */
    public static AlchemyGenerator<Date> toDate(@NonNull AlchemyGenerator<Instant> generator) throws IllegalArgumentException
    {
        checkNotNull(generator, "generator cannot be null");
        checkNotNull(generator.get(), "generator produced null");

        return () ->
        {
            return Date.from(generator.get());
        };
    }

    
    /**
     * Generates Dates between the specified Times.
     * 
     * @param startDate Dates produced will be at or after this date.
     * @param endDate Dates produced will be before this date.
     * 
     * @return
     * @throws IllegalArgumentException If either date is null, or startDate is not before endDate
     */
    public static AlchemyGenerator<Date> datesBetween(@NonNull Date startDate, @NonNull Date endDate) throws IllegalArgumentException
    {
        checkNotNull(startDate, "startDate is null");
        checkNotNull(endDate, "endDate is null");
        checkThat(startDate.before(endDate), "endDate must be after startDate");
        
        long startTime = startDate.getTime();
        long endtime = endDate.getTime();
        AlchemyGenerator<Long> timestampGenerator = NumberGenerators.longs(startTime, endtime);
        
        return () -> 
        {
            long timestamp = timestampGenerator.get();
            return new Date(timestamp);
        };
    }
    
}
