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
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonNull;

import static java.lang.String.format;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.Checks.checkNotEmpty;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphabeticString;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphanumericString;
import static tech.sirwellington.alchemy.generator.StringGenerators.stringsFromFixedList;

/**
 * Generators for common information about People: names, addresses, phone numbers, emails.
 *
 * @author SirWellington
 */
@NonInstantiable
public final class PeopleGenerators
{

    private final static Logger LOG = LoggerFactory.getLogger(PeopleGenerators.class);

    PeopleGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
    }

    /**
     * Generates a "name". There are no guarantees on the name except that it is an
     * {@linkplain StringGenerators#alphabeticString() Alphabetic String} where the first letter
     * is capitalized.
     */
    public static AlchemyGenerator<String> name()
    {
        //TODO: It may be better to use an actual dictionary resource file for names instead of generating them.
        return () ->
        {
            String firstLetter = one(alphabeticString(1)).toUpperCase();
            int length = one(integers(2, 15));
            String restOfTheName = one(alphabeticString(length - 1)).toLowerCase();
            return firstLetter + restOfTheName;
        };

    }

    /**
     * Generates a human age, from 1 to 100.
     */
    public static AlchemyGenerator<Integer> age()
    {
        return integers(1, 100);
    }

    /**
     * Generates an "Adult" age, from 18 to 100.
     */
    public static AlchemyGenerator<Integer> adultAge()
    {
        return integers(18, 100);
    }

    /**
     * Generates a "Child" age, from 1 to 17.
     */
    public static AlchemyGenerator<Integer> childAge()
    {
        return integers(1, 18);
    }

    /**
     * Returns a US-based phone number in Long form, without the leading country code.
     * For example, "7545185179".
     */
    public static AlchemyGenerator<Long> phoneNumber()
    {
        return () ->
        {
            int firstPart = one(integers(100, 1000));
            int secondPart = one(integers(100, 1000));
            int thirdPart = one(integers(1000, 10000));
            String phoneString = format("%d%d%d", firstPart, secondPart, thirdPart);
            return Long.valueOf(phoneString);
        };
    }

    /**
     * Returns a US-based phone number in String form, without the leading country code. The format currently uses
     * dashes "-", to separate the phone number parts, although that may change in the future.
     * For example, "547-412-4578".
     */
    public static AlchemyGenerator<String> phoneNumberString()
    {
        return () ->
        {
            int firstPart = one(integers(100, 1000));
            int secondPart = one(integers(100, 1000));
            int thirdPart = one(integers(1000, 10000));
            return format("%d-%d-%d", firstPart, secondPart, thirdPart);
        };
    }

    /**
     * Returns popular email domains from a fixed list. This list currently includes:
     * <ol>
     * <li> yahoo.com
     * <li> google.com
     * <li> gmail.com
     * <li> apple.com
     * <li> icloud.com
     * <li> microsoft.com
     * <li> sirwellington.tech
     * </ol>
     */
    public static AlchemyGenerator<String> popularEmailDomains()
    {
        return stringsFromFixedList("yahoo.com",
                                    "google.com",
                                    "gmail.com",
                                    "sirwellington.tech",
                                    "apple.com",
                                    "icloud.com",
                                    "microsoft.com");
    }

    /**
     * Generates email addresses using the {@linkplain #popularEmailDomains() Popular Email Domains}.
     *
     * @see #popularEmailDomains()
     * @see #emails(tech.sirwellington.alchemy.generator.AlchemyGenerator)
     */
    public static AlchemyGenerator<String> emails()
    {
        return emails(popularEmailDomains());
    }

    /**
     * Generates email addresses using the Supplied Domain Generator.
     *
     * @param domainGenerator
     *
     * @throws IllegalArgumentException If the Domain generator is null, or returns an empty domain.
     */
    public static AlchemyGenerator<String> emails(@NonNull AlchemyGenerator<String> domainGenerator) throws IllegalArgumentException
    {
        checkNotNull(domainGenerator, "domainGenerator missing");
        checkNotEmpty(domainGenerator.get(), "Email Domain Generator returned empty String");

        return () ->
        {
            String username = one(alphanumericString());
            String domain = domainGenerator.get();
            return format("%s@%s", username, domain);
        };
    }
}
