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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.longs;
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
    public void testPresentInstants()
    {
        System.out.println("testPresentInstants");
        
        AlchemyGenerator<Instant> instance = TimeGenerators.presentInstants();
        assertThat(instance, notNullValue());
        
        doInLoop(i ->
        {
            Instant result = instance.get();
            assertThat(Dates.isNow(result, 20), is(true));
        });
    }
    
    @Test
    public void testPastInstants()
    {
        System.out.println("testPastInstants");
        
        AlchemyGenerator<Instant> instance = TimeGenerators.pastInstants();
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
    public void testFutureInstants()
    {
        System.out.println("testFutureInstants");
        
        AlchemyGenerator<Instant> instance = TimeGenerators.futureInstants();
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
    
    @Test
    public void testTimesBetween()
    {
        System.out.println("testTimesBetween");
        
        //Check the Edge Cases
        Instant startTime = Instant.now();
        Instant endTime = startTime.plus(4, DAYS);
        
        assertThrows(() -> TimeGenerators.timesBetween(startTime, null))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThrows(() -> TimeGenerators.timesBetween(null, endTime))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThrows(() -> TimeGenerators.timesBetween(endTime, startTime))
            .isInstanceOf(IllegalArgumentException.class);
        
        
        doInLoop(i ->
        {
            long startTimestamp = one(longs(1, Long.MAX_VALUE / 2));
            long endTimestamp = one(longs(startTimestamp + 1, Long.MAX_VALUE));
            
            Instant start = Instant.ofEpochMilli(startTimestamp);
            Instant end = Instant.ofEpochMilli(endTimestamp);
            
            AlchemyGenerator<Instant> instance = TimeGenerators.timesBetween(start, end);
            assertThat(instance, notNullValue());
            
            Instant result = instance.get();
            assertThat(result.toEpochMilli(), greaterThanOrEqualTo(start.toEpochMilli()));
            assertThat(result.toEpochMilli(), lessThan(end.toEpochMilli()));
        });
    }
    
}
