/*
 * Copyright 2025 Sir Wellington.
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
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.generator.ChecksJ.checkThat;

/**
 * {@link AlchemyGenerator Alchemy Generators} for raw binary (`byte[]`).
 * 
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
public class BinaryGenerators {

    private BinaryGenerators() throws IllegalAccessException  {
        throw new IllegalAccessException("Cannot instantiate this class");
    }
    
    /**
     * Generates binary of the specified length.
     * @param length The size of the byte arrays created.
     * @return A binary generator.
     * @throws IllegalArgumentException If `length < 0`.
     * @see #byteBuffers(int) 
     */
    static AlchemyGenerator<byte[]> binary(int length) {
        checkThat(length >= 0, "length must be >= 0");
        return () -> RandomUtils.secure().randomBytes(length);
    }
    
    /**
     * Generates a {@link ByteBuffer} of the specified size.
     * @param size The size of the byte buffers produced.
     * @return A {@link ByteBuffer} of the specified size.
     * @throws IllegalArgumentException If `length < 0`.
     * @see #binary(int) 
     */
    static AlchemyGenerator<ByteBuffer> byteBuffers(int size) throws IllegalArgumentException {
        checkThat(size > 0, "length must be >= 0");
        AlchemyGenerator<byte[]> delegate = binary(size);
        return () -> ByteBuffer.wrap(delegate.get());
    }
    
    /**
     * Generates a single `byte`.
     * @return A {@link AlchemyGenerator} that produces a single byte.
     */
    static AlchemyGenerator<Byte> bytes() {
        return () -> RandomUtils.secure().randomBytes(1)[0];
    }
}
