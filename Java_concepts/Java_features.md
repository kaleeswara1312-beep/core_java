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

# Java 11, 17 and 21 Features – Interview Reference

---

# Java 11 Features

## 1. String API Enhancements

Java 11 introduced several useful String methods to simplify string handling.

### `isBlank()`

Checks whether a string is empty or contains only whitespace.

```java
String str1 = "";
String str2 = "   ";
String str3 = "Java";

System.out.println(str1.isBlank()); // true
System.out.println(str2.isBlank()); // true
System.out.println(str3.isBlank()); // false
```

**Difference between `isEmpty()` and `isBlank()`:**

- `isEmpty()` checks whether the length is zero.
- `isBlank()` checks whether the string is empty or contains only whitespace.

### `strip()`

Removes leading and trailing whitespace based on Unicode whitespace rules.

```java
String str = "   Java   ";

System.out.println(str.strip()); // Java
```

### `stripLeading()`

Removes whitespace from the beginning of the string.

```java
String str = "   Java   ";

System.out.println(str.stripLeading()); // Java   
```

### `stripTrailing()`

Removes whitespace from the end of the string.

```java
String str = "   Java   ";

System.out.println(str.stripTrailing()); //    Java
```

### `repeat()`

Repeats a string a specified number of times.

```java
String str = "Java ";

System.out.println(str.repeat(3)); // Java Java Java
```

### `lines()`

Returns a stream of lines from a string separated by line terminators.

```java
String str = "Java\nSpring\nKafka";

str.lines().forEach(System.out::println);
```

Output:

```text
Java
Spring
Kafka
```

**Interview point:** These methods reduce the need for custom logic to handle whitespace, repeating strings, or processing multi-line strings.

---

## 2. New HTTP Client API

Java 11 introduced a standardized HTTP Client API through `java.net.http`.

Previously, developers commonly used `HttpURLConnection` or third-party libraries.

The new API supports:

- HTTP/1.1 and HTTP/2
- Synchronous requests
- Asynchronous requests
- GET, POST, PUT, DELETE and other HTTP methods
- Request and response handling

### Example – GET Request

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpExample {

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}
```

### Important classes

| Class | Purpose |
|---|---|
| `HttpClient` | Sends HTTP requests |
| `HttpRequest` | Represents an HTTP request |
| `HttpResponse` | Represents an HTTP response |
| `HttpResponse.BodyHandlers` | Defines how response bodies are handled |

### Asynchronous request

```java
HttpClient client = HttpClient.newHttpClient();

HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
        .GET()
        .build();

client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApply(HttpResponse::body)
      .thenAccept(System.out::println);
```

`sendAsync()` returns a `CompletableFuture`, allowing the request to be processed asynchronously.

**Interview point:** Java 11's HTTP Client API provides a modern, built-in alternative to older HTTP APIs and supports asynchronous programming using `CompletableFuture`.

---

## 3. Local-Variable Syntax for Lambda Parameters (`var`)

Java 11 allows `var` to be used for lambda parameters.

It was introduced to allow annotations on lambda parameters and to provide consistent syntax.

### Before

```java
list.forEach((name) -> System.out.println(name));
```

### Using `var`

```java
list.forEach((var name) -> System.out.println(name));
```

### Example with annotations

```java
list.forEach((@Deprecated var name) -> System.out.println(name));
```

**Important:**

- `var` does not mean dynamic typing.
- The type is still determined by the compiler.
- You must use `var` for all lambda parameters or none of them.

Invalid:

```java
list.forEach((var name) -> System.out.println(name)); // Valid

// Invalid mixed syntax:
// list.forEach((var name, age) -> {});
```

**Interview point:** Lambda parameter `var` was introduced in Java 11, allowing annotations and consistent parameter declarations.

---

## 4. Files API Enhancements

Java 11 introduced convenient methods in `java.nio.file.Files` for reading and writing entire files.

### `readString()`

Reads the entire file content into a String.

```java
import java.nio.file.Files;
import java.nio.file.Path;

public class FileExample {

    public static void main(String[] args) throws Exception {

        Path path = Path.of("data.txt");

        String content = Files.readString(path);

        System.out.println(content);
    }
}
```

### `writeString()`

Writes a string directly into a file.

```java
import java.nio.file.Files;
import java.nio.file.Path;

public class FileExample {

    public static void main(String[] args) throws Exception {

        Path path = Path.of("data.txt");

        Files.writeString(path, "Hello Java 11");

        System.out.println("File written successfully");
    }
}
```

### Important points

- `readString()` reads the entire file into memory.
- `writeString()` writes content to a file.
- UTF-8 is the default charset for these methods.
- These methods are useful for smaller files.
- For large files, streaming-based approaches are generally better.

**Interview point:** Java 11 simplified file operations by providing direct String-based reading and writing methods.

---

## 5. Running Java Source Files Directly

Java 11 allows us to run a Java source file directly without explicitly compiling it using `javac` first.

### Traditionally

```bash
javac Hello.java
java Hello
```

### Java 11

```bash
java Hello.java
```

Example:

```java
public class Hello {

    public static void main(String[] args) {
        System.out.println("Hello Java 11");
    }
}
```

Run:

```bash
java Hello.java
```

The Java launcher compiles and executes the source file in one step.

**Interview point:** This feature is useful for scripting, learning, quick experiments and small programs.

It does not eliminate compilation; the Java launcher handles compilation internally.

---

# Java 17 Features

> Note: Some features listed below were introduced before Java 17 but are part of the modern Java feature set. Java 17 is a useful milestone because it is an LTS release.

---

## 6. Sealed Classes

Sealed classes restrict which classes or interfaces are allowed to extend or implement them.

Before sealed classes, any class could extend a non-final class unless other restrictions were applied.

Sealed classes allow us to control the inheritance hierarchy.

### Example

```java
public sealed class Vehicle
        permits Car, Bike {
}
```

Only `Car` and `Bike` are allowed to extend `Vehicle`.

```java
public final class Car extends Vehicle {
}
```

```java
public final class Bike extends Vehicle {
}
```

### Important rules

A permitted subclass must declare one of the following:

- `final` – cannot be extended further.
- `sealed` – further inheritance is restricted.
- `non-sealed` – inheritance restrictions are removed for that subclass.

### Example

```java
public sealed class Vehicle
        permits Car, Bike {
}
```

```java
public final class Car extends Vehicle {
}
```

```java
public sealed class Bike extends Vehicle
        permits SportsBike {
}
```

```java
public final class SportsBike extends Bike {
}
```

### Why do we need sealed classes?

- Restrict inheritance.
- Control the class hierarchy.
- Improve maintainability.
- Support more predictable pattern matching.

**Interview point:** Sealed classes give developers control over which classes can extend a parent class.

<caption>Sealed classes were finalized in Java 17.</caption>

---

## 7. Pattern Matching for Switch

Pattern matching for `switch` allows us to check the type of an object and bind it to a variable directly inside a switch case.

<caption>Pattern matching for switch was a preview feature in Java 17. It became a standard feature in Java 21.</caption>

### Before

```java
Object obj = "Java";

if (obj instanceof String) {
    String str = (String) obj;
    System.out.println(str.length());
}
```

### Pattern matching in switch

```java
Object obj = "Java";

switch (obj) {

    case String str ->
            System.out.println(str.length());

    case Integer number ->
            System.out.println(number);

    default ->
            System.out.println("Unknown type");
}
```

Instead of manually checking the type and casting, the switch pattern directly provides a variable of the matched type.

### Why do we need it?

- Avoid unnecessary casting.
- Simplify type-based logic.
- Improve readability.
- Make switch statements more expressive.

**Interview point:** Pattern matching for switch combines type checking, variable binding and switch logic.

---

## 8. Records

Records are used to create simple data carrier classes.

They reduce boilerplate code such as:

- Constructors
- Getters
- `equals()`
- `hashCode()`
- `toString()`

### Traditional class

```java
public class Employee {

    private final int id;
    private final String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

### Using Record

```java
public record Employee(int id, String name) {
}
```

Usage:

```java
Employee emp = new Employee(101, "Kali");

System.out.println(emp.id());
System.out.println(emp.name());
System.out.println(emp);
```

### Important points

- Records are implicitly final.
- Record components are private and final.
- Accessors are generated.
- Constructor is generated.
- `equals()`, `hashCode()` and `toString()` are generated.
- Records are shallowly immutable, not necessarily deeply immutable.

### Why do we use Records?

- DTOs
- API request and response models
- Data transfer
- Immutable data representation

**Interview point:** Records reduce boilerplate when the primary purpose of a class is to hold data.

<caption>Records became a standard feature in Java 16.</caption>

---

## 9. Text Blocks

Text blocks allow us to write multi-line strings more easily.

They are especially useful for SQL, JSON and HTML.

### Before

```java
String json = "{\n" +
              "  \"name\": \"Java\",\n" +
              "  \"version\": 17\n" +
              "}";
```

### Using Text Blocks

```java
String json = """
        {
            "name": "Java",
            "version": 17
        }
        """;
```

### SQL example

```java
String sql = """
        SELECT *
        FROM employees
        WHERE department = 'IT'
        """;
```

### Why do we need it?

- Easier multi-line strings.
- Better readability.
- Less escaping.
- Convenient for SQL, JSON and HTML.

**Interview point:** Text blocks simplify the representation of multi-line string literals.

<caption>Text blocks became a standard feature in Java 15.</caption>

---

## 10. Enhanced `instanceof`

Enhanced `instanceof`, also called pattern matching for `instanceof`, allows us to check a type and declare a variable in one step.

### Before

```java
Object obj = "Java";

if (obj instanceof String) {

    String str = (String) obj;

    System.out.println(str.length());
}
```

### After

```java
Object obj = "Java";

if (obj instanceof String str) {

    System.out.println(str.length());
}
```

The variable `str` is automatically available when the type check succeeds.

### Example

```java
public static void printValue(Object obj) {

    if (obj instanceof Integer number) {
        System.out.println(number * 2);
    }
    else if (obj instanceof String str) {
        System.out.println(str.toUpperCase());
    }
}
```

### Why do we need it?

- Avoid manual casting.
- Reduce boilerplate.
- Improve readability.
- Make type checking easier.

**Interview point:** Pattern matching for `instanceof` combines the type check and casting into a single expression.

<caption>It became a standard feature in Java 16.</caption>

---

## 11. New Random Generator API

Java 17 introduced a new random number generator API through `java.util.random`.

It provides a common abstraction for different random number generator algorithms.

### Traditional approach

```java
import java.util.Random;

Random random = new Random();

int number = random.nextInt(100);

System.out.println(number);
```

### New API

```java
import java.util.random.RandomGenerator;

RandomGenerator generator = RandomGenerator.getDefault();

int number = generator.nextInt(100);

System.out.println(number);
```

### Important interfaces

- `RandomGenerator`
- `RandomGeneratorFactory`

### Why do we need it?

- Common interface for different random algorithms.
- More flexible random generation.
- Access to different generator implementations.
- Useful for simulation, testing and random data generation.

**Interview point:** The new Random Generator API provides a unified abstraction for random number generation and supports multiple algorithms.

<caption>The broader Random Generator API was introduced in Java 17.</caption>

---

# Java 21 Features

## 12. Sequenced Collections

Sequenced Collections were introduced in Java 21 to provide a common API for collections that maintain a defined encounter order.

Examples include:

- `ArrayList`
- `LinkedList`
- `LinkedHashSet`
- `TreeSet`
- `LinkedHashMap`
- `TreeMap`

### Problem before Java 21

Different collection types used different methods to access their first and last elements.

For example:

```java
List<Integer> list = new ArrayList<>();

list.add(10);
list.add(20);
list.add(30);

System.out.println(list.get(0)); // First element
System.out.println(list.get(list.size() - 1)); // Last element
```

For a Deque, we could use:

```java
deque.getFirst();
deque.getLast();
```

The API was inconsistent across collection types.

### Solution – Sequenced Collections

Java 21 introduced the following interfaces:

- `SequencedCollection`
- `SequencedSet`
- `SequencedMap`

These interfaces provide a consistent API for accessing and manipulating elements at both ends of an ordered collection.

### Common methods

| Method | Purpose |
|---|---|
| `getFirst()` | Returns the first element |
| `getLast()` | Returns the last element |
| `addFirst()` | Adds an element at the beginning |
| `addLast()` | Adds an element at the end |
| `removeFirst()` | Removes the first element |
| `removeLast()` | Removes the last element |
| `reversed()` | Returns a reverse-ordered view |

### Example – List

```java
List<Integer> list = new ArrayList<>();

list.add(10);
list.add(20);
list.add(30);

System.out.println(list.getFirst()); // 10
System.out.println(list.getLast());  // 30

list.addFirst(5);
list.addLast(40);

System.out.println(list); // [5, 10, 20, 30, 40]
```

### Example – Reversed view

```java
List<Integer> list = new ArrayList<>();

list.add(10);
list.add(20);
list.add(30);

System.out.println(list.reversed());
```

Output:

```text
[30, 20, 10]
```

`reversed()` provides a reverse-ordered view rather than necessarily creating an independent copy.

### Why do we need Sequenced Collections?

- Consistent API across ordered collections.
- Easy access to first and last elements.
- Easy insertion and removal at either end.
- Easier reverse-order processing.
- Better consistency across collection implementations.

**Interview point:** Java 21 introduced Sequenced Collections to standardize operations on collections that maintain a defined encounter order.

---

## 13. Virtual Threads

Virtual threads were introduced to improve scalability when applications handle a large number of concurrent tasks.

### Traditional platform threads

Traditionally, Java threads are mapped to operating-system threads.

Creating and managing a large number of platform threads can consume considerable resources.

For example:

```java
Thread thread = new Thread(() -> {
    System.out.println("Task executed");
});

thread.start();
```

When applications create thousands of threads, resource usage and thread management can become challenging.

### What are Virtual Threads?

Virtual threads are lightweight threads managed by the JVM.

They are scheduled onto platform threads, which act as carrier threads.

When a virtual thread performs a supported blocking operation, it can often unmount from its carrier thread, allowing the carrier thread to execute another virtual thread.

This improves scalability for applications that handle many concurrent, mostly blocking tasks.

### Example – Platform thread

```java
Thread thread = new Thread(() -> {
    System.out.println("Platform thread");
});

thread.start();
```

### Example – Virtual thread

```java
Thread thread = Thread.startVirtualThread(() -> {
    System.out.println("Virtual thread");
});
```

### Example – Using ExecutorService

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadExample {

    public static void main(String[] args) {

        try (ExecutorService executor =
                     Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < 1000; i++) {

                executor.submit(() -> {
                    System.out.println(Thread.currentThread());
                });
            }
        }
    }
}
```

### Platform threads vs Virtual threads

| Platform Threads | Virtual Threads |
|---|---|
| Generally more resource-intensive | Lightweight |
| Typically tied more closely to OS thread resources | Managed and scheduled by JVM |
| Suitable for CPU-intensive work and general threading | Particularly useful for high-concurrency, blocking I/O workloads |
| Creating very large numbers can be costly | Can support very large numbers of concurrent tasks |

### Why do we need Virtual Threads?

- Handle many concurrent requests.
- Improve scalability for blocking I/O operations.
- Reduce the cost of thread creation.
- Simplify concurrent programming.
- Reduce the need for thread pooling for certain I/O-bound workloads.

### Important interview points

- Virtual threads are not faster for CPU-intensive tasks.
- They improve scalability, not necessarily execution speed.
- They are useful for I/O-intensive workloads.
- Virtual threads are managed by the JVM.
- They are not a replacement for asynchronous APIs in every situation.
- They should generally not be pooled like platform threads.
- Blocking operations may allow a virtual thread to unmount, but not every blocking operation guarantees unmounting.

**Interview point:** Virtual threads allow Java applications to handle a large number of concurrent tasks efficiently by using lightweight JVM-managed threads, especially for blocking I/O workloads.

<caption>Virtual threads became a standard feature in Java 21.</caption>

---

# Quick Interview Revision

| Feature | Java Version | Main Purpose |
|---|---|---|
| `isBlank()` | Java 11 | Checks empty or whitespace-only strings |
| `strip()` | Java 11 | Removes leading and trailing Unicode whitespace |
| `stripLeading()` | Java 11 | Removes leading whitespace |
| `stripTrailing()` | Java 11 | Removes trailing whitespace |
| `repeat()` | Java 11 | Repeats a string |
| `lines()` | Java 11 | Returns a stream of lines |
| HTTP Client API | Java 11 | Modern HTTP communication |
| `var` in lambda parameters | Java 11 | Consistent lambda parameter syntax and annotations |
| `Files.readString()` | Java 11 | Reads a file into a String |
| `Files.writeString()` | Java 11 | Writes a String to a file |
| Single-file execution | Java 11 | Runs source files directly |
| Text Blocks | Java 15 | Multi-line string literals |
| Enhanced `instanceof` | Java 16 | Pattern matching for `instanceof` |
| Records | Java 16 | Simplified data carrier classes |
| Sealed Classes | Java 17 | Restricts inheritance |
| Random Generator API | Java 17 | Unified random generator abstraction |
| Pattern Matching for switch | Java 21 (standard) | Type-based switch patterns |
| Sequenced Collections | Java 21 | Uniform ordered collection operations |
| Virtual Threads | Java 21 | Lightweight concurrency |

---

# Java Versions to Remember for Interviews

- **Java 8:** Lambda, Stream API, Optional, functional interfaces, Date and Time API.
- **Java 11:** String enhancements, HTTP Client API, Files API enhancements, single-file execution.
- **Java 17:** Sealed classes (and the LTS milestone; several other features listed here became standard earlier).
- **Java 21:** Sequenced Collections, Virtual Threads, Pattern Matching for switch (standard).

