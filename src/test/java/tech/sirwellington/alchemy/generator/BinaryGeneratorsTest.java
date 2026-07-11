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

import java.nio.ByteBuffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.negativeIntegers;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 * Tests for {@link BinaryGenerators}.
 *
 * @author SirWellington
 */
@DisplayName("BinaryGenerators should")
class BinaryGeneratorsTest extends BaseGeneratorTest {

    @Test
    void testCannotInstantiate() throws Exception {
        assertThrows(
            () -> BinaryGenerators.class.getDeclaredConstructor().newInstance()
        ).isInstanceOf(IllegalAccessException.class);
    }

    @Test
    void testBinary() {
        int bytes = integers(50, 5000).get();
        var instance = BinaryGenerators.binary(bytes);

        assertNotNull(instance);
        repeatBlock(3, () -> {
            var value = instance.get();
            assertThat(value, notNullValue());
            assertEquals(bytes, value.length);
        });
    }

    @DisplayName("BinaryGenerators.binary() should reject non positive number")
    @Test
    void testBinaryEdgeCases() {
        var instance = BinaryGenerators.binary(0);
        assertNotNull(instance);

        var result = instance.get();
        assertNotNull(result);
        assertEquals(0, result.length);

        int length = one(negativeIntegers());
        assertThrows(
            () -> BinaryGenerators.binary(length),
            "should reject negative size"
        );
    }

    @Test
    void testByteBuffers() {
        int size = one(integers(10, 1000));
        var instance = BinaryGenerators.byteBuffers(size);
        assertNotNull(instance);

        repeatBlock(
            3, () -> {
                ByteBuffer result = instance.get();
                assertThat(result, notNullValue());
                assertEquals(size, result.limit());
                assertEquals(size, result.array().length);
            }
        );
    }

    @Test
    void testByteBuffersEdgeCases() {
        int size = one(negativeIntegers());
        assertThrows(
            () -> BinaryGenerators.byteBuffers(size)
        ).isInstanceOf(IllegalArgumentException.class);

        ByteBuffer result = BinaryGenerators.byteBuffers(0).get();
        assertNotNull(result);
        assertEquals(0, result.limit());
    }

    @Test
    void testBytes() {
        var generator = BinaryGenerators.bytes();
        assertNotNull(generator);

        repeatBlock(
            5, () -> {
                var result = generator.get();
                assertThat(result, notNullValue());
            }
        );
    }

}
