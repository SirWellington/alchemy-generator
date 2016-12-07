/*
 * Copyright 2016 RedRoma, Inc..
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
public class GeolocationGeneratorsTest
{

    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public void testLatitudes()
    {
        AlchemyGenerator<Double> generator = GeolocationGenerators.latitudes();
        assertThat(generator, notNullValue());
        
        Tests.doInLoop(i -> 
        {
            double latitude = generator.get();
            
            assertThat(latitude, greaterThanOrEqualTo(-90.0));
            assertThat(latitude, lessThanOrEqualTo(90.0));
        });
    }

    @Test
    public void testLongitudes()
    {
        AlchemyGenerator<Double> generator = GeolocationGenerators.longitudes();
        assertThat(generator, notNullValue());
        
        Tests.doInLoop(i -> 
        {
            double latitude = generator.get();
            
            assertThat(latitude, greaterThanOrEqualTo(-180.0));
            assertThat(latitude, lessThanOrEqualTo(180.0));
        });
    }

}