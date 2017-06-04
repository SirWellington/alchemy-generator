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

import java.util.concurrent.atomic.AtomicInteger
import org.apache.commons.lang3.RandomUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern

import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR

/**
 * Generators for [Booleans][BooleanGenerators].

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

        private val LOG = LoggerFactory.getLogger(BooleanGenerators::class.java)

        /**
         * Generates a series of randomly selected booleans.

         * @return
         */
        fun booleans(): AlchemyGenerator<Boolean>
        {
            return AlchemyGenerator { RandomUtils.nextInt(0, 2) == 1 }
        }

        /**
         * Generates a series of alternating booleans. Values alternate between `true` and
         * `false`.

         * @return
         */
        fun alternatingBooleans(): AlchemyGenerator<Boolean>
        {
            val count = AtomicInteger()
            return AlchemyGenerator { isEven(count.incrementAndGet()) }
        }

        private fun isEven(number: Int): Boolean
        {
            return number % 2 == 0
        }
    }

}