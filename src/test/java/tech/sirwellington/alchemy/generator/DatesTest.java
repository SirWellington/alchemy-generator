package tech.sirwellington.alchemy.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import static java.text.MessageFormat.format;
import static java.time.temporal.ChronoUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.negativeLongs;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 * Tests for {@link Dates}.
 *
 * @author SirWellington
 */
@DisplayName("Dates Utilities")
final class DatesTest extends BaseGeneratorTest {

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testNow() {
        // Given
        var result = Dates.now();
        var after = new Date();

        // Then
        assertThat(result, notNullValue());
        assertThat(result.getTime(), lessThanOrEqualTo(after.getTime()));
        assertThat(System.currentTimeMillis() - result.getTime(), lessThan(100L));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testIsNow_Date() {
        var now = Dates.now();
        assertThat(Dates.isNow(now), is(true));

        var yesterday = Instant.now().minus(Duration.ofDays(1));
        var notNow = new Date(yesterday.toEpochMilli());
        assertThat(Dates.isNow(notNow), is(false));

        assertThrows(IllegalArgumentException.class, () -> Dates.isNow(null));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testIsNow_Date_long() throws Exception {
        assertThrows(() -> Dates.isNow(new Date(), -1))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> Dates.isNow((Date) null, 0L))
            .isInstanceOf(IllegalArgumentException.class);

        var now = Dates.now();

        assertThat(Dates.isNow(now, 10), is(true));

        Thread.sleep(1);
        assertThat(Dates.isNow(now, 0), is(false));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testIsNow_Instant_long() throws Exception {
        assertThrows(() -> Dates.isNow((Instant) null, 0))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> {
            var now = Instant.now();
            var negativeNumber = one(negativeLongs());
            Dates.isNow(now, negativeNumber);
        }).isInstanceOf(IllegalArgumentException.class);

        var now = Instant.now();
        assertThat(Dates.isNow(now, 10), is(true));

        Thread.sleep(1);
        assertThat(Dates.isNow(now, 0), is(false));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testIsNow_ToleranceBoundaries() {
        var slightlyEarly = new Date(Instant.now().minusSeconds(2).toEpochMilli());
        var slightlyLate   = new Date(Instant.now().plusSeconds(2).toEpochMilli());
        var oneSecondDuration = Duration.ofSeconds(1).toMillis();
        var fourSecondsDuration = Duration.ofSeconds(4).toMillis();

        assertThat(Dates.isNow(slightlyEarly, fourSecondsDuration), is(true)); // within ±4s
        assertThat(Dates.isNow(slightlyEarly, oneSecondDuration), is(false)); // outside ±1s

        assertThat(Dates.isNow(slightlyLate, fourSecondsDuration), is(true));
        assertThat(Dates.isNow(slightlyLate, oneSecondDuration), is(false));
    }

    @RepeatedTest(20)
    void testCurrentYear() {
        // Given
        var calendar = Calendar.getInstance();
        var expectedYear = calendar.get(Calendar.YEAR);

        // When
        var result = Dates.currentYear();

        // Then
        assertThat(result, equalTo(expectedYear));
    }

    @RepeatedTest(20)
    void testFromInstant() {
        // Given
        var daysAhead = one(integers(0, 365));
        var instant = Instant.now().plus(Duration.ofDays(daysAhead));

        // When
        var date = Dates.from(instant);

        // Then
        assertThat(date.getTime(), equalTo(instant.toEpochMilli()));
    }
}
