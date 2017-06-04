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

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.either
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.isEmptyString
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.alphabeticString
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

        assertThrows { PeopleGenerators() }
                .isInstanceOf(IllegalAccessException::class.java)

        assertThrows { PeopleGenerators::class.java.newInstance() }
                .isInstanceOf(IllegalAccessException::class.java)
    }

    @Test
    fun testNames()
    {
        println("testNames")

        val instance = PeopleGenerators.names()
        assertThat(instance, notNullValue())

        val upperCasePattern = Pattern.compile("[A-Z]{1}[a-z]+")
        doInLoop { i ->
            val name = instance.get()
            assertThat(name, not(isEmptyString()))
            assertThat(name.length, greaterThanOrEqualTo(2))
            assertThat(upperCasePattern.asPredicate().test(name), `is`(true))
        }
    }

    @Test
    fun testAges()
    {
        println("testAges")

        val instance = PeopleGenerators.ages()
        assertThat(instance, notNullValue())

        doInLoop { i ->
            val age = instance.get()
            assertThat(age, greaterThanOrEqualTo(1))
            assertThat(age, lessThanOrEqualTo(100))
        }
    }

    @Test
    fun testAdultAges()
    {
        println("testAdultAges")

        val instance = PeopleGenerators.adultAges()
        assertThat(instance, notNullValue())

        doInLoop { i ->
            val age = instance.get()
            assertThat(age, greaterThanOrEqualTo(18))
            assertThat(age, lessThanOrEqualTo(100))
        }
    }

    @Test
    fun testChildAges()
    {
        println("testChildAges")

        val instance = PeopleGenerators.childAges()
        assertThat(instance, notNullValue())

        doInLoop { i ->
            val age = instance.get()
            assertThat(age, greaterThanOrEqualTo(1))
            assertThat(age, lessThanOrEqualTo(17))
        }
    }

    @Test
    fun testPhoneNumbers()
    {
        println("testPhoneNumbers")

        val instance = PeopleGenerators.phoneNumbers()
        assertThat(instance, notNullValue())

        doInLoop { i ->
            val phoneNumber = instance.get()
            assertThat(phoneNumber, greaterThanOrEqualTo(100_000_0000L))
            assertThat(phoneNumber, lessThanOrEqualTo(999_999_9999L))
        }
    }

    @Test
    fun testPhoneNumberStrings()
    {
        println("testPhoneNumberStrings")

        val instance = PeopleGenerators.phoneNumberStrings()
        assertThat(instance, notNullValue())

        val phoneNumberPattern = Pattern.compile("\\d{3}\\-\\d{3}\\-\\d{4}")
        doInLoop { i ->
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

        doInLoop { i ->
            val domain = instance.get()
            assertThat(domain, not(isEmptyString()))
            assertThat(domain, either(endsWith(".com")).or(endsWith(".tech")))
        }
    }

    @Test
    fun testEmails()
    {
        println("testEmails")

        val instance = PeopleGenerators.emails()
        assertThat(instance, notNullValue())

        doInLoop { i ->
            val email = instance.get()
            assertThat(email, not(isEmptyString()))
            assertThat(email.contains("@"), `is`(true))
        }
    }

    @Test
    fun testEmailsWithCustomDomains()
    {
        println("testEmailsWithCustomDomains")

        val domains = CollectionGenerators.listOf(alphabeticString(), 10)
        val domainGenerator = StringGenerators.stringsFromFixedList(domains)

        val instance = PeopleGenerators.emails(domainGenerator)
        assertThat(instance, notNullValue())

        doInLoop { i ->
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
    fun testEmailsWithCustomDomainsEdgeCases()
    {
        assertThrows { PeopleGenerators.emails(null!!) }
        assertThrows { PeopleGenerators.emails(AlchemyGenerator<String> { null }) }
        assertThrows { PeopleGenerators.emails(AlchemyGenerator<String> { "" }) }
    }

}