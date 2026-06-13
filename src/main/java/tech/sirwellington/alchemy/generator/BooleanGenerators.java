/*
 *Copyright © 2026 Wellington Moreno<jwellington.moreno@gmail.com>.
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


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;

/**
 * Generators for {@link Boolean Booleans}.
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
public final class BooleanGenerators {
    private final static Logger LOG = LoggerFactory.getLogger(BooleanGenerators.class);

    private BooleanGenerators() throws IllegalAccessException {
        throw new IllegalAccessException("cannot directly instatiate this class");
    }
    
    /**
     * Generates a series of randomly selected booleans.
     * @see #alternatingBooleans()
     */
    static AlchemyGenerator<Boolean> booleans() {
        return () -> RandomUtils.secure().randomBoolean();
    }
    
    /**
     * Generates a series of alternating boolean. In other words, `true, false, true, false, etc`.
     * @see #booleans()
     */
    static AlchemyGenerator<Boolean> alternatingBooleans() {
        AtomicInteger count = new AtomicInteger();
        return () -> isEven(count.incrementAndGet());
    }
    
    private static boolean isEven(int num) {
        return num % 2 == 0;
    }
}
