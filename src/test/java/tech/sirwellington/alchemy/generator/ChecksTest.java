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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.Checks.*;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphabeticStrings;

/**
 * Tests for {@link Checks}.
 *
 * @author SirWellington
 */
@DisplayName("Checks should")
class ChecksTest {

    private static final String MESSAGE = "some message";

    @Test
    void testCheckNotNull() {
        Object obj = new Object();
        checkNotNull(obj);
        checkNotNull(obj, MESSAGE);
    }

    @Test
    void testCheckNotNullExpecting() {
        assertThrows(IllegalArgumentException.class, () -> checkNotNull(null));
    }

    @Test
    void testCheckNotNullExpectingWithMessage() {
        assertThrows(
            IllegalArgumentException.class,
            () -> checkNotNull(null, MESSAGE),
            "message should be included"
        );
    }

    @Test
    void testCheckThat() {
        checkThat(true);
        checkThat(true, MESSAGE);
    }

    @Test
    void testCheckThatExpecting() {
        assertThrows(
            IllegalArgumentException.class,
            () -> checkThat(false)
        );
    }

    @Test
    void testCheckThatExpectingWithMessage() {
        assertThrows(
            IllegalArgumentException.class,
            () -> checkThat(false, MESSAGE),
            "message should be included"
        );
    }

    @Test
    void testCheckNotEmptyString() {
        String valid = alphabeticStrings().get();
        checkNotEmpty(valid);

        // null → throws
        assertThrows(
            IllegalArgumentException.class,
            () -> checkNotEmpty((String) null)
        );

        // empty → throws
        assertThrows(IllegalArgumentException.class, () -> checkNotEmpty(""));
    }

    @Test
    void testCheckNotEmptyStringWithMessage() {
        String msg = "test message";
        String valid = one(alphabeticStrings());

        // Valid cases
        checkNotEmpty(valid, msg);
        checkNotEmpty(valid, null);     // uses default message
        checkNotEmpty(valid, "");       // empty string allowed as message

        // Invalid cases
        assertThrows(
            IllegalArgumentException.class,
            () -> checkNotEmpty((String) null, msg)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> checkNotEmpty("", msg)
        );
    }
}