package tech.sirwellington.alchemy.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.longs;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;


/**
 * Tests for {@link DateGenerators}.
 *
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Date Generators")
class DateGeneratorsTest extends BaseGeneratorTest {
    
    @Test
    void testCannotInstantiate() {
        // Given
        var constructors = DateGenerators.class.getDeclaredConstructors();
        assertThat(constructors.length, is(1));
        var constructor = constructors[0];
        constructor.setAccessible(true);

        // Then
        assertThrows(() -> constructor.newInstance())
            .isInstanceOf(IllegalAccessError.class);
    }

    @Test
    void testPresentDates() {
        // Given
        var instance = DateGenerators.presentDates();

        // When
        repeatTest(() -> {
            var date = instance.get();
            assertThat(date, notNullValue());
            assertThat(isNow(date), is(true));
        });
    }

    @Test
    void testPastDates() {
        var instance = DateGenerators.pastDates();

        repeatTest(() -> {
            var date = instance.get();
            assertThat(date, notNullValue());
            assertThat(date.before(Dates.now()), is(true));
        });
    }

    @Test
    void testFutureDates() {
        var instance = DateGenerators.futureDates();

        repeatTest(() -> {
            var date = instance.get();
            assertThat(date, notNullValue());
            assertThat(date.after(Dates.now()), is(true));
        });
    }

    @Test
    void testAnyTime() {
        repeatTest(() -> {
            var generator = DateGenerators.anyTime();
            assertThat(generator, notNullValue());
            assertThat(generator.get(), notNullValue());
        });
    }

    @Test
    void testBefore() {
        var ref = Dates.now();

        var instance = DateGenerators.before(ref);
        assertThat(instance, notNullValue());

        repeatTest(() -> {
            var date = instance.get();
            assertThat(date, notNullValue());
            assertThat(date.before(ref), is(true));
        });

        assertThrows(() -> DateGenerators.before(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testAfter() {
        var ref = Dates.now();

        var instance = DateGenerators.after(ref);
        assertThat(instance, notNullValue());

        repeatTest(() -> {
            var date = instance.get();
            assertThat(date, notNullValue());
            assertThat(date.after(ref), is(true));
        });

        assertThrows(() -> DateGenerators.after(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testToDate() {
        var now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AlchemyGenerator<Instant> generator = () -> now;

        var instance = DateGenerators.toDate(generator);
        assertThat(instance, notNullValue());

        repeatTest(() -> {
            var instant = instance.get();
            assertThat(instant, notNullValue());
            assertThat(
                instant.toInstant().truncatedTo(ChronoUnit.MILLIS),
                is(now)
            );
        });

        assertThrows(
            IllegalArgumentException.class,
            () -> DateGenerators.toDate(null)
        );

        AlchemyGenerator<Instant> nullSupplier = () -> null;
        assertThrows(IllegalStateException.class, () -> DateGenerators.toDate(nullSupplier));
    }

    @Test
    void testDatesBetween() {
        var startDate = Dates.daysAgo(4);
        var endDate = Dates.daysAhead(5);

        assertThrows(
            IllegalArgumentException.class,
            () -> DateGenerators.datesBetween(null, endDate)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> DateGenerators.datesBetween(startDate, null)
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> DateGenerators.datesBetween(endDate, startDate)
        ).hasMessageContaining("before");

        repeatTest(() -> {
            long begin = one(longs(1, Long.MAX_VALUE / 2));
            long end = one(longs(begin + 1, Long.MAX_VALUE));

            startDate.setTime(begin);
            endDate.setTime(end);

            var generator = DateGenerators.datesBetween(startDate, endDate);
            assertThat(generator, notNullValue());

            var date = generator.get();
            assertThat(date.getTime(), greaterThanOrEqualTo(startDate.getTime()));
            assertThat(date.getTime(), lessThan(endDate.getTime()));
        });
    }

    @Test
    void testAsSqlDateGenerator() {
        var date = one(DateGenerators.anyTime());
        var generator = (AlchemyGenerator<Date>) () -> date;
        var resultGen = DateGenerators.toSqlDateGenerator(generator);

        java.sql.Date sqlDate = resultGen.get();
        java.sql.Date expected = new java.sql.Date(date.getTime());

        assertThat(sqlDate, is(expected));
    }

    @Test
    void testAsLocalDateGenerator() {
        var date = one(DateGenerators.anyTime());
        var gen = (AlchemyGenerator<java.util.Date>) () -> date;
        var resultGen = DateGenerators.toLocalDateGenerator(gen);

        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        assertThat(resultGen.get(), is(sqlDate.toLocalDate()));
    }

    @Test
    void testAsSqlTimestampGenerator() {
        var date = one(DateGenerators.anyTime());
        AlchemyGenerator<Date> generator = () -> date;

        var resultGen = DateGenerators.toSqlTimestampGenerator(generator);
        java.sql.Timestamp sqlTimestamp = resultGen.get();

        assertThat(sqlTimestamp, is(new java.sql.Timestamp(date.getTime())));
    }

    // ────────────────────────────────────────────────────────────────
    // Utilities
    // ────────────────────────────────────────────────────────────────

    private boolean isNow(Date date) {
        return isNow(date, 5);
    }

    /**
     * Returns true if {@code date} is within ±tolerance seconds of current time.
     */
    private boolean isNow(Date date, int toleranceSeconds) {
        long now = System.currentTimeMillis();
        long diff = Math.abs(date.getTime() - now);
        return diff <= (long) toleranceSeconds * 1_000;
    }
}