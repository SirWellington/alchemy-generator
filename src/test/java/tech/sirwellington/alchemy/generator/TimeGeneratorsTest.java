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
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.longs;
import static tech.sirwellington.alchemy.generator.NumberGenerators.smallPositiveIntegers;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;
import static tech.sirwellington.alchemy.generator.TimeGenerators.toZonedDateTimeGenerator;

class TimeGeneratorsTest extends BaseGeneratorTest {

    @DisplayName("testCannotInstantiate")
    @Test
    void testCannotInstantiate() {
        assertThrows(() -> TimeGenerators.class.getDeclaredConstructor().newInstance())
            .isInstanceOf(IllegalAccessException.class);
    }

    @DisplayName("testPresentInstants")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testPresentInstants() {
        var instance = TimeGenerators.presentInstants();
        assertThat(instance, notNullValue());

        var result = instance.get();
        assertThat(Dates.isNow(result, 30), is(true));
    }

    @DisplayName("testPastInstants")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testPastInstants() {
        var instance = TimeGenerators.pastInstants();
        assertThat(instance, notNullValue());

        var now = Instant.now();
        var result = instance.get();

        assertThat(result, notNullValue());
        assertThat(result.isBefore(now), is(true));
        assertThat(result.isAfter(result), is(false)); // reflexive property
    }

    @DisplayName("testFutureInstants")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testFutureInstants() {
        var instance = TimeGenerators.futureInstants();
        assertThat(instance, notNullValue());

        var now = Instant.now();
        var result = instance.get();

        assertThat(result, notNullValue());
        assertThat(result.isAfter(now), is(true));
        assertThat(result.isAfter(result), is(false)); // reflexive property
    }

    @DisplayName("testBefore")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testBefore() {
        int daysBefore = one(smallPositiveIntegers());

        var referenceTime = Instant.now().minus(daysBefore, DAYS);
        var instance = TimeGenerators.before(referenceTime);

        assertThat(instance, notNullValue());

        var result = instance.get();
        assertThat(result, notNullValue());
        assertThat(result.isBefore(referenceTime), is(true));
    }

    @DisplayName("testAfter")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAfter() {
        int daysAhead = one(smallPositiveIntegers());

        var referenceTime = Instant.now().plus(daysAhead, DAYS);
        var instance = TimeGenerators.after(referenceTime);

        assertThat(instance, notNullValue());

        var result = instance.get();
        assertThat(result, notNullValue());
        assertThat(result.isAfter(referenceTime), is(true));
    }

    @DisplayName("testAnyTime")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAnyTime() {
        var instance = TimeGenerators.anyTime();

        assertThat(instance, notNullValue());

        var result = instance.get();
        assertThat(result, notNullValue());
    }

    @DisplayName("testTimesBetween")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testTimesBetween() {
        // Edge cases
        var now = Instant.now();
        var later = now.plus(4, DAYS);

        assertThrows(() -> TimeGenerators.timesBetween(now, null));
        assertThrows(() -> TimeGenerators.timesBetween(null, now));
        assertThrows(() -> TimeGenerators.timesBetween(later, now));

        long startTimestamp = one(longs(1L, Long.MAX_VALUE / 2));
        long endTimestamp = one(longs(startTimestamp + 1, Long.MAX_VALUE));

        var start = Instant.ofEpochMilli(startTimestamp);
        var end = Instant.ofEpochMilli(endTimestamp);

        var instance = TimeGenerators.timesBetween(start, end);
        assertThat(instance, notNullValue());

        var result = instance.get();
        assertThat(result.toEpochMilli(), greaterThanOrEqualTo(start.toEpochMilli()));
        assertThat(result.toEpochMilli(), lessThan(end.toEpochMilli()));
    }

    @DisplayName("testAsZonedDateTimeGenerator")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAsZonedDateTimeGenerator() {
        var zone = anyZone();

        var time = TimeGenerators.anyTime().get();
        var expected = time.atZone(zone);

        AlchemyGenerator<Instant> fixedTimeGen = () -> time;
        var result = toZonedDateTimeGenerator(fixedTimeGen, zone).get();
        assertThat(result, equalTo(expected));
    }

    @DisplayName("testAsZonedDateTimeGeneratorWithPastGenerator")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAsZonedDateTimeGeneratorWithPastGenerator() {
        var zone = anyZone();
        var result = toZonedDateTimeGenerator(
            TimeGenerators.pastInstants(),
            zone
        ).get();

        assertThat(result, notNullValue());
        assertThat(result.isBefore(ZonedDateTime.now(zone)), is(true));
    }

    private ZoneId anyZone() {
        var zoneIds = ZoneId.getAvailableZoneIds().toArray(new String[0]);
        int index = RANDOM.nextInt(0, zoneIds.length);
        return ZoneId.of(zoneIds[index]);
    }

}
