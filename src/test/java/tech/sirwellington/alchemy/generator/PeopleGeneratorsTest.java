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

package tech.sirwellington.alchemy.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphabeticStrings;
import static tech.sirwellington.alchemy.generator.StringGenerators.stringsFromFixedList;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

class PeopleGeneratorsTest extends BaseGeneratorTest {

    static Pattern PHONE_NUMBER_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");

    @DisplayName("testCannotInstantiate")
    @Test
    void testCannotInstantiate() {
        assertThrows(() -> PeopleGenerators.class.getDeclaredConstructor().newInstance())
            .isInstanceOf(IllegalAccessException.class);
    }

    @DisplayName("testFirstNames")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testFirstNames() {
        var firstName = PeopleGenerators.firstNames();
        testNameGenerator(firstName);
    }

    @DisplayName("testLastNames")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testLastNames() {
        var lastNames = PeopleGenerators.lastNames();
        testNameGenerator(lastNames);
    }

    @DisplayName("testFullNames")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testFullNames() {
        var fullNames = PeopleGenerators.fullNames();
        testNameGenerator(fullNames);

        var name = fullNames.get();
        assertThat(name, not(isEmptyString()));
        var split = name.split(" ");
        assertThat(split.length, greaterThanOrEqualTo(2));
        assertThat(
            "name is too long: " + name,
            split.length,
            lessThanOrEqualTo(4)
        );
    }

    private void testNameGenerator(AlchemyGenerator<String> generator) {
        assertThat(generator, notNullValue());

        var titleCasePattern = Pattern.compile("[A-Z][a-z]+");

        var name = generator.get();
        assertThat(name, not(isEmptyString()));
        assertThat(name.length(), greaterThanOrEqualTo(2));
        assertThat(titleCasePattern.asPredicate().test(name), is(true));
    }

    @DisplayName("testAges")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAges() {
        var generator = PeopleGenerators.ages();
        assertThat(generator, notNullValue());

        var age = generator.get();
        assertThat(age, greaterThanOrEqualTo(1));
        assertThat(age, lessThanOrEqualTo(108));
    }

    @DisplayName("testAdultAges")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAdultAges() {
        var generator = PeopleGenerators.adultAges();
        assertThat(generator, notNullValue());

        var age = generator.get();
        assertThat(age, greaterThanOrEqualTo(18));
        assertThat(age, lessThanOrEqualTo(108));
    }

    @DisplayName("testChildAges")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testChildAges() {
        var generator = PeopleGenerators.childAges();
        assertThat(generator, notNullValue());

        var age = generator.get();
        assertThat(age, greaterThanOrEqualTo(1));
        assertThat(age, lessThanOrEqualTo(17));
    }

    @DisplayName("testPhoneNumbers_NoCountryCode")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testPhoneNumbers_NoCountryCode() {
        var instance = PeopleGenerators.phoneNumbers();
        assertThat(instance, notNullValue());

        var phoneNumber = instance.get();
        assertThat(phoneNumber, not(isEmptyString()));
        var matchesPattern = PHONE_NUMBER_PATTERN.asPredicate().test(phoneNumber);
        assertThat(matchesPattern, is(true));
    }

    @DisplayName("testPhoneNumbers_USCountryCode")
    @RepeatedTest(20)
    void testPhoneNumbers_USCountryCode() {
        var phoneCountryCode = "+1";
        var generator = PeopleGenerators.phoneNumbers(phoneCountryCode);
        assertThat(generator, notNullValue());

        var phoneNumber = generator.get();
        assertThat(phoneNumber, not(isEmptyString()));
        var matchesPattern = PHONE_NUMBER_PATTERN.asPredicate().test(phoneNumber);
        assertThat(matchesPattern, is(true));
    }

    @DisplayName("testPopularEmailDomains")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testPopularEmailDomains() {
        var generator = PeopleGenerators.popularEmailDomains();
        assertThat(generator, notNullValue());

        var domain = generator.get();
        assertThat(domain, not(isEmptyString()));
        assertThat(domain, either(endsWith(".com")).or(endsWith(".tech")));
    }

    @DisplayName("testEmailAddresses")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testEmailAddresses() {
        var generator = PeopleGenerators.emailAddresses();
        assertThat(generator, notNullValue());

        var email = generator.get();
        assertThat(email, not(isEmptyString()));
        assertThat(email.contains("@"), is(true));
    }

    @DisplayName("testEmailAddressesWithCustomDomains")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testEmailAddressesWithCustomDomains() {
        var domains = CollectionGenerators.listOf(alphabeticStrings(), 10);
        var domainGenerator = stringsFromFixedList(domains);

        var generator = PeopleGenerators.emailAddresses(domainGenerator);
        assertThat(generator, notNullValue());

        var email = generator.get();
        assertThat(email, not(isEmptyString()));
        assertEndsWithOneOfTheDomains(email, domains);
    }

    private void assertEndsWithOneOfTheDomains(String email, List<String> domains) {
        boolean anyMatch = domains.stream()
                                  .anyMatch(email::endsWith);

        if (!anyMatch) {
            throw new AssertionError("Expected email " + email + " to end with one of these domains: " + domains);
        }

    }

    @DisplayName("testEmailAddressesWithCustomDomainsEdgeCases")
    @RepeatedTest(1)
    void testEmailAddressesWithCustomDomainsEdgeCases() {
        assertThrows(() -> PeopleGenerators.emailAddresses(() -> null));
        assertThrows(() -> PeopleGenerators.emailAddresses(() -> ""));
    }

    @DisplayName("testProfessions")
    @RepeatedTest(20)
    void testProfessions() {
        var generator = PeopleGenerators.professions();

        var result = generator.get();
        assertThat(result, notNullValue());
        assertThat(result, not(isEmptyString()));
    }
}
