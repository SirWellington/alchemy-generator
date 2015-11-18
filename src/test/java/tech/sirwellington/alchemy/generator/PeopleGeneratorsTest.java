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

import java.util.List;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static tech.sirwellington.alchemy.generator.CollectionGenerators.listOf;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphabeticString;
import static tech.sirwellington.alchemy.generator.Tests.doInLoop;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class PeopleGeneratorsTest 
{

    @Before
    public void setUp()
    {
    }

    @Test
    public void testCannotInstantiate()
    {
        System.out.println("testCannotInstantiate");

        assertThrows(() -> new PeopleGenerators())
        .isInstanceOf(IllegalAccessException.class);

        assertThrows(() -> PeopleGenerators.class.newInstance())
        .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void testName()
    {
        System.out.println("testName");

        AlchemyGenerator<String> instance = PeopleGenerators.name();
        assertThat(instance, notNullValue());
        
        Pattern upperCasePattern = Pattern.compile("[A-Z]{1}[a-z]+");
        doInLoop(i -> 
        {
            String name = instance.get();
            assertThat(name, not(isEmptyString()));
            assertThat(name.length(), greaterThanOrEqualTo(2));
            assertThat(upperCasePattern.asPredicate().test(name), is(true));
        });
    }

    @Test
    public void testAge()
    {
        System.out.println("testAge");

        AlchemyGenerator<Integer> instance = PeopleGenerators.age();
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            int age = instance.get();
            assertThat(age, greaterThanOrEqualTo(1));
            assertThat(age, lessThanOrEqualTo(100));
        });
    }

    @Test
    public void testAdultAge()
    {
        System.out.println("testAdultAge");

        AlchemyGenerator<Integer> instance = PeopleGenerators.adultAge();
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            int age = instance.get();
            assertThat(age, greaterThanOrEqualTo(18));
            assertThat(age, lessThanOrEqualTo(100));
        });
    }

    @Test
    public void testChildAge()
    {
        System.out.println("testChildAge");

        AlchemyGenerator<Integer> instance = PeopleGenerators.childAge();
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            int age = instance.get();
            assertThat(age, greaterThanOrEqualTo(1));
            assertThat(age, lessThanOrEqualTo(17));
        });
    }

    @Test
    public void testPhoneNumber()
    {
        System.out.println("testPhoneNumber");

        AlchemyGenerator<Long> instance = PeopleGenerators.phoneNumber();
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            long phoneNumber = instance.get();
            assertThat(phoneNumber, greaterThanOrEqualTo(100_000_0000L));
            assertThat(phoneNumber, lessThanOrEqualTo(999_999_9999L));
        });
    }

    @Test
    public void testPhoneNumberString()
    {
        System.out.println("testPhoneNumberString");

        AlchemyGenerator<String> instance = PeopleGenerators.phoneNumberString();
        assertThat(instance, notNullValue());

        Pattern phoneNumberPattern = Pattern.compile("\\d{3}\\-\\d{3}\\-\\d{4}");
        doInLoop(i ->
        {
            String phoneNumber = instance.get();
            assertThat(phoneNumber, not(isEmptyString()));
            assertThat(phoneNumberPattern.asPredicate().test(phoneNumber), is(true));
        });
    }

     @Test
    public void testPopularEmailDomains()
    {
        System.out.println("testPopularEmailDomains");

        AlchemyGenerator<String> instance = PeopleGenerators.popularEmailDomains();
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            String domain = instance.get();
            assertThat(domain, not(isEmptyString()));
            assertThat(domain, either(endsWith(".com")).or(endsWith(".tech")));
        });
    }

    @Test
    public void testEmails()
    {
        System.out.println("testEmails");

        AlchemyGenerator<String> instance = PeopleGenerators.emails();
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            String email = instance.get();
            assertThat(email, not(isEmptyString()));
            assertThat(email.contains("@"), is(true));
        });
    }

    @Test
    public void testEmailsWithCustomDomains()
    {
        System.out.println("testEmailsWithCustomDomains");

        List<String> domains = listOf(alphabeticString(), 10);
        AlchemyGenerator<String> domainGenerator = StringGenerators.stringsFromFixedList(domains);

        AlchemyGenerator<String> instance = PeopleGenerators.emails(domainGenerator);
        assertThat(instance, notNullValue());

        doInLoop(i ->
        {
            String email = instance.get();
            assertThat(email, not(isEmptyString()));
            assertEndsWithOneOfTheDomains(email, domains);
        });
    }

    private void assertEndsWithOneOfTheDomains(String email, List<String> domains)
    {
        boolean anyMatch = domains.stream()
        .anyMatch(domain -> email.endsWith(domain));

        if (!anyMatch)
        {
            fail("Expected email " + email + " to end with one of these domains: " + domains);
        }

    }

    @Test
    public void testEmailsWithCustomDomainsEdgeCases()
    {
        assertThrows(() -> PeopleGenerators.emails(null))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> PeopleGenerators.emails(() -> null))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> PeopleGenerators.emails(() -> ""))
            .isInstanceOf(IllegalArgumentException.class);
    }

}