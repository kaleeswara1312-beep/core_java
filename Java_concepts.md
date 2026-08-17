# Java Interview Preparation — 4 to 10 Years Experience

> **Goal:** Build Java fundamentals strongly enough for interviews, then connect them to the depth expected from a mid/senior engineer.
>
> **How to use:** First read the concept + example. Then hide the explanation and answer the **Interview Answer** yourself. Finally, solve the **Practice Questions**.

---

## 0. Interview Strategy

For every topic, prepare at four levels:

1. **Definition** — What is it?
2. **Why** — Why does Java use it?
3. **How** — How does it work internally?
4. **Trade-offs** — When would you use it, and what are the limitations?

For **4–6 years**, expect strong fundamentals, debugging, collections, exceptions, Java 8+, concurrency basics, and practical coding.

For **7–10 years**, expect deeper questions around JVM internals, concurrency, memory, GC, collections internals, design decisions, performance, production debugging, Java 17/21+ features, and system design.

---

# 1. Java Platform Fundamentals

## 1.1 Why Java is platform independent

Java follows:

**Write Once, Run Anywhere (WORA)**.

```text
Java Source Code (.java)
        |
      javac
        |
Bytecode (.class)
        |
       JVM
   /     |      \
Windows Linux   macOS
```

The Java compiler does **not** normally compile Java source directly into machine code. It compiles it into **bytecode**. A JVM implementation for each operating system executes that bytecode.

### Key point

Java is platform independent because **bytecode is platform independent**, while the **JVM implementation is platform dependent**.

### Interview Answer

> Java achieves platform independence by compiling source code into platform-neutral bytecode. The JVM available for each operating system executes that bytecode, so the same `.class` file can run on different platforms.

---

# 2. JDK vs JRE vs JVM

```text
JDK
├── Development tools
│   ├── javac
│   ├── java
│   ├── javadoc
│   └── jar
└── JRE
    ├── JVM
    └── Java runtime libraries
```

| Component | Purpose |
|---|---|
| JVM | Executes bytecode |
| JRE | JVM + runtime libraries |
| JDK | JRE + development tools |

### Important modern note

From Java 9 onward, the traditional standalone JRE distribution is no longer the normal Oracle distribution model. Conceptually, the JRE idea is still useful for understanding the runtime environment.

### Memory Trick

**JDK = Develop**  
**JRE = Run**  
**JVM = Execute**

---

# 3. Java Compilation and Execution

Example:

```java
class Hello {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```

Compile:

```bash
javac Hello.java
```

Output:

```text
Hello.class
```

Run:

```bash
java Hello
```

Flow:

```text
.java
  |
  | javac
  v
.class bytecode
  |
  | Class Loader
  v
Runtime Data Areas
  |
  | Execution Engine
  v
Machine Instructions
```

---

# 4. Class Loader

The **Class Loader** loads `.class` bytecode into JVM memory when classes are required.

Typical loading process:

```text
Loading
   ↓
Linking
   ├── Verification
   ├── Preparation
   └── Resolution
   ↓
Initialization
```

### Class Loader hierarchy

Conceptually:

```text
Bootstrap ClassLoader
        ↓
Platform ClassLoader
        ↓
Application/System ClassLoader
```

### Important point

Class loading is generally **lazy**: a class is loaded when the JVM needs it, although exact behavior can depend on the situation.

### Interview question

**What happens when you create an object?**

Simplified flow:

```text
new Student()
     ↓
Is Student class loaded?
     ↓
Load/link/initialize if required
     ↓
Allocate object on heap
     ↓
Run constructor
```

---

# 5. JVM Runtime Memory Areas

The JVM runtime data areas are broadly divided into:

```text
JVM Memory
├── Heap
├── Method Area / Metaspace
├── JVM Stacks
├── PC Registers
└── Native Method Stacks
```

## Heap

Stores objects and arrays.

```java
Student s = new Student();
```

The `Student` object is allocated on the heap.

The heap is shared by JVM threads.

## Stack

Each thread has its own JVM stack.

A method call creates a stack frame containing things such as:

- Local variables
- Operand stack
- Method invocation information

Example:

```java
void test() {
    int x = 10;
}
```

`x` is a local variable in the method's stack frame.

> Be careful: saying "all primitives are on the stack" is an oversimplification. Storage depends on whether the value is a local variable, field, array element, etc.

## PC Register

The **Program Counter register** keeps track of the current JVM instruction being executed by a thread.

Each JVM thread has its own PC register.

## Native Method Stack

Used when JVM threads execute native methods, typically through mechanisms such as JNI.

## Method Area / Metaspace

The JVM specification defines the **method area** conceptually. HotSpot commonly implements class metadata using **Metaspace**, which uses native memory.

### Senior interview point

Do not blindly say:

> Method Area = Metaspace.

Better:

> Method Area is a JVM specification concept; HotSpot uses Metaspace for class metadata.

---

# 6. Execution Engine

The Execution Engine executes JVM bytecode.

Major concepts:

- Interpreter
- JIT compiler
- Garbage Collector works with the runtime memory system

## Interpreter

The interpreter executes bytecode instruction by instruction.

Advantage:

- Fast startup

Disadvantage:

- Repeated execution can be slower

## JIT Compiler

**JIT = Just-In-Time compiler**

The JVM identifies frequently executed/hot code and compiles it into optimized native machine code.

```text
Bytecode
   |
Interpreter
   |
Hot code detected
   |
JIT compilation
   |
Native machine code
```

### Interview Answer

> The interpreter executes bytecode directly, while the JIT compiler identifies frequently executed code and compiles it into native machine code for better long-term performance.

---

# 7. Garbage Collection

Garbage Collection automatically reclaims heap memory that is no longer reachable.

Example:

```java
Student s = new Student();

s = null;
```

If no other live reference points to the object, it may become eligible for GC.

### Important

`System.gc()` is only a request/hint to the JVM. It does not guarantee immediate garbage collection.

### Senior topics

Be prepared for:

- Reachability
- GC roots
- Young/old generation concepts
- Minor/major/full GC terminology
- G1 GC
- ZGC
- Shenandoah
- Stop-the-world pauses
- Allocation rate
- Memory leaks caused by retained references

---

# 8. Data Types

Java has two major categories:

```text
Data Types
├── Primitive
└── Reference
```

## Primitive types

```text
byte
short
int
long
float
double
char
boolean
```

Example:

```java
int age = 30;
double salary = 50000.50;
char grade = 'A';
boolean active = true;
```

## Reference types

Examples:

```java
String
Student
int[]
Object
```

---

# 9. Literals

A literal is a fixed value written directly in source code.

```java
int x = 10;
double d = 10.5;
char c = 'A';
String s = "Java";
boolean b = true;
```

Examples of numeric literals:

```java
int decimal = 10;
int binary = 0b1010;
int hex = 0xA;
long value = 10L;
float f = 10.5F;
```

---

# 10. Variables

Types of variables:

1. Local variable
2. Instance variable
3. Static/class variable
4. Parameter

```java
class Student {

    int age;              // instance variable
    static String school; // static variable

    void test(int x) {    // parameter
        int y = 10;       // local variable
    }
}
```

---

# 11. Type Conversion

## Widening / implicit conversion

Smaller compatible numeric type → larger type.

```java
int x = 10;
double y = x;
```

No explicit cast required.

## Narrowing / explicit casting

```java
double x = 10.8;
int y = (int) x;
```

Result:

```text
10
```

The fractional part is discarded.

## Type Promotion

During arithmetic expressions, smaller integral types are commonly promoted to `int`.

```java
byte a = 10;
byte b = 20;

int c = a + b;
```

This is why:

```java
byte c = a + b;
```

does not compile without casting.

---

# 12. Operators

Categories:

- Arithmetic
- Relational
- Logical
- Assignment
- Unary
- Bitwise
- Shift
- Ternary

Example:

```java
int a = 10;
int b = 20;

System.out.println(a + b);
System.out.println(a < b);
System.out.println(a > 5 && b > 10);
```

---

# 13. Conditional Statements

```java
if
if-else
else-if
switch
```

Example:

```java
if (age >= 18) {
    System.out.println("Adult");
} else {
    System.out.println("Minor");
}
```

Modern Java `switch` can also use expressions:

```java
String result = switch (day) {
    case "MONDAY", "TUESDAY" -> "Weekday";
    default -> "Other";
};
```

---

# 14. Ternary Operator

Short form of conditional assignment.

```java
int age = 20;

String result = age >= 18 ? "Adult" : "Minor";
```

Use it when the expression remains readable.

---

# 15. Loops

```java
for
while
do-while
for-each
```

Example:

```java
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}
```

---

# 16. Method Overloading

Same method name with different parameter lists.

```java
class Calculator {

    int add(int a, int b) {
        return a + b;
    }

    int add(int a, int b, int c) {
        return a + b + c;
    }

    double add(double a, double b) {
        return a + b;
    }
}
```

### Important

Changing only the return type is **not** method overloading.

```java
int test()
double test()
```

This is invalid.

### Interview Answer

> Method overloading is compile-time polymorphism where multiple methods have the same name but different parameter lists.

---

# 17. Arrays

Arrays have fixed length.

```java
int[] nums = new int[5];

int[] values = {10, 20, 30};
```

## Multidimensional array

```java
int[][] matrix = {
    {1, 2},
    {3, 4}
};
```

## 3D array

```java
int[][][] cube = new int[2][3][4];
```

## Jagged array

Rows can have different lengths.

```java
int[][] data = new int[3][];

data[0] = new int[2];
data[1] = new int[4];
data[2] = new int[1];
```

Java multidimensional arrays are arrays of arrays.

---

# 18. For-Each Loop

```java
int[] nums = {10, 20, 30};

for (int n : nums) {
    System.out.println(n);
}
```

Useful when you don't need the index.

---

# 19. Array of Objects

```java
Student[] students = new Student[3];

students[0] = new Student("A");
students[1] = new Student("B");
students[2] = new Student("C");
```

Important:

```java
new Student[3]
```

creates an array capable of holding **three references**. It does not create three `Student` objects.

---

# 20. Class and Object

Class = blueprint.

Object = runtime instance.

```java
class Student {
    String name;

    void display() {
        System.out.println(name);
    }
}

Student s = new Student();
s.name = "John";
s.display();
```

---

# 21. Public Class Rule

A Java source file can contain multiple top-level classes, but there can be **at most one public top-level class**, and its name must match the file name.

Example:

```java
// Student.java

public class Student {
}

class Address {
}
```

---

# 22. String

`String` is a class and is immutable.

```java
String s = "Java";
s.concat(" Programming");

System.out.println(s);
```

Output:

```text
Java
```

Because the original String was not changed.

Correct:

```java
s = s.concat(" Programming");
```

---

# 23. String Constant Pool

String literals are commonly stored/reused through the **String Pool**.

```java
String a = "Java";
String b = "Java";

System.out.println(a == b); // true
```

Because both references can point to the same pooled String object.

But:

```java
String a = new String("Java");
String b = new String("Java");

System.out.println(a == b);      // false
System.out.println(a.equals(b)); // true
```

### Interview rule

- `==` → compares references for objects
- `.equals()` → compares logical equality when properly overridden

---

# 24. String Immutable vs Mutable

## String

Immutable.

Good for:

- Safety
- Sharing
- String pool
- Hash-based collections

## StringBuilder

Mutable and generally preferred for single-threaded string modifications.

```java
StringBuilder sb = new StringBuilder();

sb.append("Hello");
sb.append(" Java");

System.out.println(sb);
```

## StringBuffer

Mutable and synchronized/thread-safe, generally with more synchronization overhead than `StringBuilder`.

### Interview Answer

> Use StringBuilder when thread safety is not required. StringBuffer provides synchronized operations and is useful when shared mutable string state requires that synchronization.

---

# 25. Encapsulation

Encapsulation means controlling access to object state and exposing behavior through methods.

```java
class BankAccount {

    private double balance;

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public double getBalance() {
        return balance;
    }
}
```

Benefits:

- Data protection
- Validation
- Maintainability
- Reduced coupling

---

# 26. `this` Keyword

`this` refers to the current object.

```java
class Student {

    private String name;

    Student(String name) {
        this.name = name;
    }
}
```

Uses:

- Resolve variable shadowing
- Call current class constructor using `this()`
- Pass current object
- Return current object

---

# 27. Constructor

A constructor initializes an object.

```java
class Student {

    String name;

    Student() {
        name = "Unknown";
    }

    Student(String name) {
        this.name = name;
    }
}
```

## Important correction

A **default constructor** specifically means the constructor automatically provided by the compiler **when you declare no constructor**.

A constructor you write yourself with no arguments is better called a **no-argument constructor**, not necessarily a default constructor.

---

# 28. Static

Static members belong to the class rather than an individual object.

```java
class Student {

    static String school = "ABC";

    static void displaySchool() {
        System.out.println(school);
    }
}

System.out.println(Student.school);
Student.displaySchool();
```

## Static variable

One class-level variable shared by instances.

## Static method

Can be called using the class name.

```java
Student.displaySchool();
```

A static method cannot directly access an instance field because there is no implicit `this`.

---

# 29. Static Block

A static block is executed when the class is initialized.

```java
class Test {

    static {
        System.out.println("Static block");
    }

    Test() {
        System.out.println("Constructor");
    }
}
```

The static initialization runs once per class initialization, not once per object.

Example:

```java
Test a = new Test();
Test b = new Test();
```

Conceptually:

```text
Class initialization
    ↓
static block        ← once
    ↓
constructor          ← object 1
    ↓
constructor          ← object 2
```

### Important correction about `Class.forName`

```java
Class.forName("com.example.Student");
```

Historically, `Class.forName(String)` loads and initializes the class, so static initialization can occur as part of that operation. Modern overloads/APIs can differ, so don't state that every class-loading mechanism always initializes the class immediately.

---

# 30. Anonymous Object

An object created without assigning its reference to a variable.

```java
new Student().getName();
```

The reference is not retained by your variable, so you cannot directly reuse that reference later.

---

# 31. Inheritance

Inheritance allows a child class to reuse/extend behavior from a parent.

```java
class Animal {
    void eat() {
        System.out.println("Eating");
    }
}

class Dog extends Animal {
    void bark() {
        System.out.println("Barking");
    }
}
```

Types commonly discussed:

- Single
- Multilevel
- Hierarchical

Java does not support multiple inheritance of **classes**.

```java
class C extends A, B { } // invalid
```

However, Java supports multiple inheritance of type through interfaces:

```java
class C implements A, B {
}
```

---

# 32. Why Java Doesn't Support Multiple Class Inheritance

Classic ambiguity:

```text
       A
     /   \
    B     C
     \   /
       D
```

If B and C both override the same method, D could face ambiguity about which implementation to inherit.

Interfaces provide controlled mechanisms for multiple inheritance of type, including explicit conflict resolution where necessary.

---

# 33. Constructor Chaining

When a child object is created, the parent constructor must be initialized before the child constructor completes.

```java
class A {
    A() {
        System.out.println("A");
    }
}

class B extends A {
    B() {
        super();
        System.out.println("B");
    }
}
```

Output:

```text
A
B
```

If you don't explicitly write `super()`, the compiler inserts an implicit no-argument `super()` when possible.

### Important

The ultimate root superclass is `Object` unless the class hierarchy otherwise has a special form such as interfaces (which are not classes).

---

# 34. `this()` vs `super()`

`this()` → calls another constructor in the same class.

```java
class Student {

    Student() {
        this("Unknown");
    }

    Student(String name) {
        System.out.println(name);
    }
}
```

`super()` → calls the parent constructor.

### Rule

`this()` or `super()` must be the **first statement** in a constructor.

You cannot use both in the same constructor because only one can be first.

---

# 35. Method Overriding

A child class provides a new implementation of an inherited method.

```java
class Animal {
    void sound() {
        System.out.println("Animal sound");
    }
}

class Dog extends Animal {
    @Override
    void sound() {
        System.out.println("Bark");
    }
}
```

Rules include:

- Same method signature
- Child must inherit the method
- Cannot reduce access visibility
- Static methods are hidden, not overridden
- `final` methods cannot be overridden
- Private methods are not overridden

---

# 36. Polymorphism

Polymorphism = many forms.

## Compile-time polymorphism

Method overloading.

```java
add(int, int)
add(double, double)
```

## Runtime polymorphism

Method overriding + dynamic dispatch.

```java
Animal a = new Dog();
a.sound();
```

Output:

```text
Bark
```

The reference type is `Animal`, but the actual object is `Dog`.

---

# 37. Dynamic Method Dispatch

Runtime polymorphism uses the actual object's overridden method.

```java
Animal animal = new Dog();
animal.sound();
```

Conceptually:

```text
Reference type → Animal
Object type    → Dog

sound()
  ↓
Dog.sound()
```

This is one of the most important Java interview concepts.

---

# 38. Packages

A package organizes classes and controls namespace/access.

```java
package com.example.service;

public class UserService {
}
```

Import:

```java
import com.example.service.UserService;
```

Wildcard:

```java
import com.example.service.*;
```

A wildcard import imports types directly in that package; it does not recursively import subpackages.

Example:

```java
import com.example.service.*;
```

does not automatically import:

```text
com.example.service.impl.*
```

---

# 39. Access Modifiers

| Modifier | Same Class | Same Package | Subclass Other Package | Other Package |
|---|---:|---:|---:|---:|
| private | Yes | No | No | No |
| default/package-private | Yes | Yes | No* | No |
| protected | Yes | Yes | Yes** | No |
| public | Yes | Yes | Yes | Yes |

`*` Package-private members are accessible only within the same package.

`**` For a subclass in another package, protected access is subject to the Java protected-access rules; it is not equivalent to unrestricted package access.

---

# 40. `final`

## Final variable

Cannot be reassigned after initialization.

```java
final int MAX = 100;
```

## Final method

Cannot be overridden.

```java
final void display() {
}
```

## Final class

Cannot be extended.

```java
final class Utility {
}
```

### Important

A final reference cannot point to another object, but the object itself may still be mutable.

```java
final List<String> list = new ArrayList<>();
list.add("Java");       // allowed
// list = new ArrayList<>(); // not allowed
```

---

# 41. Object Class

Every normal Java class ultimately derives from `Object`.

Common methods:

```java
toString()
equals()
hashCode()
getClass()
```

Example:

```java
class Student {
    String name;

    @Override
    public String toString() {
        return name;
    }
}
```

### Critical interview rule

If you override `equals()`, you should normally also override `hashCode()` consistently.

Contract:

```text
a.equals(b) == true
        =>
a.hashCode() == b.hashCode()
```

---

# 42. Upcasting and Downcasting

## Upcasting

Child → parent.

```java
Dog dog = new Dog();
Animal animal = dog;
```

Usually implicit.

## Downcasting

Parent reference → child type.

```java
Animal animal = new Dog();

Dog dog = (Dog) animal;
```

Explicit cast required.

### Dangerous example

```java
Animal animal = new Cat();

Dog dog = (Dog) animal;
```

This causes:

```text
ClassCastException
```

Use `instanceof` when appropriate:

```java
if (animal instanceof Dog dog) {
    dog.bark();
}
```

---

# 43. Wrapper Classes

Wrapper classes represent primitive values as objects.

| Primitive | Wrapper |
|---|---|
| byte | Byte |
| short | Short |
| int | Integer |
| long | Long |
| float | Float |
| double | Double |
| char | Character |
| boolean | Boolean |

## Boxing

```java
int x = 100;
Integer y = Integer.valueOf(x);
```

## Autoboxing

```java
Integer y = 100;
```

## Unboxing

```java
int x = y.intValue();
```

## Auto-unboxing

```java
int x = y;
```

### Interview trap

```java
Integer a = 100;
Integer b = 100;

System.out.println(a == b);
```

May print `true` because of Integer caching.

Do not use `==` for logical equality of wrapper values. Prefer:

```java
a.equals(b)
```

---

# 44. Abstract Class

An abstract class cannot be instantiated directly.

```java
abstract class Animal {

    abstract void sound();

    void eat() {
        System.out.println("Eating");
    }
}
```

Concrete subclass:

```java
class Dog extends Animal {

    @Override
    void sound() {
        System.out.println("Bark");
    }
}
```

Abstract classes can contain:

- Abstract methods
- Concrete methods
- Fields
- Constructors
- Static methods
- Final methods

---

# 45. Inner Classes

A non-static inner class belongs to an instance of the outer class.

```java
class Outer {

    class Inner {
        void display() {
            System.out.println("Inner");
        }
    }
}
```

Usage:

```java
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();

inner.display();
```

---

# 46. Static Nested Class

A nested class can be static.

```java
class Outer {

    static class Inner {
    }
}
```

Usage:

```java
Outer.Inner inner = new Outer.Inner();
```

No outer object is required.

### Important correction

A **top-level class cannot be declared `static`**. A nested class can be static.

---

# 47. Anonymous Inner Class

An anonymous class creates an unnamed subclass/implementation inline.

```java
abstract class Animal {
    abstract void sound();
}

Animal animal = new Animal() {
    @Override
    void sound() {
        System.out.println("Bark");
    }
};

animal.sound();
```

Useful for one-off implementations.

---

# 48. Interface

An interface defines a contract/type.

```java
interface Payment {

    int TIMEOUT = 30;

    void pay();
}
```

Fields declared in an interface are implicitly:

```text
public static final
```

Abstract instance methods declared without a body are implicitly:

```text
public abstract
```

Interfaces can also contain:

- `default` methods
- `static` methods
- private methods

Modern Java interfaces are more than "only abstract methods."

Implementation:

```java
class UpiPayment implements Payment {

    @Override
    public void pay() {
        System.out.println("UPI");
    }
}
```

Interface inheritance:

```java
interface A {}
interface B extends A {}
```

A class can implement multiple interfaces:

```java
class C implements A, B {
}
```

---

# 49. Multiple Inheritance Through Interfaces

```java
interface A {
    default void show() {
        System.out.println("A");
    }
}

interface B {
    default void show() {
        System.out.println("B");
    }
}

class C implements A, B {

    @Override
    public void show() {
        A.super.show();
    }
}
```

The class can explicitly resolve the default-method conflict.

---

# 50. Functional Interface

A functional interface has exactly **one abstract method**.

```java
@FunctionalInterface
interface Calculator {
    int add(int a, int b);
}
```

It may still contain default/static methods.

---

# 51. Lambda Expression

Lambda is a concise way to provide an implementation for a functional interface.

```java
Calculator c = (a, b) -> a + b;

System.out.println(c.add(10, 20));
```

Think:

```text
Functional Interface
        +
Lambda
        =
Behavior passed as a value
```

---

# 52. Interface Types

Important categories:

## Normal interface

Can have multiple abstract methods.

## Functional interface

Exactly one abstract method.

Examples:

```java
Runnable
Comparator
Predicate
Function
Consumer
Supplier
```

## Marker interface

No abstract methods; communicates metadata/type semantics.

Examples historically include:

```java
Serializable
Cloneable
```

---

# 53. Enum

Enum represents a fixed set of constants.

```java
enum Status {
    CREATED,
    PROCESSING,
    COMPLETED,
    FAILED
}
```

Usage:

```java
Status status = Status.CREATED;
```

Enums can also have:

- Fields
- Constructors
- Methods
- Implement interfaces

---

# 54. Annotations

Annotations provide metadata/instructions associated with program elements.

Example:

```java
@Override
public String toString() {
    return "Student";
}
```

`@Override` helps the compiler verify that a method actually overrides an inherited method.

Common annotations:

```text
@Override
@Deprecated
@SuppressWarnings
@FunctionalInterface
```

Frameworks such as Spring heavily use annotations.

Annotations can have retention policies such as:

```text
SOURCE
CLASS
RUNTIME
```

---

# 55. Exception Hierarchy

Simplified:

```text
Throwable
├── Error
└── Exception
    ├── RuntimeException
    └── Checked Exceptions
```

## Error

Serious JVM/system-level problems.

Examples:

```text
OutOfMemoryError
StackOverflowError
```

Applications generally should not attempt normal recovery from these.

## Checked Exception

Compiler requires handling or declaration.

Example:

```java
IOException
SQLException
```

## Unchecked Exception

Runtime exceptions.

Examples:

```java
NullPointerException
IllegalArgumentException
ArithmeticException
IndexOutOfBoundsException
```

### Important correction

It is better to say **checked vs unchecked exceptions** rather than "compile-time exception vs runtime exception." All exceptions ultimately occur at runtime; checked exceptions are the ones the compiler enforces handling/declaring.

---

# 56. try-catch-finally

```java
try {
    int x = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Cannot divide by zero");
} finally {
    System.out.println("Cleanup");
}
```

`finally` is generally used for cleanup.

For resource management, prefer **try-with-resources**.

---

# 57. Try-With-Resources

```java
try (BufferedReader br =
         new BufferedReader(new FileReader("data.txt"))) {

    System.out.println(br.readLine());

} catch (IOException e) {
    e.printStackTrace();
}
```

The resource is automatically closed.

The resource should implement `AutoCloseable`.

---

# 58. `throw` vs `throws`

## throw

Actually throws an exception.

```java
throw new IllegalArgumentException("Invalid age");
```

## throws

Declares that a method may propagate exceptions.

```java
void readFile() throws IOException {
}
```

Memory trick:

```text
throw  → action
throws → declaration
```

---

# 59. Custom Exception

```java
class InvalidAgeException extends Exception {

    public InvalidAgeException(String message) {
        super(message);
    }
}
```

Usage:

```java
if (age < 18) {
    throw new InvalidAgeException("Age must be 18+");
}
```

---

# 60. Thread Basics

A thread is an independent execution path within a process.

## Extending Thread

```java
class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("Running");
    }
}

MyThread t = new MyThread();
t.start();
```

## Runnable

Prefer separating task from thread management:

```java
Runnable task = () -> {
    System.out.println("Running");
};

Thread t = new Thread(task);
t.start();
```

### Critical interview question: start() vs run()

```java
t.start();
```

Requests a new thread to execute `run()`.

```java
t.run();
```

Is just a normal method call and does not create a new thread.

---

# 61. Thread Lifecycle / States

Actual Java `Thread.State` values are:

```text
NEW
RUNNABLE
BLOCKED
WAITING
TIMED_WAITING
TERMINATED
```

A common conceptual diagram:

```text
NEW
 |
 | start()
 v
RUNNABLE
 |
 | scheduler executes
 v
executing
 |
 +--> BLOCKED
 |
 +--> WAITING
 |
 +--> TIMED_WAITING
 |
 +--> TERMINATED
```

### Important correction

"RUNNING" is commonly used conceptually, but Java's official `Thread.State` enum does **not** have a separate `RUNNING` state. A thread executing on CPU is represented under `RUNNABLE`.

---

# 62. sleep(), wait(), join()

## sleep

```java
Thread.sleep(1000);
```

Puts the current thread into timed waiting.

It does **not** release intrinsic locks held by the thread.

## wait

```java
object.wait();
```

Used for coordination and releases the object's monitor while waiting.

Must be called while owning that object's monitor.

## join

```java
thread.join();
```

Makes the current thread wait for another thread to terminate.

---

# 63. Thread Priority

Java threads have priorities, but priority is only a scheduling hint and should not be used as a correctness mechanism.

```java
thread.setPriority(Thread.MAX_PRIORITY);
```

Do not design correctness around exact scheduling order.

---

# 64. Race Condition

A race condition occurs when the result depends on timing/interleaving between concurrent operations.

Example:

```java
count++;
```

This is not a single conceptual atomic operation. It involves reading, modifying, and writing.

Two threads can interfere.

Solutions may include:

- `synchronized`
- `Lock`
- Atomic classes
- Concurrent collections
- Proper immutable design
- Higher-level concurrency abstractions

---

# 65. `synchronized`

```java
synchronized void increment() {
    count++;
}
```

Or:

```java
synchronized (lock) {
    count++;
}
```

It provides mutual exclusion and important memory-visibility guarantees.

### Senior question

Be ready to explain:

- Object monitor
- Lock ownership
- Visibility
- Happens-before
- Contention
- Deadlock

---

# 66. Collections Framework

Collections provide reusable data structures and algorithms.

```text
Collection
├── List
├── Set
└── Queue

Map   ← separate hierarchy
```

## List

Ordered collection, duplicates allowed.

Examples:

```text
ArrayList
LinkedList
Vector
```

## Set

No duplicate elements.

Examples:

```text
HashSet
LinkedHashSet
TreeSet
```

## Queue

Useful for processing elements in an ordering model.

Examples:

```text
PriorityQueue
Deque
ArrayDeque
```

## Map

Key-value structure.

Examples:

```text
HashMap
LinkedHashMap
TreeMap
Hashtable
ConcurrentHashMap
```

---

# 67. ArrayList

Dynamic array implementation.

```java
List<String> names = new ArrayList<>();

names.add("Java");
names.add("Spring");
```

Strengths:

- Fast random access
- Good cache locality
- Amortized efficient append

Typical complexity:

| Operation | Typical |
|---|---:|
| get(index) | O(1) |
| add(end) | Amortized O(1) |
| insert middle | O(n) |
| remove middle | O(n) |

---

# 68. HashSet

Stores unique elements.

```java
Set<String> names = new HashSet<>();

names.add("Java");
names.add("Java");
```

Only one `"Java"` remains.

Uses hashing internally.

---

# 69. TreeSet

Sorted set.

```java
Set<Integer> nums = new TreeSet<>();

nums.add(30);
nums.add(10);
nums.add(20);

System.out.println(nums);
```

Output:

```text
[10, 20, 30]
```

Typically based on a balanced search tree and gives O(log n) basic operations.

---

# 70. HashMap

Key-value data structure.

```java
Map<Integer, String> users = new HashMap<>();

users.put(1, "John");
users.put(2, "Alex");

System.out.println(users.get(1));
```

Important concepts:

- Hashing
- Bucket
- Hash collision
- `equals()`
- `hashCode()`
- Resizing
- Load factor

### Critical interview statement

For a key:

```text
hashCode()
   ↓
bucket selection
   ↓
equals() for candidate keys
```

Both `equals()` and `hashCode()` matter.

---

# 71. Hashtable

`Hashtable` is a legacy synchronized map implementation.

Prefer modern alternatives such as:

```text
HashMap
ConcurrentHashMap
```

depending on the requirement.

---

# 72. Generics

Generics provide compile-time type safety.

Without generics:

```java
List list = new ArrayList();
list.add("Java");
list.add(10);
```

With generics:

```java
List<String> list = new ArrayList<>();
list.add("Java");
```

Now the compiler prevents invalid types.

### Important senior topic

Generics use **type erasure** in Java.

---

# 73. Comparable vs Comparator

## Comparable

Defines natural ordering inside the class.

```java
class Student implements Comparable<Student> {

    int age;

    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.age, other.age);
    }
}
```

Usage:

```java
Collections.sort(students);
```

## Comparator

Defines external/custom ordering.

```java
Comparator<Student> byAge =
    Comparator.comparingInt(s -> s.age);
```

### Memory Trick

**Comparable = class compares itself**  
**Comparator = external comparison rule**

---

# 74. Stream API

Streams process data declaratively.

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);

List<Integer> result = nums.stream()
        .filter(n -> n % 2 == 0)
        .map(n -> n * 10)
        .toList();
```

Flow:

```text
Source
  ↓
filter
  ↓
map
  ↓
terminal operation
```

### Important correction

A stream does **not necessarily make a copy of the collection**.

A stream is a pipeline for processing data from a source.

It also does not modify the source merely by existing. Whether a particular operation mutates an object depends on the operation/code.

---

# 75. Stream Operations

## Intermediate operations

Usually lazy:

```text
filter
map
sorted
distinct
limit
```

## Terminal operations

Trigger processing:

```text
forEach
collect
reduce
count
findFirst
findAny
```

A stream is generally consumed by a terminal operation and should not be reused afterward.

---

# 76. Parallel Stream

```java
nums.parallelStream()
    .map(...)
    .toList();
```

Can process work concurrently using the common ForkJoinPool.

### Important

Parallel stream is not automatically faster.

It can hurt performance when:

- Dataset is small
- Work is cheap
- Operations are blocking
- Ordering is important
- Shared mutable state exists
- Thread-pool behavior is unsuitable

Use it based on measurement and workload characteristics.

---

# 77. Optional

Represents a value that may or may not be present.

```java
Optional<String> name =
    Optional.ofNullable(getName());

name.ifPresent(System.out::println);
```

Example:

```java
Optional<Integer> result =
    nums.stream().filter(n -> n > 100).findFirst();
```

You can use:

```java
result.orElse(0);
```

### Important correction

Optional does **not automatically throw an error when no value exists**. It provides APIs such as `orElse`, `orElseGet`, `orElseThrow`, and `isPresent`.

---

# 78. Method Reference

Shorter syntax for certain lambdas.

Instead of:

```java
nums.forEach(n -> System.out.println(n));
```

Use:

```java
nums.forEach(System.out::println);
```

Types include:

```text
Class::staticMethod
object::instanceMethod
Class::instanceMethod
Class::new
```

---

# 79. Constructor Reference

```java
Function<String, Student> creator = Student::new;
```

Equivalent conceptually to:

```java
Function<String, Student> creator =
    name -> new Student(name);
```

---

# 80. `var` — Local Variable Type Inference

Introduced in Java 10.

```java
var name = "Java";
var count = 10;
var student = new Student();
```

The compiler infers the static type.

Important:

```java
var x = 10;
```

means `x` is still statically typed as `int`; `var` does not make Java dynamically typed.

`var` is for local variable declarations, not normal instance fields.

---

# 81. Sealed Classes

Sealed classes restrict which classes may directly extend them.

```java
sealed class Payment
        permits CardPayment, CashPayment {
}

final class CardPayment extends Payment {
}

non-sealed class CashPayment extends Payment {
}
```

Possible modifiers:

```text
final
sealed
non-sealed
```

### Purpose

Useful when you want controlled inheritance and a known hierarchy.

---

# 82. Records

Records provide concise syntax for data-carrying classes.

```java
public record Student(
        String name,
        int age
) {
}
```

A record automatically provides components/accessors, `equals`, `hashCode`, and `toString`, subject to record semantics.

Example:

```java
Student s = new Student("John", 30);

System.out.println(s.name());
System.out.println(s.age());
```

### Important correction

Record components are not simply "private and final parameters." A record is a special final class whose components correspond to private final fields and public accessor methods.

---

# 83. Canonical Constructor

For:

```java
record Student(String name, int age) {
}
```

The canonical constructor corresponds to all record components:

```java
Student(String name, int age)
```

You can explicitly define it:

```java
record Student(String name, int age) {

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

---

# 84. Compact Canonical Constructor

You can validate/normalize without explicitly assigning every field.

```java
record Student(String name, int age) {

    public Student {
        if (age < 0) {
            throw new IllegalArgumentException("Invalid age");
        }
    }
}
```

The compiler supplies the component field assignments.

---

# 85. High-Value Interview Comparisons

## JDK vs JRE vs JVM

```text
JDK → development
JRE → runtime environment concept
JVM → bytecode execution
```

## Stack vs Heap

| Stack | Heap |
|---|---|
| Per-thread | Shared |
| Stack frames | Objects/arrays |
| Method execution data | Object state |
| Automatically unwound with calls | Managed by GC |

## StringBuilder vs StringBuffer

| StringBuilder | StringBuffer |
|---|---|
| Mutable | Mutable |
| Not synchronized | Synchronized |
| Usually faster | Usually slower |
| Preferred for local single-threaded building | Useful when synchronized mutable string operations are required |

## Overloading vs Overriding

| Overloading | Overriding |
|---|---|
| Same class commonly | Parent-child relationship |
| Different parameters | Same signature |
| Compile-time selection | Runtime dispatch |
| Compile-time polymorphism | Runtime polymorphism |

## Abstract Class vs Interface

| Abstract Class | Interface |
|---|---|
| Can have instance state | Fields are constants by default |
| Constructors allowed | No instance constructors |
| Single class inheritance | Multiple interfaces can be implemented |
| Abstract + concrete methods | Abstract + default + static + private methods |

---

# 86. Common Interview Traps

### Trap 1

**"Java is 100% platform independent."**

Better:

> Java source/bytecode is portable, while JVM implementations are platform-specific.

### Trap 2

**"All objects are always on heap and all primitives are always on stack."**

Too simplistic.

Explain in terms of variables, fields, arrays, escape analysis, and JVM implementation when discussing advanced memory behavior.

### Trap 3

**"String pool means every String is stored only there."**

False.

String literals are interned; Strings created with `new` are ordinary heap objects unless interned.

### Trap 4

**"Static block executes every time an object is created."**

False.

Class initialization happens once per class initialization.

### Trap 5

**"Java supports multiple inheritance."**

Java does not support multiple inheritance of classes, but it supports implementing multiple interfaces.

### Trap 6

**"Optional throws an exception when empty."**

False.

`orElseThrow()` throws; Optional itself does not automatically throw merely because it is empty.

### Trap 7

**"Parallel streams are always faster."**

False.

Measure and consider workload characteristics.

### Trap 8

**"RUNNING is a Java Thread.State."**

False.

Official states include `RUNNABLE`, not a separate `RUNNING`.

### Trap 9

**"Changing return type creates method overloading."**

False.

Parameter list must differ.

### Trap 10

**"HashMap uses only hashCode()."**

False.

Hashing helps locate candidates; equality checks determine key equality.

---

# 87. Senior-Level Questions You Must Be Able to Explain

Before a 7–10 year interview, be ready to answer these without memorized definitions:

1. Explain the complete lifecycle of a Java program from `.java` to machine execution.
2. Explain JVM memory areas.
3. What happens when `new Object()` executes?
4. How does class loading work?
5. What is parent delegation?
6. What is JIT compilation?
7. How does Java GC determine whether an object is reachable?
8. What causes `OutOfMemoryError`?
9. What causes `StackOverflowError`?
10. Explain `equals()` and `hashCode()`.
11. How does HashMap work internally?
12. What happens when HashMap gets many collisions?
13. Why are immutable objects useful?
14. Why is String immutable?
15. ArrayList vs LinkedList.
16. HashSet vs TreeSet.
17. HashMap vs ConcurrentHashMap.
18. Comparable vs Comparator.
19. Fail-fast vs weakly consistent iteration.
20. Checked vs unchecked exceptions.
21. `throw` vs `throws`.
22. `start()` vs `run()`.
23. `sleep()` vs `wait()`.
24. `synchronized` and intrinsic locks.
25. What is a race condition?
26. What is deadlock?
27. What is starvation?
28. What is livelock?
29. What is volatile?
30. What is happens-before?
31. AtomicInteger vs synchronized counter.
32. ExecutorService vs manually creating threads.
33. How does ForkJoinPool work?
34. Why can parallel streams be dangerous?
35. Stream intermediate vs terminal operations.
36. What is lazy evaluation in streams?
37. What is type erasure?
38. What are PECS and bounded wildcards?
39. Why use records?
40. When would you use sealed classes?
41. Abstract class vs interface in real architecture.
42. Composition vs inheritance.
43. How would you diagnose a Java production memory problem?
44. How would you diagnose high CPU in a Java application?
45. How would you investigate thread contention?
46. How would you tune an API that suddenly becomes slow under load?
47. How would you design a thread-safe cache?
48. How would you process millions of records without exhausting memory?
49. How would you prevent concurrent updates from corrupting data?
50. How would you design a scalable Java service?

---

# 88. Practical Coding Questions

Practice these repeatedly.

## Beginner → Intermediate

1. Reverse a String.
2. Check palindrome.
3. Count character frequency.
4. Find duplicate characters.
5. Find first non-repeated character.
6. Reverse an integer.
7. Check prime number.
8. Fibonacci.
9. Factorial.
10. Find max/min in an array.
11. Remove duplicates.
12. Sort an array.
13. Find second-largest number.
14. Merge two sorted arrays.
15. Rotate an array.
16. Find missing number.
17. Find duplicate number.
18. Check anagrams.
19. Count words.
20. Find common elements.

## Java Collections

21. Sort objects using Comparator.
22. Group employees by department using streams.
23. Find highest salary by department.
24. Find duplicate values using a Set.
25. Convert List to Map.
26. Handle duplicate keys with `Collectors.toMap`.
27. Find top N records.
28. Partition data using `partitioningBy`.
29. Flatten nested lists using `flatMap`.
30. Find first matching record using Optional.

## Concurrency

31. Build a thread-safe counter.
32. Producer-consumer.
33. Implement a bounded queue.
34. Demonstrate race condition.
35. Fix race condition using synchronized.
36. Fix using AtomicInteger.
37. Use ExecutorService.
38. Use Callable and Future.
39. Demonstrate deadlock and explain prevention.
40. Build a simple concurrent cache.

---

# 89. Stream API Revision Sheet

Remember:

```text
filter → keep/remove
map → transform
flatMap → flatten
sorted → order
distinct → remove duplicates
limit → take first N
skip → ignore first N

forEach → consume
collect → gather
reduce → combine
count → count
findFirst → first
findAny → any
anyMatch → at least one
allMatch → all
noneMatch → none
```

Example:

```java
List<String> names = List.of("John", "Alex", "Bob", "Andrew");

List<String> result = names.stream()
        .filter(n -> n.startsWith("A"))
        .map(String::toUpperCase)
        .sorted()
        .toList();
```

---

# 90. Java Concurrency Revision Sheet

Know these deeply:

```text
Thread
Runnable
Callable
ExecutorService
Future
CompletableFuture
synchronized
volatile
Lock
ReentrantLock
AtomicInteger
ConcurrentHashMap
BlockingQueue
CountDownLatch
Semaphore
CyclicBarrier
ForkJoinPool
```

For senior interviews, don't stop at definitions. Explain:

```text
Problem
  ↓
Why it occurs
  ↓
Possible solutions
  ↓
Trade-off
  ↓
Production use case
```

---

# 91. The 4 → 10 Year Progression

## 4–5 Years

Focus on:

- Core Java
- OOP
- Collections
- Exception handling
- Java 8+
- Streams
- Lambdas
- Basic concurrency
- SQL
- Spring/Spring Boot
- REST APIs
- Unit testing
- Git
- Debugging

## 5–7 Years

Add:

- JVM internals
- GC
- Memory troubleshooting
- Concurrent collections
- CompletableFuture
- Executor framework
- Advanced generics
- Design patterns
- Performance optimization
- Caching
- Messaging
- Database transactions
- Microservices
- Observability

## 7–10 Years

Expect:

- Architecture
- System design
- Distributed systems
- Scalability
- Reliability
- Resilience
- Concurrency design
- JVM performance
- GC analysis
- Production troubleshooting
- Security
- Cloud
- CI/CD
- Kubernetes
- Event-driven architecture
- Technical leadership
- Trade-off discussions
- Mentoring and engineering decisions

---

# 92. How to Answer a Senior Interview Question

Use this structure:

```text
1. Definition
2. Internal working
3. Example
4. Real-world use case
5. Advantages
6. Limitations/trade-offs
7. Production consideration
```

Example: **HashMap**

> HashMap stores key-value pairs using hashing. When a key is inserted, Java calculates its hash and uses it to identify a bucket. If multiple keys map to the same bucket, collision handling is required. Equality is then used to identify the correct key. HashMap provides expected constant-time lookup under good hashing, but resizing, collisions, poor key design, and concurrency can affect behavior. For concurrent access, I would consider ConcurrentHashMap rather than using HashMap unsafely.

That is much stronger than:

> HashMap stores key-value pairs.

---

# 93. Final Revision Checklist

## Java Fundamentals

- [ ] WORA
- [ ] JDK/JRE/JVM
- [ ] javac
- [ ] Bytecode
- [ ] Class Loader
- [ ] JVM memory
- [ ] Interpreter
- [ ] JIT
- [ ] Garbage Collection

## Language

- [ ] Primitive/reference types
- [ ] Literals
- [ ] Variables
- [ ] Type conversion
- [ ] Type promotion
- [ ] Operators
- [ ] Conditions
- [ ] Loops
- [ ] Arrays

## OOP

- [ ] Class/object
- [ ] Encapsulation
- [ ] Inheritance
- [ ] Polymorphism
- [ ] Overloading
- [ ] Overriding
- [ ] Dynamic dispatch
- [ ] Abstract class
- [ ] Interface
- [ ] Composition vs inheritance
- [ ] `this`
- [ ] `super`
- [ ] Constructors

## Java Features

- [ ] Static
- [ ] Final
- [ ] Enum
- [ ] Annotation
- [ ] Lambda
- [ ] Functional interface
- [ ] Method reference
- [ ] Optional
- [ ] Stream API
- [ ] Parallel stream
- [ ] `var`
- [ ] Records
- [ ] Sealed classes

## Collections

- [ ] ArrayList
- [ ] LinkedList
- [ ] HashSet
- [ ] TreeSet
- [ ] HashMap
- [ ] TreeMap
- [ ] LinkedHashMap
- [ ] Hashtable
- [ ] ConcurrentHashMap
- [ ] Queue
- [ ] Deque
- [ ] Generics
- [ ] Comparable
- [ ] Comparator

## Exceptions

- [ ] Throwable hierarchy
- [ ] Error
- [ ] Checked exception
- [ ] Unchecked exception
- [ ] try/catch/finally
- [ ] try-with-resources
- [ ] throw
- [ ] throws
- [ ] Custom exception

## Concurrency

- [ ] Thread
- [ ] Runnable
- [ ] Callable
- [ ] Thread states
- [ ] start vs run
- [ ] sleep
- [ ] wait
- [ ] notify/notifyAll
- [ ] join
- [ ] synchronized
- [ ] volatile
- [ ] Race condition
- [ ] Deadlock
- [ ] ExecutorService
- [ ] Future
- [ ] CompletableFuture
- [ ] Atomic classes
- [ ] Locks
- [ ] Concurrent collections

---

# 94. Final Interview Mindset

At 4 years, an interviewer asks:

> "Do you know Java?"

At 7–10 years, they increasingly ask:

> "Why did you choose this approach, what happens internally, what can go wrong under production load, and what trade-off did you make?"

So don't prepare Java as a list of definitions.

Prepare every topic using:

```text
WHAT?
 ↓
WHY?
 ↓
HOW?
 ↓
INTERNALS?
 ↓
EXAMPLE?
 ↓
TRADE-OFF?
 ↓
PRODUCTION SCENARIO?
```

If you can explain a concept at all seven levels, you are moving from **mid-level knowledge toward senior-level interview readiness**.
