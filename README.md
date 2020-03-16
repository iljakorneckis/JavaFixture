[![Build Status](https://travis-ci.org/Nylle/JavaFixture.svg?branch=master)](https://travis-ci.org/Nylle/JavaFixture)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.nylle/javafixture.svg?label=maven-central)](http://search.maven.org/#artifactdetails|com.github.nylle|javafixture|2.0.0|)
[![MIT license](http://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat)](http://opensource.org/licenses/MIT)

# JavaFixture
JavaFixture is the attempt to bring the incredibly easy usage of [Mark Seemann's AutoFixture for .NET](https://github.com/AutoFixture/AutoFixture) to the Java world.

The purpose of this project is to generate full object graphs for use in test suites.

## Contents
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Generics](#generics)
- [Configuration](#configuration)
- [JUnit5 Support](#junit5-support)
- [Parameterized Tests](#parameterized-tests)


## Getting Started
```xml
<dependency>
    <groupId>com.github.nylle</groupId>
    <artifactId>javafixture</artifactId>
    <version>2.0.0</version>
    <scope>test</scope>
</dependency>
```
## Usage

### Create a Fixture
Via constructor:
```java
var fixture = new Fixture();
```
...or using a static factory method for convenience:
```java
var fixture = Fixture.fixture()
```

### Autogenerated String
```java
String result = fixture.create(String.class);
```
#### Sample Result
String: "c3932f6f-59ae-43df-8ee9-8788474a3f87"

### Autogenerated Number
```java
int result = fixture.create(int.class);
```
#### Sample Result
int: -1612385443

### Complex Type
```java
ParentDto result = fixture.create(ParentDto.class);
```
#### Sample Result
- ParentDto:
    - id: String: "4ed0f3c4-5ea3-4dbb-b31c-f92c036af463"
    - child: ChildDto:
        - id: String: "c3932f6f-59ae-43df-8ee9-8788474a3f87"
        - names: ArrayList:
            - String: "9452541b-c6f9-4316-b254-28d00b327d0d"
            - String: "52ac46e4-1b21-40c8-9213-31fc839fbdf7"
            - String: "333af3f6-4ed1-4580-9cae-aaee271d7ba7"

### Collection of Strings
```java
List<String> result = fixture.createMany(String.class).collect(Collectors.toList());
```
#### Sample Result
ArrayList: 
- String: "333af3f6-4ed1-4580-9cae-aaee271d7ba7"
- String: "9452541b-c6f9-4316-b254-28d00b327d0d"
- String: "4ed0f3c4-5ea3-4dbb-b31c-f92c036af463"

### Add to Collection
```java
List<String> result = new ArrayList<>();
result.add("HELLO!");
fixture.addManyTo(result, String.class);
```
#### Sample Result
ArrayList: 
- String: "HELLO!"
- String: "333af3f6-4ed1-4580-9cae-aaee271d7ba7"
- String: "9452541b-c6f9-4316-b254-28d00b327d0d"
- String: "4ed0f3c4-5ea3-4dbb-b31c-f92c036af463"

### Set Public Field
```java
TestDto result = fixture.build(TestDto.class)
                        .with(x -> x.myPublicField = 123)
                        .create();
```
#### Sample Result
TestDto:
- myPrivateField: String: "349a1f87-9d00-4623-89cb-3031bb84ddb3"
- myPublicField: int: 123

### Set Private Field
```java
TestDto result = fixture.build(TestDto.class)
                        .with("myPrivateField", "HELLO!")
                        .create();
```
#### Sample Result
TestDto:
- myPrivateField: String: "HELLO!"
- myPublicField: int: 26123854

### Call a Setter
```java
TestDto result = fixture.build(TestDto.class)
                        .with(x -> x.SetMyPrivateField("HELLO!"))
                        .create();
```
#### Sample Result
TestDto:
- myPrivateField: String: "HELLO!"
- myPublicField: int: 71

### Omit Field
```java
TestDto result = fixture.build(TestDto.class)
                        .without("myPrivateField")
                        .create();
```
#### Sample Result
TestDto:
- myPrivateField: String: null
- myPublicField: int: -128564

### Omit Primitive Field
```java
TestDto result = fixture.build(TestDto.class)
                        .without("myPublicField")
                        .create();
```
#### Sample Result
TestDto:
- myPrivateField: String: "349a1f87-9d00-4623-89cb-3031bb84ddb3"
- myPublicField: int: 0


### Perform Multiple Operations
```java
String child = fixture.create(String.class);
ParentDto parent = fixture.build(ParentDto.class)
                          .with(x -> x.addChild(child))
                          .with(x -> x.youngestChild = child)
                          .create();
```
#### Sample Result
ParentDto:
- children: ArrayList:
    - String: "710ba467-01a7-4bcc-b880-84eda5458989"
    - String: "9452541b-c6f9-4316-b254-28d00b327d0d"
    - String: "4ed0f3c4-5ea3-4dbb-b31c-f92c036af463"
    - String: "349a1f87-9d00-4623-89cb-3031bb84ddb3"
- youngestChild: String: "349a1f87-9d00-4623-89cb-3031bb84ddb3"

## Generics
Due to Java's type erasure (further reading: [baeldung](https://www.baeldung.com/java-type-erasure)), it is difficult to reflect generic classes on runtime and the following doesn't work:
 ```java
 fixture.create(Optional<String>.class); // does not compile
 ```
 Using JavaFixture however it can be achieved through a little trick:

```java
Optional<String> result = fixture.create(new SpecimenType<Optional<String>>(){});
```
Please note the empty curly braces (`{}`) after the call to the constructor of `SpecimenType`. These are necessary for generic reflection through an abstract superclass.

`SpecimenType` can also be used for non-generic classes, but will lose any parametrisation for generic classes:
```java
Optional result = fixture.create(SpecimenType.fromClass(Optional.class));
```
This syntax however is equivalent to:
```java
Optional result = fixture.create(Optional.class);
```

## Configuration
The values below are the default values, used when no configuration is provided.
```java
var config = Configuration.configure()
                    .collectionSizeRange(2, 10)
                    .streamSize(3)
                    .clock(Clock.fixed(Instant.now(), ZoneOffset.UTC));

var fixture = new Fixture(config);
```
- `collectionSizeRange` determines the range from which a random collection size will be picked when creating any collection, map or array
- `streamSize` determines the number of objects to be returned when using `Stream<T> Fixture.createMany(Class<T>)`
- `clock` sets the clock to use when creating time-based values

## JUnit5 Support

In order to use JUnit5 support you need to add the following dependencies if not already present.

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.5.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <version>5.5.1</version>
    <scope>test</scope>
</dependency>
```

_Remember to also include the `vintage` dependency if you still have JUnit4-tests, otherwise they won't be run._

```xml
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <version>5.5.1</version>
    <scope>test</scope>
</dependency>
```

### Inject Random Values Into Single Test
All arguments of the test-method below will be provided as a random object generated by JavaFixture.
```java
@TestWithFixture
void injectParameterViaMethodExtension(TestDto testObject, int intValue, Optional<String> genericObject) {
    assertThat(testObject).isInstanceOf(TestDto.class);
    
    assertThat(intValue).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    
    assertThat(genericObject).isInstanceOf(Optional.class);
    assertThat(genericObject).isPresent();
    assertThat(genericObject.get()).isInstanceOf(String.class);
}
```


### Parameterized Tests
For some syntactic sugar, this library comes with a wrapper for JUnit5's parameterized 
test feature, called `@TestWithCases`.
```java
@TestWithCases
@TestCase(str1 = "", int2 = 0)
@TestCase(str1 = " ", int2 = 1)
@TestCase(str1 = "foo", int2 = 3)
@TestCase(str1 = "hello", int2 = 5)
void testStringLength(String input, int expected) {
    assertThat(input.length()).isEqualTo(expected);
}
```
The test will be run for every `@TestCase`-annotation injecting the provided values into
the test's arguments.

Due to Java's limited annotation design, the following rules apply:
- Values can only be of type `String`, `Class` or primitive like `int`, `boolean`, `float`,
 etc.
- Annotation parameters are indexed and they must fit to the test method argument. 
 _Example:_ `str1 = "foo"` can only be applied to the first argument which must be of type
  `String`, while `int2 = 3` can only be applied to the second argument which obviously must
  be of type `int` and so on. 
- The current implementation only supports up to 6 arguments per test method.

