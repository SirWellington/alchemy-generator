package tech.sirwellington.alchemy.generator;

import org.junit.jupiter.api.RepeatedTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tech.sirwellington.alchemy.generator.NetworkGenerators.*;
import static tech.sirwellington.alchemy.generator.StringGenerators.stringsFromFixedList;

class NetworkGeneratorsTest extends BaseGeneratorTest {

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testHttpUrls() {
        var generator = httpURLs();
        assertThat(generator, notNullValue());
        var url = generator.get();
        assertThat(url, notNullValue());
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testHttpsUrls() {
        // Given
        var generator = httpsURLs();
        assertThat(generator, notNullValue());

        // When
        var url = generator.get();

        // Then
        assertThat(url, notNullValue());
        assertThat(url.getProtocol(), startsWith("https"));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testUrlsWithProtocol() {
        var scheme = stringsFromFixedList("http", "https", "file", "ftp").get();
        var generator = urlsWithProtocol(scheme);
        assertThat(generator, notNullValue());

        var url = generator.get();
        assertThat(url, notNullValue());
        assertThat(url.getProtocol(), startsWith(scheme));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testPorts() {
        // Given
        var generator = ports();
        assertThat(generator, notNullValue());

        // When
        int port = generator.get();
        assertThat(port, greaterThanOrEqualTo(22));
        assertThat(port, lessThan(Short.MAX_VALUE & 0xFFFF));
    }

    @RepeatedTest(DEFAULT_ITERATIONS)
    void testIp4Addresses() {
        // Given
        var generator = ipv4Addresses();
        assertThat(generator, notNullValue());
        String max = "999.999.999.999";
        String min = "1.1.1.1";

        // When
        String address = generator.get();

        // Then
        assertThat(countOccurrencesOfCharInString(address, '.'), is(3));
        assertThat(address.length(), greaterThanOrEqualTo(min.length()));
        assertThat(address.length(), lessThanOrEqualTo(max.length()));
    }

    private static int countOccurrencesOfCharInString(String string, char character) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == character) count++;
        }
        return count;
    }
}
