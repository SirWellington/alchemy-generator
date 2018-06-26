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

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.isEmptyOrNullString
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.startsWith
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.strings
import java.net.URL
import java.nio.ByteBuffer
import java.time.Instant
import java.util.Date
import java.util.function.Consumer
import kotlin.test.assertFalse

/**
 *
 * @author SirWellington
 */
class ObjectGeneratorsTest
{

    @Before
    fun setUp()
    {
    }

    @Test
    fun testWithSimplePojo()
    {
        println("testWithSimplePojo")

        val generator = ObjectGenerators.pojos(Computer::class.java)
        assertThat(generator, notNullValue())

        doInLoop {
            val computer = generator.get()
            checkComputer(computer)
        }


    }

    @Test
    fun testWithNestedPojo()
    {
        println("testWithNestedPojo")

        val generator = ObjectGenerators.pojos(Person::class.java)
        assertThat(generator, notNullValue())

        doInLoop {
            val result = generator.get()
            checkPerson(result)
        }
    }

    @Test
    fun testWithCollectionInPojo()
    {
        println("testWithCollectionInPojo")

        val generator = ObjectGenerators.pojos(Building::class.java)
        assertThat(generator, notNullValue())

        doInLoop {
            val result = generator.get()
            checkBuilding(result)
        }

    }

    @Test
    fun testPojosWithMap()
    {
        println("testPojosWithMap")

        val generator = ObjectGenerators.pojos(AddressBook::class.java)
        assertThat<AlchemyGenerator<AddressBook>>(generator, notNullValue())

        doInLoop {
            val result = generator.get()
            checkAddressBook(result)
        }
    }

    @Test
    fun testComplexPojo()
    {
        println("testComplexPojo")

        val generator = ObjectGenerators.pojos(CityBlock::class.java)
        assertThat(generator, notNullValue())

        val result = generator.get()
        checkCityBlock(result)
    }

    @Test
    fun testPojosHandlesPrimitives()
    {
        println("testPojosHandlesPrimitives")

        val primitives = setOf(Int::class.java,
                               Double::class.java,
                               Long::class.java,
                               String::class.java,
                               Date::class.java,
                               Instant::class.java,
                               Char::class.java,
                               Short::class.java)

        primitives.forEach { p ->
            val generator = ObjectGenerators.pojos(p)
            assertThat(generator, notNullValue())

            val value = generator.get()
            assertThat(value, notNullValue())
        }

    }

    @Test
    fun testPojosHandlesNonDefaultConstructor()
    {
        println("testPojosHandlesNonDefaultConstructor")

        class ExampleNonDefaultConstructor(val argument: String)

        val generator = ObjectGenerators.pojos(ExampleNonDefaultConstructor::class.java)

        val instance = generator.get()

        assertThat(instance, notNullValue())
        assertThat(instance.argument, not(isEmptyOrNullString()))
    }

    @Test
    fun testWithJavaCode()
    {
        println("testWithJavaCode")

        val generator = ObjectGenerators.pojos(JavaCode.Person::class.java)

        doInLoop {
            val result = generator.get()
            JavaCode.Person.check(result)
        }
    }

    @Test
    fun testWithDataClass()
    {
        println("testWithDataClass")

        val generator = ObjectGenerators.pojos(Band::class.java)

        doInLoop {
            val result = generator.get()
            result.check()
        }
    }


    @Test
    fun testWithAnotherDataClass()
    {
        data class Holder(val string: String,
                          val number: Int,
                          val strings: List<String>)

        val generator = ObjectGenerators.pojos<Holder>()

        doInLoop {
            val result = generator.get()
            assertThat(result, notNullValue())
        }
    }

    @Test
    fun testWithADataClassThatContainsAMap()
    {
        data class Holder(val string: String,
                          val number: Int,
                          val map: Map<String, Computer>)

        val generator = ObjectGenerators.pojos<Holder>()

        doInLoop {
            val result = generator.get()
            assertThat(result, notNullValue())
        }
    }

    /**
     * This is a very complex generator.
     * If you want this level of sophistication, you're going to have to generated these things on
     * your own.
     */
    @Ignore
    @Test
    fun testWithADataClassThatContainsAMapOfAList()
    {
        data class Holder(val string: String,
                          val number: Int,
                          val map: Map<String, List<Computer>>)

        val generator = ObjectGenerators.pojos<Holder>()

        val result = generator.get()
        assertThat(result, notNullValue())
    }

    private fun checkPerson(person: Person)
    {
        assertThat(person, notNullValue())
        assertThat(person.name, not(isEmptyOrNullString()))
        assertThat(person.middleName, not(isEmptyOrNullString()))
        assertThat(person.age, not(0))
        assertThat(person.money, not(0.0))
        assertThat(person.website, notNullValue())
        assertThat(person.website.toString(), startsWith("http"))
        assertThat(Person.staticField.toString(), `is`(ObjectGeneratorsTest.staticField))
        checkComputer(person.computer)
    }

    private fun checkComputer(computer: Computer)
    {
        assertThat(computer, notNullValue())
        assertThat(computer.name, not(isEmptyOrNullString()))
        assertThat(computer.model, not(isEmptyOrNullString()))
        assertThat(computer.manufacturer, not(isEmptyOrNullString()))
        assertThat(computer.year, greaterThan(0))
        assertThat(computer.cost, greaterThan(0.0))

        assertThat(computer.data, notNullValue())
        assertThat(computer.data.size, greaterThan(0))
    }

    private fun checkBuilding(building: Building)
    {
        assertThat(building, notNullValue())
        assertThat(building.address, not(isEmptyOrNullString()))
        assertThat(building.age, greaterThan(0))
        assertThat(building.floors, greaterThan(0))
        assertThat(building.people, notNullValue())
        assertThat(building.people.size, greaterThan(0))
        building.people.forEach { this.checkPerson(it) }
    }

    private fun checkAddressBook(addressBook: AddressBook)
    {
        assertThat(addressBook, notNullValue())
        assertThat(addressBook.directory, notNullValue())
        assertThat(addressBook.directory.size, greaterThan(0))
        addressBook.directory.keys.forEach { key -> assertThat(key, not(isEmptyOrNullString())) }
        addressBook.directory.values.forEach(Consumer<Building> { this.checkBuilding(it) })
    }

    private fun checkCityBlock(cityBlock: CityBlock)
    {
        assertThat(cityBlock, notNullValue())
        assertThat(cityBlock.name, not(isEmptyOrNullString()))
        assertThat(cityBlock.distance, greaterThan(0))
        assertThat(cityBlock.state, notNullValue())
        assertThat(cityBlock.memory, notNullValue())
        assertThat(cityBlock.isNearOcean, notNullValue())
        assertThat(cityBlock.code, notNullValue())

        assertThat(cityBlock.homes, notNullValue())
        assertThat(cityBlock.stores, notNullValue())
        assertThat(cityBlock.internetUsers, notNullValue())

        assertThat(cityBlock.homes.size, greaterThan(0))
        assertThat(cityBlock.stores.size, greaterThan(0))
        assertThat(cityBlock.internetUsers.size, greaterThan(0))
        assertThat(cityBlock.memory.limit(), greaterThan(0))

        cityBlock.homes.forEach { this.checkBuilding(it) }
        cityBlock.stores.forEach { this.checkBuilding(it) }
        cityBlock.internetUsers.keys.forEach { this.checkPerson(it) }
        cityBlock.internetUsers.values.forEach { this.checkComputer(it) }
    }

    private class Computer
    {
        lateinit var name: String
        lateinit var model: String
        var year: Int = 0
        lateinit var manufacturer: String
        var cost: Double = 0.0
        lateinit var data: ByteArray

    }

    private class Person
    {
        lateinit var name: String
        var age: Int = 0
        var money: Double = 0.toDouble()
        lateinit var middleName: String
        lateinit var computer: Computer
        lateinit var website: URL

        companion object
        {

            val staticField = ObjectGeneratorsTest.staticField
        }
    }

    private class Building
    {

        lateinit var people: List<Person>
        lateinit var address: String
        var age: Int = 0
        var floors: Int = 0
    }

    private class AddressBook
    {

        lateinit var directory: Map<String, Building>
    }

    private enum class State
    {
        NY,
        NJ,
        CA
    }

    private class CityBlock
    {
        lateinit var name: String
        var distance: Int = 0
        lateinit var homes: List<Building>
        lateinit var stores: List<Building>
        lateinit var internetUsers: Map<Person, Computer>
        lateinit var state: State
        lateinit var memory: ByteBuffer
        var isNearOcean: Boolean? = null
        var code: Byte? = null
    }

    companion object
    {

        val staticField = one(strings())
    }

    private data class Band(val name: String,
                            val fans: Int,
                            val money: Double,
                            val city: String,
                            val data: ByteArray,
                            val onTour: Boolean,
                            val workstation: Computer)

    private fun Band?.check()
    {
        assertThat(this, notNullValue())
        if (this == null) return

        assertThat(name, not(isEmptyOrNullString()))
        assertThat(fans, greaterThan(0))
        assertThat(money, greaterThan(0.0))
        assertThat(city, not(isEmptyOrNullString()))
        assertFalse { data.isEmpty() }
        assertThat(workstation, notNullValue())
        checkComputer(workstation)
    }
}
