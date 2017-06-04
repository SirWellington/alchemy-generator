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

import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.arguments.Required
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.integers
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.alphabeticString
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.alphanumericString
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.stringsFromFixedList

/**
 * Generators for common information about People: names, addresses, phone numbers, emails.

 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
class PeopleGenerators
@Throws(IllegalAccessException::class)
internal constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate")
    }

    companion object
    {

        private val LOG = LoggerFactory.getLogger(PeopleGenerators::class.java)

        /**
         * Generates a "names". There are no guarantees on the names except that it is an
         * [Alphabetic String][StringGenerators.alphabeticString] where the first letter is capitalized.
         */
        fun names(): AlchemyGenerator<String>
        {
            //TODO: It may be better to use an actual dictionary resource file for names instead of generating them.
            return AlchemyGenerator {
                val firstLetter = one(alphabeticString(1)).toUpperCase()
                val length = one(integers(2, 15))
                val restOfTheName = one(alphabeticString(length - 1)).toLowerCase()
                firstLetter + restOfTheName
            }

        }

        /**
         * Generates a human ages, from 1 to 100.
         */
        fun ages(): AlchemyGenerator<Int>
        {
            return integers(1, 100)
        }

        /**
         * Generates an "Adult" ages, from 18 to 100.
         */
        fun adultAges(): AlchemyGenerator<Int>
        {
            return integers(18, 100)
        }

        /**
         * Generates a "Child" ages, from 1 to 17.
         */
        fun childAges(): AlchemyGenerator<Int>
        {
            return integers(1, 18)
        }

        /**
         * Returns a US-based phone number in Long form, without the leading country code.
         * For example, "7545185179".
         */
        fun phoneNumbers(): AlchemyGenerator<Long>
        {
            return AlchemyGenerator {

                val firstPart = one(integers(100, 1000))
                val secondPart = one(integers(100, 1000))
                val thirdPart = one(integers(1000, 10000))

                val phoneString = "$firstPart$secondPart$thirdPart"

                phoneString.toLong()
            }
        }

        /**
         * Returns a US-based phone number in String form, without the leading country code. The format currently uses
         * dashes "-", to separate the phone number parts, although that may change in the future.
         * For example, "547-412-4578".
         */
        fun phoneNumberStrings(): AlchemyGenerator<String>
        {
            return AlchemyGenerator {

                val firstPart = one(integers(100, 1000))
                val secondPart = one(integers(100, 1000))
                val thirdPart = one(integers(1000, 10000))

                "$firstPart-$secondPart-$thirdPart"
            }
        }

        /**
         * Returns popular email domains from a fixed list. This list currently includes:
         *
         *  1.  yahoo.com
         *  1.  google.com
         *  1.  gmail.com
         *  1.  apple.com
         *  1.  icloud.com
         *  1.  microsoft.com
         *  1.  sirwellington.tech
         *
         */
        fun popularEmailDomains(): AlchemyGenerator<String>
        {
            return stringsFromFixedList("yahoo.com",
                                        "google.com",
                                        "gmail.com",
                                        "sirwellington.tech",
                                        "apple.com",
                                        "icloud.com",
                                        "microsoft.com")
        }

        /**
         * Generates email addresses using the Supplied Domain Generator.

         * @param domainGenerator
         * *
         * *
         * @throws IllegalArgumentException If the Domain generator is null, or returns an empty domain.
         */
        @Throws(IllegalArgumentException::class)
        @JvmOverloads fun emails(@Required domainGenerator: AlchemyGenerator<String> = popularEmailDomains()): AlchemyGenerator<String>
        {
            checkNotNull(domainGenerator, "domainGenerator missing")
            checkNotEmpty(domainGenerator.get(), "Email Domain Generator returned empty String")

            return AlchemyGenerator {
                val username = one(alphanumericString())
                val domain = domainGenerator.get()

                "$username@$domain"
            }
        }
    }
}
/**
 * Generates email addresses using the [Popular Email Domains][.popularEmailDomains].

 * @see .popularEmailDomains
 * @see .emails
 */
