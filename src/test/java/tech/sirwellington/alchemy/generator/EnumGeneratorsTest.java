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

import java.util.function.Supplier;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class EnumGeneratorsTest
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

        assertThrows(() -> new EnumGenerators())
                .isInstanceOf(IllegalAccessException.class);

        assertThrows(() -> EnumGenerators.class.newInstance())
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void testEnumValueOf()
    {
        System.out.println("testEnumValueOf");
        Supplier<Fruit> fruits = EnumGenerators.enumValueOf(Fruit.class);
        assertThat(fruits, notNullValue());
        for (int i = 0; i < iterations; ++i)
        {
            Fruit fruit = fruits.get();
            assertThat(fruit, notNullValue());
            assertThat(fruit, Matchers.isA(Fruit.class));
        }
    }

    enum Fruit
    {

        Apple, Orange, Pear
    }

}
