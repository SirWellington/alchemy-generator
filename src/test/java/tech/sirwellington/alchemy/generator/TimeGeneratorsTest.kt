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
import org.hamcrest.Matchers.lessThan
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.longs
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.smallPositiveIntegers
import tech.sirwellington.alchemy.generator.Throwables.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

/**

 * @author SirWellington
 */
class TimeGeneratorsTest
{

    @Before
    fun setUp()
    {
    }

    @Test
    fun testCannotInstantiate()
    {
        println("testCannotInstantiate")

        assertThrows { TimeGenerators() }
                .isInstanceOf(IllegalAccessException::class.java)

        assertThrows { TimeGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }

    @Test
    fun testPresentInstants()
    {
        println("testPresentInstants")

        val instance = TimeGenerators.presentInstants()
        assertThat(instance, notNullValue())

        doInLoop { i ->
            val result = instance.get()
            assertThat(Dates.isNow(result, 30), `is`(true))
        }
    }

    @Test
    fun testPastInstants()
    {
        println("testPastInstants")

        val instance = TimeGenerators.pastInstants()
        assertThat(instance, notNullValue())

        doInLoop { i ->
            val now = Instant.now()
            val result = instance.get()
            assertThat(result, notNullValue())
            assertThat(result.isBefore(now), `is`(true))
            assertThat(result.isAfter(result), `is`(false))
        }
    }

    @Test
    fun testFutureInstants()
    {
        println("testFutureInstants")

        val instance = TimeGenerators.futureInstants()
        assertThat(instance, notNullValue())

        doInLoop { i ->
            val now = Instant.now()
            val result = instance.get()
            assertThat(result, notNullValue())
            assertThat(result.isAfter(now), `is`(true))
            assertThat(result.isAfter(result), `is`(false))
        }
    }

    @Test
    fun testBefore()
    {
        println("testBefore")

        doInLoop { i ->
            val daysBefore = one(smallPositiveIntegers())

            val referenceTime = Instant.now().minus(daysBefore.toLong(), DAYS)
            val instance = TimeGenerators.before(referenceTime)
            assertThat(instance, notNullValue())

            val result = instance.get()
            assertThat(result, notNullValue())
            assertThat(result.isBefore(referenceTime), `is`(true))
        }
    }

    @Test
    fun testAfter()
    {
        println("testAfter")

        doInLoop { i ->
            val daysAhead = one(smallPositiveIntegers())

            val referenceTime = Instant.now().plus(daysAhead.toLong(), DAYS)
            val instance = TimeGenerators.after(referenceTime)
            assertThat(instance, notNullValue())

            val result = instance.get()
            assertThat(result, notNullValue())
            assertThat(result.isAfter(referenceTime), `is`(true))
        }
    }

    @Test
    fun testAnytime()
    {
        println("testAnytime")

        doInLoop { i ->
            val instance = TimeGenerators.anytime()
            assertThat(instance, notNullValue())

            val result = instance.get()
            assertThat(result, notNullValue())
        }
    }

    @Test
    fun testTimesBetween()
    {
        println("testTimesBetween")

        //Check the Edge Cases
        val startTime = Instant.now()
        val endTime = startTime.plus(4, DAYS)

        assertThrows { TimeGenerators.timesBetween(startTime, null!!) }
        assertThrows { TimeGenerators.timesBetween(null!!, endTime) }
        assertThrows { TimeGenerators.timesBetween(endTime, startTime) }


        doInLoop { i ->
            val startTimestamp = one(longs(1, java.lang.Long.MAX_VALUE / 2))
            val endTimestamp = one(longs(startTimestamp + 1, java.lang.Long.MAX_VALUE))

            val start = Instant.ofEpochMilli(startTimestamp)
            val end = Instant.ofEpochMilli(endTimestamp)

            val instance = TimeGenerators.timesBetween(start, end)
            assertThat(instance, notNullValue())

            val result = instance.get()
            assertThat(result.toEpochMilli(), greaterThanOrEqualTo(start.toEpochMilli()))
            assertThat(result.toEpochMilli(), lessThan(end.toEpochMilli()))
        }
    }

}
