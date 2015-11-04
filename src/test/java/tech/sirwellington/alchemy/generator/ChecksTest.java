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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class ChecksTest
{

    private String message;

    @Before
    public void setUp()
    {
        message = "some message";
    }

    @Test(expected = IllegalAccessException.class)
    public void testCannotInstantiate() throws InstantiationException, IllegalAccessException
    {
        System.out.println("testCannotInstantiate");
        Checks.class.newInstance();
    }

    @Test
    public void testCheckNotNull()
    {
        System.out.println("testCheckNotNull");

        Object object = new Object();
        Checks.checkNotNull(object);
        Checks.checkNotNull(object, message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckNotNullExpecting()
    {
        System.out.println("testCheckNotNullExpecting");

        Checks.checkNotNull(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckNotNullExpectingWithMessage()
    {
        System.out.println("testCheckNotNullExpectingWithMessage");

        Checks.checkNotNull(null, message);
    }

    @Test
    public void testCheckThat()
    {
        System.out.println("testCheckThat");

        Checks.checkThat(true);
        Checks.checkThat(true, message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckThatExpecting()
    {
        System.out.println("testCheckThatExpecting");

        Checks.checkThat(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckThatExpectingWithMessage()
    {
        System.out.println("testCheckThatExpectingWithMessage");

        Checks.checkThat(false, message);
    }
}
