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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

/**
 * Tests for {@link AlchemyGenerator}.
 *
 * @author SirWellington
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlchemyGenerator should")
class AlchemyGeneratorTest {

    @Test
    void get_shouldReturnWrappedValue() {
        // Given
        Object expected = mock(Object.class);
        AlchemyGenerator<Object> generator = mock();
        when(generator.get()).thenReturn(expected);

        // When
        Object result = AlchemyGenerator.Get.one(generator);

        // Then
        assertEquals(expected, result);
        verify(generator).get();
    }

    @ParameterizedTest(name = "one should reject null input")
    @ValueSource(classes = {Void.class, String.class})
    void one_shouldRejectNull(Class<?> type) {
        assertThrowsExactly(
            IllegalArgumentException.class,
            () -> AlchemyGenerator.Get.one(null),
            "should reject null generator"
        );
    }
}
