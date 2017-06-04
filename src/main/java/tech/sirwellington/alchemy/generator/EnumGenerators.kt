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

import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR

/**

 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
class EnumGenerators
@Throws(IllegalAccessException::class)
internal constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate")
    }

    companion object
    {

        private val LOG = LoggerFactory.getLogger(EnumGenerators::class.java)


        /**
         * Returns sequence of Enum values from the supplied arguments.
         * <pre>
         * Example:
         *
         * `enum Fruit {APPLE, ORANGE, PEAR}
         *
         * Fruit someFruit = enumValueOf(Fruit.class).get();
        ` *
        </pre> *

         * @param <E>       The type of the Enum
         * *
         * @param enumClass The `class` of the Enum.
         * *
         * *
         * @return A generator that produces values of the supplied enum type.
        </E> */
        inline fun <reified E : Enum<*>> enumValueOf(): AlchemyGenerator<E>
        {
            val klass = E::class.java
            val constants = klass.enumConstants ?: throw IllegalArgumentException("Class is not an Enum type: [${klass.name}]")

            if (constants.isEmpty())
            {
                throw IllegalArgumentException("Enum class has no values")
            }

            return AlchemyGenerator {
                val index = NumberGenerators.integers(0, constants.size).get()
                constants[index]
            }
        }
    }
}
