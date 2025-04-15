package tech.sirwellington.alchemy.generator;

import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;
import tech.sirwellington.alchemy.annotations.arguments.Required;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import java.text.MessageFormat;
import java.util.List;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.AlchemyResources.readLinesFromResource;
import static tech.sirwellington.alchemy.generator.Checks.checkNotEmpty;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.NumberGenerators.doubles;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.StringGenerators.stringsFromFixedList;

/**
 * Generators for common information about people: names, addresses, phone numbers,
 * social-security numbers, emails, etc.
 */
@NonInstantiable
@StrategyPattern(role = StrategyPattern.Role.CONCRETE_BEHAVIOR)
public class PeopleGeneratorsJ {

    private static final List<String> FIRST_NAMES = readLinesFromResource("names/first-names.txt");
    private static final List<String> MIDDLE_NAMES = readLinesFromResource("names/middle-names.txt");
    private static final List<String> LAST_NAMES = readLinesFromResource("names/last-names.txt");
    private static final List<String> PROFESSIONS = readLinesFromResource("other/professions.txt");

    private PeopleGeneratorsJ() throws IllegalAccessException {
        throw new IllegalAccessException("cannot directly instantiate");
    }

    /**
     * Generates a "first name".
     * <blockquote>
     * There are no guarantees on the names except that it is a
     * String where the first letter is capitalized.
     * </blockquote>
     */
    public static AlchemyGenerator<String> firstNames() {
        return stringsFromFixedList(FIRST_NAMES);
    }
    /**
     * Generates a "middle name".
     * <blockquote>
     * There are no guarantees on the names except that it is a
     * String where the first letter is capitalized.
     * </blockquote>
     */
    public static AlchemyGenerator<String> middleNames() {
        return stringsFromFixedList(MIDDLE_NAMES);
    }

    /**
     * Generates a "last name".
     * <blockquote>
     * There are no guarantees on the names except that it is a
     * String where the first letter is capitalized.
     * </blockquote>
     */
    public static AlchemyGenerator<String> lastNames() {
        return stringsFromFixedList(LAST_NAMES);
    }

    /**
     * Generates a "full name", including a first name and a last name.
     * <blockquote>
     * The name may or may not include a middle name.
     * </blockquote>
     */
    public static AlchemyGenerator<String> fullNames() {
        AlchemyGenerator<String> firstNames = firstNames();
        AlchemyGenerator<String> middleNames = middleNames();
        AlchemyGenerator<String> lastNames = lastNames();
        AlchemyGenerator<Double> seeds = NumberGenerators.doubles(0.0, 1.0);

        return () -> {
          StringBuilder builder = new StringBuilder();
          builder.append(one(firstNames));

          double seed = one(seeds);
          boolean includeMiddleName = seed <= 0.4;
          if (includeMiddleName) {
              builder.append(" ")
                      .append(one(middleNames));
          }

          return builder
                  .append(" ")
                  .append(one(lastNames))
                  .toString();
        };
    }

    /**
     * Generates a human's age, from 1 to 108.
     */
    public static AlchemyGenerator<Integer> ages() {
        return integers(1, 108);
    }

    /**
     * Generates adult ages from 18 to 108.
     */
    public static AlchemyGenerator<Integer> adultAges() {
        return integers(18, 108);
    }

    /**
     * Generates child ages from 1 to 17.
     */
    public static AlchemyGenerator<Integer> childAges() {
        return integers(1, 18);
    }

    public static AlchemyGenerator<String> phoneNumbers() {
        return phoneNumbers("+1");
    }

    public static AlchemyGenerator<String> phoneNumbers(
            @NonEmpty String phoneCountryCode
    ) {
        checkNotEmpty(phoneCountryCode, "missing phoneCountryCode");
        AlchemyGenerator<Integer> threeDigits = integers(100, 1000);
        AlchemyGenerator<Integer> fourDigits = integers(1_000, 10_000);

        return () -> MessageFormat.format(
                "{0} {1}-{2}-{3}",
                phoneCountryCode,
                one(threeDigits),
                one(threeDigits),
                one(fourDigits)
        );
    }

    public static AlchemyGenerator<String> popularEmailDomains() {
        return stringsFromFixedList(
                "yahoo.com",
                "google.com",
                "gmail.com",
                "sirwellington.tech",
                "apple.com",
                "icloud.com",
                "microsoft.com",
                "kw.com",
                "walmart.com"
        );
    }

    public static AlchemyGenerator<String> emails(
            @Required AlchemyGenerator<String> domainGenerator
    ) {
        checkNotNull(domainGenerator, "missing domainGenerator");
        checkNotEmpty(domainGenerator.get(), "Domain Generator returned empty String");

        AlchemyGenerator<Integer> numbers = integers(0, 999);
        AlchemyGenerator<String> firstNames = firstNames();
        AlchemyGenerator<String> lastNames = lastNames();
        AlchemyGenerator<Double> seeds = doubles(0.0, 1.0);

        return () -> {
            double seed = one(seeds);
            boolean includeLastname = seed >= 0.6;

            if (includeLastname) {
                return MessageFormat.format(
                        "{0}.{1}@{2}",
                        one(firstNames),
                        one(lastNames),
                        one(domainGenerator)
                );
            }
            else {
                return MessageFormat.format(
                        "{0}{1}@{2}",
                        one(firstNames),
                        one(numbers),
                        one(domainGenerator)
                );
            }
        };
    }

    public static AlchemyGenerator<String> professions() {
        return stringsFromFixedList(PROFESSIONS);
    }
}
