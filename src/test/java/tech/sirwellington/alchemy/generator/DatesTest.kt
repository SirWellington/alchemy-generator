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
package tech.sirwellington.alchemy.generator

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.negativeIntegers
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.positiveIntegers
import tech.sirwellington.alchemy.generator.Throwables.assertThrows
import java.time.Instant
import java.time.Instant.now
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*


/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class DatesTest
{

    @Before
    fun setUp()
    {
    }

    @After
    fun tearDown()
    {

    }

    @Test
    fun testNow()
    {
        println("testNow")
        val result = Dates.now()
        val after = Date()
        assertThat(after.time, greaterThanOrEqualTo(result.time))
    }

    @Test
    fun testDaysAgo()
    {
        println("testDaysAgo")

        val days = positiveIntegers().get()

        val right = now().toEpochMilli()
        val left = now().minus(days.toLong(), DAYS).toEpochMilli()

        val result = Dates.daysAgo(days)
        assertThat(result.time, greaterThanOrEqualTo(left))
        assertThat(result.time, lessThanOrEqualTo(right))
    }

    @Test
    fun testDaysAhead()
    {
        println("testDaysAhead")
        val days = positiveIntegers().get()

        val left = now().toEpochMilli()

        val result = Dates.daysAhead(days)
        val right = now().plus(days.toLong(), ChronoUnit.DAYS).toEpochMilli()

        assertThat(result.time, greaterThanOrEqualTo(left))
        assertThat(result.time, lessThanOrEqualTo(right))
    }

    @Test
    fun testHoursAgo()
    {
        println("testHoursAgo")
        val hours = positiveIntegers().get()

        val left = now().minus(hours.toLong(), ChronoUnit.HOURS).toEpochMilli()
        val right = now().toEpochMilli()

        val result = Dates.hoursAgo(hours)

        assertThat(result.time, greaterThanOrEqualTo(left))
        assertThat(result.time, lessThanOrEqualTo(right))
    }

    @Test
    fun testHoursAhead()
    {
        println("testHoursAhead")
        val hours = positiveIntegers().get()

        val left = Instant.now().toEpochMilli()

        val result = Dates.hoursAhead(hours)
        val right = Instant.now().plus(hours.toLong(), ChronoUnit.HOURS).toEpochMilli()

        assertThat(result.time, greaterThanOrEqualTo(left))
        assertThat(result.time, lessThanOrEqualTo(right))

    }

    @Test
    fun testMinutesAgo()
    {
        println("testMinutesAgo")
        val minutes = positiveIntegers().get()

        val left = now().minus(minutes.toLong(), MINUTES).toEpochMilli()
        val right = now().toEpochMilli()

        val result = Dates.minutesAgo(minutes)

        assertThat(result.time, greaterThanOrEqualTo(left))
        assertThat(result.time, lessThanOrEqualTo(right))
    }

    @Test
    fun testMinutesAhead()
    {
        println("testMinutesAhead")
        val minutes = positiveIntegers().get()

        val left = now().toEpochMilli()

        val result = Dates.minutesAhead(minutes)
        val right = now().plus(minutes.toLong(), MINUTES).toEpochMilli()

        assertThat(result.time, greaterThanOrEqualTo(left))
        assertThat(result.time, lessThanOrEqualTo(right))
    }

    @Test
    fun testIsNow_Date()
    {
        println("testIsNow_Date")

        val now = Dates.now()
        assertThat(Dates.isNow(now), `is`(true))

        val notNow = Dates.daysAgo(1)
        assertThat(Dates.isNow(notNow), `is`(false))

        assertThrows { Dates.isNow(null) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testIsNow_Date_long()
    {
        println("testIsNow_Date_long")

        assertThrows { Dates.isNow(null as Date?, 0) }
                .isInstanceOf(IllegalArgumentException::class.java)

        val now = Dates.now()
        assertThat(Dates.isNow(now, 10), `is`(true))
        assertThrows { Dates.isNow(now, -1) }
                .isInstanceOf(IllegalArgumentException::class.java)

        Thread.sleep(1)
        assertThat(Dates.isNow(now, 0), `is`(false))

    }

    @Test
    @Throws(Exception::class)
    fun testIsNow_Instant_long()
    {
        println("testIsNow_Instant_long")

        assertThrows { Dates.isNow(null as Instant?, 0) }
                .isInstanceOf(IllegalArgumentException::class.java)

        assertThrows { Dates.isNow(Instant.now(), one(negativeIntegers()).toLong()) }
                .isInstanceOf(IllegalArgumentException::class.java)

        val now = Instant.now()
        assertThat(Dates.isNow(now, 10), `is`(true))

        Thread.sleep(1)
        assertThat(Dates.isNow(now, 0), `is`(false))
    }

}
