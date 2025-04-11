package tech.sirwellington.alchemy.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static tech.sirwellington.alchemy.generator.Checks.checkNotEmpty;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphanumericStrings;

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
            url = new URL("https://google.com");
        } catch (MalformedURLException e) {
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

        String cleanProtocol = protocol.replace("://", "");
        try {
            new URL(cleanProtocol + "://");
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Unknown protocol: " + protocol);
        }

        return () -> {
            String url = MessageFormat.format(
                    "{0}://{1}.{2}",
                    cleanProtocol,
                    alphanumericStrings().get(),
                    PeopleGenerators.popularEmailDomains().get()
            );
            try {
                return new URL(url);
            } catch (MalformedURLException ex) {
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
        AlchemyGenerator<Integer> integers = integers(1, 1000);
        return () -> MessageFormat.format(
                "{0}.{1}.{2}.{3}",
                integers.get(),
                integers.get(),
                integers.get(),
                integers.get()
        );
    }
}
