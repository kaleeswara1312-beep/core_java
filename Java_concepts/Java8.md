# Java 8+ Important Features -- Exam & Interview Notes

## 1. Default Methods in Interface

### Definition

A **default method** is a method inside an interface that has a **method
implementation** using the `default` keyword.

Before Java 8, adding a new abstract method to an existing interface
could break all classes that implemented that interface because every
implementing class had to provide the new method.

Java 8 introduced default methods mainly to allow interfaces to evolve
without forcing every existing implementation to change.

### Example

``` java
interface Vehicle {

    void start();

    default void stop() {
        System.out.println("Vehicle stopped");
    }
}

class Car implements Vehicle {

    @Override
    public void start() {
        System.out.println("Car started");
    }
}

class Bike implements Vehicle {

    @Override
    public void start() {
        System.out.println("Bike started");
    }

    @Override
    public void stop() {
        System.out.println("Bike stopped");
    }
}
```

Here:

-   `start()` is an abstract method, so `Car` and `Bike` must implement
    it.
-   `stop()` is a default method, so implementing classes do **not**
    have to override it.
-   A class can still override a default method when it needs different
    behavior.

### Why use it?

Suppose an interface already has 100 implementing classes. If you add a
new abstract method, all 100 classes may need modification.

With a default method, you can provide a common implementation in the
interface itself.

### Pros

-   Helps maintain **backward compatibility**.
-   Allows adding new behavior to existing interfaces.
-   Reduces duplicate implementation code.
-   Implementing classes can override the default behavior.

### Cons

-   Too many default methods can make interfaces harder to understand.
-   Multiple interfaces can create a **default-method conflict**.
-   It can blur the responsibility between an interface and an abstract
    class.

### Interview Answer

> A default method is an interface method with a concrete implementation
> introduced in Java 8. It allows interfaces to add new behavior without
> forcing every implementing class to implement the new method. An
> implementing class can use the default implementation or override it.

### Memory Trick

**Default = "Implementation available, overriding optional."**

------------------------------------------------------------------------

# 2. Static Methods in Interface

### Definition

An interface can contain a **static method with an implementation**.

A static interface method belongs to the **interface itself**, not to
the implementing class.

### Example

``` java
interface Calculator {

    static int add(int a, int b) {
        return a + b;
    }
}

class MathOperation implements Calculator {
}
```

Access the method using the interface name:

``` java
int result = Calculator.add(10, 20);

System.out.println(result);
```

You cannot call it like this:

``` java
MathOperation.add(10, 20); // Compile-time error
```

### Important Point

Static interface methods are **not inherited by implementing classes**.

So the correct syntax is:

``` java
InterfaceName.methodName();
```

### Pros

-   Keeps utility/helper methods logically grouped with the interface.
-   Prevents unnecessary implementation in every class.
-   Provides a clear interface-level utility operation.

### Cons

-   Cannot be overridden by implementing classes.
-   Cannot be accessed through an implementation class.
-   Excessive use can make an interface behave like a utility class.

### Interview Answer

> A static method in an interface belongs to the interface itself. It is
> not inherited by implementing classes and must be accessed using the
> interface name.

### Memory Trick

**Static = "Interface owns it."**

------------------------------------------------------------------------

# 3. Functional Interface

### Definition

A **functional interface** is an interface that contains exactly **one
abstract method**.

It can contain:

-   One abstract method
-   Multiple default methods
-   Multiple static methods

The `@FunctionalInterface` annotation is optional but recommended
because the compiler validates that the interface has only one abstract
method.

### Example

``` java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}
```

It can be used with a lambda expression:

``` java
Calculator addition = (a, b) -> a + b;

System.out.println(addition.calculate(10, 20));
```

### Can it contain default and static methods?

Yes.

``` java
@FunctionalInterface
interface Calculator {

    int calculate(int a, int b);

    default void display() {
        System.out.println("Calculator");
    }

    static void info() {
        System.out.println("This is a calculator");
    }
}
```

There is still only **one abstract method**: `calculate()`.

### Common Functional Interfaces

Java provides many built-in functional interfaces:

  Interface         Abstract Method     Typical Use
  ----------------- ------------------- -----------------------
  `Predicate<T>`    `boolean test(T)`   Condition/check
  `Function<T,R>`   `R apply(T)`        Transform value
  `Consumer<T>`     `void accept(T)`    Consume/process value
  `Supplier<T>`     `T get()`           Supply/create value

### Pros

-   Works naturally with lambda expressions.
-   Reduces boilerplate code.
-   Encourages functional-style programming.
-   Useful with Streams API.

### Cons

-   Best suited for simple behavior.
-   Complex business logic inside lambdas can become difficult to read.
-   Developers need to understand functional programming concepts.

### Interview Answer

> A functional interface is an interface with exactly one abstract
> method. It can have multiple default and static methods. Functional
> interfaces are commonly used with lambda expressions and method
> references.

### Memory Trick

**Functional Interface = One abstract method.**

------------------------------------------------------------------------

# 4. Lambda Expressions

### Definition

A **lambda expression** is a concise way to provide an implementation of
a functional interface.

It reduces boilerplate code, especially when the implementation is
small.

### Without Lambda

``` java
@FunctionalInterface
interface Calculator {
    int add(int a, int b);
}

class Addition implements Calculator {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
```

This requires a separate class just to provide a small implementation.

### With Lambda

``` java
Calculator calculator = (a, b) -> a + b;

System.out.println(calculator.add(10, 20));
```

The lambda is assigned to a variable whose type is a functional
interface.

### Syntax

``` java
(parameters) -> expression
```

Example:

``` java
(a, b) -> a + b
```

Multiple statements:

``` java
(a, b) -> {
    int result = a + b;
    return result;
}
```

### Pros

-   Less boilerplate code.
-   More readable for simple operations.
-   Works very well with Streams API.
-   Supports functional programming.

### Cons

-   Complex lambdas can reduce readability.
-   Debugging can sometimes be harder.
-   A lambda cannot be used without a functional-interface target type.

### Interview Answer

> A lambda expression provides a concise implementation of a functional
> interface. It was introduced in Java 8 to reduce boilerplate and
> enable functional-style programming.

### Memory Trick

**Lambda = Short implementation of one behavior.**

------------------------------------------------------------------------

# 5. Streams API

### Definition

The **Stream API**, introduced in Java 8, provides a way to process
collections and other data sources using a pipeline of operations.

A stream does not represent stored data. Instead, it represents a
**sequence of elements on which operations can be performed**.

### Example

``` java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);

List<Integer> evenNumbers = numbers.stream()
        .filter(n -> n % 2 == 0)
        .map(n -> n * 10)
        .toList();

System.out.println(evenNumbers);
```

Output:

``` text
[20, 40, 60]
```

### Stream Pipeline

``` text
Collection
    |
    v
 stream()
    |
    v
 filter()
    |
    v
 map()
    |
    v
 collect()/toList()
    |
    v
 Result
```

### Types of Operations

#### Intermediate Operations

These return another stream.

Examples:

``` java
filter()
map()
sorted()
distinct()
limit()
```

#### Terminal Operations

These produce the final result or side effect.

Examples:

``` java
collect()
toList()
forEach()
count()
reduce()
```

### Important Point

Streams generally **do not modify the original collection**.

For example:

``` java
List<Integer> numbers = List.of(1, 2, 3);

List<Integer> result = numbers.stream()
        .map(n -> n * 2)
        .toList();
```

`numbers` remains unchanged.

### Parallel Stream

``` java
numbers.parallelStream()
       .forEach(System.out::println);
```

Parallel streams use Java's **Fork/Join framework**, typically through
the common `ForkJoinPool`, to process elements concurrently.

### When should you avoid parallel streams?

Parallel processing is not automatically faster.

For small collections or cheap operations, parallel-stream overhead may
make performance worse.

It can also be problematic when operations involve:

-   Shared mutable state
-   Blocking I/O
-   Poorly designed thread-unsafe code
-   Operations where ordering is important

### Pros

-   Concise data-processing code.
-   Supports filtering, mapping, sorting, grouping, reduction, etc.
-   Encourages declarative programming.
-   Can support parallel processing.

### Cons

-   Can be harder to debug than normal loops.
-   Poorly designed streams can reduce readability.
-   Parallel streams are not always faster.
-   Complex stream pipelines can become difficult to maintain.

### Interview Answer

> Stream API provides a declarative pipeline for processing data from
> collections or other sources. It supports intermediate and terminal
> operations and generally does not modify the source collection.
> Parallel streams can process data concurrently using the Fork/Join
> framework.

### Memory Trick

**Stream = Source → Process → Result**

------------------------------------------------------------------------

# 6. Method References

### Definition

A **method reference** is a shorter form of a lambda expression when the
lambda simply calls an existing method.

### Lambda

``` java
List<String> names = List.of("John", "Alex", "David");

names.forEach(name -> System.out.println(name));
```

### Method Reference

``` java
names.forEach(System.out::println);
```

Both mean essentially the same thing.

### Common Forms

#### Static Method

``` java
ClassName::staticMethod
```

Example:

``` java
Integer::parseInt
```

#### Instance Method of a Particular Object

``` java
object::instanceMethod
```

Example:

``` java
System.out::println
```

#### Instance Method of an Arbitrary Object of a Type

``` java
ClassName::instanceMethod
```

Example:

``` java
String::toUpperCase
```

#### Constructor Reference

``` java
ClassName::new
```

Example:

``` java
List<String> names = List.of("John", "Alex");

List<Integer> lengths = names.stream()
        .map(String::length)
        .toList();
```

### Pros

-   Very concise.
-   Improves readability when the method name clearly expresses the
    operation.
-   Avoids unnecessary lambda syntax.

### Cons

-   Can initially be confusing for beginners.
-   Not useful when additional logic is required.

### Interview Answer

> A method reference is a shorthand for a lambda expression when the
> lambda only invokes an existing method.

### Memory Trick

**Lambda: `x -> x.method()`**

**Method Reference: `Type::method`**

------------------------------------------------------------------------

# 7. Optional

### Definition

`Optional<T>` is a container that may contain a value or may be empty.

It was introduced in Java 8 to make the absence of a value more explicit
and help reduce accidental `NullPointerException` in certain situations.

### Problem Without Optional

``` java
String name = getName();

if (name != null) {
    System.out.println(name.toUpperCase());
}
```

### Using Optional

``` java
Optional<String> name = Optional.ofNullable(getName());

name.ifPresent(value ->
        System.out.println(value.toUpperCase())
);
```

### Common Methods

``` java
Optional.of(value)
Optional.ofNullable(value)
Optional.empty()

isPresent()
isEmpty()

orElse()
orElseGet()
orElseThrow()

ifPresent()
map()
filter()
```

### `orElse()` vs `orElseGet()`

``` java
String result = optional.orElse(getDefaultValue());
```

`getDefaultValue()` is evaluated even when the Optional already contains
a value.

``` java
String result = optional.orElseGet(() -> getDefaultValue());
```

The supplier is evaluated only when the Optional is empty.

### Important Point

Optional is **not a replacement for null everywhere**.

It is primarily useful for representing an optional return value and
making absence explicit.

### Pros

-   Makes absence of a value explicit.
-   Can reduce null-checking code.
-   Supports functional-style operations such as `map()` and `filter()`.

### Cons

-   Can add unnecessary complexity if used everywhere.
-   Should generally not be used as a field or parameter without a good
    reason.
-   Improper use can make simple code harder to read.

### Interview Answer

> Optional is a container introduced in Java 8 that can either contain a
> value or be empty. It helps represent potentially absent return values
> and can reduce certain null-related errors.

### Memory Trick

**Optional = Value OR Empty**

------------------------------------------------------------------------

# 8. New Date-Time API

### Definition

Java 8 introduced the modern Date-Time API in the `java.time` package.

Important classes include:

``` text
LocalDate
LocalTime
LocalDateTime
ZonedDateTime
Instant
Duration
Period
DateTimeFormatter
```

### Example

``` java
LocalDate today = LocalDate.now();

LocalDate nextWeek = today.plusDays(7);

System.out.println(today);
System.out.println(nextWeek);
```

### Immutability

Date-Time objects are **immutable**.

For example:

``` java
LocalDate date = LocalDate.of(2026, 8, 20);

LocalDate newDate = date.plusDays(5);
```

`date` is not changed.

Instead, `plusDays()` returns a new `LocalDate`.

This makes these objects naturally safer to share between threads.

### Old API vs New API

  Old API                      Java 8+ API
  ---------------------------- ----------------------------------
  `Date`                       `LocalDate`, `Instant`, etc.
  `Calendar`                   `LocalDateTime`, `ZonedDateTime`
  `SimpleDateFormat`           `DateTimeFormatter`
  Mutable/problematic design   Immutable design
  Less intuitive               More readable

### Pros

-   Immutable and thread-safe date/time classes.
-   Cleaner API.
-   Better timezone support.
-   Better separation between date, time, instant, and timezone
    concepts.
-   Better formatting support.

### Cons

-   Developers familiar with old `Date`/`Calendar` APIs need to learn
    the new API.
-   Legacy applications may still require conversion between old and new
    APIs.

### Interview Answer

> Java 8 introduced the `java.time` API to provide a cleaner and more
> robust date-time model. Its core classes are immutable and
> thread-safe, which makes them safer for concurrent applications.

### Memory Trick

**java.time = Immutable + Thread-safe + Cleaner API**

------------------------------------------------------------------------

# 9. Metaspace

### Definition

**Metaspace** is the memory area used by the JVM to store **class
metadata**.

In **HotSpot Java 8 and later**, Metaspace replaced **PermGen (Permanent
Generation)** for class metadata.

### What is stored?

Examples include metadata related to:

-   Class structure
-   Methods
-   Fields
-   Runtime constant-pool-related metadata
-   Class-related JVM information

### PermGen vs Metaspace

  -----------------------------------------------------------------------
  PermGen                             Metaspace
  ----------------------------------- -----------------------------------
  Used before Java 8                  Used from Java 8 onward in HotSpot

  Part of JVM-managed heap space      Uses native memory

  Fixed/limited configuration was     Can grow based on native-memory
  common                              availability, subject to limits

  Could cause                         Can cause
  `OutOfMemoryError: PermGen space`   `OutOfMemoryError: Metaspace`
  -----------------------------------------------------------------------

### Example Scenario

Applications that dynamically create/load a very large number of classes
can consume significant Metaspace.

This can happen with:

-   Dynamic class generation
-   Large frameworks
-   Excessive class loading
-   Classloader leaks

### Important Interview Point

Metaspace is **not the same as the Java heap**.

The heap primarily stores objects, while Metaspace stores class
metadata.

### Pros

-   Removes the old PermGen limitation/design.
-   Can dynamically grow in native memory.
-   Better suited for applications with large numbers of classes.

### Cons

-   Can consume significant native memory.
-   Classloader leaks can cause Metaspace growth.
-   It still has practical limits and can throw `OutOfMemoryError`.

### Interview Answer

> Metaspace is the native-memory area used by the HotSpot JVM to store
> class metadata. It replaced PermGen starting with Java 8. Unlike
> PermGen, Metaspace is allocated from native memory and can grow
> dynamically, subject to available memory and configured limits.

### Memory Trick

**PermGen → Java 7 and earlier**

**Metaspace → Java 8+**

------------------------------------------------------------------------

# Quick Comparison -- When to Use What?

  ----------------------------------------------------------------------------
  Feature           Main Purpose      Key Benefit       Main Concern
  ----------------- ----------------- ----------------- ----------------------
  Default Method    Add               Backward          Interface conflicts
                    implementation to compatibility     
                    interface                           

  Static Interface  Interface-level   No implementation Not
  Method            utility behavior  required          inherited/overridden

  Functional        One abstract      Lambda support    Only one abstract
  Interface         behavior                            method

  Lambda            Concise           Less boilerplate  Complex lambdas hurt
                    implementation                      readability

  Stream API        Process data      Declarative       Can become complex
                                      pipeline          

  Method Reference  Shorten simple    Very concise      Less obvious to
                    lambda                              beginners

  Optional          Represent absent  Safer null        Can be overused
                    value             handling          

  Date-Time API     Work with         Immutable +       Learning/legacy
                    dates/times       thread-safe       conversion

  Metaspace         Store class       Replaces PermGen  Native memory usage
                    metadata                            
  ----------------------------------------------------------------------------

------------------------------------------------------------------------

# Important Interview Traps

## 1. Can a default method be overridden?

**Yes.**

``` java
interface A {
    default void show() {
        System.out.println("A");
    }
}

class B implements A {
    @Override
    public void show() {
        System.out.println("B");
    }
}
```

------------------------------------------------------------------------

## 2. Can a static interface method be overridden?

**No.**

It belongs to the interface.

``` java
A.show();
```

It is not inherited by the implementing class.

------------------------------------------------------------------------

## 3. Can a functional interface have default methods?

**Yes.**

It can have multiple default and static methods, but only **one abstract
method**.

------------------------------------------------------------------------

## 4. Does Stream API modify the original collection?

Generally, stream operations do not modify the source collection unless
your own operation introduces side effects.

------------------------------------------------------------------------

## 5. Are parallel streams always faster?

**No.**

Parallelism has overhead. It depends on data size, operation cost, CPU
resources, splitting efficiency, and whether the operation is suitable
for parallel execution.

------------------------------------------------------------------------

## 6. Is Optional completely preventing NullPointerException?

**No.**

Optional helps represent absent values and can reduce certain
null-related errors, but it does not eliminate `NullPointerException`
from Java applications.

------------------------------------------------------------------------

## 7. Why is the new Date-Time API thread-safe?

Because its core date/time classes are **immutable**.

Methods such as:

``` java
plusDays()
plusYears()
withYear()
```

return new objects instead of modifying the existing object.

------------------------------------------------------------------------

## 8. Where does Metaspace reside?

Metaspace uses **native memory**, not the normal Java heap.

------------------------------------------------------------------------

# One-Minute Revision

``` text
Default Method
    → Interface + implementation
    → Override is optional

Static Interface Method
    → Belongs to interface
    → InterfaceName.method()

Functional Interface
    → Exactly ONE abstract method
    → Multiple default/static methods allowed

Lambda
    → Short implementation of functional interface

Stream
    → Source → Intermediate Operations → Terminal Operation

Method Reference
    → Lambda shortcut
    → Class/Object::method

Optional
    → Value OR Empty
    → Helps with absence/null handling

Date-Time API
    → java.time
    → Immutable + Thread-safe

Metaspace
    → Class metadata
    → Java 8+
    → Replaced PermGen
    → Native memory
```

# Final Interview Summary

> Java 8 introduced several important features that improved Java's
> support for functional and modern programming. Default and static
> methods enhanced interfaces, functional interfaces and lambda
> expressions reduced boilerplate, Streams provided a declarative way to
> process data, method references simplified lambdas, Optional provided
> a better way to represent potentially absent values, and the new
> Date-Time API introduced immutable and thread-safe date/time classes.
> In Java 8, HotSpot also replaced PermGen with Metaspace for storing
> class metadata.
