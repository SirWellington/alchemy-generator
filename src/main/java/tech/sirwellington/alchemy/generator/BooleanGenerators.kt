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

import org.apache.commons.lang3.RandomUtils
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR
import java.util.concurrent.atomic.AtomicInteger

/**
 * Generators for [Booleans][BooleanGenerators].
 *
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
class BooleanGenerators
@Throws(IllegalAccessException::class)
internal constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate this class")
    }

    companion object
    {

        /**
         * Generates a series of randomly selected booleans.
         *
         * @return
         */
        @JvmStatic
        fun booleans(): AlchemyGenerator<Boolean>
        {
            return AlchemyGenerator { RandomUtils.nextInt(0, 2) == 1 }
        }

        /**
         * Generates a series of alternating booleans. Values alternate between `true` and
         * `false`.
         *
         * @return
         */
        @JvmStatic
        fun alternatingBooleans(): AlchemyGenerator<Boolean>
        {
            val count = AtomicInteger()
            return AlchemyGenerator { count.incrementAndGet().isEven() }
        }

        private fun Int.isEven(): Boolean
        {
            return this % 2 == 0
        }
    }

}
