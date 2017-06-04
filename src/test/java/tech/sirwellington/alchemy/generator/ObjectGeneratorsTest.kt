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

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.isEmptyOrNullString
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.startsWith
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one
import tech.sirwellington.alchemy.generator.StringGenerators.Companion.strings
import tech.sirwellington.alchemy.generator.Throwables.assertThrows
import java.net.URL
import java.nio.ByteBuffer
import java.time.Instant
import java.util.*
import java.util.function.Consumer

/**

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

        doInLoop { i ->
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

        doInLoop { i ->
            val result = generator.get()
            checkPerson(result)
        }
    }

    @Test
    fun testWithCollectionInPojo()
    {
        println("testWithCollectionInPojo")

        val generator = ObjectGenerators.pojos(Building::class.java)
        assertThat<AlchemyGenerator<Building>>(generator, notNullValue())

        doInLoop { i ->
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

        doInLoop { i ->
            val result = generator.get()
            checkAddressBook(result)
        }
    }

    @Test
    fun testComplexPojo()
    {
        println("testComplexPojo")

        val generator = ObjectGenerators.pojos(CityBlock::class.java)
        assertThat<AlchemyGenerator<CityBlock>>(generator, notNullValue())

        val result = generator.get()
        checkCityBlock(result)
    }

    @Test
    fun testPojosRejectsPrimitives()
    {
        println("testPojosRejectsPrimitives")

        val primitives = HashSet<Class<*>>()
        primitives.add(Int::class.java)
        primitives.add(Double::class.java)
        primitives.add(Long::class.java)
        primitives.add(Char::class.java)
        primitives.add(String::class.java)
        primitives.add(Date::class.java)
        primitives.add(Instant::class.java)

        primitives.forEach { p ->
            assertThrows { ObjectGenerators.pojos(p) }
                    .isInstanceOf(IllegalArgumentException::class.java)
        }

    }

    @Test
    fun testPojosRejectsNonInstantiables()
    {
        println("testPojosRejectsNonInstantiables")

        class ExampleNonInstantiable(argument: String)

        assertThrows { ObjectGenerators.pojos(ExampleNonInstantiable::class.java) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun testPojosCustom()
    {
        println("testPojosCustom")
    }

    private fun checkPerson(person: Person)
    {
        assertThat(person, notNullValue())
        assertThat(person.name, not(isEmptyOrNullString()))
        assertThat(person.middleName, not(isEmptyOrNullString()))
        assertThat(person.age, not(0))
        assertThat(person.money, not(0.0))
        assertThat<URL>(person.website, notNullValue())
        assertThat(person.website!!.toString(), startsWith("http"))
        assertThat(Person.staticField.toString(), `is`(ObjectGeneratorsTest.staticField))
        checkComputer(person.computer!!)
    }

    private fun checkComputer(computer: Computer)
    {
        assertThat(computer, notNullValue())
        assertThat<String>(computer.name, not(isEmptyOrNullString()))
        assertThat<String>(computer.model, not(isEmptyOrNullString()))
        assertThat<String>(computer.manufacturer, not(isEmptyOrNullString()))
        assertThat(computer.year, greaterThan(0))
        assertThat(computer.cost, greaterThan(0.0))

        assertThat<ByteArray>(computer.data, notNullValue())
        assertThat(computer.data!!.size, greaterThan(0))
    }

    private fun checkBuilding(building: Building)
    {
        assertThat(building, notNullValue())
        assertThat<String>(building.address, not(isEmptyOrNullString()))
        assertThat(building.age, greaterThan(0))
        assertThat(building.floors, greaterThan(0))
        assertThat<List<Person>>(building.people, notNullValue())
        assertThat(building.people!!.size, greaterThan(0))
        building.people.forEach(Consumer<Person> { this.checkPerson(it) })
    }

    private fun checkAddressBook(addressBook: AddressBook)
    {
        assertThat(addressBook, notNullValue())
        assertThat<Map<String, Building>>(addressBook.directory, notNullValue())
        assertThat(addressBook.directory!!.size, greaterThan(0))
        addressBook.directory.keys.forEach { key -> assertThat(key, not(isEmptyOrNullString())) }
        addressBook.directory.values.forEach(Consumer<Building> { this.checkBuilding(it) })
    }

    private fun checkCityBlock(cityBlock: CityBlock)
    {
        assertThat(cityBlock, notNullValue())
        assertThat<String>(cityBlock.name, not(isEmptyOrNullString()))
        assertThat(cityBlock.distance, greaterThan(0))
        assertThat<State>(cityBlock.state, notNullValue())
        assertThat<ByteBuffer>(cityBlock.memory, notNullValue())
        assertThat<Boolean>(cityBlock.isNearOcean, notNullValue())
        assertThat<Byte>(cityBlock.code, notNullValue())

        assertThat<List<Building>>(cityBlock.homes, notNullValue())
        assertThat<List<Building>>(cityBlock.stores, notNullValue())
        assertThat<Map<Person, Computer>>(cityBlock.internetUsers, notNullValue())

        assertThat(cityBlock.homes!!.size, greaterThan(0))
        assertThat(cityBlock.stores!!.size, greaterThan(0))
        assertThat(cityBlock.internetUsers!!.size, greaterThan(0))
        assertThat(cityBlock.memory!!.limit(), greaterThan(0))

        cityBlock.homes.forEach(Consumer<Building> { this.checkBuilding(it) })
        cityBlock.stores.forEach(Consumer<Building> { this.checkBuilding(it) })
        cityBlock.internetUsers.keys.forEach(Consumer<Person> { this.checkPerson(it) })
        cityBlock.internetUsers.values.forEach(Consumer<Computer> { this.checkComputer(it) })
    }

    private class Computer
    {

        val name: String? = null
        val model: String? = null
        val year: Int = 0
        val manufacturer: String? = null
        val cost: Double = 0.toDouble()
        val data: ByteArray? = null

    }

    private class Person
    {
        var name: String? = null
        var age: Int = 0
        val money: Double = 0.toDouble()
        val middleName: String? = null
        val computer: Computer? = null
        val website: URL? = null

        companion object
        {

            val staticField = ObjectGeneratorsTest.staticField
        }
    }

    private class Building
    {

        val people: List<Person>? = null
        val address: String? = null
        val age: Int = 0
        val floors: Int = 0
    }

    private class AddressBook
    {

        val directory: Map<String, Building>? = null
    }

    private enum class State
    {
        NY,
        NJ,
        CA
    }

    private class CityBlock
    {
        val name: String? = null
        val distance: Int = 0
        val homes: List<Building>? = null
        val stores: List<Building>? = null
        val internetUsers: Map<Person, Computer>? = null
        val state: State? = null
        val memory: ByteBuffer? = null
        val isNearOcean: Boolean? = null
        val code: Byte? = null
    }

    companion object
    {

        val staticField = one(strings())
    }
}
