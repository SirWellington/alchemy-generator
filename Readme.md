Alchemy Generator
==============================================
<img src="https://raw.githubusercontent.com/SirWellington/alchemy/develop/Graphics/Logo/Alchemy-Logo-v3-name.png" width="200">

## "More Data => Better tests"

[![Build Status](https://travis-ci.org/SirWellington/alchemy-generator.svg)](https://travis-ci.org/SirWellington/alchemy-generator)

# Purpose
Part of the Alchemy collection, this library makes it easier to test your code by providing generators for common Objects and Data.

Using randomly generated data sets helps improve test quality by assuring that your code can work over a wide range of data,
and not just what you hard-code in.

This library makes it painless to generate primitive types,
and you can even supply your own Generators for use in conjunction with the ones in this library.


# Download

To use, simply add the following maven dependency.

## Release
```xml
<dependency>
	<groupId>tech.sirwellington.alchemy</groupId>
	<artifactId>alchemy-generator</artifactId>
	<version>1.0</version>
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
	<version>1.1-SNAPSHOT</version>
</dependency>
```

# Javadocs
## [Latest](http://www.javadoc.io/doc/tech.sirwellington.alchemy/alchemy-generator/)


API
==============================================

>Examples use static imports

# Numbers

## Integers

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

## Doubles
```java
//A double in the range [0.1, 1999.0]
AlchemyGenerator<Double> doubleGenerator = doubles(0.1, 1999.0);
for(int i = 0; i < 100; ++i)
{
	LOG.info("Received double {}", doubleGenerator.get());
}
```

# Strings
These are all the same type: `AlchemyGenerator<String>`

## Alphabetical
Uses the Latin Alphabet, a-z | A-Z

```java
String alphabetical = alphabeticString().get();
```

## Hexadecimal
```java
String hex = hexadecimalString(32).get();
```

## Any String
These strings may have unicode characters as well

```java

String anyCharacterString = strings(30).get();
assertThat(anyCharacterString.length(), is(30));
```
## UUIDs
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
## From Fixed Set
Strings can be generated from a preselected set of String values.
```java
//The generated strings can only be one of the supplied ones.
String stringFromList = stringsFromFixedList("one", "something else", "Java").get();
```

# Collections

## Lists

```java
List<String> randomStrings = listOf(alphabeticString(20), 100);
List<Integer> ages = listOf(integers(1, 100));
```

## Maps
```java
AlchemyGenerator<String> names = alphabeticalStrings();
AlchemyGenerator<Integer> ages = integers(1, 100);

int numberOfPeople = 50;
Map<String,Integer> ages = mapOf(names, ages, numberOfPeople);
```

# Enums

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

# Requirements

+ Java 8
+ Maven


# Building
This project builds with maven. Just run a `mvn clean install` to compile and install to your local maven repository



# Release Notes

## 1.1

## 1.0
+ Initial Public Release

# License

This Software is licensed under the Apache 2.0 License

http://www.apache.org/licenses/LICENSE-2.0
