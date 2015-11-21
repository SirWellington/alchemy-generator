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

import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class BinaryGeneratorsTest
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

        assertThrows(() -> new BinaryGenerators())
                .isInstanceOf(IllegalAccessException.class);

        assertThrows(() -> BinaryGenerators.class.newInstance())
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void testBinaryGenerator()
    {
        System.out.println("binaryGenerator");
        int bytes = integers(50, 5000).get();
        AlchemyGenerator<byte[]> instance = BinaryGenerators.binary(bytes);

        assertNotNull(instance);

        for (int i = 0; i < iterations; ++i)
        {
            byte[] value = instance.get();
            assertThat(value, notNullValue());
            assertThat(value.length, is(bytes));
        }
    }

}
