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
import static tech.sirwellington.alchemy.generator.StringGenerators.alphanumericString;
import static tech.sirwellington.alchemy.generator.StringGenerators.stringsFromFixedList;

/**
 *
 * @author SirWellington
 */
@NonInstantiable
public final class EmailGenerators
{
    private final static Logger LOG = LoggerFactory.getLogger(EmailGenerators.class);

    EmailGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
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
        checkNotNull(domainGenerator,"domainGenerator missing");
        checkNotEmpty(domainGenerator.get(), "Email Domain Generator returned empty String");

        return () ->
        {
            String username = one(alphanumericString());
            String domain = domainGenerator.get();
            return format("%s@%s", username, domain);
        };
    }
}
