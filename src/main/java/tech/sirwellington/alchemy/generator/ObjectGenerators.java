/*
 * Copyright © 2026. Sir Wellington.
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.arguments.Required;
import tech.sirwellington.alchemy.annotations.designs.patterns.SingletonPattern;

import java.lang.reflect.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;
import static tech.sirwellington.alchemy.generator.NumberGenerators.*;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphabeticStrings;

/**
 * {@summary Contains Convenience Generators for a basic data object (an object whose purpose is to contain data.)}
 *
 * These generators should work for most plain data objects.
 *
 * @author SirWellington
 */
@NonInstantiable
@SingletonPattern
public final class ObjectGenerators {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectGenerators.class);

    private static final AlchemyGenerator<Short> shortGenerator = positiveIntegers()
        .mapping(Integer::shortValue);
    private static final AlchemyGenerator<Character> charGenerator = alphabeticStrings()
        .mapping(s -> s.charAt(0));

    private static final Map<Class<?>, AlchemyGenerator<?>> DEFAULT_GENERATOR_MAPPINGS = Map.ofEntries(
        makeEntry(boolean.class, BooleanGenerators.booleans()),
        makeEntry(Boolean.class, BooleanGenerators.booleans()),
        makeEntry(byte.class, BinaryGenerators.bytes()),
        makeEntry(Byte.class, BinaryGenerators.bytes()),
        makeEntry(byte[].class, BinaryGenerators.binary(1_000)),
        makeEntry(Byte[].class, BinaryGenerators.binary(1_000)),
        makeEntry(ByteBuffer.class, BinaryGenerators.byteBuffers(333)),
        makeEntry(char.class, charGenerator),
        makeEntry(Character.class, charGenerator),
        makeEntry(float.class, positiveFloats()),
        makeEntry(Float.class, positiveFloats()),
        makeEntry(double.class, positiveDoubles()),
        makeEntry(Double.class, positiveDoubles()),
        makeEntry(int.class, smallPositiveIntegers()),
        makeEntry(Integer.class, smallPositiveIntegers()),
        makeEntry(short.class, shortGenerator),
        makeEntry(Short.class, shortGenerator),
        makeEntry(long.class, positiveLongs()),
        makeEntry(Long.class, positiveLongs()),
        makeEntry(String.class, alphabeticStrings()),
        makeEntry(Date.class, DateGenerators.anyTime()),
        makeEntry(Instant.class, TimeGenerators.anyTime()),
        makeEntry(
            ZonedDateTime.class,
            TimeGenerators.toZonedDateTimeGenerator(TimeGenerators.anyTime())
        ),
        makeEntry(
            LocalDate.class,
            DateGenerators.toLocalDateGenerator(DateGenerators.anyTime())
        ),
        makeEntry(
            java.sql.Date.class,
            DateGenerators.toSqlDateGenerator(DateGenerators.anyTime())
        ),
        makeEntry(
            Timestamp.class,
            DateGenerators.toSqlTimestampGenerator(DateGenerators.anyTime())
        ),
        makeEntry(URL.class, NetworkGenerators.httpsURLs())
    );

    private static <K, V> Map.Entry<K, V> makeEntry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    /**
     * Use at your own risk! This [AlchemyGenerator] Inflates a Basic POJO
     * Object with randomly generated values.  Do not use this to generate Primitive types;
     * use instead the Alchemy Generators carefully designed and crafted for Primitives.
     * <p>
     * The basic rules for the POJO are the following.
     * Each field must be:
     * <p>
     *  Non-Static
     *  Non-Final
     *  Primitive type: Integer, Double, etc
     *  [String] Type
     *  [enum][Enum] Type
     *  [Date] Type
     *  [Instant] Type
     *  Another `POJO` That satisfies these rules (embedded Object)
     *  Non-Circular (Cannot contain circular references). A Stack Overflow will occur otherwise.
     *  A [List] with a Type Parameter matching the above.
     *  A [Set] with a Type Parameter matching the above.
     *  A [Map] with Type Parameters matching the above conditions.
     * <p>
     *
     * Valid Examples:
     *
     * <pre>{@code
     * private class Computer {
     *   private Date releaseDate;
     *   private String name;
     *   private String manufacturer;
     *   private double cost;
     * }
     *
     * private class Person {
     *   private String name;
     *   private int age;
     *   private double money;
     *   private Computer computer;
     * }
     *
     * private class Company {
     *   private String name;
     *   private int numberOfEmployees;
     *   private List<Person> employees;
     * }
     *
     * private class CompanyIndex {
     *   private String indexName;
     *   private Map<String, Company> index;
     * }
     * }</pre>
     *
     * @param <T> The type of the object to be generated. Inferred from the class.
     * @param classOfPojo The class to be generated.
     * @return An {@link AlchemyGenerator} capable of generating objects of the provided type.
     *
     * @see StringGenerators
     * @see NumberGenerators
     * @see DateGenerators
     * @see TimeGenerators
     */
    public static <T> AlchemyGenerator<T> pojos(@Required Class<T> classOfPojo) {
        return _pojos(classOfPojo, DEFAULT_GENERATOR_MAPPINGS);
    }

    /**
     * A version of {@link #pojos(Class)} that allows you to provide your own custom
     * type mappings.
     *
     * @param <T> Generic type of the object to be generated. This is inferred from the class.
     * @param classOfPojo The class of the object to be generated.
     * @param overrideTypeMappings Allows you to override type mappings by providing your own {@code Type -> AlchemyGenerator<Type>}.
     * @return An {@link AlchemyGenerator} capable of generating objects of the provided type.
     * @see #pojos(Class) 
     */
    public static <T> AlchemyGenerator<T> pojos(
        @Required Class<T> classOfPojo,
        @Required Map<Class<?>, AlchemyGenerator<?>> overrideTypeMappings
    ) {
        checkNotNull(classOfPojo, "classOfPojo is required");
        checkNotNull(overrideTypeMappings, "overrideTypeMappings is required");

        var mappings = new HashMap<>(DEFAULT_GENERATOR_MAPPINGS);
        mappings.putAll(overrideTypeMappings);
        
        return _pojos(
            classOfPojo,
            mappings
        );
    }

    private static <T> AlchemyGenerator<T> _pojos(
        Class<T> classOfPojo,
        Map<Class<?>, AlchemyGenerator<?>> generatorMappings
    ) {
        checkNotNull(classOfPojo, "missing class of POJO");
        checkNotNull(generatorMappings, "generatorMappings is required");

        if (generatorMappings.containsKey(classOfPojo)) {
            return (AlchemyGenerator<T>) generatorMappings.get(classOfPojo);
        }

        checkThat(
            canInstantiate(classOfPojo),
            "cannot instantiate class: " + classOfPojo
        );

        var validFields = Arrays.stream(classOfPojo.getDeclaredFields())
            .filter(f -> !isFinal(f))
            .filter(f -> !isStatic(f))
            .toList();

        return () -> {
            var instance = tryToInstantiate(classOfPojo);
            if (instance == null) {
                return null;
            }

            validFields.forEach(f -> {
                tryInjectField(instance, f, generatorMappings);
            });

            return instance;
        };

    }

    private static <T> boolean canInstantiate(Class<T> classOfPojo) {
        return tryToInstantiate(classOfPojo) != null;
    }

    private static <T> T tryToInstantiate(Class<T> tClass) {
        try {
            return instantiate(tClass);
        } catch (Throwable ex) {
            LOG.warn("cannot instantiate type {}", tClass);
            return null;
        }
    }

    private static <T> T instantiate(Class<T> tClass) throws InstantiationException,
                                                             IllegalAccessException,
                                                             IllegalArgumentException,
                                                             InvocationTargetException {
        var defaultConstructor = firstAvailableConstructor(tClass);
        var args = defaultConstructor.getParameters();
        var values = createValuesFor(args).toArray();

        LOG.debug(
            "Constructor parameters for {} are {}",
            tClass,
            args
        );

        defaultConstructor.setAccessible(true);
        var instance = defaultConstructor.newInstance(values);
        return tClass.cast(instance);
    }

    private static List<?> createValuesFor(Parameter[] args) {
        if (args.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(args)
                     .map(ObjectGenerators::getValueFor)
                     .toList();
    }

    private static <T> void tryInjectField(
        T instance,
        Field field,
        Map<Class<?>, AlchemyGenerator<?>> generatorMappings
        ) {
        try {
            injectField(
                instance,
                field,
                generatorMappings
            );
        }
        catch (Exception ex) {
            LOG.warn("Could not inject field {}", field, ex);
        }

    }

    private static void injectField(
        Object pojo,
        Field field,
        Map<Class<?>, AlchemyGenerator<?>> generatorMappings
    ) throws IllegalArgumentException, IllegalAccessException {
        var typeOfField = field.getType();
        var args = new GeneratorFieldParameters(
            Optional.of(field),
            Optional.empty(),
            typeOfField,
            generatorMappings
        );
        var generator = determineGeneratorFor(args);

        if (generator == null) {
            LOG.warn(
                "Could not find a suitable AlchemyGenerator for field {} with type {}",
                field,
                typeOfField
            );
            return;
        }

        var value = generator.get();
        field.setAccessible(true);
        field.set(pojo, value);
    }

    private static <T> T getValueFor(Parameter parameter) {
        var args = new GeneratorFieldParameters(
            Optional.empty(),
            Optional.of(parameter),
            parameter.getType(),
            DEFAULT_GENERATOR_MAPPINGS
        );
        var generator = determineGeneratorFor(
            args
        );
        if (generator == null) {
            return null;
        }
        try {
            return (T) generator.get();
        }
        catch (ClassCastException _) {
            return null;
        }
    }

    record GeneratorFieldParameters(
        Optional<Field> field,
        Optional<Parameter> parameter,
        @Required  Class<?> typeOfField,
        @Required Map<Class<?>, AlchemyGenerator<?>> generatorMappings
    ) {
        GeneratorFieldParameters {
            Checks.checkNotNull(typeOfField, "typeOfField is required");
            Checks.checkNotNull(generatorMappings, "generatorMappings is required");
        }
    }
    private static AlchemyGenerator<?> determineGeneratorFor(
        @Required GeneratorFieldParameters args
    ) {
        var generatorMappings = args.generatorMappings;
        var typeOfField = args.typeOfField;
        var generator = generatorMappings.get(typeOfField);
        var field = args.field;

        if (generator != null) {
            //Already found it, now see if there's a more specialized version
            return tryToLoadSpecializedGenerator(
                args.parameter.orElse(null),
                field.orElse(null),
                typeOfField,
                generator
            );
        }

        if (isCollectionType(typeOfField)) {
            return generatorForCollectionType(new GeneratorFieldParameters(
                field,
                args.parameter,
                typeOfField,
                generatorMappings
            ));
        }
        else if (isEnumType(typeOfField)) {
            return generatorForEnumType(typeOfField);
        }
        else {
            //Assume it's a POJO and recurse
            generator = pojos(typeOfField);
        }

        return generator;
    }

    @SuppressWarnings("ReassignedVariable")
    private static AlchemyGenerator<?> tryToLoadSpecializedGenerator(
        Parameter parameter,
        Field field,
        Class<?> typeOfField,
        AlchemyGenerator<?> generator
    ) {
        String fieldName = null;
        if (field != null) {
            fieldName = field.getName();
        } else if (parameter != null) {
            fieldName = parameter.getName();
        }

        if (fieldName == null) return generator;

        return switch (typeOfField) {
            case Class<?> cls when cls == String.class -> switch (fieldName) {
                case "firstName"        -> PeopleGenerators.firstNames();
                case "middleName"       -> PeopleGenerators.middleNames();
                case "lastName"         -> PeopleGenerators.lastNames();
                case "name", "fullName" -> PeopleGenerators.fullNames();
                case "email"            -> PeopleGenerators.emailAddresses();
                case "city"             -> PlaceGenerators.cities();
                case "country"          -> PlaceGenerators.countries();
                default      -> generator;
            };

            case Class<?> cls when cls == Double.class || cls == double.class -> switch (fieldName) {
                case "latitude", "lat"  -> GeolocationGenerators.latitudes();
                case "longitude", "lon" -> GeolocationGenerators.longitudes();
                case "price"            -> NumberGenerators.doubles(10.0, 300_000.0);
                default      -> generator;
            };

            case Class<?> cls when cls == Float.class || cls == float.class -> switch (fieldName) {
                case "price", "cost" -> NumberGenerators.floats(10.0f, 300_000.0f);
                case "temperature"   -> NumberGenerators.floats(-50f, 500f);
                default      -> generator;
            };

            case Class<?> cls when cls == Integer.class || cls == int.class -> switch (fieldName) {
                case "age"         -> PeopleGenerators.adultAges();
                case "year"        -> NumberGenerators.integers(1996, 2026);
                default -> generator;
            };

            default -> generator;
        };
    }

    private static AlchemyGenerator<?> generatorForEnumType(Class<?> typeOfField) {
        var enumValues = typeOfField.getEnumConstants();
        if (enumValues == null) {
            LOG.warn("Enum Class {} has no Enum Values: ", typeOfField);
            return null;
        }

        return () -> {
            var position = one(integers(0, enumValues.length));
            return enumValues[position];
        };
    }

    private static AlchemyGenerator<?> generatorForCollectionType(
        GeneratorFieldParameters args
    ) {
        var field = args.field;
        var parameter = args.parameter;
        if (field.isPresent()) {
            if (fieldLacksGenericTypeArguments(field.get())) {
                LOG.warn(
                    "POJO {} contains a Collection field {} which is not type-parametrized. Cannot inject.",
                    field.get().getDeclaringClass(),
                    field
                );

                return null;
            }

            var typeOfField = args.typeOfField;
            var generatorMappings = args.generatorMappings;
            return determineGeneratorForCollectionField(
                field.orElse(null),
                typeOfField,
                generatorMappings
            );
        }
        else if (parameter.isPresent()) {
            var parameterType = (ParameterizedType) parameter.get().getParameterizedType();
            if (parameterType == null) {
                LOG.warn(
                    "POJO {} contains a Collection parameter {} which is not type-parameterized: [{}]. Cannot inject.",
                    args.typeOfField,
                    parameter,
                    parameter.get().getParameterizedType()
                );
                return null;
            }

            return determineGeneratorForCollectionParameter(
                parameter.orElse(null),
                args.typeOfField,
                args.generatorMappings
            );
        }
        else {
            LOG.warn(
                "Cannot Instantiate: No generic information available in order to generate values for $typeOfField"
            );
            return null;
        }

    }

    private static boolean isCollectionType(Class<?> type) {
        return isListType(type) ||
            isSetType(type) ||
            isMapType(type);
    }

    private static boolean isListType(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }

    private static boolean isSetType(Class<?> type) {
        return Set.class.isAssignableFrom(type);
    }

    private static boolean isMapType(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    private static boolean fieldLacksGenericTypeArguments(
        Field field
    ) {
        var genericType = field.getGenericType();
        return !(genericType instanceof ParameterizedType);
    }

    private static AlchemyGenerator<?> determineGeneratorForCollectionField(
        Field collectionField,
        Class<?> collectionType,
        Map<Class<?>, AlchemyGenerator<?>> generatorMappings
    ) {
        if (isMapType(collectionType)) {
            return determineGeneratorForMapField(
                collectionField,
                generatorMappings
            );
        }

        var genericType = collectionField.getGenericType();
        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return null;
        }

        var actualType = Arrays.stream(parameterizedType.getActualTypeArguments())
                               .findFirst()
                               .orElse(null);

        var valueType = (actualType instanceof Class<?> clazz) ?
            clazz :
            tryToDetermineClassFrom(actualType);

        if (valueType == null) return null;

        return determineGeneratorForCollectionWithValueType(
            valueType,
            collectionType,
            generatorMappings
        );
    }

    private static AlchemyGenerator<?> determineGeneratorForCollectionParameter(
        Parameter collectionParameter,
        Class<?> collectionType,
        Map<Class<?>, AlchemyGenerator<?>> generatorMappings
    ) {
        if (collectionType == null && collectionParameter != null) {
            collectionType = collectionParameter.getType();
        }
        if (isMapType(collectionType)) {
            return determineGeneratorForMapParameter(
                collectionParameter,
                generatorMappings
            );
        }

        if (collectionParameter == null) return null;

        if (!(collectionParameter.getParameterizedType() instanceof ParameterizedType parameterizedType)) {
            return null;
        }
        var actualTypes = parameterizedType.getActualTypeArguments();
        var actualType = Arrays.stream(actualTypes).findFirst().orElse(null);
        if (actualType == null) return null;

        Class<?> valueType;
        if ((actualType instanceof Class<?> v)) {
            valueType = v;
        } else {
            valueType = tryToDetermineClassFrom(actualType);
        }

        if (valueType == null) {
            return null;
        }

        return determineGeneratorForCollectionWithValueType(
            valueType,
            collectionType,
            generatorMappings
        );
    }

    private static Class<?> tryToDetermineClassFrom(Type actualType) {
        return switch(actualType) {
            case WildcardType wildcardType when wildcardType.getUpperBounds().length != 0 -> {
                var className = wildcardType.getTypeName().replaceFirst("\\? extends", "");
                yield tryToLoadClass(className);
            }
            case WildcardType wildcardType when wildcardType.getLowerBounds().length != 0 -> {
                var className = wildcardType.getTypeName().replaceFirst("\\? super", "");
                yield tryToLoadClass(className);
            }
            case null, default -> null;
        };
    }

    private static Class<?> tryToLoadClass(String classname) {
        try {
            return ObjectGenerators.class.getClassLoader().loadClass(classname);
        } catch (Throwable _) {
            return null;
        }
    }

    private static AlchemyGenerator<?> determineGeneratorForCollectionWithValueType(
        Class<?> valueType,
        Class<?> collectionType,
        Map<Class<?>, AlchemyGenerator<?>> generatorMappings
    ) {
        var generator = determineGeneratorFor(new GeneratorFieldParameters(
            Optional.empty(),
            Optional.empty(),
            valueType,
            generatorMappings
        ));
        if (generator == null) return null;

        var size = one(integers(3, 25));
        return () -> {
            var list = IntStream.range(0, size)
                .mapToObj(_ -> generator.get())
                .toList();
            return isSetType(collectionType) ? Set.copyOf(list) : list;
        };
    }

    private static AlchemyGenerator<?> determineGeneratorForMapField(
        Field mapField,
        Map<Class<?>, AlchemyGenerator<?>> generatorMappings
    ) {
        var genericType = mapField.getGenericType();
        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return null;
        }

        var typeParameters = parameterizedType.getActualTypeArguments();
        if (typeParameters.length != 2) {
            LOG.warn("Field {} is not a map field as it does not have two type parameters", mapField);
            return null;
        }

        var keyType = (Class<?>) typeParameters[0];
        var valueType = (Class<?>) typeParameters[1];

        var keyGenerator = determineGeneratorFor(new GeneratorFieldParameters(
            Optional.of(mapField),
            Optional.empty(),
            keyType,
            generatorMappings
        ));
        var valueGenerator = determineGeneratorFor(new GeneratorFieldParameters(
            Optional.of(mapField),
            Optional.empty(),
            valueType,
            generatorMappings
        ));
        
        if (keyGenerator == null || valueGenerator == null) {
            return null;
        }
        return makeMapGenerator(keyGenerator, valueGenerator);
    }


    private static AlchemyGenerator<?> determineGeneratorForMapParameter(
        Parameter mapParameter,
        Map<Class<?>, AlchemyGenerator<?>> generatorMappings
    ) {
        if (mapParameter == null) return null;

        if (!(mapParameter.getParameterizedType() instanceof ParameterizedType parameterizedType)) {
            return null;
        }
        if (!(parameterizedType.getActualTypeArguments()[0] instanceof Class<?> keyType)) {
            return null;
        }
        if (!(parameterizedType.getActualTypeArguments()[1] instanceof Class<?> valueType)) {
            return null;
        }

        var keyGenerator = determineGeneratorFor(
            new GeneratorFieldParameters(
                Optional.empty(),
                Optional.of(mapParameter),
                keyType,
                generatorMappings
            )
        );
        if (keyGenerator == null) return null;

        var valueGenerator = determineGeneratorFor(
            new GeneratorFieldParameters(
                Optional.empty(),
                Optional.empty(),
                valueType,
                generatorMappings
            )
        );
        if (valueGenerator == null) return null;

        return makeMapGenerator(keyGenerator, valueGenerator);
    }

    private static AlchemyGenerator<?> makeMapGenerator(
        AlchemyGenerator<?> keyGenerator,
        AlchemyGenerator<?> valueGenerator
    ) {
        return () -> {
            var map = new HashMap<>();
            var size = one(integers(3, 25));

            IntStream.range(0, size).forEach(_ -> {
                var key = keyGenerator.get();
                var value = valueGenerator.get();
                map.put(key, value);
            });

            return map;
        };
    }

    private static boolean isStatic(Field field) {
        var modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers);
    }

    private static boolean isFinal(Field field) {
        var modifiers = field.getModifiers();
        return Modifier.isFinal(modifiers);
    }

    private static boolean isEnumType(Class<?> typeOfField) {
        return typeOfField.isEnum();
    }

    private static Constructor<?> firstAvailableConstructor(Class<?> clazz) {
        var constructors = clazz.getConstructors();

        return Arrays.stream(constructors)
                     .filter(ObjectGenerators::hasNoParameters)
                     .findFirst()
                     .or(() -> Arrays.stream(constructors).findFirst())
                     .or(() -> tryToGet(clazz::getDeclaredConstructor))
                     .or(() -> tryToGet(() -> getRecordConstructor(clazz)))
                     .orElse(null);
    }

    private static boolean hasNoParameters(Constructor<?> constructor) {
        return constructor.getParameterCount() == 0;
    }

    private static <T> Optional<T> tryToGet(ThrowingSupplier<T, Throwable> supplier) {
        try {
            var result = supplier.get();
            return Optional.of(result);
        }
        catch (Throwable ex) {
            return Optional.empty();
        }
    }
    private static Constructor<?> getRecordConstructor(Class<?> clazz) throws NoSuchMethodException {
        if (!clazz.isRecord()) return null;

        var components = clazz.getRecordComponents();
        var params = Arrays.stream(components)
            .map(RecordComponent::getType)
            .toArray(Class<?>[]::new);

        var constructor = clazz.getDeclaredConstructor(params);
        constructor.setAccessible(true);
        return constructor;
    }
}