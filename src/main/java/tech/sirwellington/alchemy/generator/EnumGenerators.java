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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;

/**
 *
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
public final class EnumGenerators
{

    private final static Logger LOG = LoggerFactory.getLogger(EnumGenerators.class);
    
    EnumGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
    }


    /**
     * Returns sequence of Enum values from the supplied arguments.
     * <pre>
     * Example:
     *
     * {@code
     * enum Fruit {APPLE, ORANGE, PEAR}
     *
     * Fruit someFruit = enumValueOf(Fruit.class).get();
     * }
     * </pre>
     *
     * @param <E>       The type of the Enum
     * @param enumClass The {@code class} of the Enum.
     *
     * @return A generator that produces values of the supplied enum type.
     */
    public static <E extends Enum> Supplier<E> enumValueOf(Class<E> enumClass)
    {
        checkNotNull(enumClass);

        E[] constants = enumClass.getEnumConstants();
        checkThat(constants != null, "Class is not an Enum: " + enumClass.getName());
        checkThat(constants.length > 0, "Enum has no values");
        return () ->
        {
            int index = integers(0, constants.length).get();
            return constants[index];
        };
    }
}
