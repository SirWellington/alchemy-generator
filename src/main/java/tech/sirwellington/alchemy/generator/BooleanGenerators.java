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

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generators for {@link BooleanGenerators Booleans}.
 *
 * @author SirWellington
 */
public final class BooleanGenerators
{

    private final static Logger LOG = LoggerFactory.getLogger(BooleanGenerators.class);

    private BooleanGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate this class");
    }

    /**
     * Generates a series of randomly selected booleans.
     *
     * @return
     */
    public static AlchemyGenerator<Boolean> booleans()
    {
        return () -> RandomUtils.nextInt(0, 2) == 1;
    }

    /**
     * Generates a series of alternating booleans. Values alternate between {@code true} and
     * {@code false}.
     *
     * @return
     */
    public static AlchemyGenerator<Boolean> alternatingBooleans()
    {
        AtomicInteger count = new AtomicInteger();
        return () ->
        {
            return isEven(count.incrementAndGet());
        };
    }

    private static boolean isEven(int number)
    {
        return number % 2 == 0;
    }

}
