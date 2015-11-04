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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class AlchemyGeneratorTest
{

    @Before
    public void setUp()
    {
    }

    @Test
    public void testGet()
    {
    }

    @Test
    public void testOne()
    {
        System.out.println("testOne");

        AlchemyGenerator instance = mock(AlchemyGenerator.class);
        Object expected = mock(Object.class);
        when(instance.get()).thenReturn(expected);
        Object result = AlchemyGenerator.one(instance);
        verify(instance).get();
        assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOneWithBadArgs()
    {
        System.out.println("testOneWithBadArgs");

        AlchemyGenerator.one(null);
    }

}
