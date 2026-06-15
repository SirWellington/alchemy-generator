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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Object expected = mock(Object.class);
        AlchemyGenerator<Object> generator = mock();
        when(generator.get()).thenReturn(expected);

        // When
        Object result = one(generator);

        // Then
        assertEquals(expected, result);
        verify(generator).get();
    }

    @Test
    void testOne_shouldProduceValue() {
        repeatTest(() -> {
            var string = StringGenerators.alphanumericStrings().get();
            AlchemyGenerator<String> generator =  () -> string;

            var result = one(generator);
            assertThat(result, equalTo(string));
        });
    }

    @ParameterizedTest(name = "one should reject null input")
    @ValueSource(classes = {Void.class, String.class})
    void testOne_shouldRejectNull(Class<?> type) {
        assertThrowsExactly(
            IllegalArgumentException.class,
            () -> one(null),
            "should reject null generator"
        );
    }
}
