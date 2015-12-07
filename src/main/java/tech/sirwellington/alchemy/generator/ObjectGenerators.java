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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;

import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.Checks.checkNotNull;
import static tech.sirwellington.alchemy.generator.Checks.checkThat;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;

/**
 * Contains Convenience Generators for POJOs (Plain-Old-Java-Objects).
 * Use at your own risk.
 * 
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
public final class ObjectGenerators
{

    private final static Logger LOG = LoggerFactory.getLogger(ObjectGenerators.class);

    private static final Map<Class<?>, AlchemyGenerator<?>> DEFAULT_GENERATOR_MAPPINGS = new ConcurrentHashMap<>();

    static
    {
        DEFAULT_GENERATOR_MAPPINGS.put(String.class, StringGenerators.alphabeticString());
        DEFAULT_GENERATOR_MAPPINGS.put(Integer.class, NumberGenerators.smallPositiveIntegers());
        DEFAULT_GENERATOR_MAPPINGS.put(Long.class, NumberGenerators.positiveLongs());
        DEFAULT_GENERATOR_MAPPINGS.put(Double.class, NumberGenerators.positiveDoubles());
        DEFAULT_GENERATOR_MAPPINGS.put(Date.class, DateGenerators.anyTime());
        DEFAULT_GENERATOR_MAPPINGS.put(Instant.class, TimeGenerators.anytime());
    }

    ObjectGenerators() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate this class");
    }

    /**
     * Use at your own risk! This {@link AlchemyGenerator } Inflates a Basic POJO
     * Object with randomly generated values.  Do not use this to generate Primitive types;
     * use instead the Alchemy Generators carefully designed and crafted for Primitives.
     * <p>
     * The basic rules for the POJO are the following.
     * Each field must be:
     * <ul> 
     * <li> Non-Static
     * <li> Non-Final
     * <li> Primitive type: Integer, Double, etc
     * <li> {@link String} Type
     * <li> {@link Date} Type
     * <li> {@link Instant} Type
     * <li> Another {@code POJO} That satisfies these rules (embbeded Object)
     * <li> Non-Circular (Cannot contain circular references). A Stack Overflow will occur otherwise.
     * <li> A {@link List} with a Type Parameter matching the above.
     * <li> A {@link Set} with a Type Parameter matching the above.
     * <li> A {@link Map} with Type Parameters matching the above conditions.
     * </ul>
     * <p>
     * Valid Examples:
     * <pre>
     * {@code
     * 
     * private class Computer
     * {
     *      private Date releaseDate;
     *      private String name;
     *      private String manufacturer;
     *      private double cost;
     * }
     * 
     * private class Person
     * {
     *      private String name;
     *      private int age;
     *      private double money;
     *      private Computer computer;
     * }
     * 
     * private class Company
     * {
     * 
     *      private String name;
     *      private int numberOfEmployees;
     *      private List<Person> employees;
     * }
     * 
     * private class CompanyIndex
     * {
     *      private String indexName;
     *      private Map<String, Company> index;
     * }
     * }
     * </pre>
     * @param <T>
     * @param classOfPojo
     * @return 
     * @see StringGenerators
     * @see NumberGenerators
     * @see DateGenerators
     * @see TimeGenerators
     */
    public static <T> AlchemyGenerator<T> pojos(Class<T> classOfPojo)
    {
        return pojos(classOfPojo, DEFAULT_GENERATOR_MAPPINGS);
    }

    public static <T> AlchemyGenerator<T> pojos(Class<T> classOfPojo, Map<Class<?>, AlchemyGenerator<?>> customMappings)
    {
        checkNotNull(classOfPojo, "missing class of POJO");
        checkThat(canInstantiate(classOfPojo),
                  "cannot instantiate class: " + classOfPojo);
        checkThat(!isPrimitiveClass(classOfPojo),
                  "Cannot use pojos with Primitive Type. Use one of the Primitive generators instead.");

        List<Field> validFields = Arrays.asList(classOfPojo.getDeclaredFields())
            .stream()
            .filter(f -> !isStatic(f))
            .filter(f -> !isFinal(f))
            .collect(Collectors.toList());

        return () ->
        {
            
            T instance;
            try
            {
                instance = instantiate(classOfPojo);
            }
            catch (Exception ex)
            {
                LOG.error("Failed to instantiate {}", classOfPojo.getName(), ex);
                return null;
            }
            
            for (Field field : validFields)
            {
                tryInjectField(instance, field, customMappings);
            }
            
            return instance;
        };

    }

    private static <T> boolean canInstantiate(Class<T> classOfPojo)
    {
        try
        {
            instantiate(classOfPojo);
            return true;
        }
        catch (Exception ex)
        {
            LOG.warn("cannot instatiate {}", classOfPojo, ex);
            return false;
        }
    }

    private static boolean isStatic(Field field)
    {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers);
    }

    private static boolean isFinal(Field field)
    {
        int modifiers = field.getModifiers();
        return Modifier.isFinal(modifiers);
    }

    private static <T> T instantiate(Class<T> classOfT) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Constructor<T> defaultConstructor = classOfT.getDeclaredConstructor();
        boolean originalAccessibility = defaultConstructor.isAccessible();
        try
        {
            defaultConstructor.setAccessible(true);
            return defaultConstructor.newInstance();
        }
        finally
        {
            defaultConstructor.setAccessible(originalAccessibility);
        }
    }

    private static <T> boolean isPrimitiveClass(Class<T> classOfPojo)
    {
        if (classOfPojo.isPrimitive())
        {
            return true;
        }

        Set<Class> otherPrimitives = new HashSet<>();
        otherPrimitives.add(String.class);
        otherPrimitives.add(Date.class);
        otherPrimitives.add(Instant.class);

        if (otherPrimitives.contains(classOfPojo))
        {
            return true;
        }

        return false;
    }

    private static <T> void tryInjectField(T instance, Field field, Map<Class<?>, AlchemyGenerator<?>> generatorMappings)
    {
        try
        {
            injectField(instance, field, generatorMappings);
        }
        catch (IllegalAccessException | IllegalArgumentException ex)
        {
            LOG.warn("Could not inject field {}", field.toString(), ex);
        }
    }

    private static void injectField(Object pojo, Field field, Map<Class<?>, AlchemyGenerator<?>> generatorMappings) throws IllegalArgumentException, IllegalAccessException
    {
        Class<?> typeOfField = field.getType();
        typeOfField = ClassUtils.primitiveToWrapper(typeOfField);

        AlchemyGenerator<?> generator = determineGeneratorFor(typeOfField, field, generatorMappings);

        if (generator == null)
        {
            LOG.warn("Could not find a suitable AlchemyGenerator for field {} with type {}", field, typeOfField);
            return;
        }
        
        Object value = generator.get();

        boolean originalAccessibility = field.isAccessible();

        try
        {
            field.setAccessible(true);
            field.set(pojo, value);
        }
        finally
        {
            field.setAccessible(originalAccessibility);
        }
    }


    private static AlchemyGenerator<?> determineGeneratorFor(Class<?> typeOfField, Field field, Map<Class<?>, AlchemyGenerator<?>> generatorMappings)
    {
        AlchemyGenerator<?> generator = generatorMappings.get(typeOfField);
        
        if (generator != null)
        {
            //Already found it
            return generator;
        }

        if (isCollectionType(typeOfField))
        {

            if (lacksGenericTypeArguments(field))
            {
                LOG.warn("POJO {} contains a Collection field {} which is not type-parametrized. Cannot inject.",
                         field.getDeclaringClass(),
                         field);
                
                return null;
            }

            generator = determineGeneratorForCollectionField(field, typeOfField, generatorMappings);
        }
        else
        {
            //Assume Pojo and recurse
            generator = pojos(typeOfField);
        }
        
        return generator;
    }
    
    private static boolean isCollectionType(Class<?> type)
    {
        return isListType(type) ||
               isSetType(type) ||
               isMapType(type);
    }

    private static boolean isListType(Class<?> type)
    {
        return List.class.isAssignableFrom(type);
    }

    private static boolean isSetType(Class<?> type)
    {
        return Set.class.isAssignableFrom(type);
    }

    private static boolean isMapType(Class<?> type)
    {
        return Map.class.isAssignableFrom(type);
    }


    private static boolean lacksGenericTypeArguments(Field field)
    {
        Type genericType = field.getGenericType();

        return !(genericType instanceof ParameterizedType);
    }

    private static AlchemyGenerator<?> determineGeneratorForCollectionField(Field collectionField,
                                                                            Class<?> collectionType,
                                                                            Map<Class<?>, AlchemyGenerator<?>> generatorMappings)
    {
        if (isMapType(collectionType))
        {
            return determineGeneratorForMapField(collectionField, collectionType, generatorMappings);
        }

        ParameterizedType parameterizedType = (ParameterizedType) collectionField.getGenericType();
        Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

        AlchemyGenerator<?> generator = generatorMappings.getOrDefault(valueType, 
                                                                       determineGeneratorFor(valueType, collectionField, generatorMappings));

        List<Object> list = new ArrayList<>();
        int size = one(integers(10, 100));

        for (int i = 0; i < size; ++i)
        {
            list.add(generator.get());
        }

        if (Set.class.isAssignableFrom(collectionType))
        {
            Set set = new HashSet<>(list);
            return () -> set;
        }

        return () -> list;
    }
    
    private static AlchemyGenerator<?> determineGeneratorForMapField(Field mapField,
                                                                     Class<?> mapType,
                                                                     Map<Class<?>, AlchemyGenerator<?>> generatorMappings)
    {
        ParameterizedType parameterizedType = (ParameterizedType) mapField.getGenericType();
        Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];

        AlchemyGenerator<?> keyGenerator = generatorMappings.getOrDefault(keyType, 
                                                                          determineGeneratorFor(keyType, mapField, generatorMappings));
        AlchemyGenerator<?> valueGenerator = generatorMappings.getOrDefault(valueType, 
                                                                            determineGeneratorFor(valueType, mapField, generatorMappings));

        Map<Object, Object> map = new HashMap<>();
        int size = one(integers(10, 100));

        for (int i = 0; i < size; ++i)
        {
            map.put(keyGenerator.get(), valueGenerator.get());
        }

        return () -> map;
    }

}
