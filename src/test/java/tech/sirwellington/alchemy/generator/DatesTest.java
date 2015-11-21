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
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.negativeIntegers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.positiveIntegers;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;


/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class DatesTest
{

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {

    }

    @Test
    public void testNow()
    {
        System.out.println("testNow");
        Date result = Dates.now();
        Date after = new Date();
        assertThat(after.getTime(), greaterThanOrEqualTo(result.getTime()));
    }

    @Test
    public void testDaysAgo()
    {
        System.out.println("testDaysAgo");

        int days = positiveIntegers().get();

        long right = now().toEpochMilli();
        long left = now().minus(days, DAYS).toEpochMilli();

        Date result = Dates.daysAgo(days);
        assertThat(result.getTime(), greaterThanOrEqualTo(left));
        assertThat(result.getTime(), lessThanOrEqualTo(right));
    }

    @Test
    public void testDaysAhead()
    {
        System.out.println("testDaysAhead");
        int days = positiveIntegers().get();

        long left = now().toEpochMilli();

        Date result = Dates.daysAhead(days);
        long right = now().plus(days, ChronoUnit.DAYS).toEpochMilli();

        assertThat(result.getTime(), greaterThanOrEqualTo(left));
        assertThat(result.getTime(), lessThanOrEqualTo(right));
    }

    @Test
    public void testHoursAgo()
    {
        System.out.println("testHoursAgo");
        int hours = positiveIntegers().get();

        long left = now().minus(hours, ChronoUnit.HOURS).toEpochMilli();
        long right = now().toEpochMilli();

        Date result = Dates.hoursAgo(hours);

        assertThat(result.getTime(), greaterThanOrEqualTo(left));
        assertThat(result.getTime(), lessThanOrEqualTo(right));
    }

    @Test
    public void testHoursAhead()
    {
        System.out.println("testHoursAhead");
        int hours = positiveIntegers().get();

        long left = Instant.now().toEpochMilli();

        Date result = Dates.hoursAhead(hours);
        long right = Instant.now().plus(hours, ChronoUnit.HOURS).toEpochMilli();

        assertThat(result.getTime(), greaterThanOrEqualTo(left));
        assertThat(result.getTime(), lessThanOrEqualTo(right));

    }

    @Test
    public void testMinutesAgo()
    {
        System.out.println("testMinutesAgo");
        int minutes = positiveIntegers().get();

        long left = now().minus(minutes, MINUTES).toEpochMilli();
        long right = now().toEpochMilli();

        Date result = Dates.minutesAgo(minutes);

        assertThat(result.getTime(), greaterThanOrEqualTo(left));
        assertThat(result.getTime(), lessThanOrEqualTo(right));
    }

    @Test
    public void testMinutesAhead()
    {
        System.out.println("testMinutesAhead");
        int minutes = positiveIntegers().get();

        long left = now().toEpochMilli();

        Date result = Dates.minutesAhead(minutes);
        long right = now().plus(minutes, MINUTES).toEpochMilli();

        assertThat(result.getTime(), greaterThanOrEqualTo(left));
        assertThat(result.getTime(), lessThanOrEqualTo(right));
    }

    @Test
    public void testIsNow_Date()
    {
        System.out.println("testIsNow_Date");

        Date now = Dates.now();
        assertThat(Dates.isNow(now), is(true));

        Date notNow = Dates.daysAgo(1);
        assertThat(Dates.isNow(notNow), is(false));

        assertThrows(() -> Dates.isNow(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testIsNow_Date_long() throws InterruptedException
    {
        System.out.println("testIsNow_Date_long");

        assertThrows(() -> Dates.isNow((Date) null, 0))
                .isInstanceOf(IllegalArgumentException.class);

        Date now = Dates.now();
        assertThat(Dates.isNow(now, 10), is(true));
        assertThrows(() -> Dates.isNow(now, -1))
                .isInstanceOf(IllegalArgumentException.class);
        
        Thread.sleep(1);
        assertThat(Dates.isNow(now, 0), is(false));

    }

    @Test
    public void testIsNow_Instant_long() throws Exception
    {
        System.out.println("testIsNow_Instant_long");

        assertThrows(() -> Dates.isNow((Instant) null, 0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> Dates.isNow(Instant.now(), one(negativeIntegers())))
                .isInstanceOf(IllegalArgumentException.class);

        Instant now = Instant.now();
        assertThat(Dates.isNow(now, 10), is(true));
        
        Thread.sleep(1);
        assertThat(Dates.isNow(now, 0), is(false));
    }

}
