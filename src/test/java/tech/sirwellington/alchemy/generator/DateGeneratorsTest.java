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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.Dates.isNow;
import static tech.sirwellington.alchemy.generator.Dates.now;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.Tests.doInLoop;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class DateGeneratorsTest
{

    private int iterations;

    @Before
    public void setUp()
    {
        iterations = one(integers(100, 1000));
    }

    @Test
    public void testCannotInstantiate()
    {
        System.out.println("testCannotInstantiate");

        assertThrows(() -> new DateGenerators())
                .isInstanceOf(IllegalAccessException.class);

        assertThrows(() -> DateGenerators.class.newInstance())
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void testPresentDates()
    {
        System.out.println("testPresentDates");

        AlchemyGenerator<Date> instance = DateGenerators.presentDates();

        doInLoop(i ->
        {
            Date value = instance.get();
            assertThat(value, notNullValue());
            assertThat(isNow(value, 15), is(true));
        });
    }

    @Test
    public void testPastDates()
    {
        System.out.println("testPastDates");
        AlchemyGenerator<Date> instance = DateGenerators.pastDates();

        doInLoop(i ->
        {
            Date value = instance.get();
            assertThat(value, notNullValue());
            assertThat(value.before(now()), is(true));
        });
    }

    @Test
    public void testFutureDates()
    {
        System.out.println("testFutureDates");

        AlchemyGenerator<Date> instance = DateGenerators.futureDates();

        doInLoop(i ->
        {
            Date value = instance.get();
            assertThat(value, notNullValue());
            assertThat(value.after(now()), is(true));
        });
    }

    @Test
    public void testAnyTime()
    {
        System.out.println("testAnyTime");

        doInLoop(i ->
        {
            AlchemyGenerator<Date> instance = DateGenerators.anyTime();
            assertThat(instance, notNullValue());
            assertThat(instance.get(), notNullValue());
        });

    }

    @Test
    public void testBefore()
    {
        System.out.println("testBefore");

        doInLoop(i ->
        {
            Date referenceDate = Dates.now();

            AlchemyGenerator<Date> instance = DateGenerators.before(referenceDate);
            assertThat(instance, notNullValue());

            Date result = instance.get();
            assertThat(result, notNullValue());
            assertThat(result.before(referenceDate), is(true));
        });

        //Edge case
        assertThrows(() -> DateGenerators.before(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAfter()
    {
        System.out.println("testAfter");

        doInLoop(i ->
        {
            Date referenceDate = Dates.now();

            AlchemyGenerator<Date> instance = DateGenerators.after(referenceDate);
            assertThat(instance, notNullValue());

            Date result = instance.get();
            assertThat(result, notNullValue());
            assertThat(result.after(referenceDate), is(true));
        });

        //Edge case
        assertThrows(() -> DateGenerators.after(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testToDate()
    {
        System.out.println("testToDate");

        doInLoop(i ->
        {
            Instant now = Instant.now();
            AlchemyGenerator<Instant> generator = () -> now;
            
            AlchemyGenerator<Date> instance = DateGenerators.toDate(generator);
            assertThat(instance, notNullValue());
            
            Date result = instance.get();
            assertThat(result, notNullValue());
            assertThat(result.toInstant(), is(now));
        });

        //Edge cases
        assertThrows(() -> DateGenerators.toDate(null))
                .isInstanceOf(IllegalArgumentException.class);
        
        assertThrows(() -> DateGenerators.toDate(() -> null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
