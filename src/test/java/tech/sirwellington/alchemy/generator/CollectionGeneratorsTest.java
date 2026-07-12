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
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.positiveIntegers;
import static tech.sirwellington.alchemy.generator.StringGenerators.*;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 * Tests for {@link CollectionGenerators}.
 *
 * @author SirWellington
 */
@DisplayName("Collection Generators")
final class CollectionGeneratorsTest extends BaseGeneratorTest {

    @Mock
    private AlchemyGenerator<Object> mockGenerator;

    @DisplayName("cannot be instantiated")
    @Test
    void testCannotInstantiate() throws Exception {
        assertThrows(
            () -> CollectionGenerators.class.getDeclaredConstructor().newInstance()
        ).isInstanceOf(IllegalAccessException.class);
    }

    @DisplayName("listOf(AlchemyGenerator) produces non-empty list")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testListOf_AlchemyGenerator() {
        var expectedValue = new Object();
        when(mockGenerator.get()).thenReturn(expectedValue);

        var result = CollectionGenerators.listOf(mockGenerator);

        assertThat(result, notNullValue());
        assertThat(result.isEmpty(), is(false));

        result.forEach(item -> assertThat(item, is(equalTo(expectedValue))));
    }

    @DisplayName("listOf(AlchemyGenerator, int) produces list of specified size")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testListOf_AlchemyGenerator_int() {
        // Given
        AlchemyGenerator<String> valueGenerator = mock(
            AdditionalAnswers.delegatesTo(alphabeticStrings())
        );
        var size = one(integers(10, 100));
        // When
        var result = CollectionGenerators.listOf(valueGenerator, size);

        // Then
        assertThat(result, notNullValue());
        assertThat(result.size(), is(size));
        verify(valueGenerator, times(size)).get();
    }

    @DisplayName("mapOf(String, String) produces map with UUID keys")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testMapOfWithInt() {
        var expectedValue = "fixed-string-for-test";
        var valueGenerator = AlchemyGenerator.of(() -> expectedValue);
        var size = 10;

        var result = CollectionGenerators.mapOf(
            StringGenerators.UUIDS,
            valueGenerator,
            size
        );

        assertThat(result, notNullValue());
        assertThat(result.size(), is(size));

        for (var entry : result.entrySet()) {
            var uuid = UUID.fromString(entry.getKey());
            assertThat(uuid, notNullValue());
            assertThat(entry.getValue(), is(expectedValue));
        }
    }

    @DisplayName("mapOf(AlchemyGenerator, AlchemyGenerator) produces non-empty map")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testConvenienceMapOf() {
        // Given
        AlchemyGenerator<String> keyGen = mock(AdditionalAnswers.delegatesTo(uuids()));
        AlchemyGenerator<String> valueGen = mock(AdditionalAnswers.delegatesTo(strings()));

        // When
        var result = CollectionGenerators.mapOf(keyGen, valueGen);

        // Then
        assertThat(result, notNullValue());
        assertThat(result.isEmpty(), is(false));

        // Given
        var size = result.size();
        verify(keyGen, times(size)).get();
        verify(valueGen, times(size)).get();
    }

    @DisplayName("fromList(Iterable) returns generator selecting from list")
    @Test
    void testFromList() {
        // Given
        var list = new ArrayList<String>();
        var size = RANDOM.nextInt(1, 100);

        for (int i = 0; i < size; ++i) {
            var value = Integer.toHexString(RANDOM.nextInt());
            list.add(value);
        }

        // When
        var generator = CollectionGenerators.fromList(list);
        assertThat(generator, notNullValue());

        // Then
        repeatBlock(DEFAULT_ITERATIONS, () -> {
            var value = generator.get();
            assertThat(
                "Value '" + value + "' not in source list",
                list.contains(value), is(true)
            );
        });
    }

    @DisplayName("listOf handles edge cases")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testListOfEdgeCases() {
        // Given
        var badSize = -5;
        var generator = uuids();

        // When, Then
        assertThrows(() -> CollectionGenerators.listOf(generator, badSize))
            .isInstanceOf(IllegalArgumentException.class)
            .hasSomeMessage();

        // Then
        var result = CollectionGenerators.listOf(generator, 0);
        assertThat(result, notNullValue());
        assertThat(result, is(empty()));
    }

    @DisplayName("mapGeneratorOf returns generator from parameters")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testMapGeneratorOf() {
        // Given
        AlchemyGenerator<String> keys = mock(AdditionalAnswers.delegatesTo(uuids()));
        AlchemyGenerator<Integer> values = mock(AdditionalAnswers.delegatesTo(positiveIntegers()));
        var size = one(integers(10, 100));

        // When
        var generator = CollectionGenerators.mapGeneratorOf(keys, values, size);

        // Then
        assertThat(generator, notNullValue());
        var map = generator.get();
        for (var entry : map.entrySet()) {
            assertThat(UUID.fromString(entry.getKey()), notNullValue());
            assertThat(entry.getValue(), notNullValue());
        }
        verify(keys, times(size)).get();
        verify(values, times(size)).get();
    }

    @DisplayName("mapGeneratorOf - edge cases")
    @Test
    void testMapGenerator_EdgeCase() {
        assertThrows(
            () -> CollectionGenerators.mapGeneratorOf(
                null,
                strings(),
                10
            )
        ).isIllegalArgumentException();
        assertThrows(
            () -> CollectionGenerators.mapGeneratorOf(
                strings(),
                null,
                10
            )
        ).isIllegalArgumentException();
        assertThrows(
            () -> CollectionGenerators.mapGeneratorOf(
                strings(),
                strings(),
                -10
            )
        ).isIllegalArgumentException();

    }
}