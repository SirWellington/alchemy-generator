package tech.sirwellington.alchemy.generator;

import org.junit.jupiter.api.RepeatedTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tech.sirwellington.alchemy.generator.GeolocationGenerators.latitudes;
import static tech.sirwellington.alchemy.generator.GeolocationGenerators.longitudes;

class GeolocationGeneratorsTest extends BaseGeneratorTest {

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testLatitudes() {
        AlchemyGenerator<Double> generator = latitudes();
        assertThat(generator, notNullValue());

        for (int i = 0; i < 100; i++) {
            Double latitude = generator.get();
            assertThat(latitude, greaterThanOrEqualTo(-90.0));
            assertThat(latitude, lessThanOrEqualTo(90.0));
        }
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testLongitudes() {
        AlchemyGenerator<Double> generator = longitudes();
        assertThat(generator, notNullValue());

        for (int i = 0; i < 100; i++) {
            Double longitude = generator.get();
            assertThat(longitude, greaterThanOrEqualTo(-180.0));
            assertThat(longitude, lessThanOrEqualTo(180.0));
        }
    }
}
