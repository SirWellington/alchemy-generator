package tech.sirwellington.alchemy.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import static java.time.temporal.ChronoUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.*;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 * Tests for {@link Dates}.
 *
 * @author SirWellington
 */
@DisplayName("Dates Utilities")
class DatesTest extends BaseGeneratorTest {

    @Test
    void testNow() {
        // Given
        Date result = Dates.now();
        Date after = new Date();

        // Then
        assertThat(result, notNullValue());
        assertThat(result.getTime(), lessThanOrEqualTo(after.getTime()));
        assertThat(System.currentTimeMillis() - result.getTime(), lessThan(100L));
    }

    @Test
    void testDaysAgo() {
        // Given
        int daysAgo = one(integers(1, 3650));
        long nowMillis = Instant.now().toEpochMilli();
        long expectedLeft = nowMillis - Duration.ofDays(daysAgo).toMillis();

        // When
        Date result = Dates.daysAgo(daysAgo);

        // Then
        assertThat(result.getTime(), lessThanOrEqualTo(nowMillis));
        assertThat(result.getTime(), greaterThanOrEqualTo(expectedLeft));
    }

    @Test
    void testDaysAhead() {
        int days = one(integers(1, 3650));
        long nowMillis = Instant.now().toEpochMilli();
        long expectedRight = nowMillis + (long) days * DAYS.getDuration().toMillis();

        Date result = Dates.daysAhead(days);

        assertThat(result.getTime(), greaterThanOrEqualTo(nowMillis));
        assertThat(result.getTime(), lessThanOrEqualTo(expectedRight));
    }

    @Test
    void testHoursAgo() {
        int hours = one(integers(1, 24 * 365)); // up to ~1 year
        long nowMillis = Instant.now().toEpochMilli();
        long expectedLeft = nowMillis - (long) hours * HOURS.getDuration().toMillis();

        Date result = Dates.hoursAgo(hours);

        assertThat(result.getTime(), greaterThanOrEqualTo(expectedLeft));
        assertThat(result.getTime(), lessThanOrEqualTo(nowMillis));
    }

    @Test
    void testHoursAhead() {
        int hours = one(integers(1, 24 * 365));
        long nowMillis = Instant.now().toEpochMilli();
        long expectedRight = nowMillis + (long) hours * HOURS.getDuration().toMillis();

        Date result = Dates.hoursAhead(hours);

        assertThat(result.getTime(), greaterThanOrEqualTo(nowMillis));
        assertThat(result.getTime(), lessThanOrEqualTo(expectedRight));
    }

    @Test
    void testMinutesAgo() {
        int minutes = one(integers(1, 60 * 24)); // up to ~1 day
        long nowMillis = Instant.now().toEpochMilli();
        long expectedLeft = nowMillis - (long) minutes * MINUTES.getDuration().toMillis();

        Date result = Dates.minutesAgo(minutes);

        assertThat(result.getTime(), greaterThanOrEqualTo(expectedLeft));
        assertThat(result.getTime(), lessThanOrEqualTo(nowMillis));
    }

    @Test
    void testMinutesAhead() {
        int minutes = one(integers(1, 60 * 24));
        long nowMillis = Instant.now().toEpochMilli();
        long expectedRight = nowMillis + (long) minutes * MINUTES.getDuration().toMillis();

        Date result = Dates.minutesAhead(minutes);

        assertThat(result.getTime(), greaterThanOrEqualTo(nowMillis));
        assertThat(result.getTime(), lessThanOrEqualTo(expectedRight));
    }

    @Test
    void testIsNow_Date() {
        Date now = Dates.now();
        assertThat(Dates.isNow(now), is(true));

        Date notNow = Dates.daysAgo(1);
        assertThat(Dates.isNow(notNow), is(false));

        assertThrows(IllegalArgumentException.class, () -> Dates.isNow(null));
    }

    @Test
    void testIsNow_Date_long() throws Exception {
        assertThrows(() -> Dates.isNow(new Date(), -1))
            .isInstanceOf(IllegalArgumentException.class);

        assertThrows(() -> Dates.isNow((Date) null, 0L))
            .isInstanceOf(IllegalArgumentException.class);

        Date now = Dates.now();

        assertThat(Dates.isNow(now, 10), is(true));

        Thread.sleep(1);
        assertThat(Dates.isNow(now, 0), is(false));
    }

    @Test
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
        var now = new Date();
        var calendar = Calendar.getInstance();
        var expectedYear = calendar.get(Calendar.YEAR);

        // When
        var result = Dates.currentYear();

        // Then
        assertThat(result, equalTo(expectedYear));
    }
}
