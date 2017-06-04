package tech.sirwellington.alchemy.generator

/*
 * Copyright 2017 RedRoma, Inc.
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

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GeneratorsTest
{
    private lateinit var generator: AlchemyGenerator<String>
    private lateinit var string: String

    @Test
    fun testOne()
    {
        doInLoop {
            string = StringGenerators.alphanumericString().get()
            generator = AlchemyGenerator { string }

            val result = one(generator)
            assertThat(result, equalTo(string))
        }
    }

}