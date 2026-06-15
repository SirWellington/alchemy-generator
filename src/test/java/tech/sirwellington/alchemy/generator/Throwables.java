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

import net.bytebuddy.implementation.bytecode.Throw;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Objects;

/**
 * Utility for asserting exception throwing behavior.
 *
 * <p>Usage:
 * {@code
 * Throwables.assertThrows(() -> riskyOperation())
 *     .isInstanceOf(IllegalArgumentException.class);
 * }
 *
 * @author SirWellington
 */
interface ThrowingRunnable {
    void run() throws Exception;
}

final class Throwables {

    private Throwables() {
        throw new AssertionError("cannot instantiate utility class");
    }

    static Assertion assertThrows(
        Class<? extends Throwable> exceptionType,
        ThrowingRunnable runnable
    ) {
        return  assertThrows(runnable)
            .isInstanceOf(exceptionType);
    }

    static Assertion assertThrows(ThrowingRunnable operation) {
        return assertThrows(
            operation,
            "Expected an exception to be thrown, but none was"
        );
    }

    static Assertion assertThrows(ThrowingRunnable operation, String message) {
        Objects.requireNonNull(operation, "operation must not be null");

        Throwable ex = null;
        try {
            operation.run();
        } catch (Throwable t) {
            ex = t;
        }

        if (ex == null) {
            fail(message);
        }

        return new Assertion(ex);
    }

    /**
     * Fluent assertion for the caught exception.
     */
    static final class Assertion {
        private final Throwable ex;

        private Assertion(Throwable ex) {
            this.ex = Objects.requireNonNull(ex, "exception must not be null");
        }

        Assertion isInstanceOf(Class<? extends Throwable> expectedType) {
            Objects.requireNonNull(expectedType, "expectedType must not be null");
            assertThat(ex, instanceOf(expectedType));
            return this;
        }

        Assertion hasSomeMessage() {
            assertThat(
                "exception message empty",
                ex.getMessage(),
                is(not(Matchers.isEmptyOrNullString()))
            );
            return this;
        }

        Assertion hasMessage(String expectedMessage) {
            if (!Objects.equals(ex.getMessage(), expectedMessage)) {
                assertThat(
                    "exception message",
                    ex.getMessage(),
                    is(expectedMessage)
                );
            }
            return this;
        }

        Assertion hasMessageContaining(String substring) {
            String msg = ex.getMessage();
            if (msg == null || !msg.contains(substring)) {
                throw new AssertionError(
                    "Expected exception message to contain '" + substring +
                        "', but was: " + msg);
            }
            return this;
        }
    }
}