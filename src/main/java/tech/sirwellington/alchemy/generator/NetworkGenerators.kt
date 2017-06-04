/*
 * Copyright 2017 SirWellington Tech.
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


package tech.sirwellington.alchemy.generator


import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.integers
import tech.sirwellington.alchemy.generator.PeopleGenerators.Companion.popularEmailDomains
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.alphanumericString
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**

 * @author SirWellington
 */
@NonInstantiable
class NetworkGenerators
@Throws(IllegalAccessException::class)
private constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate")
    }

    companion object
    {
        private val LOG = LoggerFactory.getLogger(NetworkGenerators::class.java)

        private val FALLBACK_URL: URL

        init
        {
            try
            {
                FALLBACK_URL = URL("http://google.com")
            }
            catch (ex: MalformedURLException)
            {
                throw RuntimeException("Could not generate URL", ex)
            }

        }

        private val VALID_PROTOCOLS = Arrays.asList("http", "https", "ftp", "file", "ssh")

        /**
         * @return URLs beginning with `http://`
         */
        @JvmStatic
        fun httpUrls(): AlchemyGenerator<URL>
        {
            return urlsWithProtocol("http")
        }

        /**
         *
         * @return URLs beginning with `https://`
         */
        @JvmStatic
        fun httpsUrls(): AlchemyGenerator<URL>
        {
            return urlsWithProtocol("https")
        }

        /**
         *
         * @param protocol The protocol to use for the URLs created. Do not include the "://".
         *
         * @return  URLs beginning with the `protocol`
         */
        @JvmStatic
        fun urlsWithProtocol(@NonEmpty protocol: String): AlchemyGenerator<URL>
        {
            checkNotEmpty(protocol, "missing protocol")
            val cleanProtocol = protocol.replace("://", "")

            try
            {
                URL(cleanProtocol + "://")
            }
            catch (ex: MalformedURLException)
            {
                throw IllegalArgumentException("Unknown protocol: " + protocol, ex)
            }

            return AlchemyGenerator {

                val url = "$cleanProtocol://${alphanumericString().get()}.${popularEmailDomains().get()}"

                try
                {
                    URL(url)
                }
                catch (ex: MalformedURLException)
                {
                    LOG.error("Failed to create url from scheme {}", cleanProtocol, ex)
                    FALLBACK_URL
                }
            }
        }

        /**
         * @return Ports from 22 to [32767][Short.MAX_VALUE].
         */
        @JvmStatic
        fun ports(): AlchemyGenerator<Int>
        {
            return integers(22, Short.MAX_VALUE.toInt())
        }

        /**
         * Generates IPV4 addresses in the form `xxx.xx.xxx.xx`.
         * @return An IPV4 address
         */
        @JvmStatic
        fun ip4Addresses(): AlchemyGenerator<String>
        {
            val integers = integers(1, 1000)

            return AlchemyGenerator { "${integers.get()}.${integers.get()}.${integers.get()}.${integers.get()}" }
        }
    }
}
