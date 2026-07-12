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
    void testDaysBeforeNow() {
        // Given
        var daysAgo = one(integers(1, 3650));
        var now = Instant.now();
        var expectedLeft = now.minus(Duration.ofDays(daysAgo));

        // When
        var result = Dates.daysBeforeNow(daysAgo);

        // Then
        assertThat(result.getTime(), lessThanOrEqualTo(now.toEpochMilli()));
        assertThat(result.getTime(), greaterThanOrEqualTo(expectedLeft.toEpochMilli()));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testDaysAfterNow() {
        var days = one(integers(1, 3000));
        var now = Instant.now();
        var expectedRight = now.plus(Duration.ofDays(days));

        var result = Dates.daysAfterNow(days);

        assertThat(result.getTime(), greaterThanOrEqualTo(now.toEpochMilli()));
        assertThat(result.getTime(), lessThanOrEqualTo(expectedRight.toEpochMilli()));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testHoursBeforeNow() {
        var hours = one(integers(1, 24 * 365)); // up to ~1 year
        var now = Instant.now();
        var expectedLeft = now.minus(Duration.ofHours(hours));

        var result = Dates.hoursBeforeNow(hours);

        assertThat(result.getTime(), lessThanOrEqualTo(now.toEpochMilli()));
        assertThat(result.getTime(), greaterThanOrEqualTo(expectedLeft.toEpochMilli()));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testHoursAfterNow() {
        // Given
        var hours = one(integers(1, 24 * 365));
        // When
        var result = Dates.hoursAfterNow(hours);
        var now = Instant.now();

        // Then
        var future = now.plus(Duration.ofHours(hours));

        assertThat(result.getTime(), greaterThanOrEqualTo(now.toEpochMilli()));
        assertThat(result.getTime(), lessThanOrEqualTo(future.toEpochMilli()));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testMinutesBeforeNow() {
        var minutes = one(integers(1, 60 * 24)); // up to ~1 day
        var now = Instant.now();
        var expectedLeft = now.minus(Duration.ofMinutes(minutes));

        var result = Dates.minutesBeforeNow(minutes);

        assertThat(result.getTime(), lessThanOrEqualTo(now.toEpochMilli()));
        assertThat(result.getTime(), greaterThanOrEqualTo(expectedLeft.toEpochMilli()));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testMinutesAfterNow() {
        // Given
        int minutes = one(integers(1, 60 * 24));
        // When
        var now = Instant.now();
        var result = Dates.minutesAfterNow(minutes);

        // Then
        var future = now.plus(Duration.ofMinutes(minutes));

        assertThat(result.getTime(), greaterThanOrEqualTo(now.toEpochMilli()));

        if (result.getTime() >= future.toEpochMilli()) {
            System.out.println(
                format("minutes: {0}, result: {1}, future: {2}", minutes, result, future)
            );
        }
        assertThat(result.getTime(), lessThanOrEqualTo(future.toEpochMilli()));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testIsNow_Date() {
        var now = Dates.now();
        assertThat(Dates.isNow(now), is(true));

        Date notNow = Dates.daysBeforeNow(1);
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
}
