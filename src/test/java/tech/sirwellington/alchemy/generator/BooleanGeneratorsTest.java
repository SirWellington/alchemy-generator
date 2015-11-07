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

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class BooleanGeneratorsTest
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

        assertThrows(() -> new BooleanGenerators())
                .isInstanceOf(IllegalAccessException.class);

        assertThrows(() -> BooleanGenerators.class.newInstance())
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void testBooleans()
    {
        System.out.println("testBooleans");

        AlchemyGenerator<Boolean> instance = BooleanGenerators.booleans();
        assertNotNull(instance);

        Set<Boolean> values = new HashSet<>();
        for (int i = 0; i < iterations; ++i)
        {
            Boolean value = instance.get();
            assertNotNull(value);
            values.add(value);
        }

        assertThat(values.size(), is(2));
    }

    @Test
    public void testAlternatingBooleans()
    {
        System.out.println("testAlternatingBooleans");

        AlchemyGenerator<Boolean> instance = BooleanGenerators.alternatingBooleans();
        
        boolean value = instance.get();
        boolean previous;
        for (int i = 0; i < iterations; ++i)
        {
            previous = value;
            value = instance.get();
            
            assertThat(value, is(not(previous)));
        }
    }

}
