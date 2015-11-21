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
 * Generators for {@linkplain Instant Java Instants}.
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

    /**
     * Produces {@linkplain Instant Instants} representing the <i>present</i>, i.e <i>now</i>. Note that
     * the 'present'
     * depends on when the Generator is {@linkplain AlchemyGenerator#get() called}.
     */
    public static AlchemyGenerator<Instant> presentInstants()
    {
        return Instant::now;
    }

    /**
     * Produces {@linkplain Instant Instants} that are always in the past, i.e. before the present.
     */
    public static AlchemyGenerator<Instant> pastInstants()
    {
        /*
         * There is no need to recalculate the present instant per-call. We simply capture the present Instant, and
         * supply dates before that reference point. They will always be in the past.
         */
        return before(Instant.now());
    }

    /**
     * Produces {@linkplain Instant Instants} that are always in the future, i.e. after the present.
     */
    public static AlchemyGenerator<Instant> futureInstants()
    {
        // In order to stay in the future, the "present" must be continuously recalculated.
        return () ->
        {
            Instant present = Instant.now();
            return after(present).get();
        };
    }

    /**
     * Produces {@linkplain Instant Instants} that are always before the specified time.
     *
     * @throws IllegalArgumentException
     */
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

    /**
     * Produces {@linkplain Instant Instants} that are always after the specified time.
     *
     * @throws IllegalArgumentException
     */
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

    /**
     * Produces {@linkplain Instant Instants} from any time, past, present, or future.
     */
    public static AlchemyGenerator<Instant> anytime()
    {
        return () ->
        {
            int choice = one(integers(0, 3));

            switch (choice)
            {
                case 0:
                    return pastInstants().get();
                case 1:
                    return futureInstants().get();
                default:
                    return presentInstants().get();
            }
        };
    }

}
