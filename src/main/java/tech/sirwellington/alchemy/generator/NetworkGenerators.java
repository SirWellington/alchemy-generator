/*
 * Copyright 2015 Aroma Tech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package tech.sirwellington.alchemy.generator;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;

import static java.lang.String.format;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.Checks.checkNotEmpty;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.PeopleGenerators.popularEmailDomains;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphanumericString;

/**
 *
 * @author SirWellington
 */
@NonInstantiable
public final class NetworkGenerators 
{
    private final static Logger LOG = LoggerFactory.getLogger(NetworkGenerators.class);
    
    private static final URL FALLBACK_URL;
    static
    {
        try
        {
            FALLBACK_URL = new URL("http://google.com");
        }
        catch (MalformedURLException ex)
        {
            throw new RuntimeException("Could not generate URL", ex);
        }
    }

    private static final List<String> VALID_PROTOCOLS = Arrays.asList("http", "https", "ftp", "file", "ssh");

    private NetworkGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
    }
    
    /**
     * @return URLs beginning with {@code http://}
     */
    public static AlchemyGenerator<URL> httpUrls()
    {
        return urlsWithProtocol("http");
    }
    
    /**
     * 
     * @return URLs beginning with {@code https://}
     */
    public static AlchemyGenerator<URL> httpsUrls()
    {
        return urlsWithProtocol("https");
    }
    
    /**
     * 
     * @param protocol The protocol to use for the URLs created. Do not include the "://".
     * @return  URLs beginning with the {@code protocol}
     */
    public static AlchemyGenerator<URL> urlsWithProtocol(@NonEmpty String protocol)
    {
        checkNotEmpty(protocol, "missing protocol");
        String cleanProtocol = protocol.replace("://", "");
        
        try
        {
            new URL(cleanProtocol + "://");
        }
        catch (MalformedURLException ex)
        {
            throw new IllegalArgumentException("Unknown protocol: " + protocol, ex);
        }
        
        return () ->
        {
            String url = format("%s://%s%s", cleanProtocol, one(alphanumericString()), one(popularEmailDomains()));
            
            try
            {
                return new URL(url);
            }
            catch (MalformedURLException ex)
            {
                LOG.error("Failed to create url from scheme {}", cleanProtocol, ex);
                return FALLBACK_URL;
            }
        };
    }
    
    /**
     * @return Ports from 22 to {@linkplain Short#MAX_VALUE 32767}.
     */
    public static AlchemyGenerator<Integer> ports()
    {
        return integers(22, Short.MAX_VALUE);
    }

    /**
     * Generates IPV4 addresses in the form {@code xxx.xx.xxx.xx}.
     * @return An IPV4 address
     */
    public static AlchemyGenerator<String> ip4Addresses()
    {
        AlchemyGenerator<Integer> integers = integers(10, 1000);
        
        return () ->
        {
            return String.format("%d.%d.%d.%d", 
                                 integers.get(),
                                 integers.get(),
                                 integers.get(),
                                 integers.get());
        };
    }
}
