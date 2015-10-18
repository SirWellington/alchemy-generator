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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.StringGenerators.strings;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class StringGeneratorsTest
{

    private int iterations;

    @Before
    public void setUp()
    {
        iterations = RandomUtils.nextInt(500, 5000);
    }

    @Test
    public void testStrings()
    {
        System.out.println("testStrings");
        int length = 59;
        AlchemyGenerator<String> instance = strings(length);
        assertNotNull(instance);
        for (int i = 0; i < iterations; ++i)
        {
            String value = instance.get();
            assertTrue(value.length() == length);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStringsWithBadSize()
    {
        System.out.println("testStringsWithBadSize");
        int length = -59;
        AlchemyGenerator<String> instance = StringGenerators.strings(length);
        assertNotNull(instance);
        instance.get();
    }

    @Test
    public void testHexadecimalString()
    {
        System.out.println("testHexadecimalString");
        int length = 90;
        AlchemyGenerator<String> instance = StringGenerators.hexadecimalString(length);
        for (int i = 0; i < iterations; ++i)
        {
            String value = instance.get();
            assertTrue(value.length() == length);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexadecimalStringWithBadSize()
    {
        System.out.println("testHexadecimalStringWithBadSize");
        int length = -90;
        AlchemyGenerator<String> instance = StringGenerators.hexadecimalString(length);
        instance.get();
    }

    @Test
    public void testAlphabeticString_int()
    {
        System.out.println("testAlphabeticString");
        int length = one(integers(40, 100));

        AlchemyGenerator<String> instance = StringGenerators.alphabeticString(length);
        for (int i = 0; i < iterations; ++i)
        {
            String value = instance.get();
            assertTrue(value.length() == length);
        }
    }

    @Test
    public void testAlphabeticString()
    {
        System.out.println("testAlphabeticString");
        AlchemyGenerator<String> instance = StringGenerators.alphabeticString();
        for (int i = 0; i < iterations; ++i)
        {
            String value = instance.get();
            assertThat(StringUtils.isEmpty(value), is(false));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlphabeticStringWithBadSize()
    {
        System.out.println("testAlphabeticStringWithBadSize");
        int length = 0;
        AlchemyGenerator<String> instance = StringGenerators.alphabeticString(length);
        instance.get();
    }

    @Test
    public void testStringsFromFixedList()
    {
        System.out.println("testStringsFromFixedList");
        List<String> values = new ArrayList<>();
        for (int i = 0; i < iterations; ++i)
        {
            values.add(RandomStringUtils.randomAlphabetic(i + 1));
        }
        AlchemyGenerator<String> instance = StringGenerators.stringsFromFixedList(values);

        for (int i = 0; i < iterations; ++i)
        {
            String value = instance.get();
            assertTrue(values.contains(value));
        }
    }

    @Test
    public void testStringsFromFixedList_List()
    {
        System.out.println("testStringsFromFixedList_List");
        List<String> values = new ArrayList<>();
        String one = strings(10).get();
        String two = strings(10).get();
        String three = strings(10).get();
        values.add(one);
        values.add(two);
        values.add(three);

        AlchemyGenerator<String> instance = StringGenerators.stringsFromFixedList(one, two, three);
        for (int i = 0; i < iterations; ++i)
        {
            assertThat(instance.get(), org.hamcrest.Matchers.isIn(values));
        }
    }

    @Test
    public void testStringsFromFixedList_StringArr()
    {
        System.out.println("testStringsFromFixedList_StringArr");
        AlchemyGenerator<String> alphabetic = StringGenerators.alphabeticString(10);
        String[] values = new String[]
        {
            alphabetic.get(), alphabetic.get(), alphabetic.get()
        };
        AlchemyGenerator<String> instance = StringGenerators.stringsFromFixedList(values);
        assertThat(instance, notNullValue());
        for (int i = 0; i < 50; ++i)
        {
            String result = instance.get();
            assertThat(result, Matchers.isIn(values));
        }
    }

    @Test
    public void testAlphabeticStringWithNoArgs()
    {
        System.out.println("testAlphabeticStringWithNoArgs");

        AlchemyGenerator<String> instance = StringGenerators.alphabeticString();
        assertThat(instance, notNullValue());

        for (int i = 0; i < iterations; ++i)
        {
            String value = instance.get();
            assertThat(value, notNullValue());
            assertThat(value.length(), greaterThanOrEqualTo(5));
            assertThat(value.length(), lessThanOrEqualTo(20));
        }

    }
}
