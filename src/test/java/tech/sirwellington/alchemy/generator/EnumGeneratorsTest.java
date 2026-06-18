/*
 * Copyright © 2026. Sir Wellington.
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
package tech.sirwellington.alchemy.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 * @author SirWellington
 */
class EnumGeneratorsTest extends BaseGeneratorTest {

    @Test
    @DisplayName("EnumGenerators cannot instantiate")
    void testCannotInstantiate() {
        assertThrows(
            IllegalAccessException.class,
            () -> EnumGenerators.class.getDeclaredConstructor().newInstance()
        );
    }

    @Test
    @DisplayName("EnumGenerators valueOf should produce values")
    void testEnumValueOf() {
        // Given
        var generator = EnumGenerators.enumValueOf(Fruit.class);
        assertThat(generator, notNullValue());

        repeatBlock(() -> {
           var fruit = generator.get();
           assertThat(fruit, notNullValue());
           assertThat(fruit, isA(Fruit.class));
        });
    }

    enum Fruit {
        Apple, Orange, Pear, Banana
    }
}