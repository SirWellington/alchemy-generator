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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author SirWellington
 */
final class JavaCode {

    static class Computer {

        private String name;
        private String model;
        private int year;
        private String manufacturer;
        private double cost;
        private byte[] data;

        static void check(Computer computer) {
            assertThat(computer, notNullValue());
            assertThat(computer.name, is(not(isEmptyOrNullString())));
            assertThat(computer.model, not(isEmptyOrNullString()));
            assertThat(computer.manufacturer, not(isEmptyOrNullString()));
            assertThat(computer.year, greaterThan(0));
            assertThat(computer.cost, greaterThan(0.0));
            assertThat(computer.data, notNullValue());
            assertThat(computer.data.length, greaterThan(0));
        }
    }

    static class Person {

        public String name;
        public int age;
        private double money;
        private String middleName;
        private Computer computer;
        private URL website;


        static void check(Person person) {
            assertThat(person, notNullValue());
            assertThat(person.name, is(not(isEmptyOrNullString())));
            assertThat(person.age, greaterThan(0));
            assertThat(person.money, greaterThan(0.0));
            assertThat(person.middleName, not(isEmptyOrNullString()));
            assertThat(person.computer, notNullValue());
            assertThat(person.website, notNullValue());

            Computer.check(person.computer);
        }
    }

    static Matcher<String> isEmptyOrNullString() {
        return new EmptyOrNullString();
    }

    static Matcher<Number> greaterThan(int number) {
        return new GreaterThan(number);
    }

    static Matcher<Number> greaterThan(double number) {
        return new GreaterThan(number);
    }

    static class EmptyOrNullString extends BaseMatcher<String> {

        @Override
        public boolean matches(Object item) {
            if (item == null) {
                return false;
            }

            if (item instanceof String) {
                return ((String) item).isEmpty();
            }

            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Empty or null string");
        }
    }

    static class GreaterThan extends BaseMatcher<Number> {
        private final Number target;

        GreaterThan(Number target) {
            this.target = target;
        }

        @Override
        public boolean matches(Object item) {
            if (item instanceof Number) {
                return ((Number) item).doubleValue() > target.doubleValue();
            }
            else {
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Greater than or equal to");
        }
    }
}
