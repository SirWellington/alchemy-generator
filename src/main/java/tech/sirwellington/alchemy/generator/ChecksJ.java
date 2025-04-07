/*
 * Copyright 2025 Wellington Moreno<jwellington.moreno@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package tech.sirwellington.alchemy.generator;

import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Used internally to perform argument checks.
 * @author SirWellington
 */
class ChecksJ  {
    
    static void checkNotNull(Object any) throws IllegalArgumentException {
        checkNotNull(any, "Expected non-null object.");
    }
    
    static void checkNotNull(Object any, String message) throws IllegalArgumentException {
        if (any == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    static void checkThat(boolean predicate) throws IllegalArgumentException {
        checkThat(predicate, "");
    }
    
    static void checkThat(boolean predicate, String message) throws IllegalArgumentException {
        if (!predicate) {
            throw new IllegalArgumentException(message);
        }
    }
    
    static void checkThat(BooleanSupplier predicate, String message) throws IllegalArgumentException {
        if (predicate == null) {
            return;
        }
        if (!predicate.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }
    
    static void checkNotEmpty(String string) throws IllegalArgumentException {
        checkNotEmpty(string, "Expected non-empty String.");
    }
    
    static void checkNotEmpty(String string, String message) throws IllegalArgumentException {
        checkThat(string != null, message);
        checkThat(!string.isEmpty(), message);
    }
    
    static <T> void checkNotEmpty(List<T> list) throws IllegalArgumentException {
        checkNotEmpty(list, "Expected non-empty list");
    }
    
    static <T> void checkNotEmpty(List<T> list, String message) throws IllegalArgumentException {
        checkNotNull(list, message);
        checkThat(!list.isEmpty(), message);
    }
}
