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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.StringGenerators.strings;

/**
 * Tests for {@link AlchemyGenerator}.
 *
 * @author SirWellington
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlchemyGenerator should")
class AlchemyGeneratorTest extends BaseGeneratorTest {

    @Test
    void testGet_shouldReturnWrappedValue() {
        // Given
        var expected = mock(Object.class);
        AlchemyGenerator<Object> generator = mock();
        when(generator.get()).thenReturn(expected);

        // When
        var result = one(generator);

        // Then
        assertEquals(expected, result);
        verify(generator).get();
    }

    @Test
    void testOne_shouldProduceValue() {
        repeatBlock(() -> {
            var string = StringGenerators.alphanumericStrings().get();
            AlchemyGenerator<String> generator = () -> string;

            var result = one(generator);
            assertThat(result, equalTo(string));
        });
    }

    @Test
    @DisplayName("one should reject null input")
    void testOne_shouldRejectNull() {
        assertThrowsExactly(
            IllegalArgumentException.class,
            () -> one(null),
            "should reject null generator"
        );
    }

    @Test
    void testOfSupplier() {
        // Given
        Supplier<String> supplier = mock();
        when(supplier.get()).thenReturn(one(strings()));
        var generator = AlchemyGenerator.of(supplier);
        var i = new AtomicInteger();

        repeatBlock(DEFAULT_ITERATIONS, () -> {
            // When
            var string = generator.get();
            // Then
            assertThat(string, not(emptyOrNullString()));
            verify(supplier, times(i.incrementAndGet())).get();
        });
    }

    @RepeatedTest(2)
    void testMappingModifiesResult() {
        repeatBlock(DEFAULT_ITERATIONS, () -> {
            // Given
            var number = one(integers(1, 100_000));
            AlchemyGenerator<Integer> generator = () -> number;

            // When
            var mapped = generator.mapping(Object::toString);

            // Then
            var result = mapped.get();
            assertThat(result, equalTo(number.toString()));
        });
    }
}
