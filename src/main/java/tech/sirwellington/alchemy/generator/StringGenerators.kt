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

import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.arguments.Required
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR
import tech.sirwellington.alchemy.generator.BinaryGenerators.Companion.binary
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.integers
import java.util.*
import javax.xml.bind.annotation.adapters.HexBinaryAdapter

/**

 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
class StringGenerators
@Throws(IllegalAccessException::class)
internal constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate this class")
    }

    companion object
    {

        private val LOG = LoggerFactory.getLogger(StringGenerators::class.java)

        //==============================================================================================
        //BASIC STRINGS
        //==============================================================================================

        /**
         * Generates a random string of a random length. Characters can include ASCII, Unicode, or
         * International Characters.

         * @return
         */
        fun strings(): AlchemyGenerator<String>
        {
            return AlchemyGenerator {
                val size = one(integers(5, 1000))
                RandomStringUtils.random(size)
            }
        }

        /**
         * Generates a random string of specified length. Characters are included from all sets.

         * @param length The length of the String, must be at least 1.
         * *
         * *
         * @return
         */
        fun strings(length: Int): AlchemyGenerator<String>
        {
            checkThat(length > 0, "Length must be at least 1")

            return AlchemyGenerator { RandomStringUtils.random(length) }
        }

        /**
         * Generates a random hexadecimal string.

         * @param length The length of the String, must be at least 1.
         * *
         * *
         * @return
         */
        fun hexadecimalString(length: Int): AlchemyGenerator<String>
        {
            checkThat(length > 0, "Length must be at least 1")

            val hexBinaryAdapter = HexBinaryAdapter()
            val binaryGenerator = binary(length)

            return AlchemyGenerator {
                val binary = one(binaryGenerator)
                val hex = hexBinaryAdapter.marshal(binary)

                StringUtils.left(hex, length)
            }
        }

        /**
         * Generates a random alphabetic string.

         * @param length The length of the String, must be at least 1.
         * *
         * @return
         * *
         * *
         * @throws IllegalArgumentException If `length < 0`
         * *
         * *
         * @see .alphabeticString
         */
        @Throws(IllegalArgumentException::class)
        fun alphabeticString(length: Int): AlchemyGenerator<String>
        {
            checkThat(length > 0, "length must be > 0")

            return AlchemyGenerator { RandomStringUtils.randomAlphabetic(length) }
        }

        /**
         * Generates a random alphabetic string anywhere between `10 - 100` characters. Well suited for the case when
         * you don't really care for the size of the string returned.

         * @return
         * *
         * @see .alphabeticString
         */
        fun alphabeticString(): AlchemyGenerator<String>
        {
            val length = one(integers(10, 100))

            return alphabeticString(length)
        }

        /**
         * Generates a random alphanumeric string anywhere between `10 - 100` characters. Well suited for the case
         * when you don't really care what the size of the string returned.

         * @return
         * *
         * @see .alphanumericString
         */
        fun alphanumericString(): AlchemyGenerator<String>
        {
            val length = one(integers(10, 100))

            return alphabeticString(length)
        }

        /**

         * @param length The length of the Generated Strings.
         * *
         * @return
         * *
         * *
         * @throws IllegalArgumentException If `length < 0`
         */
        @Throws(IllegalArgumentException::class)
        fun alphanumericString(length: Int): AlchemyGenerator<String>
        {
            checkThat(length > 0, "length must be > 0")

            return AlchemyGenerator { RandomStringUtils.randomAlphanumeric(length) }
        }

        /**
         * Creates a numeric integer-based String. The sizes of the Strings will vary across instances.
         *
         *
         * Each resulting string will be directly [parsable into an Integer][Integer.parseInt].

         * @return
         */
        fun numericString(): AlchemyGenerator<String>
        {
            val length = one(integers(4, 25))

            return numericString(length)
        }

        /**
         * Creates a numeric integer-based String.
         *
         *
         * For Example:
         * <pre>
         * String result = numericString(5).get();
         * //49613
        </pre> *

         * @param length
         * *
         * @return
         * *
         * @throws IllegalArgumentException
         */
        @Throws(IllegalArgumentException::class)
        fun numericString(length: Int): AlchemyGenerator<String>
        {
            checkThat(length > 0, "length must be > 0")

            val digits = integers(0, 10)

            return AlchemyGenerator {
                val builder = StringBuilder()

                while (builder.length < length)
                {
                    builder.append(digits.get())
                }

                builder.toString()
            }

        }

        //==============================================================================================
        //UUIDs
        //==============================================================================================

        /**
         * Generates random [UUIDs][UUID].
         */
        @JvmStatic var uuids = AlchemyGenerator { UUID.randomUUID().toString() }

        /**
         * Just returns [.uuids]. This exists for consistency.

         * @return
         */
        fun uuids(): AlchemyGenerator<String>
        {
            return uuids
        }

        //==============================================================================================
        //From Fixed targets
        //==============================================================================================

        /**
         * Generates a string value from the specified set.

         * @param values
         * *
         * *
         * @return
         */
        fun stringsFromFixedList(values: List<String>): AlchemyGenerator<String>
        {
            checkNotNull(values)
            checkThat(!values.isEmpty(), "No values specified")

            return AlchemyGenerator {
                val index = integers(0, values.size).get()
                values[index]
            }
        }

        /**
         * Generates a string value from the specified set.

         * @param values
         * *
         * *
         * @return
         */
        fun stringsFromFixedList(vararg values: String): AlchemyGenerator<String>
        {
            checkNotNull(values)
            checkThat(values.isNotEmpty(), "No values specified")

            return stringsFromFixedList(values.toList())
        }

        /**
         * Takes an existing [Generator][AlchemyGenerator] and transforms its values to a
         * String using the [Object.toString] method.

         * @param <T>
         * *
         * @param generator The backing Alchemy Generator.
         * *
         * *
         * @return
         * *
         * *
         * @throws IllegalArgumentException If the Generator is null.
        </T> */
        @Throws(IllegalArgumentException::class)
        fun <T> asString(@Required generator: AlchemyGenerator<T>): AlchemyGenerator<String>
        {
            checkNotNull(generator, "generator missing")

            return AlchemyGenerator {
                val value = generator.get()
                value?.toString() ?: ""
            }
        }
    }

}
