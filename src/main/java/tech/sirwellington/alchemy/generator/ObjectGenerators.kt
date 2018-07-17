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

package tech.sirwellington.alchemy.generator

import org.apache.commons.lang3.ClassUtils
import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.integers
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.positiveDoubles
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.positiveFloats
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.positiveIntegers
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.positiveLongs
import tech.sirwellington.alchemy.generator.NumberGenerators.Companion.smallPositiveIntegers
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.alphabeticStrings
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.Parameter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.net.URL
import java.nio.ByteBuffer
import java.time.Instant
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/**
 * Contains Convenience Generators for POJOs (Plain-Old-Java-Objects).
 * Use at your own risk.
 *
 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
object ObjectGenerators
{

    private val LOG = LoggerFactory.getLogger(ObjectGenerators::class.java)

    private val DEFAULT_GENERATOR_MAPPINGS = ConcurrentHashMap<Class<*>, AlchemyGenerator<*>>()

    private val shortGenerator = AlchemyGenerator {
        one(positiveIntegers()).toShort()
    }

    private val charGenerator = AlchemyGenerator {
        one(alphabeticStrings()).first()
    }

    init
    {
        DEFAULT_GENERATOR_MAPPINGS.put(Boolean::class.java, BooleanGenerators.booleans())
        DEFAULT_GENERATOR_MAPPINGS.put(Byte::class.java, BinaryGenerators.bytes())
        DEFAULT_GENERATOR_MAPPINGS.put(ByteBuffer::class.java, BinaryGenerators.byteBuffers(333))
        DEFAULT_GENERATOR_MAPPINGS.put(ByteArray::class.java, BinaryGenerators.binary(333))
        DEFAULT_GENERATOR_MAPPINGS.put(Char::class.java, charGenerator)
        DEFAULT_GENERATOR_MAPPINGS.put(Character::class.java, charGenerator)
        DEFAULT_GENERATOR_MAPPINGS.put(Float::class.java, positiveFloats())
        DEFAULT_GENERATOR_MAPPINGS.put(Double::class.java, positiveDoubles())
        DEFAULT_GENERATOR_MAPPINGS.put(Int::class.java, smallPositiveIntegers())
        DEFAULT_GENERATOR_MAPPINGS.put(Long::class.java, positiveLongs())
        DEFAULT_GENERATOR_MAPPINGS.put(Short::class.java, shortGenerator)
        DEFAULT_GENERATOR_MAPPINGS.put(String::class.java, alphabeticStrings())
        DEFAULT_GENERATOR_MAPPINGS.put(Instant::class.java, TimeGenerators.anytime())
        DEFAULT_GENERATOR_MAPPINGS.put(URL::class.java, NetworkGenerators.httpUrls())
        DEFAULT_GENERATOR_MAPPINGS.put(java.lang.Boolean::class.java, BooleanGenerators.booleans())
        DEFAULT_GENERATOR_MAPPINGS.put(java.lang.Byte::class.java, BinaryGenerators.bytes())
        DEFAULT_GENERATOR_MAPPINGS.put(java.lang.Byte::class.java, BinaryGenerators.bytes())
        DEFAULT_GENERATOR_MAPPINGS.put(java.lang.Float::class.java, positiveFloats())
        DEFAULT_GENERATOR_MAPPINGS.put(java.lang.Double::class.java, positiveDoubles())
        DEFAULT_GENERATOR_MAPPINGS.put(java.lang.Integer::class.java, smallPositiveIntegers())
        DEFAULT_GENERATOR_MAPPINGS.put(java.lang.Long::class.java, positiveLongs())
        DEFAULT_GENERATOR_MAPPINGS.put(java.lang.Short::class.java, shortGenerator)
        DEFAULT_GENERATOR_MAPPINGS.put(java.util.Date::class.java, DateGenerators.anyTime())
        DEFAULT_GENERATOR_MAPPINGS.put(java.sql.Date::class.java, DateGenerators.anyTime().asSqlDateGenerator())
        DEFAULT_GENERATOR_MAPPINGS.put(java.sql.Timestamp::class.java, DateGenerators.anyTime().asSqlTimestampGenerator())
    }

    /**
     * Use at your own risk! This [AlchemyGenerator] Inflates a Basic POJO
     * Object with randomly generated values.  Do not use this to generate Primitive types;
     * use instead the Alchemy Generators carefully designed and crafted for Primitives.
     *
     *
     * The basic rules for the POJO are the following.
     * Each field must be:
     *
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
     *
     *
     *
     * Valid Examples:
     *
     * ```
     * private class Computer
     * {
     * private Date releaseDate;
     * private String name;
     * private String manufacturer;
     * private double cost;
     * }
     *
     * private class Person
     * {
     * private String name;
     * private int age;
     * private double money;
     * private Computer computer;
     * }
     *
     * private class Company
     * {
     *
     * private String name;
     * private int numberOfEmployees;
     * private List<Person> employees;
     * }
     *
     * private class CompanyIndex
     * {
     * private String indexName;
     * private Map<String, Company> index;
     * }
     * ```
     *
     * @param <T>
     *
     * @param classOfPojo
     *
     * @return
     *
     * @see StringGenerators
     * @see NumberGenerators
     * @see DateGenerators
     * @see TimeGenerators
     */
    @JvmStatic
    fun <T : Any> pojos(classOfPojo: Class<T>): AlchemyGenerator<T>
    {
        return pojos(classOfPojo, DEFAULT_GENERATOR_MAPPINGS)
    }

    /**
     * Kotlin shorthand method for [pojos]
     */
    inline fun <reified T : Any> pojos(): AlchemyGenerator<T>
    {
        return pojos(T::class.java)
    }


    @JvmStatic
    fun <T : Any> pojos(classOfPojo: Class<T>, customMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<T>
    {
        checkNotNull(classOfPojo, "missing class of POJO")

        if (customMappings.containsKey(classOfPojo))
        {
            return customMappings[classOfPojo] as AlchemyGenerator<T>
        }

        checkThat(canInstantiate(classOfPojo), "cannot instantiate class: " + classOfPojo)

        val validFields = classOfPojo.declaredFields
                .filter { f -> !isStatic(f) }
                .filter { f -> !isFinal(f) }
                .toList()

        return AlchemyGenerator<T>()
        {

            val instance = classOfPojo.tryToInstantiate() ?: return@AlchemyGenerator null

            validFields.forEach { tryInjectField(instance, it, customMappings) }

            return@AlchemyGenerator instance
        }

    }

    private fun <T : Any> canInstantiate(classOfPojo: Class<T>): Boolean
    {
        return classOfPojo.tryToInstantiate() != null
    }

    private fun <T : Any> Class<T>.tryToInstantiate(): T?
    {
        return try
        {
            instantiate(this)
        }
        catch (ex: Exception)
        {
            LOG.warn("cannot instantiate type $this")
            null
        }
    }

    @Throws(NoSuchMethodException::class, InstantiationException::class, IllegalAccessException::class, IllegalArgumentException::class, InvocationTargetException::class)
    private fun <T : Any> instantiate(classOfT: Class<T>): T
    {
        val defaultConstructor = classOfT.firstAvailableConstructor

        val originalAccessibility = defaultConstructor.isAccessible
        val args = defaultConstructor.parameters
        val values = createValuesFor(args).toTypedArray()

        LOG.debug("Constructor parameters for $classOfT are $args")

        try
        {
            defaultConstructor.isAccessible = true
            return defaultConstructor.newInstance(*values) as T
        }
        finally
        {
            defaultConstructor.isAccessible = originalAccessibility
        }
    }

    private fun createValuesFor(args: Array<out Parameter>): List<Any?>
    {
        if (args.isEmpty()) return emptyList()

        return args.map { getValueFor<Any>(it) }
                .toList()

    }

    private fun <T : Any> tryInjectField(instance: T, field: Field, generatorMappings: Map<Class<*>, AlchemyGenerator<*>>)
    {
        try
        {
            injectField(instance, field, generatorMappings)
        }
        catch (ex: Exception)
        {
            LOG.warn("Could not inject field $field", ex)
        }

    }

    @Throws(IllegalArgumentException::class, IllegalAccessException::class)
    private fun injectField(pojo: Any, field: Field, generatorMappings: Map<Class<*>, AlchemyGenerator<*>>)
    {
        var typeOfField = field.type
        typeOfField = ClassUtils.primitiveToWrapper(typeOfField)

        val generator = determineGeneratorFor(field = field,
                                              parameter = null,
                                              typeOfField = typeOfField,
                                              generatorMappings = generatorMappings)

        if (generator == null)
        {
            LOG.warn("Could not find a suitable AlchemyGenerator for field {} with type {}", field, typeOfField)
            return
        }

        val value = generator.get()

        val originalAccessibility = field.isAccessible

        try
        {
            field.isAccessible = true
            field.set(pojo, value)
        }
        finally
        {
            field.isAccessible = originalAccessibility
        }
    }

    private fun <T : Any> getValueFor(parameter: Parameter): T?
    {
        val generator = determineGeneratorFor(typeOfField = parameter.type, parameter = parameter, generatorMappings = DEFAULT_GENERATOR_MAPPINGS)

        return generator?.get() as? T
    }

    private fun determineGeneratorFor(field: Field? = null,
                                      parameter: Parameter? = null,
                                      typeOfField: Class<*>,
                                      generatorMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<*>?
    {
        var generator: AlchemyGenerator<*>? = generatorMappings[typeOfField]

        if (generator != null)
        {
            //Already found it, now see if there's a more specialized version
            tryToLoadSpecializedGenerator(field, typeOfField, generator)
        }

        if (isCollectionType(typeOfField))
        {
            return generatorForCollectionType(field = field,
                                              parameter = parameter,
                                              typeOfField =  typeOfField,
                                              generatorMappings = generatorMappings)
        }
        else if (isEnumType(typeOfField))
        {
            return generatorForEnumType(typeOfField)
        }
        else
        {
            //Assume it's a POJO and recurse
            generator = pojos(typeOfField)
        }

        return generator
    }

    private fun tryToLoadSpecializedGenerator(field: Field?, typeOfField: Class<*>, generator: AlchemyGenerator<*>) : AlchemyGenerator<*>
    {
        val fieldName = field?.name ?: ""

        return when (typeOfField)
        {
            String::class.java ->
            {
                when (fieldName)
                {
                    "name", "firstName", "lastName" -> PeopleGenerators.names()
                    "email"                         -> PeopleGenerators.emails()
                    "city"                          -> PlaceGenerators.cities()
                    "country"                       -> PlaceGenerators.countries()
                    else                            -> generator
                }
            }

            Double::class.java ->
            {
                when (fieldName)
                {
                    "latitude"  -> GeolocationGenerators.latitudes()
                    "longitude" -> GeolocationGenerators.longitudes()
                    else        -> generator
                }
            }

            Int::class.java ->
            {
                when (fieldName)
                {
                    "age" -> PeopleGenerators.adultAges()
                    else  -> generator
                }
            }

            else -> generator
        }
    }

    private fun generatorForEnumType(typeOfField: Class<*>): AlchemyGenerator<*>?
    {
        val enumValues = typeOfField.enumConstants

        if (enumValues == null)
        {
            LOG.warn("Enum Class {} has no Enum Values: " + typeOfField)
            return null
        }

        return AlchemyGenerator()
        {
            val position = one(integers(0, enumValues.size))
            enumValues[position]
        }
    }

    private fun generatorForCollectionType(field: Field?, parameter: Parameter? = null, typeOfField: Class<*>, generatorMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<*>?
    {
        if (field != null)
        {

            if (field.lacksGenericTypeArguments())
            {
                LOG.warn("POJO {} contains a Collection field {} which is not type-parametrized. Cannot inject.",
                         field.declaringClass,
                         field)

                return null
            }

            return determineGeneratorForCollectionField(field, typeOfField, generatorMappings)
        }
        else if (parameter != null)
        {
            if (parameter.parameterizedType !is ParameterizedType)
            {
                LOG.warn("POJO $typeOfField contains a Collection parameter $parameter which is not type-parameterized. Cannot inject.")
                return null
            }

            return determineGeneratorForCollectionParameter(collectionParameter = parameter,
                                                            collectionType = typeOfField,
                                                            generatorMappings = generatorMappings)
        }
        else
        {
            LOG.warn("Cannot Instantiate: No generic information available in order to generate values for $typeOfField")
            return null
        }

    }

    private fun isCollectionType(type: Class<*>): Boolean
    {
        return isListType(type) or
               isSetType(type) or
               isMapType(type)
    }

    private fun isListType(type: Class<*>): Boolean
    {
        return List::class.java.isAssignableFrom(type)
    }

    private fun isSetType(type: Class<*>): Boolean
    {
        return Set::class.java.isAssignableFrom(type)
    }

    private fun isMapType(type: Class<*>): Boolean
    {
        return Map::class.java.isAssignableFrom(type)
    }

    private fun Field.lacksGenericTypeArguments(): Boolean
    {
        return genericType !is ParameterizedType
    }

    private fun determineGeneratorForCollectionField(collectionField: Field,
                                                     collectionType: Class<*>,
                                                     generatorMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<*>?
    {
        if (isMapType(collectionType))
        {
            return determineGeneratorForMapField(collectionField, generatorMappings)
        }

        val parameterizedType = collectionField.genericType as ParameterizedType
        val valueType = parameterizedType.actualTypeArguments.firstOrNull() as? Class<*> ?: return null

        return determineGeneratorForCollectionWithValueType(valueType, collectionType, generatorMappings)
    }

    private fun determineGeneratorForCollectionParameter(collectionParameter: Parameter,
                                                        collectionType: Class<*> = collectionParameter.type,
                                                        generatorMappings: Map<Class<*>, AlchemyGenerator<*>>) : AlchemyGenerator<*>?
    {
        if (isMapType(collectionType))
        {
            return determineGeneratorForMapParameter(collectionParameter, collectionType, generatorMappings)
        }

        val parameterizedType = collectionParameter.parameterizedType as? ParameterizedType ?: return null
        val actualType = parameterizedType.actualTypeArguments.firstOrNull()
        val name = actualType?.typeName
        val valueType = actualType as? Class<*>
                        ?: tryToDetermineClassFrom(actualType)
                        ?: return null

        return determineGeneratorForCollectionWithValueType(valueType = valueType,
                                                            collectionType = collectionType,
                                                            generatorMappings = generatorMappings)
    }

    private fun tryToDetermineClassFrom(actualType: Type?): Class<*>?
    {
        return when
        {
            actualType == null -> null

            actualType is WildcardType && actualType.upperBounds.isNotEmpty() ->
            {
                val className = actualType.typeName.removePrefix("? extends ")
                tryToLoadClass(className)
            }

            actualType is WildcardType && actualType.lowerBounds.isNotEmpty() ->
            {
                val className = actualType.typeName.removePrefix("? super ")
                tryToLoadClass(className)
            }

            else -> return null
        }
    }

    private fun tryToLoadClass(name: String): Class<*>?
    {
        return try
        {
            this.javaClass.classLoader.loadClass(name)
        }
        catch(ex: Throwable)
        {
            return null
        }
    }

    private fun determineGeneratorForCollectionWithValueType(valueType: Class<*>, collectionType: Class<*>, generatorMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<*>?
    {

        val generator = determineGeneratorFor(typeOfField = valueType, generatorMappings = generatorMappings) ?: return null

        val size = one(integers(3, 25))

        if (isSetType(collectionType))
        {
            return AlchemyGenerator()
            {
                List(size) { generator.get() }.toSet()
            }
        }

        return AlchemyGenerator()
        {
            List(size) { generator.get() }
        }
    }

    private fun determineGeneratorForMapField(mapField: Field,
                                              generatorMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<*>?
    {
        val parameterizedType = mapField.genericType as ParameterizedType
        val keyType = parameterizedType.actualTypeArguments[0] as Class<*>
        val valueType = parameterizedType.actualTypeArguments[1] as Class<*>

        val keyGenerator = determineGeneratorFor(field = mapField,
                                                 parameter = null,
                                                 typeOfField = keyType,
                                                 generatorMappings = generatorMappings) ?: return null

        val valueGenerator = determineGeneratorFor(field = mapField,
                                                   parameter = null,
                                                   typeOfField = valueType,
                                                   generatorMappings = generatorMappings) ?: return null

        return AlchemyGenerator()
        {
            val map = mutableMapOf<Any, Any>()
            val size = one(integers(3, 25))

            for (i in 0 until size)
            {
                map[keyGenerator.get()] = valueGenerator.get()
            }

            map
        }
    }


    private fun determineGeneratorForMapParameter(mapParameter: Parameter,
                                                  collectionType: Class<*>,
                                                  generatorMappings: Map<Class<*>, AlchemyGenerator<*>>) : AlchemyGenerator<*>?
    {
        val parameterizedType = mapParameter.parameterizedType as? ParameterizedType ?: return null
        val keyType = parameterizedType.actualTypeArguments[0] as? Class<*> ?: return null
        val valueType = parameterizedType.actualTypeArguments[1] as? Class<*> ?: return null

        val keyGenerator = determineGeneratorFor(field = null,
                                                 parameter = mapParameter,
                                                 typeOfField = keyType,
                                                 generatorMappings = generatorMappings) ?: return null

        val valueGenerator = determineGeneratorFor(field = null,
                                                   parameter = null,
                                                   typeOfField = valueType,
                                                   generatorMappings = generatorMappings) ?: return null

        return AlchemyGenerator()
        {
            val map = mutableMapOf<Any, Any>()
            val size = one(integers(3, 25))

            for (i in 0 until size)
            {
                map[keyGenerator.get()] = valueGenerator.get()
            }

            map
        }
    }

    private fun isStatic(field: Field): Boolean
    {
        val modifiers = field.modifiers
        return Modifier.isStatic(modifiers)
    }

    private fun isFinal(field: Field): Boolean
    {
        val modifiers = field.modifiers
        return Modifier.isFinal(modifiers)
    }

    private fun isEnumType(typeOfField: Class<*>): Boolean
    {
        return typeOfField.isEnum
    }

    private val Class<*>.firstAvailableConstructor: Constructor<*>
        get()
        {
            return this.constructors.find { it.hasNoParameters() } ?:
                   this.constructors.firstOrNull() ?:
                   this.getDeclaredConstructor()
        }

    private fun Constructor<*>.hasNoParameters(): Boolean
    {
        return this.parameterCount == 0
    }

    private fun <T> isPrimitiveClass(classOfPojo: Class<T>): Boolean
    {
        if (classOfPojo.isPrimitive)
        {
            return true
        }

        val otherPrimitives = setOf(String::class.java,
                                    Date::class.java,
                                    Instant::class.java)

        if (otherPrimitives.contains(classOfPojo))
        {
            return true
        }

        return false
    }
}