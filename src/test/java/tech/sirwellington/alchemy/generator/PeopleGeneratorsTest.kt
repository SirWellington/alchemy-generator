/*
 * Copyright © 2026. Sir Wellington.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.sirwellington.alchemy.generator

import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.StringGenerators.alphabeticStrings
import tech.sirwellington.alchemy.generator.StringGenerators.stringsFromFixedList
import tech.sirwellington.alchemy.generator.Throwables.assertThrows
import java.util.regex.Pattern


/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class PeopleGeneratorsTest
{

    @Before
    fun setUp()
    {
    }

    @Test
    fun testCannotInstantiate()
    {
        println("testCannotInstantiate")

        assertThrows { PeopleGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }

    @Test
    fun testFirstNames()
    {
        println("testFirstNames")

        val firstName = PeopleGenerators.firstNames()
        testNameGenerator(firstName)
    }

    @Test
    fun testLastNames()
    {
        println("testLastNames")

        val lastNames = PeopleGenerators.lastNames()
        testNameGenerator(lastNames)
    }

    @Test
    fun testFullNames()
    {
        println("testFullNames")

        val fullNames = PeopleGenerators.fullNames()
        testNameGenerator(fullNames)

        repeatTest()
        {
            val name = fullNames.get()
            assertThat(name, not(isEmptyString()))
            val split = name.split(" ")
            assertThat(split.size, greaterThanOrEqualTo(2))
            assertThat(split.size, lessThanOrEqualTo(3))
        }
    }

    private fun testNameGenerator(generator: AlchemyGenerator<String>)
    {
        assertThat(generator, notNullValue())

        val titleCasePattern = Pattern.compile("[A-Z][a-z]+")

        repeatTest()
        {
            val name = generator.get()
            assertThat(name, not(isEmptyString()))
            assertThat(name.length, greaterThanOrEqualTo(2))
            assertThat(titleCasePattern.asPredicate().test(name), `is`(true))
        }
    }

    @Test
    fun testAges()
    {
        println("testAges")

        val instance = PeopleGenerators.ages()
        assertThat(instance, notNullValue())

        repeatTest()
        {
            val age = instance.get()
            assertThat(age, greaterThanOrEqualTo(1))
            assertThat(age, lessThanOrEqualTo(108))
        }
    }

    @Test
    fun testAdultAges()
    {
        println("testAdultAges")

        val instance = PeopleGenerators.adultAges()
        assertThat(instance, notNullValue())

        repeatTest()
        {
            val age = instance.get()
            assertThat(age, greaterThanOrEqualTo(18))
            assertThat(age, lessThanOrEqualTo(108))
        }
    }

    @Test
    fun testChildAges()
    {
        println("testChildAges")

        val instance = PeopleGenerators.childAges()
        assertThat(instance, notNullValue())

        repeatTest()
        {
            val age = instance.get()
            assertThat(age, greaterThanOrEqualTo(1))
            assertThat(age, lessThanOrEqualTo(17))
        }
    }

    @Test
    fun testPhoneNumbers_NoCountryCode()
    {
        println("testPhoneNumbers_NoCountryCode")

        val instance = PeopleGenerators.phoneNumbers()
        assertThat(instance, notNullValue())

        val phoneNumberPattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}")
        repeatTest()
        {
            val phoneNumber = instance.get()
            assertThat(phoneNumber, not(isEmptyString()))
            assertThat(phoneNumberPattern.asPredicate().test(phoneNumber), `is`(true))
        }
    }

    @Test
    fun testPhoneNumbers_USCountryCode()
    {
        println("testPhoneNumbers_USCountryCode")

        val phoneCountryCode = "+1"
        val instance = PeopleGenerators.phoneNumbers(phoneCountryCode)
        assertThat(instance, notNullValue())

        val phoneNumberPattern = Pattern.compile("\\+1 \\d{3}-\\d{3}-\\d{4}")

        repeatTest()
        {
            val phoneNumber = instance.get()
            assertThat(phoneNumber, not(isEmptyString()))
            assertThat(phoneNumberPattern.asPredicate().test(phoneNumber), `is`(true))
        }
    }

    @Test
    fun testPopularEmailDomains()
    {
        println("testPopularEmailDomains")

        val instance = PeopleGenerators.popularEmailDomains()
        assertThat(instance, notNullValue())

        repeatTest()
        {
            val domain = instance.get()
            assertThat(domain, not(isEmptyString()))
            assertThat(domain, either(endsWith(".com")).or(endsWith(".tech")))
        }
    }

    @Test
    fun testEmailAddresses()
    {
        println("testEmails")

        val instance = PeopleGenerators.emailAddresses()
        assertThat(instance, notNullValue())

        repeatTest()
        {
            val email = instance.get()
            assertThat(email, not(isEmptyString()))
            assertThat(email.contains("@"), `is`(true))
        }
    }

    @Test
    fun testEmailAddressesWithCustomDomains()
    {
        println("testEmailsWithCustomDomains")

        val domains = CollectionGenerators.listOf(alphabeticStrings(), 10)
        val domainGenerator = stringsFromFixedList(domains)

        val instance = PeopleGenerators.emailAddresses(domainGenerator)
        assertThat(instance, notNullValue())

        repeatTest()
        {
            val email = instance.get()
            assertThat(email, not(isEmptyString()))
            assertEndsWithOneOfTheDomains(email, domains)
        }
    }

    private fun assertEndsWithOneOfTheDomains(email: String, domains: List<String>)
    {
        val anyMatch = domains.stream()
                .anyMatch { domain -> email.endsWith(domain) }

        if (!anyMatch)
        {
            fail("Expected email $email to end with one of these domains: $domains")
        }

    }

    @Test
    fun testEmailAddressesWithCustomDomainsEdgeCases()
    {
        assertThrows { PeopleGenerators.emailAddresses(AlchemyGenerator<String> { null }) }
        assertThrows { PeopleGenerators.emailAddresses(AlchemyGenerator<String> { "" }) }
    }

    @Test
    fun testProfessions()
    {
        val generator = PeopleGenerators.professions()

        repeatTest loop@
        {
            val result = generator.get()
            assertThat(result, notNullValue())
            assertThat(result, not(isEmptyString()))
        }

    }
}