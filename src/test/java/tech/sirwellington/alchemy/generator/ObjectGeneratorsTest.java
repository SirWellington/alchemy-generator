
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

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.StringGenerators.strings;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 *
 * @author SirWellington
 */
public class ObjectGeneratorsTest
{
    
    private static final String staticField = one(strings());
    
    @Before
    public void setUp()
    {
    }
    
    @Test
    public void testWithSimplePojo()
    {
        System.out.println("testWithSimplePojo");
        
        AlchemyGenerator<Person> generator = ObjectGenerators.pojos(Person.class);
        assertThat(generator, notNullValue());
        
        Tests.doInLoop(i ->
        {
            Person result = generator.get();
            checkPerson(result);
        });
    }
    
    @Test
    public void testWithNestedPojo()
    {
        System.out.println("testWithNestedPojo");
        
        AlchemyGenerator<Computer> generator = ObjectGenerators.pojos(Computer.class);
        assertThat(generator, notNullValue());
        
        Tests.doInLoop(i ->
        {
            Computer computer = generator.get();
            checkComputer(computer);
        });
    }
    
    @Test
    public void testWithCollectionInPojo()
    {
        System.out.println("testWithCollectionInPojo");
        
        AlchemyGenerator<Building> generator = ObjectGenerators.pojos(Building.class);
        assertThat(generator, notNullValue());
        
        Tests.doInLoop(i ->
        {
            Building result = generator.get();
            checkBuilding(result);
        });
        
    }
    
    @Test
    public void testPojosWithMap()
    {
        System.out.println("testPojosWithMap");
        
        AlchemyGenerator<AddressBook> generator = ObjectGenerators.pojos(AddressBook.class);
        assertThat(generator, notNullValue());
        
        Tests.doInLoop(i ->
        {
            AddressBook result = generator.get();
            checkAddressBook(result);
        });
    }
    
    @Test
    public void testComplexPojo()
    {
        System.out.println("testComplexPojo");
        
        AlchemyGenerator<CityBlock> generator = ObjectGenerators.pojos(CityBlock.class);
        assertThat(generator, notNullValue());

        CityBlock result = generator.get();
        checkCityBlock(result);
    }
    
    @Test
    public void testPojosRejectsPrimitives()
    {
        System.out.println("testPojosRejectsPrimitives");
        
        Set<Class<?>> primitives = new HashSet<>();
        primitives.add(Integer.class);
        primitives.add(Double.class);
        primitives.add(Long.class);
        primitives.add(Character.class);
        primitives.add(String.class);
        primitives.add(Date.class);
        primitives.add(Instant.class);
        
        primitives.forEach(p ->
        {
            assertThrows(() -> ObjectGenerators.pojos(p))
                .isInstanceOf(IllegalArgumentException.class);
        });
        
    }
    
    @Test
    public void testPojosRejectsNonInstantiables()
    {
        System.out.println("testPojosRejectsNonInstantiables");
        
        class ExampleNonInstantiable
        {
            
            public ExampleNonInstantiable(String argument)
            {
            }
        }
        
        assertThrows(() -> ObjectGenerators.pojos(ExampleNonInstantiable.class))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testPojosCustom()
    {
        System.out.println("testPojosCustom");
    }
    
    private void checkPerson(Person person)
    {
        assertThat(person, notNullValue());
        assertThat(person.name, not(isEmptyOrNullString()));
        assertThat(person.middleName, not(isEmptyOrNullString()));
        assertThat(person.age, not(0));
        assertThat(person.money, not(0.0));
        assertThat(valueOf(Person.staticField), is(ObjectGeneratorsTest.staticField));
        checkComputer(person.computer);
    }
    
    private void checkComputer(Computer computer)
    {
        assertThat(computer, notNullValue());
        assertThat(computer.name, not(isEmptyOrNullString()));
        assertThat(computer.model, not(isEmptyOrNullString()));
        assertThat(computer.manufacturer, not(isEmptyOrNullString()));
        assertThat(computer.year, greaterThan(0));
        assertThat(computer.cost, greaterThan(0.0));
    }
    
    private void checkBuilding(Building building)
    {
        assertThat(building, notNullValue());
        assertThat(building.address, not(isEmptyOrNullString()));
        assertThat(building.age, greaterThan(0));
        assertThat(building.floors, greaterThan(0));
        assertThat(building.people, notNullValue());
        assertThat(building.people.size(), greaterThan(0));
        building.people.forEach(this::checkPerson);
    }
    
    private void checkAddressBook(AddressBook addressBook)
    {
        assertThat(addressBook, notNullValue());
        assertThat(addressBook.directory, notNullValue());
        assertThat(addressBook.directory.size(), greaterThan(0));
        addressBook.directory.keySet().forEach(key ->
        {
            assertThat(key, not(isEmptyOrNullString()));
        });
        addressBook.directory.values().forEach(this::checkBuilding);
    }
    
    private void checkCityBlock(CityBlock cityBlock)
    {
        assertThat(cityBlock, notNullValue());
        assertThat(cityBlock.name, not(isEmptyOrNullString()));
        assertThat(cityBlock.distance, greaterThan(0));
        assertThat(cityBlock.state, notNullValue());
        assertThat(cityBlock.memory, notNullValue());
        assertThat(cityBlock.isNearOcean, notNullValue());
        
        assertThat(cityBlock.homes, notNullValue());
        assertThat(cityBlock.stores, notNullValue());
        assertThat(cityBlock.internetUsers, notNullValue());
        
        assertThat(cityBlock.homes.size(), greaterThan(0));
        assertThat(cityBlock.stores.size(), greaterThan(0));
        assertThat(cityBlock.internetUsers.size(), greaterThan(0));
        assertThat(cityBlock.memory.limit(), greaterThan(0));
        
        cityBlock.homes.forEach(this::checkBuilding);
        cityBlock.stores.forEach(this::checkBuilding);
        cityBlock.internetUsers.keySet().forEach(this::checkPerson);
        cityBlock.internetUsers.values().forEach(this::checkComputer);
    }
    
    private static class Computer
    {
        
        private String name;
        private String model;
        private int year;
        private String manufacturer;
        private double cost;
        
    }
    
    private static class Person
    {
        
        public String name;
        public int age;
        private double money;
        private String middleName;
        private Computer computer;
        
        private static String staticField = ObjectGeneratorsTest.staticField;
    }
    
    private static class Building
    {
        
        private List<Person> people;
        private String address;
        private int age;
        private int floors;
    }
    
    private static class AddressBook
    {
        
        private Map<String, Building> directory;
    }
    
    private static enum State
    {
        NY,
        NJ,
        CA
    }
    
    private static class CityBlock
    {
        private String name;
        private int distance;
        private List<Building> homes;
        private List<Building> stores;
        private Map<Person, Computer> internetUsers;
        private State state;
        private ByteBuffer memory;
        private Boolean isNearOcean;
    }
}
