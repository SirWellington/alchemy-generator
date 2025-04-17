package tech.sirwellington.alchemy.generator;

import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.Optional;
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
public class PeopleGenerators {

    private static final List<String> FIRST_NAMES = readLinesFromResource("names/first-names.txt");
    private static final List<String> MIDDLE_NAMES = readLinesFromResource("names/middle-names.txt");
    private static final List<String> LAST_NAMES = readLinesFromResource("names/last-names.txt");
    private static final List<String> PROFESSIONS = readLinesFromResource("other/professions.txt");

    private PeopleGenerators() throws IllegalAccessException {
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

    /**
     * Returns a US-based phone number in String form,
     * without the leading country code.
     * For example, "7545185179".
     */
    public static AlchemyGenerator<String> phoneNumbers() {
        return phoneNumbers(null);
    }

    /**
     * Returns a phone number with an optional phoneCountryCode that will be prefixed.
     * @param phoneCountryCode Optional. For example, {@code "+1", "+57"}, etc.
     *                         If {@code null}, no phone country prefix will be added.
     */
    public static AlchemyGenerator<String> phoneNumbers(
            @Optional String phoneCountryCode
    ) {
        AlchemyGenerator<Integer> threeDigits = integers(100, 1000);
        AlchemyGenerator<Integer> fourDigits = integers(1_000, 10_000);

        if (phoneCountryCode == null) {
            return () -> MessageFormat.format(
                    "{0}-{1}-{2}",
                    String.valueOf(one(threeDigits)),
                    String.valueOf(one(threeDigits)),
                    String.valueOf(one(fourDigits))
            );
        } else {
            return () -> MessageFormat.format(
                    "{0} {1}-{2}-{3}",
                    phoneCountryCode,
                    String.valueOf(one(threeDigits)),
                    String.valueOf(one(threeDigits)),
                    String.valueOf(one(fourDigits))
            );
        }
    }

    /**
     * @return A Generator of the most popular email domains used today,
     * according to ChatGPT.
     * <blockquote>
     * <ol>
     *   <li><b>@gmail.com</b> – Google's free email service; dominant globally due to Android and Google integration.</li>
     *   <li><b>@yahoo.com</b> – Yahoo’s long-standing email service, still widely used in the U.S. and parts of Asia.</li>
     *   <li><b>@outlook.com</b> – Microsoft's modern replacement for Hotmail, popular for both personal and business use.</li>
     *   <li><b>@hotmail.com</b> – Legacy Microsoft domain still in active use, especially with older accounts.</li>
     *   <li><b>@live.com</b> – Another Microsoft domain introduced with Windows Live services.</li>
     *   <li><b>@icloud.com</b> – Apple’s domain used by iCloud Mail; common among iOS/macOS users.</li>
     *   <li><b>@mail.com</b> – A customizable domain provided by GMX, popular in Europe.</li>
     *   <li><b>@yandex.com / @yandex.ru</b> – Russia’s top email provider, widely used in Russian-speaking countries.</li>
     *   <li><b>@protonmail.com</b> – A privacy-focused email service based in Switzerland, growing among security-conscious users.</li>
     *   <li><b>@163.com / @126.com / @qq.com</b> – Major Chinese providers (NetEase and Tencent) with massive local user bases.</li>
     * </ol>
     * </blockquote>
     */
    public static AlchemyGenerator<String> popularEmailDomains() {
        return stringsFromFixedList(
                "gmail.com",
                "yahoo.com",
                "outlook.com",
                "hotmail.com",
                "live.com",
                "icloud.com",
                "mail.com",
                "yandex.com",
                "protonmail.com",
                "163.com",
                "126.com",
                "qq.com"
        );
    }

    /**
     * Generates email addresses using one of the most popular domains.
     * If you want to control which domains are in the email addresses,
     * use {@link #emailAddresses(AlchemyGenerator)} and provide a custom domain generator.
     *
     * @see #emailAddresses(AlchemyGenerator)
     */
    public static AlchemyGenerator<String> emailAddresses() {
        return emailAddresses(popularEmailDomains());
    }

    /**
     * Generates email addresses using the domains provided by the {@code domainGenerator}.
     * @param domainGenerator Used to create domains for the email addresses.
     *                        You can use the {@link #popularEmailDomains()}
     * @see #emailAddresses()
     */
    public static AlchemyGenerator<String> emailAddresses(
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

    /**
     * This generator returns a profession or job title,
     * for example, "Software Engineer", or "Carpenter", or "Teacher".
     */
    public static AlchemyGenerator<String> professions() {
        return stringsFromFixedList(PROFESSIONS);
    }
}
