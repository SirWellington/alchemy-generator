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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class PlaceGeneratorsTest extends BaseGeneratorTest {

    @DisplayName("testCities")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testCities() {
        var generator = PlaceGenerators.cities();
        assertThat(generator, notNullValue());

        var city = generator.get();
        assertThat(city, not(isEmptyOrNullString()));
        assertThat(city.length(), greaterThanOrEqualTo(2));
    }

    @DisplayName("testStates")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testStates() {
        var generator = PlaceGenerators.states();
        assertThat(generator, notNullValue());

        var state = generator.get();
        assertThat(state, not(isEmptyOrNullString()));
        assertThat(state.length(), greaterThanOrEqualTo(2));
    }

    @DisplayName("testStatesShortCodes")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testStatesShortCodes() {
        var generator = PlaceGenerators.stateShortCodes();
        assertThat(generator, notNullValue());

        var stateShortCode = generator.get();
        assertThat(stateShortCode, not(isEmptyOrNullString()));
        assertThat(stateShortCode.length(), equalTo(2));
    }

    @DisplayName("testCountries")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testCountries() {
        var generator = PlaceGenerators.countries();
        assertThat(generator, notNullValue());

        var country = generator.get();
        assertThat(country, not(isEmptyOrNullString()));
        assertThat(country.length(), greaterThanOrEqualTo(2));
    }

    @DisplayName("testStreetAddresses")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testStreetAddresses() {
        var generator = PlaceGenerators.streetAddresses();
        assertThat(generator, notNullValue());

        var address = generator.get();
        assertThat(address, not(isEmptyOrNullString()));
        assertThat(address.length(), greaterThanOrEqualTo(5));
    }

    @DisplayName("testFullAddresses")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testFullAddresses() {
        var generator = PlaceGenerators.fullAddresses();
        assertThat(generator, notNullValue());

        var address = generator.get();
        assertThat(address, not(isEmptyOrNullString()));
        assertThat(address.length(), greaterThanOrEqualTo(5));
    }
}
