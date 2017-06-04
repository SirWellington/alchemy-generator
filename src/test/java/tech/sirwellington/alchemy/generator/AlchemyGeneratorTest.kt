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
package tech.sirwellington.alchemy.generator

import org.junit.Assert.assertEquals

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

import org.mockito.runners.MockitoJUnitRunner

/**

 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner::class)
class AlchemyGeneratorTest
{

    @Before
    fun setUp()
    {
    }

    @Test
    fun testGet()
    {
    }

    @Test
    fun testOne()
    {
        println("testOne")

        val instance = mock(AlchemyGenerator::class.java)
        val expected = mock(Any::class.java)
        `when`(instance.get()).thenReturn(expected)
        val result = AlchemyGenerator.Get.one(instance)
        verify(instance).get()
        assertEquals(expected, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testOneWithBadArgs()
    {
        println("testOneWithBadArgs")

        AlchemyGenerator.Get.one<Any>(null)
    }

}
