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
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.StringGenerators.strings;
import static tech.sirwellington.alchemy.generator.StringGenerators.uuids;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectionGeneratorsTest
{

    private int iterations;

    @Before
    public void setUp()
    {
        iterations = RandomUtils.nextInt(500, 5000);
    }

    @Test
    public void testCannotInstantiate()
    {
        System.out.println("testCannotInstantiate");

        assertThrows(() -> new CollectionGenerators())
                .isInstanceOf(IllegalAccessException.class);

        assertThrows(() -> CollectionGenerators.class.newInstance())
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void testListOf_DataGenerator()
    {
        System.out.println("testListOf_DataGenerator");

        Object value = new Object();
        AlchemyGenerator generator = mock(AlchemyGenerator.class);
        when(generator.get()).thenReturn(value);

        List result = CollectionGenerators.listOf(generator);
        assertThat(result.isEmpty(), is(false));
        result.forEach(i -> assertThat(i, is(value)));
    }

    @Test
    public void testListOf_DataGenerator_int()
    {
        System.out.println("testListOf_DataGenerator_int");

        Object value = new Object();
        int size = 50;
        AlchemyGenerator generator = mock(AlchemyGenerator.class);
        when(generator.get()).thenReturn(value);
        List result = CollectionGenerators.listOf(generator, 50);
        assertThat(result, notNullValue());
        assertThat(result.size(), is(size));
        result.forEach(i -> assertThat(i, is(value)));
    }

    @Test
    public void testMapOf()
    {
        System.out.println("testMapOf");
        
        String string = strings(50).get();
        AlchemyGenerator<String> valueGenerator = () -> string;
        int size = integers(5, 100).get();

        Map<String, String> result = CollectionGenerators.mapOf(uuids, valueGenerator, size);
        assertThat(result, notNullValue());
        assertThat(result.size(), is(size));

        for (Map.Entry<String, String> entry : result.entrySet())
        {
            UUID.fromString(entry.getKey());
            assertThat(entry.getValue(), is(string));
        }

    }

}
