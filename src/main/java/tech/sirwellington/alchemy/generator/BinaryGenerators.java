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

import java.nio.ByteBuffer;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;

/**
 * {@linkplain AlchemyGenerator Alchemy Generators} for raw binary ({@code byte[]}).
 * 
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
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
     * @param length The size of the byte arrays created.
     *
     * @return A binary generator
     * @throws IllegalArgumentException If {@code length < 0}.
     */
    public static AlchemyGenerator<byte[]> binary(int length) throws IllegalArgumentException
    {
        checkThat(length >= 0, "length must be >= 0");
        return () -> RandomUtils.nextBytes(length);
    }
    
    /**
     * Generates a {@link ByteBuffer} of the specified length. 
     * 
     * @param size The desired size of the Byte Buffer.
     * @return
     * @throws IllegalArgumentException If {@code size < 0}.
     */
    public static AlchemyGenerator<ByteBuffer> byteBuffers(int size) throws IllegalArgumentException
    {
        checkThat(size >= 0, "size must be at least 0");
        
        final AlchemyGenerator<byte[]> delegate = binary(size);
        return () ->
        {
            byte[] binary = delegate.get();
            return ByteBuffer.wrap(binary);
        };
    }

}
