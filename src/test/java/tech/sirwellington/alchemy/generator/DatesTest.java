package tech.sirwellington.alchemy.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;

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

    // ────────────────────────────────────────────────────────────────────

    @Test
    void testIsNow_Date() {
        Date now = Dates.now();
        assertThat(Dates.isNow(now), is(true));

        Date notNow = Dates.daysAgo(1);
        assertThat(Dates.isNow(notNow), is(false));

        assertThrowsJ5(NullPointerException.class, () -> Dates.isNow(null));
    }

    @Test
    void testIsNow_Date_long() {
        // Negative tolerance → IllegalArgumentException
        assertThrowsJ5(IllegalArgumentException.class,
                       () -> Dates.isNow(new Date(), -1));

        // Null input → NPE
        assertThrowsJ5(NullPointerException.class, () -> Dates.isNow(null, 0));

        Date now = Dates.now();

        // Tolerance of 10s should include `now`
        assertThat(Dates.isNow(now, 10), is(true));

        // Zero tolerance: even a small delay makes it false
        try {
            Thread.sleep(1);
            assertThat(Dates.isNow(now, 0), is(false));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIsNow_Instant_long() {
        assertThrowsJ5(NullPointerException.class,
                       () -> Dates.isNow((Instant) null, 0));

        assertThrowsJ5(IllegalArgumentException.class,
                       () -> Dates.isNow(Instant.now(), one(negativeIntegers()).longValue()));

        Instant now = Instant.now();
        assertThat(Dates.isNow(now, 10), is(true));

        try {
            Thread.sleep(1);
            // 0 tolerance: `now` no longer matches
            assertThat(Dates.isNow(now, 0), is(false));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIsNow_ToleranceBoundaries() {
        // Verify tolerance window is symmetric ±tolerance seconds
        long offset = 1_500; // 1.5s off
        Date slightlyEarly = new Date(Instant.now().minusSeconds(2).toEpochMilli());
        Date slightlyLate   = new Date(Instant.now().plusSeconds(2).toEpochMilli());

        assertThat(Dates.isNow(slightlyEarly, 3), is(true)); // within ±3s
        assertThat(Dates.isNow(slightlyEarly, 1), is(false)); // outside ±1s

        assertThat(Dates.isNow(slightlyLate, 3), is(true));
        assertThat(Dates.isNow(slightlyLate, 1), is(false));
    }
}
