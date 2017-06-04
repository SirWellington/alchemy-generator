/*
 * Copyright 2017 SirWellington Tech.
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

import org.apache.commons.lang3.RandomUtils
import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer

/**
 * [Alchemy Generators][AlchemyGenerator] for raw binary (`byte[]`).
 *
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
class BinaryGenerators
@Throws(IllegalAccessException::class)
internal constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate this class")
    }

    companion object
    {

        private val LOG = LoggerFactory.getLogger(BinaryGenerators::class.java)

        /**
         * Generates binary of the specified length
         *
         * @param length The size of the byte arrays created.
         *
         *
         * @return A binary generator
         *
         * @throws IllegalArgumentException If `length < 0`.
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun binary(length: Int): AlchemyGenerator<ByteArray>
        {
            checkThat(length >= 0, "length must be >= 0")

            return AlchemyGenerator { RandomUtils.nextBytes(length) }
        }

        /**
         * Generates a [ByteBuffer] of the specified length.
         *
         * @param size The desired size of the Byte Buffer.
         *
         * @return
         *
         * @throws IllegalArgumentException If `size < 0`.
         */
        @Throws(IllegalArgumentException::class)
        @JvmStatic
        fun byteBuffers(size: Int): AlchemyGenerator<ByteBuffer>
        {
            checkThat(size >= 0, "size must be at least 0")

            val delegate = binary(size)

            return AlchemyGenerator {
                val binary = delegate.get()
                ByteBuffer.wrap(binary)
            }
        }

        @JvmStatic
        fun bytes(): AlchemyGenerator<Byte>
        {
            val delegate = binary(1)

            return AlchemyGenerator {
                val array = delegate.get()
                checkThat(array != null && array.isNotEmpty(), "no Byte available to return.")

                array!![0]
            }
        }
    }

}
