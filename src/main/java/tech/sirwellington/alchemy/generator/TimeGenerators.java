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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonNull;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;

/**
 *
 * @author SirWellington
 */
@NonInstantiable
public final class TimeGenerators
{

    private final static Logger LOG = LoggerFactory.getLogger(TimeGenerators.class);

    TimeGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
    }

    public static AlchemyGenerator<Instant> now()
    {
        return Instant::now;
    }

    public static AlchemyGenerator<Instant> beforeNow()
    {
        return before(Instant.now());
    }

    public static AlchemyGenerator<Instant> afterNow()
    {
        return after(Instant.now());
    }

    public static AlchemyGenerator<Instant> before(@NonNull Instant instant) throws IllegalArgumentException
    {
        checkNotNull(instant, "instant cannot be null");

        return () ->
        {
            int daysBefore = one(integers(1, 1_000));
            int hoursBefore = one(integers(0, 100));
            int minutesBefore = one(integers(0, 60));
            int secondsBefore = one(integers(0, 60));
            int millisecondsBefore = one(integers(0, 1_000));

            return instant
                    .minus(daysBefore, DAYS)
                    .minus(hoursBefore, HOURS)
                    .minus(minutesBefore, MINUTES)
                    .minus(secondsBefore, SECONDS)
                    .minus(millisecondsBefore, MILLIS);
        };

    }

    public static AlchemyGenerator<Instant> after(@NonNull Instant instant) throws IllegalArgumentException
    {
        checkNotNull(instant, "instant cannot be null");

        return () ->
        {
            int daysAhead = one(integers(1, 1_1000));
            int hoursAhead = one(integers(0, 100));
            int minutesAhead = one(integers(0, 60));
            int secondsAhead = one(integers(0, 60));
            int millisecondsAhead = one(integers(0, 1_000));

            return instant
                    .plus(daysAhead, DAYS)
                    .plus(hoursAhead, HOURS)
                    .plus(minutesAhead, MINUTES)
                    .plus(secondsAhead, SECONDS)
                    .plus(millisecondsAhead, MILLIS);

        };
    }

    public static AlchemyGenerator<Instant> anytime()
    {
        return () ->
        {
            int decidingFactor = one(integers(0, 3));

            switch (decidingFactor)
            {
                case 0:
                    return beforeNow().get();
                case 1:
                    return afterNow().get();
                default:
                    return now().get();
            }
        };
    }

}
