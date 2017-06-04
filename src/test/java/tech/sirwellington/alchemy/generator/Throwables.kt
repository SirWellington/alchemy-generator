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

import org.hamcrest.Matchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.Internal

internal typealias ExceptionOperation = () -> Unit

/**
 * @author SirWellington
 */
@Internal
internal object Throwables
{

    private val LOG = LoggerFactory.getLogger(Throwables::class.java)


    inline fun assertThrows(operation: ExceptionOperation): Assertion
    {
        var ex: Throwable? = null

        try
        {
            operation()
        }
        catch (result: Throwable)
        {
            ex = result
        }

        if (ex == null)
        {
            fail("Expected Exception")
        }

        return Assertion(ex!!)
    }

    internal class Assertion internal constructor(private val ex: Throwable)
    {

        fun isInstanceOf(classOfThrowable: Class<out Throwable>): Assertion
        {
            checkNotNull(classOfThrowable)

            assertThat(ex, instanceOf<Throwable>(classOfThrowable))

            return this
        }
    }

}
