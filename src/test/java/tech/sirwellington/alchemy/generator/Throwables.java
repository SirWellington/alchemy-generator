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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.Internal;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 *
 * @author SirWellington
 */
@Internal
class Throwables
{

    private final static Logger LOG = LoggerFactory.getLogger(Throwables.class);

    interface ExceptionOperation
    {

        void run() throws Throwable;
    }

    static Assertion assertThrows(ExceptionOperation operation)
    {
        Throwable ex = null;

        try
        {
            operation.run();
        }
        catch (Throwable result)
        {
            ex = result;
        }

        if (ex == null)
        {
            fail("Expected Exception");
        }

        return new Assertion(ex);
    }

    static class Assertion
    {

        private final Throwable ex;

        private Assertion(Throwable ex)
        {
            this.ex = ex;
        }

        Assertion isInstanceOf(Class<? extends Throwable> classOfThrowable)
        {
            Checks.checkNotNull(classOfThrowable);

            assertThat(ex, instanceOf(classOfThrowable));

            return this;
        }
    }

}
