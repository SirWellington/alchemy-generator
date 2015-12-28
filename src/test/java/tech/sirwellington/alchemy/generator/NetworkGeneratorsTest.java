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

import java.net.URL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class NetworkGeneratorsTest 
{

    @Before
    public void setUp()
    {
    }

    @Test
    public void testUrls()
    {
        AlchemyGenerator<URL> generator = NetworkGenerators.httpUrls();
        assertThat(generator, notNullValue());
        
        Tests.doInLoop(i -> 
        {
            URL result = generator.get();
            assertThat(result, notNullValue());
        });
    }

    @Test
    public void testHttpsUrls()
    {
        AlchemyGenerator<URL> generator = NetworkGenerators.httpsUrls();
        assertThat(generator, notNullValue());
        
        Tests.doInLoop(i -> 
        {
            URL result = generator.get();
            assertThat(result, notNullValue());
            assertThat(result.toString(), startsWith("https://"));
        });
    }

    @Test
    public void testUrlsWithProtocol()
    {
        String scheme = StringGenerators.stringsFromFixedList("http", "https", "file", "ftp", "tcp", "ssh", "ssl").get();
        AlchemyGenerator<URL> generator = NetworkGenerators.urlsWithProtocol(scheme);
        assertThat(generator, notNullValue());

        Tests.doInLoop(i ->
        {
            URL result = generator.get();
            assertThat(result, notNullValue());
            assertThat(result.toString(), startsWith(scheme));
        });
    }
    

}