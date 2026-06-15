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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 * Tests for {@link CollectionGenerators}.
 *
 * @author SirWellington
 */
@DisplayName("Collection Generators")
class CollectionGeneratorsTest extends BaseGeneratorTest {

    private int iterations;

    @Mock
    private AlchemyGenerator<Object> mockGenerator;

    @BeforeEach
    void setUp() {
        iterations = RANDOM.nextInt(500, 5000);
    }

    @Test
    @DisplayName("cannot be instantiated")
    void testCannotInstantiate() throws Exception {
        var constructor = CollectionGenerators.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(IllegalAccessException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("listOf(AlchemyGenerator) produces non-empty list")
    void testListOf_AlchemyGenerator() {
        var expectedValue = new Object();
        when(mockGenerator.get()).thenReturn(expectedValue);

        List<?> result = CollectionGenerators.listOf(mockGenerator);

        assertThat(result, notNullValue());
        assertThat(result.isEmpty(), is(false));

        result.forEach(item -> assertThat(item, is(equalTo(expectedValue))));
    }

    @Test
    @DisplayName("listOf(AlchemyGenerator, int) produces list of specified size")
    void testListOf_AlchemyGenerator_int() {
        var expectedValue = new Object();
        int size = 50;
        when(mockGenerator.get()).thenReturn(expectedValue);

        List<?> result = CollectionGenerators.listOf(mockGenerator, size);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(size));

        result.forEach(item -> assertThat(item, is(equalTo(expectedValue))));
    }

    @Test
    @DisplayName("mapOf(String, String) produces map with UUID keys")
    void testMapOfWithInt() {
        String expectedValue = "fixed-string-for-test";
        AlchemyGenerator<String> valueGenerator = () -> expectedValue;
        int size = 10;

        Map<String, String> result = CollectionGenerators.mapOf(
            StringGenerators.UUIDS,
            valueGenerator,
            size
        );

        assertThat(result, notNullValue());
        assertThat(result.size(), is(size));

        for (Map.Entry<String, String> entry : result.entrySet()) {
            var uuid = UUID.fromString(entry.getKey());
            assertThat(uuid, notNullValue());
            assertThat(entry.getValue(), is(expectedValue));
        }
    }

    @Test
    @DisplayName("mapOf(AlchemyGenerator, AlchemyGenerator) produces non-empty map")
    void testConvenienceMapOf() {
        // Given
        AlchemyGenerator<String> keyGen = mock();
        AlchemyGenerator<String> valueGen = mock();

        when(keyGen.get()).thenReturn(UUID.randomUUID().toString());
        when(valueGen.get()).thenReturn(String.valueOf(RANDOM.nextDouble()));

        // When
        Map<String, String> result = CollectionGenerators.mapOf(keyGen, valueGen);

        // Then
        assertThat(result, notNullValue());
        assertThat(result.isEmpty(), is(false));

        int callsToKeys = mockingDetails(keyGen).getInvocations().size();
        int callsToValues = mockingDetails(valueGen).getInvocations().size();

        // TODO: Check if equal works over >=
        assertThat(callsToKeys, equalTo(result.size()));
        assertThat(callsToValues, equalTo(result.size()));
    }

    @Test
    @DisplayName("fromList(Iterable) returns generator selecting from list")
    void testFromList() {
        // Given
        List<String> list = new ArrayList<>();
        int size = RANDOM.nextInt(1, 100);

        for (int i = 0; i < size; ++i) {
            var value = Integer.toHexString(RANDOM.nextInt());
            list.add(value);
        }

        // When
        AlchemyGenerator<String> generator = CollectionGenerators.fromList(list);
        assertThat(generator, notNullValue());

        // Then
        for (int i = 0; i < iterations; ++i) {
            String value = generator.get();
            assertThat(
                "Value '" + value + "' not in source list",
                list.contains(value), is(true)
            );
        }
    }

    @Test
    @DisplayName("listOf handles edge cases")
    void testListOfEdgeCases() {
        // Given
        int badSize = -5;
        var generator = StringGenerators.uuids();

        // When, Then
        assertThrows(() -> CollectionGenerators.listOf(generator, badSize))
            .isInstanceOf(IllegalArgumentException.class)
            .hasSomeMessage();

        // Then
        List<String> result = CollectionGenerators.listOf(generator, 0);
        assertThat(result, notNullValue());
        assertThat(result, is(empty()));
    }
}