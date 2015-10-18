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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.BooleanGenerators.booleans;
import static tech.sirwellington.alchemy.generator.NumberGenerators.smallPositiveIntegers;

/**
 *
 * @author SirWellington
 */
@NonInstantiable
public final class DateGenerators
{

    private final static Logger LOG = LoggerFactory.getLogger(DateGenerators.class);

    /**
     * Always return the current time.
     * <br>
     * <pre>
     * Note that the current time depends on when it is called.
     * </pre>
     *
     * @return
     */
    public static AlchemyGenerator<Date> now()
    {
        return () -> Dates.now();
    }

    public static AlchemyGenerator<Date> beforeNow()
    {
        return () ->
        {
            int amountBefore = one(smallPositiveIntegers());
            boolean useDaysInsteadOfHours = one(booleans());

            if (useDaysInsteadOfHours)
            {
                return Dates.daysAgo(amountBefore);
            }
            else
            {
                return Dates.hoursAgo(amountBefore);
            }
        };
    }

    public static AlchemyGenerator<Date> afterNow()
    {
        return () ->
        {
            int amountAfter = one(smallPositiveIntegers());
            boolean useDaysInsteadOfHours = one(booleans());

            if (useDaysInsteadOfHours)
            {
                return Dates.daysAhead(amountAfter);
            }
            else
            {
                return Dates.hoursAhead(amountAfter);
            }
        };
    }

}
