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

package tech.sirwellington.alchemy.generator

import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.Dates.isNow
import tech.sirwellington.alchemy.generator.NumberGenerators.integers
import tech.sirwellington.alchemy.generator.NumberGenerators.longs
import tech.sirwellington.alchemy.generator.Throwables.assertThrows
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class DateGeneratorsTest
{

    private var iterations: Int = 0

    @Before
    fun setUp()
    {
        iterations = one(integers(100, 1000))
    }

    @Test
    fun testCannotInstantiate()
    {
        println("testCannotInstantiate")

        assertThrows { DateGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }

    @Test
    fun testPresentDates()
    {
        println("testPresentDates")

        val instance = DateGenerators.presentDates()

        repeatTest()
        {
            val value = instance.get()
            assertThat(value, notNullValue())
            assertThat(isNow(value, 30), `is`(true))
        }
    }

    @Test
    fun testPastDates()
    {
        println("testPastDates")

        val instance = DateGenerators.pastDates()

        repeatTest()
        {
            val value = instance.get()
            assertThat(value, notNullValue())
            assertThat(value.before(Dates.now()), `is`(true))
        }
    }

    @Test
    fun testFutureDates()
    {
        println("testFutureDates")

        val instance = DateGenerators.futureDates()

        repeatTest()
        {
            val value = instance.get()
            assertThat(value, notNullValue())
            assertThat(value.after(Dates.now()), `is`(true))
        }
    }

    @Test
    fun testAnyTime()
    {
        println("testAnyTime")

        repeatTest()
        {
            val instance = DateGenerators.anyTime()
            assertThat(instance, notNullValue())
            assertThat(instance.get(), notNullValue())
        }

    }

    @Test
    fun testBefore()
    {
        println("testBefore")

        repeatTest()
        {
            val referenceDate = Dates.now()

            val instance = DateGenerators.before(referenceDate)
            assertThat(instance, notNullValue())

            val result = instance.get()
            assertThat(result, notNullValue())
            assertThat(result.before(referenceDate), `is`(true))
        }

        //Edge case
        assertThrows { DateGenerators.before(null!!) }
    }

    @Test
    fun testAfter()
    {
        println("testAfter")

        repeatTest()
        {
            val referenceDate = Dates.now()

            val instance = DateGenerators.after(referenceDate)
            assertThat(instance, notNullValue())

            val result = instance.get()
            assertThat(result, notNullValue())
            assertThat(result.after(referenceDate), `is`(true))
        }

        //Edge case
        assertThrows { DateGenerators.after(null!!) }
    }

    @Test
    fun testToDate()
    {
        println("testToDate")

        repeatTest()
        {
            val now = Instant.now()
            val generator = AlchemyGenerator { now }

            val instance = DateGenerators.toDate(generator)
            assertThat(instance, notNullValue())

            val result = instance.get()
            assertThat(result, notNullValue())
            assertThat(result.toInstant(), `is`(now.truncatedTo(ChronoUnit.MILLIS)))
        }

        //Edge cases
        assertThrows { DateGenerators.toDate(null!!) }

        assertThrows { DateGenerators.toDate(AlchemyGenerator<Instant> { null }) }
    }

    @Test
    fun testDatesBetween()
    {
        println("testDatesBetween")

        val startDate = Dates.daysAgo(4)
        val endDate = Dates.daysAhead(5)

        //Edge Cases
        assertThrows { DateGenerators.datesBetween(null!!, endDate) }
        assertThrows { DateGenerators.datesBetween(startDate, null!!) }
        //Dates swapped
        assertThrows { DateGenerators.datesBetween(endDate, startDate) }


        repeatTest()
        {
            //Pick a start and end time
            val begin = one(longs(1, Long.MAX_VALUE / 2))
            val end = one(longs(begin + 1, java.lang.Long.MAX_VALUE))

            startDate.time = begin
            endDate.time = end

            val instance = DateGenerators.datesBetween(startDate, endDate)
            assertThat(instance, notNullValue())

            //Check the resulting date
            val result = instance.get()
            assertThat(result.time, greaterThanOrEqualTo(startDate.time))
            assertThat(result.time, lessThan(endDate.time))
        }

    }

    @Test
    fun testAsSqlDateGenerator()
    {
        println("testAsSqlDateGenerator")

        repeatTest()
        {
            val date = one(DateGenerators.anyTime())

            val generator = AlchemyGenerator<Date> { date }
            val result = DateGenerators.toSqlDateGenerator(generator)
            val sqlDate = result.get()
            val expected = java.sql.Date(date.time)

            assertThat(sqlDate, equalTo(expected))
        }
    }

    @Test
    fun testAsLocalDateGenerator()
    {
        println("testAsLocalDateGenerator")

        repeatTest()
        {
            val date = one(DateGenerators.anyTime())
            val generator = AlchemyGenerator<Date> { date }
            val result = DateGenerators.toLocalDateGenerator(generator)
            val expected = java.sql.Date(date.time).toLocalDate()
            assertThat(result.get(), equalTo(expected))
        }
    }

    @Test
    fun testSqlSqlTimestampGenerator()
    {
        println("testSqlSqlTimestampGenerator")

        repeatTest()
        {
            val date = one(DateGenerators.anyTime())
            val generator = AlchemyGenerator { date }

            val result = DateGenerators.toSqlTimestampGenerator(generator)
            val sqlTimestamp = result.get()

            assertThat(sqlTimestamp, equalTo(Timestamp(date.time)))
        }
    }

}
