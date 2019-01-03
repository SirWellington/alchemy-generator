/*
 * Copyright © 2019. Sir Wellington.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.sirwellington.alchemy.generator

import tech.sirwellington.alchemy.annotations.access.Internal
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.integers

/**

 * @author SirWellington
 */

internal typealias RepeatedFunction = (Int) -> Unit

@Internal
fun doInLoop(function: RepeatedFunction)
{
    val iterations = one(integers(50, 250))

    for (i in 0..iterations - 1)
    {
        function(i)
    }
}
