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
import org.junit.Before;
import org.junit.Test;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.smallPositiveIntegers;
import static tech.sirwellington.alchemy.generator.Tests.doInLoop;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 *
 * @author SirWellington
 */
public class TimeGeneratorsTest
{

    @Before
    public void setUp()
    {
    }

    @Test
    public void testCannotInstantiate()
    {
        System.out.println("testCannotInstantiate");

        assertThrows(() -> new TimeGenerators())
                .isInstanceOf(IllegalAccessException.class);

        assertThrows(() -> TimeGenerators.class.newInstance())
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void testNow()
    {
        System.out.println("testNow");

        AlchemyGenerator<Instant> instance = TimeGenerators.now();
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            Instant result = instance.get();
            assertThat(result, notNullValue());
            assertThat(Dates.isNow(result, 15), is(true));
        });
    }

    @Test
    public void testBeforeNow()
    {
        System.out.println("testBeforeNow");
        AlchemyGenerator<Instant> instance = TimeGenerators.beforeNow();
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            Instant now = Instant.now();
            Instant result = instance.get();
            assertThat(result, notNullValue());
            assertThat(result.isBefore(now), is(true));
            assertThat(result.isAfter(result), is(false));
        });
    }

    @Test
    public void testAfterNow()
    {
        System.out.println("testAfterNow");

        AlchemyGenerator<Instant> instance = TimeGenerators.afterNow();
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            Instant now = Instant.now();
            Instant result = instance.get();
            assertThat(result, notNullValue());
            assertThat(result.isAfter(now), is(true));
            assertThat(result.isAfter(result), is(false));
        });
    }

    @Test
    public void testBefore()
    {
        System.out.println("testBefore");

        doInLoop(i ->
        {
            int daysBefore = one(smallPositiveIntegers());

            Instant referenceTime = Instant.now().minus(daysBefore, DAYS);
            AlchemyGenerator<Instant> instance = TimeGenerators.before(referenceTime);
            assertThat(instance, notNullValue());

            Instant result = instance.get();
            assertThat(result, notNullValue());
            assertThat(result.isBefore(referenceTime), is(true));
        });
    }

    @Test
    public void testAfter()
    {
        System.out.println("testAfter");

        doInLoop(i ->
        {
            int daysAhead = one(smallPositiveIntegers());

            Instant referenceTime = Instant.now().plus(daysAhead, DAYS);
            AlchemyGenerator<Instant> instance = TimeGenerators.after(referenceTime);
            assertThat(instance, notNullValue());

            Instant result = instance.get();
            assertThat(result, notNullValue());
            assertThat(result.isAfter(referenceTime), is(true));
        });
    }

    @Test
    public void testAnytime()
    {
        System.out.println("testAnytime");

        doInLoop(i ->
        {
            AlchemyGenerator<Instant> instance = TimeGenerators.anytime();
            assertThat(instance, notNullValue());

            Instant result = instance.get();
            assertThat(result, notNullValue());
        });
    }

}
