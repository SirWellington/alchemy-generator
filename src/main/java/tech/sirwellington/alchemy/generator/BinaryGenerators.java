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

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;

import static tech.sirwellington.alchemy.generator.Checks.checkThat;

/**
 * {@linkplain AlchemyGenerator Alchemy Generators} for raw binary ({@code byte[]}).
 * 
 * @author SirWellington
 */
@NonInstantiable
public final class BinaryGenerators
{

    BinaryGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate this class");
    }

    private final static Logger LOG = LoggerFactory.getLogger(BinaryGenerators.class);

    /**
     * Generates binary of the specified length
     *
     * @param bytes The size of the byte arrays created.
     *
     * @return A binary generator
     */
    public static AlchemyGenerator<byte[]> binary(int bytes)
    {
        checkThat(bytes > 0, "bytes must be at least 1");
        return () -> RandomUtils.nextBytes(bytes);
    }

}
