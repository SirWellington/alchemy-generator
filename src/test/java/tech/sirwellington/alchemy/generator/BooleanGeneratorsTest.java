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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 * Tests for {@link BooleanGenerators}.
 *
 * @author SirWellington
 */
@DisplayName("BooleanGenerators should")
class BooleanGeneratorsTest extends BaseGeneratorTest {

    @Test
    void testCannotInstantiate() throws Exception {
        assertThrows(() -> BooleanGenerators.class.getDeclaredConstructor().newInstance())
            .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    void testBooleans() {
        var instance = BooleanGenerators.booleans();
        assertNotNull(instance);

        Set<Boolean> values = new HashSet<>();
        int repetitions = 25; // enough to capture both true and false with high probability

        repeatBlock(repetitions, () -> {
            Boolean value = instance.get();
            assertNotNull(value);
            values.add(value);
        });

        assertThat(values.size(), is(2));
    }

    @RepeatedTest(20)
    void testAlternatingBooleans() {
        var instance = BooleanGenerators.alternatingBooleans();
        AtomicReference<Boolean> previous = new AtomicReference<>(false);

        previous.set(instance.get());
        assertNotNull(previous.get());

        int repetitions = 20;
        repeatBlock(repetitions, () -> {
            Boolean current = instance.get();
            assertNotNull(current);
            assertThat(current, is(not(previous.get())));
            previous.set(current); // update for next iteration
        });
    }
}
