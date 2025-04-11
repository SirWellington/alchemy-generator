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


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;
import tech.sirwellington.alchemy.annotations.arguments.Positive;
import tech.sirwellington.alchemy.annotations.arguments.Required;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.BinaryGenerators.binary;
import static tech.sirwellington.alchemy.generator.Checks.*;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;

/**
 * {@link AlchemyGenerator} for {@link String Strings}.
 * @author SirWellington
 */
public final class StringGenerators {

    private static final RandomStringUtils RANDOM = RandomStringUtils.secure();

    private StringGenerators() throws IllegalAccessException {
        throw new IllegalAccessException("cannot instatiate directly");
    }


    //==============================================================================================
    //BASIC STRINGS
    //==============================================================================================

    /**
     * Generates a random string of a random length. Characters can include ASCII, Unicode, or
     * International Characters.
     */
    static AlchemyGenerator<String> strings() {
        return () -> {
            int size = one(integers(5, 1000));
            return RANDOM.next(size);
        };
    }

    /**
     * Generates a random string of specified length. Characters are included from all sets.
     * @param length The length of the String, must be at least 1.
     */
    static AlchemyGenerator<String> strings(int length) {
        checkThat(length > 0, "Length must be at least 1");
        return () -> RANDOM.next(length);
    }

    /**
     * Generates a random hexadecimal string.
     * @param length The length of the String, must be at least 1.
     */
    static AlchemyGenerator<String> hexadecimalString(@Positive int length) {
        checkThat(length > 0, "Length must be at least 1");

        HexBinaryAdapter hexBinaryAdapter = new HexBinaryAdapter();
        AlchemyGenerator<byte[]> binaryGenerator = binary(length);

        return () -> {
            byte[] binary = one(binaryGenerator);
            String hex = hexBinaryAdapter.marshal(binary);
            return StringUtils.left(hex, length);
        };
    }

    /**
     * Generates a random alphabetic string anywhere between `10 - 100` characters. Well suited for the case when
     * you don't really care for the size of the string returned.
     * @see #alphabeticStrings(int)
     */
    static AlchemyGenerator<String> alphabeticStrings() {
        int length = one(integers(10, 100));
        return alphabeticStrings(length);
    }

    /**
     * Generates a random alphabetic string.
     * @param length The length of the String, must be at least 1.
     * @throws IllegalArgumentException If `length < 0`
     * @see #alphabeticStrings()
     */
    static AlchemyGenerator<String> alphabeticStrings(@Positive int length) {
        checkThat(length > 0, "length must be > 0");
        return () -> RANDOM.nextAlphabetic(length);
    }

    /**
     * Generates a random alphanumeric string anywhere between `10 - 100` characters. Well suited for the case
     * when you don't really care what the size of the string returned.
     * @see #alphanumericStrings(int)
     */
    static AlchemyGenerator<String> alphanumericStrings() {
        int length = one(integers(10, 100));
        return alphanumericStrings(length);
    }

    /**
     * Generates a random alphanumeric string of the specified length.
     * @param length The length of the Generated Strings.
     * @throws IllegalArgumentException If `length < 0`
     * @see #alphanumericStrings()
     */
    static AlchemyGenerator<String> alphanumericStrings(@Positive int length) {
        checkThat(length > 0, "length must be > 0");
        return () -> RANDOM.nextAlphanumeric(length);
    }

    /**
     * Creates a numeric integer-based String. The sizes of the Strings will vary across instances.
     * Each resulting string will be directly [parsable into an Integer][Integer.parseInt].
     * @see #numericStrings(int)
     */
    static AlchemyGenerator<String> numericStrings() {
        int length = one(integers(4, 25));
        return numericStrings(length);
    }

    /**
     * Creates a numeric integer-based String.
     * For Example:
     * <pre>
     * String result = numericStrings(5).get();
     * //49613
     </pre> *
     *
     * @param length Size of the numeric strings generated.
     * @throws IllegalArgumentException If {@code length <= 0}.
     * @see #numericStrings()
     */
    static AlchemyGenerator<String> numericStrings(@Positive int length) {
        checkThat(length > 0, "length must be > 0");
        return () -> RANDOM.nextNumeric(length);

    }

    //==============================================================================================
    //UUIDs
    //==============================================================================================

    /**
     * Generates random [UUIDs][UUID].
     */
    static final AlchemyGenerator<String> UUIDS = () -> UUID.randomUUID().toString();

    /**
     * Just returns {@link StringGenerators#UUIDS}. This exists for convenience.
     */
    static AlchemyGenerator<String> uuids() {
        return UUIDS;
    }

    //==============================================================================================
    //From Fixed targets
    //==============================================================================================

    /**
     * Generates a string value from the specified set.
     * @param values Must be non-empty, produces the values for the generator.
     * @see #stringsFromFixedList(String...)
     */
    static AlchemyGenerator<String> stringsFromFixedList(
        @NonEmpty List<String> values
    ) {
        checkNotEmpty(values, "Values list empty");
        return () -> {
            int index = one(integers(0, values.size()));
            return values.get(index);
        };
    }

    /**
     * Generates a string value from the specified set.
     * @param args Must be non-empty, produces the values for the generator.
     * @see #stringsFromFixedList(List)
     */
    static AlchemyGenerator<String> stringsFromFixedList(String... args) {
        checkNotNull(args);
        List<String> values = Arrays.asList(args);
        checkNotEmpty(values, "no values specified");
        return stringsFromFixedList(values);
    }

    /**
     * Takes an existing {@link AlchemyGenerator Generator} and transforms its values to a
     * String using the {@link Object#toString()} method.
     * @param <T> Underlying type.
     * @param generator The underlying Alchemy Generator to convert values for.
     * @throws IllegalArgumentException If the Generator is null.
    */
    static <T> AlchemyGenerator<String> toString(
        @Required AlchemyGenerator<T> generator
    ) {
        checkNotNull(generator, "generator missing");

        return () -> {
            T value = generator.get();
            return value == null ? "" : value.toString();
        };
    }
}
