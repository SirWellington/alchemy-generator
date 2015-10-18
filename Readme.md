Alchemy Generator, for Unit generatoring
==============================================

[![Build Status](https://travis-ci.org/SirWellington/alchemy-generator.svg)](https://travis-ci.org/SirWellington/alchemy-generator)

# Purpose
Part of the Alchemy collection, this library makes it easier to test your code by providing generators of Data and common Objects.

Using randomly generated data sets helps improve test quality by assuring that your code can work over a wide range of data,
and not just what you hard-code in. This library makes it painless to generate primitive types,
and you can even supply your own Data Generators for use in conjunction with this library.

# Requirements

+ JDK 8
+ Maven


# Building
This project builds with maven. Just run a `mvn clean install` to compile and install to your local maven repository

# Download

> This library is not yet available on Maven Central

To use, simply add the following maven dependency.

## Release
```xml
<dependency>
	<groupId>tech.sirwellington.alchemy</groupId>
	<artifactId>alchemy-generator</artifactId>
	<version>1.0</version>
</dependency>
```

```xml
## Snapshot
<dependency>
	<groupId>tech.sirwellington.alchemy</groupId>
	<artifactId>alchemy-generator</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

==============================================

# Examples

>Examples assume static imports`

## Numbers

```java
//A number in the range [-50, 50)
int someNumber = integers(-50, 50).get();

//Get any positive long
long somePositiveNumber = positiveLongs().get();

//alternative way to get a single value
somePositiveNumber = one(positiveLongs());

//A double in the range [0.1, 1999.0]
AlchemyGenerator<Double> doubleGenerator = doubles(0.1, 1999.0);
for(int i = 0; i < 100; ++i)
{
	LOG.info("Received double {}", doubleGenerator.get());
}

//A list of 30 randomly selected positive integers
List<Integer> thirtyNumbers = listOf(positiveIntegers(), 30);

```
## Strings
```java
//May have unicode characters as well
String anyCharacterString = strings(30).get();
assertThat(anyCharacterString.length(), is(30));

String hex = hexadecimalString(32).get();
String alphabetical = alphabeticString(5).get();

List<String> uuids = Lists.newArrayList();
for(int i = 0; i < 40; ++i)
{
	uuids.add(uuids.get());
}

//Shorter way
uuids = listOf(uuids, 20);
List<String> strings = listOf(alphabeticString(20), 100);

//The generated strings can only be one of the supplied ones.
String stringFromList = stringsFromFixedList("one", "something else", "Java").get();
```

## Collections


## Enums

Let's say you have an enum called Fruits defined as:
```java
enum Fruit
{
	APPLE,
	ORANGE,
	BANANA,
	GUAVA
}
```
and you want to get one of the values at random. All you have to do is

```java
Fruit fruit = enumValueOf(Fruit.class).get();
```

# Release Notes

## 1.0
+ Initial Release

# License

This Software is licensed under the Apache 2.0 License
