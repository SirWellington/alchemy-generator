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

import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;

class ObjectGeneratorsTest extends BaseGeneratorTest {

    @SuppressWarnings("unused")
    private static final String STATIC_FIELD = one(StringGenerators.strings());

    @DisplayName("testWithSimplePojo")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testWithSimplePojo() {
        var generator = ObjectGenerators.pojos(Computer.class);
        assertThat(generator, notNullValue());

        var computer = generator.get();
        checkComputer(computer);
    }

    @DisplayName("testWithNestedPojo")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testWithNestedPojo() {
        var generator = ObjectGenerators.pojos(Person.class);
        assertThat(generator, notNullValue());

        var result = generator.get();
        checkPerson(result);
    }

    @DisplayName("testWithCollectionInPojo")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testWithCollectionInPojo() {
        var generator = ObjectGenerators.pojos(Building.class);
        assertThat(generator, notNullValue());

        var result = generator.get();
        checkBuilding(result);
    }

    @DisplayName("testPojosWithMap")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testPojosWithMap() {
        var generator = ObjectGenerators.pojos(AddressBook.class);
        assertThat(generator, notNullValue());

        var result = generator.get();
        checkAddressBook(result);
    }

    @DisplayName("testComplexPojo")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testComplexPojo() {
        var generator = ObjectGenerators.pojos(CityBlock.class);
        assertThat(generator, notNullValue());

        var result = generator.get();
        checkCityBlock(result);
    }

    @DisplayName("testPojosHandlesPrimitives")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testPojosHandlesPrimitives() {
        var primitives = new Class[]{
            Integer.class,
            Float.class,
            Double.class,
            Long.class,
            String.class,
            java.util.Date.class,
            java.sql.Date.class,
            Timestamp.class,
            Instant.class,
            Character.class,
            Short.class
        };

        for (Class<?> p : primitives) {
            var generator = ObjectGenerators.pojos(p);
            assertThat(generator, notNullValue());

            var value = generator.get();
            assertThat(value, notNullValue());
        }
    }

    @DisplayName("testPojosHandlesNonDefaultConstructor")
    @Test
    void testPojosHandlesNonDefaultConstructor() {
        class ExampleNonDefaultConstructor {
            private final String argument;

            public ExampleNonDefaultConstructor(String arg) {
                this.argument = arg;
            }

            @Override
            public String toString() {
                return "Example{" + argument + "}";
            }
        }

        var generator = ObjectGenerators.pojos(
            ExampleNonDefaultConstructor.class
        );

        var instance = generator.get();

        assertThat(instance, notNullValue());
        assertThat(instance.argument, not(isEmptyOrNullString()));
    }

    @DisplayName("testWithDataClass")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testWithDataClass() {
        var generator = ObjectGenerators.pojos(Band.class);
        var result = generator.get();
        checkBand(result);
    }

    @DisplayName("testWithAnotherDataClass")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testWithAnotherDataClass() {
        record Holder(
            String string,
            int number,
            List<String> strings
        ) { }

        var generator = ObjectGenerators.pojos(Holder.class);
        var result = generator.get();

        assertThat(result, notNullValue());
    }

    @DisplayName("testWithADataClassThatContainsAMap")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testWithADataClassThatContainsAMap() {
        record Holder(
            String string,
            int number,
            Map<String, Computer> computers
        ) { }

        var generator = ObjectGenerators.pojos(Holder.class);
        var result = generator.get();
        assertThat(result, notNullValue());
    }

    @DisplayName("testWithADataClassThatContainsListOfEnumValues")
    @RepeatedTest(DEFAULT_ITERATIONS)
    void testWithADataClassThatContainsListOfEnumValues() {
        var generator = ObjectGenerators.pojos(Restaurant.class);
        var result = generator.get();
        assertThat(result, notNullValue());
        assertThat(result.name(), not(isEmptyOrNullString()));
        assertThat(result.type(), notNullValue());
        assertThat(result.tags(), notNullValue());
        assertThat(result.tags().size(), greaterThan(0));
        for (Restaurant.Tag tag : result.tags()) {
            assertNotNull(tag);
        }
    }

    // This test is complex; skipping with comment instead of @Ignore
    @DisplayName("testWithADataClassThatContainsAMapOfAList — complex, skipped")
    @Test
    @Disabled("Complex object cannot be auto-reflected.")
    void testWithADataClassThatContainsAMapOfAList() {
        class Holder {
            String string;
            int number;
            Map<String, List<Computer>> map;

            @Override
            public String toString() {
                return "Holder{" + string + "," + number + "}";
            }
        }

        AlchemyGenerator<Holder> generator = ObjectGenerators.pojos(Holder.class);
        Holder result = generator.get();
        assertThat(result, notNullValue());
    }

    // -----------------------
    // Helper Checks
    // -----------------------

    private void checkPerson(Person person) {
        assertThat(person, notNullValue());

        assertThat(person.name, not(isEmptyOrNullString()));
        assertThat(person.middleName, not(isEmptyOrNullString()));

        assertThat(person.age, greaterThan(0));
        assertThat(person.money, greaterThan(0.0));
        assertThat(person.heightCm, greaterThan(0f));
        assertThat(person.weightKg, greaterThan(0f));

        assertThat(person.birthdayDate, notNullValue());
        assertThat(person.birthdayTimestamp, notNullValue());

        assertThat(person.website, notNullValue());
        String urlStr = person.website.toString();
        assertThat(urlStr, startsWith("http"));

        // Static field should match class-level constant
        assertThat(Person.STATIC_FIELD, is(STATIC_FIELD));

        checkComputer(person.computer);
    }

    private void checkComputer(Computer computer) {
        assertThat(computer, notNullValue());

        assertThat(computer.name, not(isEmptyOrNullString()));
        assertThat(computer.model, not(isEmptyOrNullString()));
        assertThat(computer.manufacturer, not(isEmptyOrNullString()));

        assertThat(computer.year, greaterThan(0));
        assertThat(computer.cost, greaterThan(0.0));
        assertThat(computer.weightKg, greaterThan(0f));

        assertNotNull(computer.data);
        assertThat(computer.data.length, greaterThan(0));

        assertNotNull(computer.purchaseTime);
        assertNotNull(computer.purchaseInstant);
    }

    private void checkBuilding(Building building) {
        assertThat(building, notNullValue());
        assertThat(building.address, not(isEmptyOrNullString()));
        assertThat(building.age, greaterThan(0));
        assertThat(building.floors, greaterThan(0));

        assertNotNull(building.people);
        assertThat(building.people.size(), greaterThan(0));

        for (Person p : building.people) {
            checkPerson(p);
        }
    }

    private void checkAddressBook(AddressBook addressBook) {
        assertThat(addressBook, notNullValue());
        assertNotNull(addressBook.directory);
        assertThat(addressBook.directory.size(), greaterThan(0));

        for (Map.Entry<String, Building> entry : addressBook.directory.entrySet()) {
            String key = entry.getKey();
            assertThat(key, not(isEmptyOrNullString()));

            Building value = entry.getValue();
            checkBuilding(value);
        }
    }

    private void checkCityBlock(CityBlock cityBlock) {
        assertThat(cityBlock, notNullValue());

        assertThat(cityBlock.name, not(isEmptyOrNullString()));
        assertThat(cityBlock.distance, greaterThan(0));
        assertNotNull(cityBlock.state);

        assertNotNull(cityBlock.memory);
        assertThat(cityBlock.memory.limit(), greaterThan(0));

        assertNotNull(cityBlock.isNearOcean);
        assertNotNull(cityBlock.code);

        // Collections
        assertNotNull(cityBlock.homes);
        assertThat(cityBlock.homes.size(), greaterThan(0));
        assertNotNull(cityBlock.stores);
        assertThat(cityBlock.stores.size(), greaterThan(0));
        assertNotNull(cityBlock.internetUsers);
        assertThat(cityBlock.internetUsers.size(), greaterThan(0));

        for (Building home : cityBlock.homes)
            checkBuilding(home);
        for (Building store : cityBlock.stores)
            checkBuilding(store);

        // Map keys → Person
        for (Person key : cityBlock.internetUsers.keySet())
            checkPerson(key);

        // Map values → Computer
        for (Computer value : cityBlock.internetUsers.values())
            checkComputer(value);
    }

    private void checkBand(Band band) {
        assertThat(band, notNullValue());

        assertThat(band.name(), not(isEmptyOrNullString()));
        assertThat(band.fans(), greaterThan(0));
        assertThat(band.money(), greaterThan(0.0));
        assertThat(band.price(), greaterThan(0f));
        assertThat(band.city(), not(isEmptyOrNullString()));

        byte[] data = band.data();
        assertNotNull(data);
        assertThat(data.length, greaterThan(0));  // .isEmpty() is Kotlin-only

        assertThat(band.dateFormed(), notNullValue());
        assertThat(band.timeFormed(), notNullValue());
        assertThat(band.nextTourDate(), notNullValue());

        Computer workstation = band.workstation();
        assertNotNull(workstation);
        checkComputer(workstation);
    }

    // -----------------------
    // Data Classes (Records and Manual Data classes)
    // -----------------------
    private static class Computer {
        String name;
        String model;
        int year = 0;
        String manufacturer;
        double cost = 0.0;
        byte[] data = new byte[0];
        float weightKg = 0f;
        ZonedDateTime purchaseTime;
        Instant purchaseInstant;

        @Override
        public String toString() {
            return "Computer{" +
                "name='" + name + '\'' +
                ", model='" + model + '\'' +
                '}';
        }
    }

    private record Person(
        String name,
        int age,
        double money,
        float heightCm,
        float weightKg,
        String middleName,
        Computer computer,
        URL website,
        Date birthdayDate,
        Timestamp birthdayTimestamp
    ) {
        static final String STATIC_FIELD = ObjectGeneratorsTest.STATIC_FIELD;
    }

    private static class Building {
        List<Person> people = new ArrayList<>();
        String address;
        int age = 0;
        int floors = 0;
    }

    private static class AddressBook {
        Map<String, Building> directory = new HashMap<>();
    }

    enum State {
        CA, NY, NJ, NV
    }

    private static class CityBlock {
        String name;
        int distance = 0;
        List<Building> homes = new ArrayList<>();
        List<Building> stores = new ArrayList<>();
        Map<Person, Computer> internetUsers = new HashMap<>();
        State state;
        ByteBuffer memory;
        Boolean isNearOcean;
        Byte code;

        @Override
        public String toString() {
            return "CityBlock{" +
                "name='" + name + '\'' +
                ", distance=" + distance +
                '}';
        }
    }

    record Band(
        String name,
        int fans,
        double money,
        float price,
        String city,
        byte[] data,
        java.util.Date dateFormed,
        Timestamp timeFormed,
        LocalDate nextTourDate,
        boolean onTour,
        Computer workstation
    ) { }

    record Restaurant(
        String name,
        VenueType type,
        List<Tag> tags
    ) {
        enum VenueType {
            RESTAURANT, CAFE, SIT_DOWN, TAKEOUT
        }

        record Tag(String label) {
            public static final Tag FANCY = new Tag("FANCY");
            public static final Tag CHEAP = new Tag("CHEAP");
            public static final Tag CASUAL = new Tag("CASUAL");
            public static final Tag ROMANTIC = new Tag("ROMANTIC");
        }
    }

}
