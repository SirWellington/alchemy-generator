package tech.sirwellington.alchemy.generator;

import tech.sirwellington.alchemy.annotations.arguments.Required;

/**
 * {@summary Alchemy Generators for Enum Types.}
 *
 * @author SirWellington
 */
public final class EnumGenerators {

    private EnumGenerators() throws IllegalAccessException {
        throw new IllegalAccessException("cannot directly instantiate");
    }

    /**
     * Returns sequence of Enum values from the supplied arguments.
     * <pre>
     * Example:
     *
     * enum Fruit {APPLE, ORANGE, PEAR}
     * Fruit someFruit = enumValueOf(Fruit.class).get();
     * </pre>
     * @param enumClass The {@code class} of the Enum.
     * @param <E> The type of the Enum.
     * @return A generator that produces values of the supplied enum type.
     */
    public static <E extends Enum<E>> AlchemyGenerator<E> enumValueOf(
        @Required Class<E> enumClass
    ) {
        Checks.checkNotNull(enumClass, "enumClass is null");
        var constants = enumClass.getEnumConstants();

        if (constants.length == 0) {
            throw new IllegalArgumentException("Enum class has no values" + enumClass);
        }

        var indices = NumberGenerators.integers(0, constants.length);
        return () -> {
            int index = indices.get();
            return constants[index];
        };
    }
}
