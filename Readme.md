Alchemy Generator
==============================================

[<img src="https://raw.githubusercontent.com/SirWellington/alchemy/develop/Graphics/Logo/Alchemy-Logo-v3-name.png" width="200">](https://github.com/SirWellington/alchemy)

## "More Data => Better tests"

[![Build Status](http://jenkins.sirwellington.tech/job/Alchemy%20Generator/badge/icon)](http://jenkins.sirwellington.tech/job/Alchemy%20Generator/)

- [Purpose](#purpose)
- [Download](#download)
  - [Release](#release)
  - [Snapshot](#snapshot)
- [[Javadocs](http://www.javadoc.io/doc/tech.sirwellington.alchemy/alchemy-generator/)](#javadocshttpwwwjavadociodoctechsirwellingtonalchemyalchemy-generator)
- [API](#api)
  - [Numbers](#numbers)
    - [Integers](#integers)
  - [Longs](#longs)
    - [Doubles](#doubles)
  - [Strings](#strings)
    - [Alphabetical](#alphabetical)
    - [Alphanumeric](#alphanumeric)
    - [Hexadecimal](#hexadecimal)
    - [Any String](#any-string)
    - [UUIDs](#uuids)
    - [From Fixed Set](#from-fixed-set)
  - [Collections](#collections)
    - [Lists](#lists)
    - [Maps](#maps)
  - [Dates and Times](#dates-and-times)
    - [Generators for `Instant` type:](#generators-for-instant-type)
    - [Generators for `Date` type:](#generators-for-date-type)
  - [Enums](#enums)
  - [People](#people)
- [Requirements](#requirements)
- [Building](#building)
- [Feature Requests](#feature-requests)
- [Release Notes](#release-notes)
  - [1.2](#12)
  - [1.1](#11)
  - [1.0](#10)
- [License](#license)

# Purpose
Part of the [Alchemy Collection](https://github.com/SirWellington/alchemy), this library makes it easier to test your code by providing generators for common Objects and Data.

Introducing randomized data to tests helps improve test quality by assuring that your code can work over a wide range of data calues,
and not just what you hard-code in. It also increases confidence that code will work in a variety of circumstances.


# Download

To use, simply add the following maven dependency.

## Release
```xml
<dependency>
	<groupId>tech.sirwellington.alchemy</groupId>
	<artifactId>alchemy-generator</artifactId>
	<version>1.2</version>
</dependency>
```

## Snapshot
>First add the Snapshot Repository
```xml
<repository>
	<id>ossrh</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>
```

```xml
<dependency>
	<groupId>tech.sirwellington.alchemy</groupId>
	<artifactId>alchemy-generator</artifactId>
	<version>1.3-SNAPSHOT</version>
</dependency>
```

# [Javadocs](http://www.javadoc.io/doc/tech.sirwellington.alchemy/alchemy-generator/)


API
==============================================

>Examples use static imports

## Numbers
`tech.sirwellington.alchemy.generator.NumberGenerators`

### Integers

```java
//A number in the range [-50, 50)
int anInteger = integers(-50, 50).get();
```

## Longs

```java
//Get any positive long
long somePositiveNumber = positiveLongs().get();

//alternative way to get a single value
somePositiveNumber = one(positiveLongs());
```

### Doubles
```java
//A double in the range [0.1, 1999.0]
AlchemyGenerator<Double> doubleGenerator = doubles(0.1, 1999.0);
for(int i = 0; i < 100; ++i)
{
	LOG.info("Received double {}", doubleGenerator.get());
}
```

## Strings
`tech.sirwellington.alchemy.generator.StringGenerators`

### Alphabetical
Uses the Latin Alphabet, a-z | A-Z

```java
String alphabetical = alphabeticString().get();
```

### Alphanumeric
Uses the Latin Alphabet, and numbers 1-9.
```java
String alphanumeric = one(alphanumericString());
```
### Hexadecimal
```java
String hex = hexadecimalString(32).get();
```

### Any String
These strings may have unicode characters as well. These are great for testing against international character sets as well.

```java

String anyCharacterString = strings(30).get();
assertThat(anyCharacterString.length(), is(30));
```
### UUIDs
Guaranteed unique strings

```java
int amount = one(smallPositiveIntegers());
AlchemyGenerator<String> uuids = uuids();
Set<String> ids = new HashSet<>();

for(int i = 0; i < amount; ++i)
{
	String id = one(uuids);
	LOG.info("UUID : {}", id);
	ids.add(id);
}
assertThat(ids.size(), is(amount));
```
### From Fixed Set
Strings can be generated from a preselected set of String values.
```java
//The generated strings can only be one of the supplied ones.
String stringFromList = stringsFromFixedList("one", "something else", "Java").get();
```

## Collections
`tech.sirwellington.alchemy.generator.CollectionGenerators`

### Lists

```java
List<String> randomStrings = listOf(alphabeticString(20), 100);
List<Integer> ages = listOf(integers(1, 100));
```

### Maps
```java
AlchemyGenerator<String> names = alphabeticalStrings();
AlchemyGenerator<Integer> ages = integers(1, 100);

int numberOfPeople = 50;
Map<String,Integer> ages = mapOf(names, ages, numberOfPeople);
```

## Dates and Times

### Generators for `Instant` type:
<br>
`tech.sirwellington.alchemy.generator.TimeGenerators`

```java
AlchemyGenerator<Instant> alwaysNow = presentInstants();
Instant timeInThePast = pastInstants().get();
Instant timeInTheFuture = futureInstants().get();
```

### Generators for `Date` type:
<br>
`tech.sirwellington.alchemy.generator.DateGenerators`
```java
AlchemyGenerator<Date> alwaysNow = presentDates();
Date dateInThePast = one(pastDates());
Date dateInTheFuture = one(futureDates());
```


## Enums
`tech.sirwellington.alchemy.generator.EnumGenerators`

Sometimes you have an `enum` and you want to randomly access a value from it.

**Alchemy Generator** makes it simple:

```java
enum Fruit
{
	APPLE,
	ORANGE,
	BANANA,
	GUAVA
}
```
You want a fruit, but don't care which one?
```java
Fruit fruit = enumValueOf(Fruit.class).get();
```

## People

Our code very often works people, and information about them
`tech.sirwellington.alchemy.generator.PeopleGenerators`

```java
String name = names().get();
int age = one(adultAges());
String phoneNumber = one(phoneNumberStrings());
String email = one(emails());
```

## POJOs

POJOs are dumb data objects, that is they tend to contain no functionality other than getters/setters and value methods
like `equals()`, `hashCode()`, and `toString()`. Alchemy Generator provides Automatic Generation of POJOs.

`tech.sirwellington.alchemy.generator.ObjectGenerators`

Let's say you have a class like

```java
class Computer
{
    private int yearReleased;
    private String modelName;
    private double cost;
    private String manufacturer;
    private String operatingSystem;
}
```

Rather than creating Boiler Plate generation code for each pojo, just use the `pojos()` generator.

```
@Test
public void testPurchaseOrder()
{
    Computer computer = one(pojos(Computer.class));
    shoppingCart.add(computer);
    shoppingCart.order();
    ...
}
```
### Nested POJOs

The POJO generator contains a sophisticated recursive algorithm to generate complex hierarchies of POJOs,
as long as it all eventually boils to down to primitive types (Integer, String, Double, Long, Date, etc).

```java
class Developer
{
    private String name;
    private String alias;
    private int age;
    private Computer developerMachine;
    private Computer serverMachine;
}

Developer developer = one(pojos(Developer.class));

assertThat(developer, notNullValue());
assertThat(developer.name, not(isEmptyOrNullString()));
assertThat(developer.age, greaterThan(0));
assertThat(developer.developerMachine, notNullValue());
...
```

> IMPORTANT: There can be NO circular references. A Computer cannot contain a Developer at the same time that Developer contains Computer.
> This would cause a StackOverflow.

### Collections

The POJO Generator also handles Generic `List`, `Set`, and `Map` types that contain either Primitive Types, or other POJOs.
```java
class City
{
    private String name;
    private long population;
    private List<Developer> developersInTown;
    private Map<String, Building> addressDirectory;
}

City sampleCity = one(pojos(City.class));
```

> IMPORTANT: Complicated and nested data structures increase the amount of time of Object Generation, since for each
> Collection, the algorithm must recurse to generate more POJOs for the collection.
> This library was for Unit Testing purposes, however, and so performance is less important.


# Requirements

+ Java 8
+ Maven


# Building
This project builds with maven. Just run a `mvn clean install` to compile and install to your local maven repository

# Feature Requests
Feature Requests are definitely welcomed! **Please drop a note in [Issues](https://github.com/SirWellington/alchemy-generator/issues).**

# Release Notes

# 1.3
+ Added Automatic POJO Generation.
    This allows very quick generation of Simple POJOs for Unit Testing and other Verification purposes.
    ```java
    class City
    {
        private String name;
        private long population;
        private List<Developer> developersInTown;
        private Map<String, Building> addressDirectory;
    }

    City sampleCity = one(pojos(City.class));
    ```

## 1.2
+ Added Alphanumeric Strings
+ New People Information Generators
	+ `popularEmailDomains()`
	+ `emails()`
	+ `name()`
	+ `age()`
	+ `phoneNumber()`
+ New Collection Generators
	+ `fromList()`
	+ `mapOf()`
+ New Date Generators
	+ `presentDates()`
	+ `pastDates()`
	+ `futureDates()`
+ New Time Generators
	+ `presentInstants()`
	+ `pastInstants()`
	+ `futureInstants()`
+ Bugfixes

## 1.1
+ New Java DateTime API Generators
+ Adding `asString()` String Generator

## 1.0
+ Initial Public Release

# License

This Software is licensed under the Apache 2.0 License

http://www.apache.org/licenses/LICENSE-2.0
