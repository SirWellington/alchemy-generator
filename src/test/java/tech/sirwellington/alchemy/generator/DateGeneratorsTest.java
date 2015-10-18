/*
 * Copyright 2015 SirWellington Tech.
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

import java.util.Date;
import java.util.function.Consumer;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.Dates.isNow;
import static tech.sirwellington.alchemy.generator.Dates.now;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class DateGeneratorsTest
{

    private int iterations;

    @Before
    public void setUp()
    {
        iterations = one(integers(100, 1000));
    }

    private void doInLoop(Consumer<Integer> function)
    {
        for (int i = 0; i < iterations; ++i)
        {
            function.accept(i);
        }
    }

    @Test
    public void testNow()
    {
        System.out.println("testNow");

        AlchemyGenerator<Date> instance = DateGenerators.now();

        doInLoop(i ->
        {
            Date value = instance.get();
            assertThat(value, notNullValue());
            assertThat(isNow(value, 15), is(true));
        });
    }

    @Test
    public void testBeforeNow()
    {
        System.out.println("testBeforeNow");
        AlchemyGenerator<Date> instance = DateGenerators.beforeNow();

        doInLoop(i ->
        {
            Date value = instance.get();
            assertThat(value, notNullValue());
            assertThat(value.before(now()), is(true));
        });
    }

    @Test
    public void testAfterNow()
    {
        System.out.println("testAfterNow");

        AlchemyGenerator<Date> instance = DateGenerators.afterNow();

        doInLoop(i ->
        {
            Date value = instance.get();
            assertThat(value, notNullValue());
            assertThat(value.after(now()), is(true));
        });
    }

}
