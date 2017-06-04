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
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.negativeIntegers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.positiveIntegers;
import static tech.sirwellington.alchemy.generator.StringGenerators.hexadecimalString;
import static tech.sirwellington.alchemy.generator.StringGenerators.strings;
import static tech.sirwellington.alchemy.generator.StringGenerators.uuids;
import static tech.sirwellington.alchemy.generator.Tests.doInLoop;
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
    public void testListOf_AlchemyGenerator()
    {
        System.out.println("testListOf_AlchemyGenerator");

        Object value = new Object();
        AlchemyGenerator generator = mock(AlchemyGenerator.class);
        when(generator.get()).thenReturn(value);

        List result = CollectionGenerators.listOf(generator);
        assertThat(result.isEmpty(), is(false));
        result.forEach(i -> assertThat(i, is(value)));
    }

    @Test
    public void testListOf_AlchemyGenerator_int()
    {
        System.out.println("testListOf_AlchemyGenerator_int");

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
    public void testMapOfWithInt()
    {
        System.out.println("testMapOfWithInt");

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

    @Test
    public void testConvenienceMapOf()
    {
        System.out.println("testConvenienceMapOf");

        AlchemyGenerator<String> keyGenerator = mock(AlchemyGenerator.class);
        AlchemyGenerator<Integer> valueGenerator = mock(AlchemyGenerator.class);

        when(keyGenerator.get())
                .thenAnswer(invk -> one(strings()));

        when(valueGenerator.get())
                .thenAnswer(invk -> one(positiveIntegers()));

        Map result = CollectionGenerators.mapOf(keyGenerator, valueGenerator);
        assertThat(result, notNullValue());
        assertThat(result.isEmpty(), is(false));

        //'At least', in case duplicate entries were generated
        verify(keyGenerator, atLeast(result.size())).get();
        verify(valueGenerator, atLeast(result.size())).get();
    }

    @Test
    public void testFromList()
    {
        System.out.println("testFromList");

        List<String> list = new ArrayList<>();

        int size = one(integers(1, 100));
        for (int i = 0; i < size; ++i)
        {
            list.add(one(hexadecimalString(15)));
        }

        AlchemyGenerator<String> instance = CollectionGenerators.fromList(list);
        assertThat(instance, notNullValue());
        
        doInLoop(i ->
        {
            String value = instance.get();
            assertThat(list.contains(value), is(true));
        });
    }

    
    @Test
    public void testListOfEdgeCases()
    {
        System.out.println("testListOfEdgeCases");
        
        int badSize = one(negativeIntegers());
        assertThrows(() -> CollectionGenerators.listOf(uuids, badSize))
            .isInstanceOf(IllegalArgumentException.class);
        
        List<String> result = CollectionGenerators.listOf(uuids, 0);
        assertThat(result, notNullValue());
        assertThat(result, is(empty()));
        
    }
}
