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

package tech.sirwellington.alchemy.generator

import org.apache.commons.lang3.ClassUtils
import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.annotations.access.NonInstantiable
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.CONCRETE_BEHAVIOR
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.net.URL
import java.nio.ByteBuffer
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.Set
import kotlin.collections.filter
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toList

/**
 * Contains Convenience Generators for POJOs (Plain-Old-Java-Objects).
 * Use at your own risk.

 * @author SirWellington
 */
@NonInstantiable
@StrategyPattern(role = CONCRETE_BEHAVIOR)
class ObjectGenerators
@Throws(IllegalAccessException::class)
internal constructor()
{

    init
    {
        throw IllegalAccessException("cannot instantiate this class")
    }

    companion object
    {

        private val LOG = LoggerFactory.getLogger(ObjectGenerators::class.java)

        private val DEFAULT_GENERATOR_MAPPINGS = ConcurrentHashMap<Class<*>, AlchemyGenerator<*>>()

        init
        {
            DEFAULT_GENERATOR_MAPPINGS.put(String::class.java, alphabeticString())
            DEFAULT_GENERATOR_MAPPINGS.put(Int::class.java, smallPositiveIntegers())
            DEFAULT_GENERATOR_MAPPINGS.put(Long::class.java, positiveLongs())
            DEFAULT_GENERATOR_MAPPINGS.put(Double::class.java, positiveDoubles())
            DEFAULT_GENERATOR_MAPPINGS.put(Date::class.java, DateGenerators.anyTime())
            DEFAULT_GENERATOR_MAPPINGS.put(Instant::class.java, TimeGenerators.anytime())
            DEFAULT_GENERATOR_MAPPINGS.put(ByteBuffer::class.java, BinaryGenerators.byteBuffers(333))
            DEFAULT_GENERATOR_MAPPINGS.put(Boolean::class.java, BooleanGenerators.booleans())
            DEFAULT_GENERATOR_MAPPINGS.put(Byte::class.java, BinaryGenerators.bytes())
            DEFAULT_GENERATOR_MAPPINGS.put(URL::class.java, NetworkGenerators.httpUrls())
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
         *  *  Non-Static
         *  *  Non-Final
         *  *  Primitive type: Integer, Double, etc
         *  *  [String] Type
         *  *  [enum][Enum] Type
         *  *  [Date] Type
         *  *  [Instant] Type
         *  *  Another `POJO` That satisfies these rules (embedded Object)
         *  *  Non-Circular (Cannot contain circular references). A Stack Overflow will occur otherwise.
         *  *  A [List] with a Type Parameter matching the above.
         *  *  A [Set] with a Type Parameter matching the above.
         *  *  A [Map] with Type Parameters matching the above conditions.
         *
         *
         *
         * Valid Examples:
         * <pre>
         * `private class Computer
         * {
         * private Date releaseDate;
         * private String name;
         * private String manufacturer;
         * private double cost;
         * }

         * private class Person
         * {
         * private String name;
         * private int age;
         * private double money;
         * private Computer computer;
         * }

         * private class Company
         * {

         * private String name;
         * private int numberOfEmployees;
         * private List<Person> employees;
         * }

         * private class CompanyIndex
         * {
         * private String indexName;
         * private Map<String, Company> index;
         * }
        ` *
        </pre> *
         * @param <T>
         * *
         * @param classOfPojo
         * *
         * @return
         * *
         * @see StringGenerators

         * @see NumberGenerators

         * @see DateGenerators

         * @see TimeGenerators
        </T> */
        fun <T : Any> pojos(classOfPojo: Class<T>): AlchemyGenerator<T?>
        {
            return pojos(classOfPojo, DEFAULT_GENERATOR_MAPPINGS)
        }

        fun <T : Any> pojos(classOfPojo: Class<T>, customMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<T?>
        {
            checkNotNull(classOfPojo, "missing class of POJO")
            checkThat(canInstantiate(classOfPojo), "cannot instantiate class: " + classOfPojo)
            checkThat(!isPrimitiveClass(classOfPojo), "Cannot use pojos with Primitive Type. Use one of the Primitive generators instead.")

            val validFields = classOfPojo.declaredFields
                    .filter { f -> !isStatic(f) }
                    .filter { f -> !isFinal(f) }
                    .toList()

            return AlchemyGenerator result@ {

                val instance: T

                try
                {
                    instance = instantiate(classOfPojo)
                }
                catch (ex: Exception)
                {
                    LOG.error("Failed to instantiate {}", classOfPojo.name, ex)
                    return@result null
                }

                for (field in validFields)
                {
                    tryInjectField(instance, field, customMappings)
                }

                return@result instance
            }

        }

        private fun <T> canInstantiate(classOfPojo: Class<T>): Boolean
        {
            try
            {
                instantiate(classOfPojo)
                return true
            }
            catch (ex: Exception)
            {
                LOG.warn("cannot instatiate type {}", classOfPojo)
                return false
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

        @Throws(NoSuchMethodException::class, InstantiationException::class, IllegalAccessException::class, IllegalArgumentException::class, InvocationTargetException::class)
        private fun <T> instantiate(classOfT: Class<T>): T
        {
            val defaultConstructor = classOfT.getDeclaredConstructor()
            val originalAccessibility = defaultConstructor.isAccessible
            try
            {
                defaultConstructor.isAccessible = true
                return defaultConstructor.newInstance()
            }
            finally
            {
                defaultConstructor.isAccessible = originalAccessibility
            }
        }

        private fun <T> isPrimitiveClass(classOfPojo: Class<T>): Boolean
        {
            if (classOfPojo.isPrimitive)
            {
                return true
            }

            val otherPrimitives = HashSet<Class<*>>()
            otherPrimitives.add(String::class.java)
            otherPrimitives.add(Date::class.java)
            otherPrimitives.add(Instant::class.java)

            if (otherPrimitives.contains(classOfPojo))
            {
                return true
            }

            return false
        }

        private fun <T : Any> tryInjectField(instance: T, field: Field, generatorMappings: Map<Class<*>, AlchemyGenerator<*>>)
        {
            try
            {
                injectField(instance, field, generatorMappings)
            }
            catch (ex: IllegalAccessException)
            {
                LOG.warn("Could not inject field {}", field.toString(), ex)
            }
            catch (ex: IllegalArgumentException)
            {
                LOG.warn("Could not inject field {}", field.toString(), ex)
            }

        }

        @Throws(IllegalArgumentException::class, IllegalAccessException::class)
        private fun injectField(pojo: Any, field: Field, generatorMappings: Map<Class<*>, AlchemyGenerator<*>>)
        {
            var typeOfField = field.type
            typeOfField = ClassUtils.primitiveToWrapper(typeOfField)

            val generator = determineGeneratorFor(typeOfField, field, generatorMappings)

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


        private fun determineGeneratorFor(typeOfField: Class<*>, field: Field, generatorMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<*>?
        {
            var generator: AlchemyGenerator<*>? = generatorMappings[typeOfField]

            if (generator != null)
            {
                //Already found it
                return generator
            }

            if (isCollectionType(typeOfField))
            {

                if (lacksGenericTypeArguments(field))
                {
                    LOG.warn("POJO {} contains a Collection field {} which is not type-parametrized. Cannot inject.",
                             field.declaringClass,
                             field)

                    return null
                }

                generator = determineGeneratorForCollectionField(field, typeOfField, generatorMappings)
            }
            else if (isEnumType(typeOfField))
            {
                val enumValues = typeOfField.enumConstants

                if (enumValues == null)
                {
                    LOG.warn("Enum Class {} has no Enum Values: " + typeOfField)
                    return null
                }

                generator = AlchemyGenerator {
                    val position = one(NumberGenerators.integers(0, enumValues.size))
                    enumValues[position]
                }
            }
            else
            {
                //Assume Pojo and recurse
                generator = pojos(typeOfField)
            }

            return generator
        }

        private fun isCollectionType(type: Class<*>): Boolean
        {
            return isListType(type) ||
                   isSetType(type) ||
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


        private fun lacksGenericTypeArguments(field: Field): Boolean
        {
            val genericType = field.genericType

            return genericType !is ParameterizedType
        }

        private fun determineGeneratorForCollectionField(collectionField: Field,
                                                         collectionType: Class<*>,
                                                         generatorMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<*>?
        {
            if (isMapType(collectionType))
            {
                return determineGeneratorForMapField(collectionField, collectionType, generatorMappings)
            }

            val parameterizedType = collectionField.genericType as ParameterizedType
            val valueType = parameterizedType.actualTypeArguments[0] as Class<*>

            val generator = generatorMappings[valueType] ?: determineGeneratorFor(valueType, collectionField, generatorMappings) ?: return null

            val list = ArrayList<Any>()
            val size = one(NumberGenerators.integers(10, 100))

            for (i in 0..size - 1)
            {
                list.add(generator.get())
            }

            if (isSetType(collectionType))
            {
                val set = HashSet(list)
                return AlchemyGenerator { set }
            }

            return AlchemyGenerator { list }
        }

        private fun determineGeneratorForMapField(mapField: Field,
                                                  mapType: Class<*>,
                                                  generatorMappings: Map<Class<*>, AlchemyGenerator<*>>): AlchemyGenerator<*>?
        {
            val parameterizedType = mapField.genericType as ParameterizedType
            val keyType = parameterizedType.actualTypeArguments[0] as Class<*>
            val valueType = parameterizedType.actualTypeArguments[1] as Class<*>

            val keyGenerator = generatorMappings[keyType] ?: determineGeneratorFor(keyType, mapField, generatorMappings) ?: return null
            val valueGenerator = generatorMappings[valueType] ?: determineGeneratorFor(valueType, mapField, generatorMappings) ?: return null

            val map = mutableMapOf<Any, Any>()
            val size = one(integers(10, 100))

            for (i in 0..size - 1)
            {
                map[keyGenerator.get()] = valueGenerator.get()
            }

            return AlchemyGenerator { map }
        }

        private fun isEnumType(typeOfField: Class<*>): Boolean
        {
            return typeOfField.isEnum
        }
    }

}
