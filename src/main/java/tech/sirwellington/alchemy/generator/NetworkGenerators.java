package tech.sirwellington.alchemy.generator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.Checks.checkNotEmpty;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.PeopleGenerators.popularEmailDomains;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphanumericStrings;

/**
 * {@summary Generators for Network-data, such as an IP Addresses.}
 *
 * @author SirWellington
 */
@NonInstantiable
public final class NetworkGenerators {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkGenerators.class);
    private static final URL FALLBACK_URL;
    private static final List<String> VALID_PROTOCOLS = Arrays.asList(
        "http",
        "https",
        "ftp",
        "file",
        "ssh"
    );

    static {
        URL url;
        try {
            url = new URI("https://google.com").toURL();
        } catch (URISyntaxException | MalformedURLException _) {
            url = null;
        }
        FALLBACK_URL = url;
    }

    private NetworkGenerators() throws IllegalAccessException {
        throw new IllegalAccessException("cannot directly instantiate");
    }

    /**
     * @return {@link URL URLs} beginning with {@code http://}.
     */
    public static AlchemyGenerator<URL> httpURLs() {
        return urlsWithProtocol("http");
    }

    /**
     * @return {@link URL URLs} beginning with {@code https://}.
     */
    public static AlchemyGenerator<URL> httpsURLs() {
        return urlsWithProtocol("https");
    }

    /**
     * Creates valid URLs starting with the provided protocol.
     *
     * @param protocol The protocol to use for the URLs created. Do not include the "://".
     *                 Must be one of the common types, {@code http, https, ftp, ssh, file}.
     * @return {@link URL URLs} beginning with the {@code protocol}.
     */
    public static AlchemyGenerator<URL> urlsWithProtocol(@NonEmpty String protocol) {
        checkNotEmpty(protocol, "missing protocol");
        checkThat(
            VALID_PROTOCOLS.contains(protocol),
            MessageFormat.format("{0} is not a valid protocol [{1}]", protocol, VALID_PROTOCOLS)
        );

        var cleanProtocol = protocol.replace("://", "");
        try {
            new URI(cleanProtocol + "://example.com");
        } catch (URISyntaxException  ex) {
            throw new IllegalArgumentException("Unknown protocol: " + protocol);
        }

        return () -> {
            var hostLength = one(integers(3, 40));
            var host = alphanumericStrings(hostLength);
            var domain = one(popularEmailDomains());
            var url = MessageFormat.format("{0}://{1}.{2}", cleanProtocol, host, domain);

            try {
                return new URI(url).toURL();
            } catch (URISyntaxException | MalformedURLException ex) {
                LOG.error("Failed to create a url from scheme {}", cleanProtocol, ex);
                return FALLBACK_URL;
            }
        };
    }

    /**
     * @return Ports from 22 to {@link Short#MAX_VALUE 32767}.
     */
    public static AlchemyGenerator<Integer> ports() {
        return integers(22, Short.MAX_VALUE);
    }

    /**
     * Generates an IPv4 Address in the form {@code xxx.xxx.xxx.xxx}
     */
    public static AlchemyGenerator<String> ipv4Addresses() {
        var integers = integers(1, 1000);
        return () -> MessageFormat.format(
            "{0}.{1}.{2}.{3}",
            integers.get(),
            integers.get(),
            integers.get(),
            integers.get()
        );
    }
}
