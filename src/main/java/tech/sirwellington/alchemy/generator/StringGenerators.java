/*
 * Copyright 2015 SirWellington Tech.
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.arguments.NonNull;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.BinaryGenerators.binary;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;

/**
 *
 * @author SirWellington
 */
public final class StringGenerators
{

    private final static Logger LOG = LoggerFactory.getLogger(StringGenerators.class);

    private StringGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate this class");
    }

    /**
     * Generates a random string of specified length. Characters are included from all sets.
     *
     * @param length The length of the String, must be at least 1.
     *
     * @return
     */
    public static AlchemyGenerator<String> strings(int length)
    {
        checkThat(length > 0, "Length must be at least 1");
        return () -> RandomStringUtils.random(length);
    }

    /**
     * Generates a random hexadecimal string.
     *
     * @param length The length of the String, must be at least 1.
     *
     * @return
     */
    public static AlchemyGenerator<String> hexadecimalString(int length)
    {
        checkThat(length > 0, "Length must be at least 1");
        HexBinaryAdapter hexBinaryAdapter = new HexBinaryAdapter();
        AlchemyGenerator<byte[]> binaryGenerator = binary(length);

        return () ->
        {
            byte[] binary = one(binaryGenerator);
            String hex = hexBinaryAdapter.marshal(binary);
            return StringUtils.left(hex, length);
        };
    }

    /**
     * Generates a random alphabetic string.
     *
     * @param length The length of the String, must be at least 1.
     *
     * @return
     * @see #alphabeticString()
     */
    public static AlchemyGenerator<String> alphabeticString(int length)
    {
        checkThat(length > 0, "Length must be at least 1");
        return () -> RandomStringUtils.randomAlphabetic(length);
    }

    /**
     * Generates a random alphabetic string anywhere between {@code 5 - 20} characters. Well suited
     * for the case when you don't really care for the size of the string returned.
     *
     * @return
     * @see #alphabeticString(int)
     */
    public static AlchemyGenerator<String> alphabeticString()
    {
        return alphabeticString(one(integers(5, 20)));
    }

    //==============================================================================================
    //UUIDs
    //==============================================================================================
    /**
     * Generates random {@linkplain UUID UUIDs}.
     */
    public static AlchemyGenerator<String> uuids = () -> UUID.randomUUID().toString();

    /**
     * Just returns {@link #uuids}. This exists for consistency.
     *
     * @return
     */
    public static AlchemyGenerator<String> uuids()
    {
        return uuids;
    }

    //==============================================================================================
    //From Fixed targets
    //==============================================================================================
    /**
     * Generates a string value from the specified set.
     *
     * @param values
     *
     * @return
     */
    public static AlchemyGenerator<String> stringsFromFixedList(List<String> values)
    {
        checkNotNull(values);
        checkThat(!values.isEmpty(), "No values specified");
        return () ->
        {
            int index = integers(0, values.size()).get();
            return values.get(index);
        };
    }

    /**
     * Generates a string value from the specified set.
     *
     * @param values
     *
     * @return
     */
    public static AlchemyGenerator<String> stringsFromFixedList(String... values)
    {
        checkNotNull(values);
        checkThat(values.length != 0, "No values specified");
        return stringsFromFixedList(Arrays.asList(values));
    }

    /**
     * Takes an existing {@linkplain AlchemyGenerator Generator} and transforms its values to a
     * String using the {@link Object#toString() } method.
     *
     * @param <T>
     * @param generator The backing Alchemy Generator.
     *
     * @return
     *
     * @throws IllegalArgumentException If the Generator is null.
     */
    public static <T> AlchemyGenerator<String> asString(@NonNull AlchemyGenerator<T> generator) throws IllegalArgumentException
    {
        checkNotNull(generator, "generator missing");
        return () ->
        {
            T value = generator.get();
            return value != null ? value.toString() : "";
        };
    }

}
