package tech.sirwellington.alchemy.generator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.AlchemyResources.*;
import static tech.sirwellington.alchemy.generator.StringGenerators.stringsFromFixedList;

/**
 * {@summary Generators for Places and addresses. }
 * {@snippet :
 * var state = PlaceGenerators.states().get();
 * var country = PlaceGenerators.countries().get();
 * }
 * @author Wellington Moreno
 */
public class PlaceGenerators {
    private static final Logger LOG = LoggerFactory.getLogger(PlaceGenerators.class);

    private static final List<String> cities = readLinesFromResource("places/cities.txt");
    private static final List<String> countries = readLinesFromResource("places/countries.txt");
    private static final List<String> places = readLinesFromResource("places/places.txt");
    private static final List<String> states = readLinesFromResource("places/states.txt");
    private static final List<String> stateShortCodes = readLinesFromResource("places/states_short_codes.txt");

    private PlaceGenerators() throws IllegalAccessException {
        throw new IllegalAccessException("cannot directly instantiate");
    }

    /**
     * @return A list of valid countries.
     */
    public static AlchemyGenerator<String> countries() {
        return stringsFromFixedList(countries);
    }

    /**
     * @return A list of valid U.S. States.
     */
    public static AlchemyGenerator<String> states() {
        return stringsFromFixedList(states);
    }

    /**
     * @return A list of short codes for U.S. States.
     */
    public static AlchemyGenerator<String> stateShortCodes() {
        return stringsFromFixedList(stateShortCodes);
    }

    /**
     * @return A list of valid U.S. Cities.
     */
    public static AlchemyGenerator<String> cities() {
        return stringsFromFixedList(cities);
    }

    /**
     * @return Names used for various places.
     */
    public static AlchemyGenerator<String> places() {
        return stringsFromFixedList(places);
    }

    /**
     * Returns a street address.
     *
     * <blockquote>
     * <strong>Note:</strong>
     * There are no guarantees as the validity of the addresses generated
     * or whether they actually exist in the real world.
     * </blockquote>
     * <br>
     * For example: {@code 145 N Longwood Blvd}
     */
    public static AlchemyGenerator<String> streetAddresses() {
        var booleans = BooleanGenerators.booleans();
        var streetNames = places();
        var streetNumbers = NumberGenerators.integers(1, 10_000);
        var lineTwoNumbers = NumberGenerators.integers(1, 100);
        var lineTwoDescs = stringsFromFixedList("Apt, Ste, Unit");
        var directions = stringsFromFixedList("N", "S", "E", "W");
        var endings = stringsFromFixedList("Blvd", "St", "Ave", "Pl", "Rd");

        return () -> {
            var streetNumber = one(streetNumbers);
            var direction = one(directions);
            var streetName = one(streetNames);
            var lineTwoDesc = one(lineTwoDescs);
            var lineTwoNumber = one(lineTwoNumbers);
            var streetEnding = one(endings);
            var useDirection = one(booleans);
            var useLineTwo = one(booleans);
            var builder = new StringBuilder();

            builder.append(streetNumber).append(" ");
            if (useDirection) {
                builder.append(direction).append(" ");
            }

            builder.append(streetName)
                   .append(" ")
                   .append(streetEnding);

            if (useLineTwo) {
                builder.append(" ")
                       .append(lineTwoDesc)
                       .append(" ")
                       .append(lineTwoNumber);
            }

            return builder.toString();
        };
    }

    /**
     * Return a full U.S., which includes:
     * 1. Street address line one,
     * 2. Street address line two (sometimes)
     * 3. City
     * 4. State
     * 5. Country
     *
     * <blockquote>
     * <strong>Note:</strong>
     * There are no guarantees as the validity of the addresses generated
     * or whether they actually exist in the real world.
     * </blockquote>
     * <br>
     * For example: {@code 4592 E 2 St New York, United States}.
     */
    public static AlchemyGenerator<String> fullAddresses() {
        return fullAddresses(true);
    }

    /**
     * Return a full U.S., which includes:
     * 1. Street address line one,
     * 2. Street address line two (sometimes)
     * 3. City
     * 4. State
     * 5. Country
     *
     * <blockquote>
     * <strong>Note:</strong>
     * There are no guarantees as the validity of the addresses generated
     * or whether they actually exist in the real world.
     * </blockquote>
     * <br>
     * For example: {@code 4592 E 2 St New York, United States}.
     *
     * @param isUSAddress Whether to return a U.S. based address which includes a [state][states].
     */
    public static AlchemyGenerator<String> fullAddresses(boolean isUSAddress) {
        var streetAddresses = streetAddresses();
        var cities = cities();
        var states = states();
        var countries = countries();

        return () -> {
            var builder = new StringBuilder();
            builder
                .append(one(streetAddresses))
                .append(" ")
                .append(one(cities))
                .append(" ")
                .append(one(states));

            if (isUSAddress) {
                builder.append(" United States");
            }
            else {
                builder
                    .append(" ")
                    .append(one(countries));
            }

            return builder.toString();
        };
    }

}
