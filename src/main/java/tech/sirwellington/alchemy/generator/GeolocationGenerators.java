package tech.sirwellington.alchemy.generator;

import tech.sirwellington.alchemy.annotations.access.NonInstantiable;

/**
 * Generators for creating Geo-Coordinates, aka longitudes and latitudes.
 */
@NonInstantiable
public final class GeolocationGenerators {

    /**
     * Object representing a latitude and longitude coordinate.
     */
    public record Coordinate(double latitude, double longitude) {
        public Coordinate {
            Checks.checkThat(latitude >= -90.0, "latitude out of bounds");
            Checks.checkThat(latitude <= 90.0, "latitude out of bounds");
            Checks.checkThat(longitude >= -180, "longitude out of bounds");
            Checks.checkThat(longitude <= 180, "longitude out of bounds");
        }
    }

    private GeolocationGenerators() throws IllegalAccessException {
        throw new IllegalAccessException("cannot directly instantiate");
    }

    /**
     * Creates valid latitudes from -90 up to and excluding 90 degrees.
     */
    public static AlchemyGenerator<Double> latitudes() {
        return NumberGenerators.doubles(-90.0, 90);
    }

    /**
     * Creates valid longitudes from -180 up to and excluding 180 degrees.
     */
    public static AlchemyGenerator<Double> longitudes() {
        return NumberGenerators.doubles(-180.0, 180.0);
    }

    /**
     * Generates valid {@link Coordinate Coordinates}.
     */
    public static AlchemyGenerator<Coordinate> coordinates() {
        var latitudes = latitudes();
        var longitudes = longitudes();
        return () -> new Coordinate(latitudes.get(), longitudes.get());
    }
}
