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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static java.lang.Character.isDigit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.DateGenerators.anyTime;
import static tech.sirwellington.alchemy.generator.NumberGenerators.*;
import static tech.sirwellington.alchemy.generator.StringGenerators.*;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

class StringGeneratorsTest extends BaseGeneratorTest {

    @DisplayName("testCannotInstantiate")
    @Test
    void testCannotInstantiate() {
        assertThrows(() -> StringGenerators.class.getDeclaredConstructor().newInstance())
            .isInstanceOf(IllegalAccessException.class);
    }

    @DisplayName("testStrings")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testStrings() {
        var generator = strings();
        assertThat(generator, notNullValue());

        var value = generator.get();
        assertThat(value, not(isEmptyOrNullString()));
    }

    @DisplayName("testStringsWithLength")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testStringsWithLength() {
        var length = one(smallPositiveIntegers());
        var instance = strings(length);
        assertThat(instance, notNullValue());

        var value = instance.get();
        assertThat(value.length(), equalTo(length));
    }

    @DisplayName("testStringsWithBadSize")
    @RepeatedTest(1)
    void testStringsWithBadSize() {
        var length = one(negativeIntegers());
        assertThrows(() -> StringGenerators.strings(length))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("testHexadecimalString")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testHexadecimalString() {
        var length = 90;
        var instance = StringGenerators.hexadecimalString(length);

        var value = instance.get();
        assertThat(value.length(), equalTo(length));
    }

    @DisplayName("testHexadecimalStringWithBadSize")
    @RepeatedTest(1)
    void testHexadecimalStringWithBadSize() {
        var length = -90;
        assertThrows(() -> StringGenerators.hexadecimalString(length))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("testAlphabeticStringWithLength")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAlphabeticStringWithLength() {
        var length = one(integers(40, 100));
        var instance = alphabeticStrings(length);

        var value = instance.get();
        assertThat(value.length(), equalTo(length));
    }

    @DisplayName("testAlphabeticString")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAlphabeticString() {
        var instance = alphabeticStrings();

        var value = instance.get();
        assertThat(value, not(isEmptyString()));
    }

    @DisplayName("testAlphabeticStringWithBadSize")
    @RepeatedTest(1)
    void testAlphabeticStringWithBadSize() {
        var length = 0;
        assertThrows(() -> alphabeticStrings(length))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("testAlphanumericString")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAlphanumericString() {
        var instance = StringGenerators.alphanumericStrings();

        var value = instance.get();
        assertThat(value, not(isEmptyString()));
        assertThat(value.length(), greaterThanOrEqualTo(10));
        assertThat(value.length(), lessThanOrEqualTo(100));
    }

    @DisplayName("testAlphanumericStringWithLength")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAlphanumericStringWithLength() {
        var length = one(integers(10, 100));
        var instance = StringGenerators.alphanumericStrings(length);

        var value = instance.get();
        assertThat(value.length(), equalTo(length));

        assertThrows(() -> alphabeticStrings(one(negativeIntegers())))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("testNumericString")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testNumericString() {
        var instance = StringGenerators.numericStrings();
        assertThat(instance, notNullValue());

        var value = instance.get();
        assertThat(value, not(isEmptyOrNullString()));
        assertAllDigits(value);
    }

    @DisplayName("testNumericStringWithLength")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testNumericStringWithLength() {
        var length = one(integers(1, 100));
        var instance = StringGenerators.numericStrings(length);
        assertThat(instance, notNullValue());

        var value = instance.get();
        assertThat(value, not(isEmptyOrNullString()));
        assertThat(value.length(), equalTo(length));
        assertAllDigits(value);
    }

    @DisplayName("testStringsFromFixedList")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testStringsFromFixedList() {
        ArrayList<String> values = new ArrayList<>();

        repeatBlock(20, () -> {
            var seed = RANDOM.nextInt(100, 10_000);
            var randomHexString = Integer.toHexString(seed);
            values.add(randomHexString);
        });

        var instance = stringsFromFixedList(values);

        var value = instance.get();
        assertThat(value, isIn(values));
    }

    @DisplayName("testStringsFromFixedList_List")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testStringsFromFixedList_List() {
        var values = new ArrayList<String>();
        var one = strings(10).get();
        var two = strings(10).get();
        var three = strings(10).get();

        values.add(one);
        values.add(two);
        values.add(three);

        var instance = stringsFromFixedList(one, two, three);

        var value = instance.get();
        assertThat(value, isIn(values));
    }

    @DisplayName("testStringsFromFixedList_StringArr")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testStringsFromFixedList_StringArr() {
        var alphabetic = alphabeticStrings(10);
        var values = new String[]{
            alphabetic.get(),
            alphabetic.get(),
            alphabetic.get()
        };

        var instance = stringsFromFixedList(values);

        assertThat(instance, notNullValue());

        String result = instance.get();
        assertThat(result, isIn(values));
    }

    @DisplayName("testAlphabeticStringWithNoArgs")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testAlphabeticStringWithNoArgs() {
        var instance = alphabeticStrings();

        assertThat(instance, notNullValue());

        var value = instance.get();
        assertThat(value, notNullValue());
        assertThat(value.length(), greaterThanOrEqualTo(10));
        assertThat(value.length(), lessThanOrEqualTo(100));
    }

    @DisplayName("testUuids")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testUuids() {
        var uuids = new HashSet<String>();
        var generator = StringGenerators.UUIDS;

        final int iterations = 20;

        repeatBlock(iterations, () -> {
            var value = generator.get();
            assertThat(value, notNullValue());
            assertThat(value.isEmpty(), is(false));
            uuids.add(value);
        });

        assertThat(uuids.size(), equalTo(iterations));
    }

    @DisplayName("testUuidsFunction")
    @RepeatedTest(1)
    void testUuidsFunction() {
        var generator = StringGenerators.UUIDS;
        assertThat(StringGenerators.uuids(), sameInstance(generator));
    }

    @DisplayName("testAsString")
    @RepeatedTest(20)
    void testAsString() {
        AlchemyGenerator<Date> generator = mock();
        var mockValue = one(anyTime());
        Mockito.when(generator.get()).thenReturn(mockValue);

        var instance = StringGenerators.toString(generator);
        assertThat(instance, notNullValue());

        AtomicInteger iterations = new AtomicInteger();
        repeatBlock(20, () -> {
            String result = instance.get();
            assertThat(result, not(isEmptyOrNullString()));
            var expectedInvocations = iterations.get() + 1;
            Mockito.verify(generator, times(expectedInvocations)).get();
            iterations.incrementAndGet();
        });
    }

    private void assertAllDigits(String value) {
        for (char c : value.toCharArray()) {
            assertThat(isDigit(c), is(true));
        }
    }

}
