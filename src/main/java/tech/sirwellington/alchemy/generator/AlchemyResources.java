/*
 * Copyright © 2018. Sir Wellington.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.sirwellington.alchemy.generator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import kotlin.io.ByteStreamsKt;
import kotlin.text.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SirWellington
 */
final class AlchemyResources
{
    private static final Logger LOG = LoggerFactory.getLogger(AlchemyResources.class);


    //===========================================
    // PRIVATE FUNCTIONS
    //===========================================
    static List<String> readLinesFromResource(String path)
    {
        String file = tryToLoadResource(path);

        if (file == null || file.isEmpty())
        {
            return Collections.emptyList();
        }

        String[] lines = file.split("\n");

        LOG.trace("Successfully read [{}] lines from resource [{}]", lines.length, path);
        return Arrays.asList(lines);
    }

    private static String tryToLoadResource(String path)
    {
        ClassLoader classLoader = AlchemyGenerator.class.getClassLoader();

        if (classLoader == null)
        {
            return null;
        }

        URL url = classLoader.getResource(path);
        if (url == null)
        {
            LOG.warn("Could not load resource at [$path]");
            return null;
        }

        byte[] bytes = tryToReadBytes(url);
        if (bytes == null)
        {
            return null;
        }

        return new String(bytes, Charsets.UTF_8);
    }

    private static byte[] tryToReadBytes(URL url)
    {

        try (InputStream istream = url.openStream())
        {
            byte[] bytes = ByteStreamsKt.readBytes(istream, 1024 * 8);
            return bytes;
        }
        catch (IOException ex)
        {
            LOG.warn("Failed to read resource at {}", url, ex);
            return null;
        }
    }


}
