package tech.sirwellington.alchemy.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.AlchemyResources.*;
import static tech.sirwellington.alchemy.generator.StringGenerators.stringsFromFixedList;

public class PlaceGeneratorsJ {
    private static final Logger LOG = LoggerFactory.getLogger(PlaceGeneratorsJ.class);

    private static final List<String> cities = readLinesFromResource("places/cities.txt");
    private static final List<String> country = readLinesFromResource("places/country.txt");
    private static final List<String> places = readLinesFromResource("places/places.txt");
    private static final List<String> states = readLinesFromResource("places/states.txt");
    private static final List<String> stateShortCodes = readLinesFromResource("places/states_short_codes.txt");

    private PlaceGeneratorsJ() throws IllegalAccessException {
        throw new IllegalAccessException("cannot directly instantiate");
    }

    public static AlchemyGenerator<String> countries() {
        return stringsFromFixedList(country);
    }

    public static AlchemyGenerator<String> states() {
        return stringsFromFixedList(states);
    }

    public static AlchemyGenerator<String> stateShortCodes() {
        return stringsFromFixedList(stateShortCodes);
    }

    public static AlchemyGenerator<String> cities() {
        return stringsFromFixedList(cities);
    }

    public static AlchemyGenerator<String> places() {
        return stringsFromFixedList(places);
    }

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
            String ending = one(endings);
            boolean useDirection = one(booleans);
            boolean useLineTwo = one(booleans);

            if (useLineTwo && useDirection) {
                // 100 W Charleston Blvd Ste 130
                return MessageFormat.format(
                        "{0} {1} {2} {3} {4} {5}",
                        streetNumber,
                        direction,
                        streetName,

                );
            }
        };
    }

}
