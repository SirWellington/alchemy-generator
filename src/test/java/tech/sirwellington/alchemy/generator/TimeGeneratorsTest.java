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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import tech.sirwellington.alchemy.generator.NumberGenerators.longs;
import tech.sirwellington.alchemy.generator.NumberGenerators.smallPositiveIntegers;
import tech.sirwellington.alchemy.generator.Throwables.assertThrows;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static com.natpryce.hamkrest.assertion.assertThat;
import static com.natpryce.hamkrest.equalTo;
import static com.natpryce.hamkrest.greaterThanOrEqualTo;
import static com.natpryce.hamkrest.lessThan;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.Matchers.*;

class TimeGeneratorsTest
{

    @DisplayName("testCannotInstantiate")
    @RepeatedTest(1)
    void testCannotInstantiate()
    {
        assertThrows(() -> TimeGenerators.class.getDeclaredConstructor().newInstance())
            .isInstanceOf(IllegalAccessException.class);
    }

    @DisplayName("testPresentInstants")
    @RepeatedTest(20)
    void testPresentInstants()
    {
        AlchemyGenerator<Instant> instance = TimeGenerators.presentInstants();
        assertThat(instance, notNullValue());

        Instant result = instance.get();
        // Assuming Dates.isNow(Instant, int secondsThreshold) exists and is accessible
        assertThat(Dates.isNow(result, 30), is(true));
    }

    @DisplayName("testPastInstants")
    @RepeatedTest(20)
    void testPastInstants()
    {
        AlchemyGenerator<Instant> instance = TimeGenerators.pastInstants();
        assertThat(instance, notNullValue());

        Instant now = Instant.now();
        Instant result = instance.get();

        assertThat(result, notNullValue());
        assertThat(result.isBefore(now), is(true));
        assertThat(result.isAfter(result), is(false)); // reflexive property
    }

    @DisplayName("testFutureInstants")
    @RepeatedTest(20)
    void testFutureInstants()
    {
        AlchemyGenerator<Instant> instance = TimeGenerators.futureInstants();
        assertThat(instance, notNullValue());

        Instant now = Instant.now();
        Instant result = instance.get();

        assertThat(result, notNullValue());
        assertThat(result.isAfter(now), is(true));
        assertThat(result.isAfter(result), is(false)); // reflexive property
    }

    @DisplayName("testBefore")
    @RepeatedTest(20)
    void testBefore()
    {
        int daysBefore = one(smallPositiveIntegers());

        Instant referenceTime = Instant.now().minus(daysBefore, DAYS);
        AlchemyGenerator<Instant> instance = TimeGenerators.before(referenceTime);

        assertThat(instance, notNullValue());

        Instant result = instance.get();
        assertThat(result, notNullValue());
        assertThat(result.isBefore(referenceTime), is(true));
    }

    @DisplayName("testAfter")
    @RepeatedTest(20)
    void testAfter()
    {
        int daysAhead = one(smallPositiveIntegers());

        Instant referenceTime = Instant.now().plus(daysAhead, DAYS);
        AlchemyGenerator<Instant> instance = TimeGenerators.after(referenceTime);

        assertThat(instance, notNullValue());

        Instant result = instance.get();
        assertThat(result, notNullValue());
        assertThat(result.isAfter(referenceTime), is(true));
    }

    @DisplayName("testAnyTime")
    @RepeatedTest(20)
    void testAnyTime()
    {
        AlchemyGenerator<Instant> instance = TimeGenerators.anyTime();

        assertThat(instance, notNullValue());

        Instant result = instance.get();
        assertThat(result, notNullValue());
    }

    @DisplayName("testTimesBetween")
    @RepeatedTest(20)
    void testTimesBetween()
    {
        // Edge cases
        Instant now = Instant.now();
        Instant later = now.plus(4, DAYS);

        assertThrows(() -> TimeGenerators.timesBetween(now, null));
        assertThrows(() -> TimeGenerators.timesBetween(null, now));
        assertThrows(() -> TimeGenerators.timesBetween(later, now));

        long startTimestamp = one(longs(1L, Long.MAX_VALUE / 2));
        long endTimestamp   = one(longs(startTimestamp + 1, Long.MAX_VALUE));

        Instant start = Instant.ofEpochMilli(startTimestamp);
        Instant end   = Instant.ofEpochMilli(endTimestamp);

        AlchemyGenerator<Instant> instance = TimeGenerators.timesBetween(start, end);
        assertThat(instance, notNullValue());

        Instant result = instance.get();
        assertThat(result.toEpochMilli(), greaterThanOrEqualTo(start.toEpochMilli()));
        assertThat(result.toEpochMilli(), lessThan(end.toEpochMilli()));
    }

    @DisplayName("testAsZonedDateTimeGenerator")
    @RepeatedTest(20)
    void testAsZonedDateTimeGenerator()
    {
        ZoneId zone = anyZone();

        Instant time = TimeGenerators.anyTime().get();
        ZonedDateTime expected = time.atZone(zone);

        AlchemyGenerator<Instant> fixedTimeGen = new AlchemyGenerator<>()
        {
            @Override
            public Instant get()
            {
                return time;
            }
        };

        ZonedDateTime result = TimeGenerators.toZonedDateTimeGenerator(fixedTimeGen, zone).get();

        assertThat(result, equalTo(expected));
    }

    @DisplayName("testAsZonedDateTimeGeneratorWithPastGenerator")
    @RepeatedTest(20)
    void testAsZonedDateTimeGeneratorWithPastGenerator()
    {
        ZoneId zone = anyZone();
        ZonedDateTime result = TimeGenerators
            .toZonedDateTimeGenerator(TimeGenerators.pastInstants(), zone)
            .get();

        assertThat(result, notNullValue());
        assertThat(result.isBefore(ZonedDateTime.now(zone)), is(true));
    }

    private ZoneId anyZone()
    {
        String[] zoneIds = ZoneId.getAvailableZoneIds().toArray(new String[0]);
        int idx = ThreadLocalRandom.current().nextInt(zoneIds.length);
        return ZoneId.of(zoneIds[idx]);
    }

}
