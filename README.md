[![Build Status](https://travis-ci.org/Nylle/JavaFixture.svg?branch=master)](https://travis-ci.org/Nylle/JavaFixture)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.nylle/javafixture/badge.svg)](http://search.maven.org/#artifactdetails|com.github.nylle|javafixture|1.1.0|)
[![MIT license](http://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat)](http://opensource.org/licenses/MIT)

# JavaFixture
JavaFixture is the attempt to bring the incredibly easy usage of [Mark Seemann's AutoFixture for .NET](https://github.com/AutoFixture/AutoFixture) to the Java world.

The purpose of this project is to generate full object graphs for use in test suites.

## Contents
- [Getting Started](#getting-started)
- [Usage](#usage)
- [JUnit5 Support](#junit5-support)
- [Configuration](#configuration)


## Getting Started
```xml
<dependency>
    <groupId>com.github.nylle</groupId>
    <artifactId>javafixture</artifactId>
    <version>1.1.0</version>
    <scope>test</scope>
</dependency>
```
## Usage

### Create a Fixture
```java
var fixture = new JavaFixture();
```

### Autogenerated String
```java
String result = fixture.create(String.class);
```
### Sample Result
String: "GPnGAwkxdahMweXqMgiqAuHjhpdqyqU"

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
    - id: String: "ZsiFwiJrNVxAxdsCXpKcCizlnPhwX"
    - child: ChildDto:
        - id: String: "AwkxdahMweGPnGXqMgiqAuHjhpdqyqU"
        - names: ArrayList:
            - String: "livkzLQQghkBpMGNOdUoqhVAGUirJ"
            - String: "KNbEismpNwD"
            - String: "qgdblIauhjsNZderSCjMHxqVjhgt"

### Collection of Strings
```java
List<String> result = fixture.createMany(String.class).collect(Collectors.toList());
```
#### Sample Result
ArrayList: 
- String: "ivscsrjNau"
- String: "oKEPtq"
- String: "ZOwobDdjXaqeaPeKWZviBeLyNhP"

### Add to Collection
```java
List<String> result = new ArrayList<>();
result.add("HELLO!");
fixture.addManyTo(result, String.class);
```
#### Sample Result
ArrayList: 
- String: "HELLO!"
- String: "ivscsrjNau"
- String: "oKEPtq"
- String: "ZOwobDdjXaqeaPeKWZviBeLyNhP"

### Set Public Field
```java
TestDto result = fixture.build(TestDto.class)
                        .with(x -> x.myPublicField = 123)
                        .create();
```
#### Sample Result
TestDto:
- myPrivateField: String: "jATiorjQTFkuVRIZTGPfqqdqEjLpld"
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
- myPrivateField: String: "WXQpGbcJisFXveSyNATFbNYrjdKsqu"
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
    - String: "KAzec"
    - String: "EZoYxaAeGkpzaDMZ"
    - String: "FCMuhGbBvVuAKGLlgCyPv"
    - String: "rgmGQsaf"
- youngestChild: String: "rgmGQsaf"

## JUnit5 Support

### Inject Random Values Into Single Test
```
@Test
@ExtendWith(JavaFixtureExtension.class)
void injectParameterViaMethodExtension(TestDto testObject, int intValue) {
    assertThat(testObject).isInstanceOf(TestDto.class);
    assertThat(intValue).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
}
```

### Inject Random Values Into Everything
Note, that the fixture is also injected into the `setup` method.
```
@ExtendWith(JavaFixtureExtension.class)
public class MyTest {

    private TestDto globalTestObject;
    private int globalIntValue;

    @BeforeEach
    void setup(TestDto globalTestObject, int globalIntValue) {
        this.globalTestObject = globalTestObject;
        this.globalIntValue = globalIntValue;
    }
    
    @Test
    void injectParameterViaSetupMethod() {
        assertThat(globalTestObject).isInstanceOf(TestDto.class);
        assertThat(globalIntValue).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Test
    void injectParameterViaClassExtension(TestDto testObject, int intValue) {
        assertThat(testObject).isInstanceOf(TestDto.class);
        assertThat(intValue).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
```


## Configuration
The values below are the default values, used when no configuration is provided.
```
var config = Configuration.configure()
                    .collectionSizeRange(2, 10)
                    .streamSize(3)
                    .useEasyRandom(false); // legacy*

var fixture = new JavaFixture(config);
```
> _\* Enabling [Easy Random™](https://github.com/j-easy/easy-random) will fall back to use easy-random-4.0.0 instead of JavaFixture's own engine for object graph generation._

