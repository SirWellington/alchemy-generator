package tech.sirwellington.alchemy.generator;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.AlchemyResources.*;
import static tech.sirwellington.alchemy.generator.StringGenerators.stringsFromFixedList;

/**
 * Generators for Places and addresses.
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
     * <p/>
     * <blockquote>
     * <strong>Note:</strong>
     * There are no guarantees as the validity of the addresses generated
     * or whether they actually exist in the real world.
     * </blockquote>
     * <p/>
     * For example: {@code 145 N Longwood Blvd}
     */
    public static AlchemyGenerator<String> streetAddresses() {
        AlchemyGenerator<Boolean> booleans = BooleanGenerators.booleans();
        AlchemyGenerator<String> streetNames = places();
        AlchemyGenerator<Integer> streetNumbers = NumberGenerators.integers(1, 10_000);
        AlchemyGenerator<Integer> lineTwoNumbers = NumberGenerators.integers(1, 100);
        AlchemyGenerator<String> lineTwoDescs = stringsFromFixedList("Apt, Ste, Unit");
        AlchemyGenerator<String> directions = stringsFromFixedList("N", "S", "E", "W");
        AlchemyGenerator<String> endings = stringsFromFixedList("Blvd", "St", "Ave", "Pl", "Rd");

        return () -> {
            int streetNumber = one(streetNumbers);
            String direction = one(directions);
            String streetName = one(streetNames);
            String lineTwoDesc = one(lineTwoDescs);
            int lineTwoNumber = one(lineTwoNumbers);
            String streetEnding = one(endings);
            boolean useDirection = one(booleans);
            boolean useLineTwo = one(booleans);
            
            StringBuilder builder = new StringBuilder();
            
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
     * Return a full address, which includes a street address, city, and country.
     * <p/>
     * <blockquote>
     * <strong>Note:</strong>
     * There are no guarantees as the validity of the addresses generated
     * or whether they actually exist in the real world.
     * </blockquote>
     * <p/>
     * For example: {@code 4592 E 2 St New York, United States}.
     * @param isUSAddress Whether to return a U.S. based address which includes a [state][states].
     */
    public static AlchemyGenerator<String> fullAddresses(boolean isUSAddress) {
        AlchemyGenerator<String> streetAddresses = streetAddresses();
        AlchemyGenerator<String> cities = cities();
        AlchemyGenerator<String> states = states();
        AlchemyGenerator<String> countries = countries();
        
        return () -> {
            StringBuilder builder = new StringBuilder();
            builder
                .append(one(streetAddresses))
                .append(" ")
                .append(one(cities))
                .append(" ")
                .append(one(states))
                ;
            
            if (isUSAddress) {
                builder.append(" United States");
            } else {
                builder
                    .append(" ")
                    .append(one(countries))
                    ;
            }
            
            return builder.toString();
        };
    }

}
