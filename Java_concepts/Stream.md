# Java Streams — Interview Reference

## 1. What is Stream?

A **Stream** is used to process data from a collection/array in a declarative way.

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);

nums.stream()
    .filter(x -> x > 2)
    .forEach(System.out::println);
```

### Stream does NOT store data

```text
Collection → stores data
Stream     → processes data
```

A Stream normally **does not modify the original collection**.

---

# 2. Why Streams?

### Traditional for-loop

```java
for (Integer n : nums) {
    if (n > 2) {
        System.out.println(n);
    }
}
```

### Stream

```java
nums.stream()
    .filter(n -> n > 2)
    .forEach(System.out::println);
```

Streams make operations like **filtering, mapping, sorting, grouping and collecting** easier to express.

---

# 3. Stream Pipeline

A stream normally has:

```text
Source → Intermediate Operations → Terminal Operation
```

Example:

```java
nums.stream()              // Source
    .filter(x -> x > 2)    // Intermediate
    .map(x -> x * 2)       // Intermediate
    .forEach(System.out::println); // Terminal
```

### Important

Intermediate operations are **lazy**.

They execute only when a terminal operation is called.

---

# 4. Creating Streams

## Collection → Stream

```java
List<Integer> nums = List.of(1, 2, 3);

Stream<Integer> stream = nums.stream();
```

For collections:

```text
List → stream()
Set  → stream()
```

---

## Array → Stream

### Object array

```java
Integer[] nums = {1, 2, 3};

Arrays.stream(nums);
```

### Primitive array

```java
int[] nums = {1, 2, 3};

Arrays.stream(nums);
```

This produces:

```java
IntStream
```

---

# 5. Stream vs IntStream / LongStream / DoubleStream

Java provides specialized streams for primitives.

```text
Stream<Integer>   → objects
IntStream         → int
LongStream        → long
DoubleStream      → double
```

Example:

```java
int[] nums = {1, 2, 3, 4};

IntStream stream = Arrays.stream(nums);
```

Why?

Primitive streams avoid unnecessary boxing/unboxing.

---

# 6. Primitive Stream → Stream<Integer>

Sometimes you need collection operations such as `collect()`.

```java
int[] nums = {1, 2, 3, 4};

List<Integer> list =
    Arrays.stream(nums)
          .boxed()
          .collect(Collectors.toList());
```

### `boxed()`

Converts:

```text
IntStream
    ↓
Stream<Integer>
```

Example:

```java
IntStream → Stream<Integer>
```

---

# 7. Basic Intermediate Operations

Intermediate operations return another Stream.

---

## filter()

Select elements based on a condition.

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);

nums.stream()
    .filter(x -> x > 3)
    .forEach(System.out::println);
```

Output:

```text
4
5
```

Use when:

> "I want only elements satisfying a condition."

---

# 8. map()

Transforms every element.

```java
List<Integer> nums = List.of(1, 2, 3);

nums.stream()
    .map(x -> x * 2)
    .forEach(System.out::println);
```

Output:

```text
2
4
6
```

Think:

```text
Integer → Integer
Employee → String
String → Integer
```

Example:

```java
List<String> names = List.of("Tom", "John");

List<Integer> lengths =
    names.stream()
         .map(String::length)
         .toList();
```

---

# 9. mapToInt()

Converts object stream to `IntStream`.

```java
List<String> names = List.of("Tom", "John", "Sam");

int totalLength =
    names.stream()
         .mapToInt(String::length)
         .sum();
```

This is useful for numeric operations.

```text
Stream<T>
   ↓
mapToInt()
   ↓
IntStream
```

Similarly:

```java
mapToLong()
mapToDouble()
```

---

# 10. flatMap()

Used when each element contains another collection/stream.

### Without flatMap

```java
List<List<Integer>> nums =
    List.of(
        List.of(1, 2),
        List.of(3, 4)
    );
```

This is:

```text
List<List<Integer>>
```

### With flatMap

```java
List<Integer> result =
    nums.stream()
        .flatMap(List::stream)
        .toList();
```

Result:

```text
[1, 2, 3, 4]
```

Think:

```text
map     → one → one
flatMap → one → many → flattened
```

---

# 11. distinct()

Removes duplicates.

```java
List<Integer> nums = List.of(1, 2, 2, 3, 3);

List<Integer> result =
    nums.stream()
        .distinct()
        .toList();
```

Result:

```text
[1, 2, 3]
```

For objects, `equals()` and `hashCode()` matter.

---

# 12. sorted()

Sorts elements.

### Natural order

```java
List<Integer> result =
    nums.stream()
        .sorted()
        .toList();
```

### Custom order

```java
List<String> names =
    List.of("Tom", "Alexander", "Bob");

List<String> result =
    names.stream()
         .sorted(Comparator.comparing(String::length))
         .toList();
```

---

# 13. limit()

Takes only the first N elements.

```java
nums.stream()
    .limit(3)
    .forEach(System.out::println);
```

```text
1 2 3
```

Useful for:

```text
Top N records
Pagination-like processing
```

---

# 14. skip()

Skips first N elements.

```java
nums.stream()
    .skip(2)
    .forEach(System.out::println);
```

```text
3 4 5
```

---

# 15. peek()

Used mainly for debugging.

```java
nums.stream()
    .filter(x -> x > 2)
    .peek(x -> System.out.println("After filter: " + x))
    .map(x -> x * 2)
    .toList();
```

Avoid using `peek()` for important business logic.

---

# 16. Terminal Operations

Terminal operations produce the final result.

Common ones:

```text
forEach()
collect()
toList()
count()
min()
max()
findFirst()
findAny()
anyMatch()
allMatch()
noneMatch()
reduce()
```

---

# 17. forEach()

Processes every element.

```java
nums.stream()
    .forEach(System.out::println);
```

---

# 18. toList()

Converts stream to a List.

```java
List<Integer> result =
    nums.stream()
        .filter(x -> x > 2)
        .toList();
```

Modern Java:

```java
stream.toList();
```

---

# 19. collect()

More flexible than `toList()`.

```java
List<Integer> result =
    nums.stream()
        .filter(x -> x > 2)
        .collect(Collectors.toList());
```

Common collectors:

```java
Collectors.toList()
Collectors.toSet()
Collectors.toMap()
Collectors.groupingBy()
Collectors.partitioningBy()
Collectors.joining()
```

---

# 20. toSet()

```java
Set<Integer> result =
    nums.stream()
        .collect(Collectors.toSet());
```

Useful when you want unique values.

---

# 21. toMap()

Convert objects into a Map.

```java
List<String> names = List.of("Tom", "John");

Map<String, Integer> result =
    names.stream()
         .collect(Collectors.toMap(
             name -> name,
             String::length
         ));
```

Result:

```text
Tom  → 3
John → 4
```

Shortcut:

```java
Collectors.toMap(
    Function.identity(),
    String::length
);
```

---

# 22. count()

Counts elements.

```java
long count =
    nums.stream()
        .filter(x -> x > 2)
        .count();
```

---

# 23. min() / max()

```java
Optional<Integer> min =
    nums.stream().min(Integer::compareTo);

Optional<Integer> max =
    nums.stream().max(Integer::compareTo);
```

Why `Optional`?

Because the stream may be empty.

---

# 24. findFirst()

Returns first element.

```java
Optional<Integer> result =
    nums.stream()
        .filter(x -> x > 2)
        .findFirst();
```

---

# 25. findAny()

Returns any matching element.

```java
Optional<Integer> result =
    nums.stream()
        .filter(x -> x > 2)
        .findAny();
```

Especially useful with parallel streams.

---

# 26. anyMatch()

Checks whether **at least one** matches.

```java
boolean result =
    nums.stream()
        .anyMatch(x -> x > 4);
```

---

# 27. allMatch()

Checks whether **all** match.

```java
boolean result =
    nums.stream()
        .allMatch(x -> x > 0);
```

---

# 28. noneMatch()

Checks whether **none** match.

```java
boolean result =
    nums.stream()
        .noneMatch(x -> x < 0);
```

### Remember

```text
anyMatch  → at least one
allMatch  → everyone
noneMatch → nobody
```

These are short-circuiting operations.

---

# 29. reduce()

Combines all elements into one result.

```java
int sum =
    nums.stream()
        .reduce(0, (a, b) -> a + b);
```

Result:

```text
15
```

Can also use:

```java
int sum =
    nums.stream()
        .reduce(0, Integer::sum);
```

Think:

```text
1 + 2 + 3 + 4 + 5
        ↓
       15
```

---

# 30. Primitive Stream Methods

Primitive streams have numeric methods.

## IntStream

```java
int[] nums = {1, 2, 3, 4, 5};

Arrays.stream(nums).sum();
```

### count()

```java
long count =
    Arrays.stream(nums).count();
```

### average()

```java
OptionalDouble avg =
    Arrays.stream(nums).average();
```

### min()

```java
OptionalInt min =
    Arrays.stream(nums).min();
```

### max()

```java
OptionalInt max =
    Arrays.stream(nums).max();
```

---

# 31. range()

Generate primitive numbers.

```java
IntStream.range(1, 5)
         .forEach(System.out::println);
```

Output:

```text
1 2 3 4
```

End value is excluded.

---

# 32. rangeClosed()

Includes the end value.

```java
IntStream.rangeClosed(1, 5)
         .forEach(System.out::println);
```

Output:

```text
1 2 3 4 5
```

Remember:

```text
range()       → end excluded
rangeClosed() → end included
```

---

# 33. String + Stream

Convert String to character stream.

```java
String str = "hello";

str.chars()
   .forEach(System.out::println);
```

`chars()` returns an `IntStream`.

Example:

```java
long count =
    str.chars()
       .filter(c -> c == 'l')
       .count();
```

---

# 34. Joining Strings

```java
List<String> names =
    List.of("Tom", "John", "Sam");

String result =
    names.stream()
         .collect(Collectors.joining(", "));
```

Result:

```text
Tom, John, Sam
```

---

# 35. groupingBy()

Very common interview + real-project operation.

Example:

```java
List<Employee> employees = ...;
```

Group employees by department:

```java
Map<String, List<Employee>> result =
    employees.stream()
             .collect(Collectors.groupingBy(
                 Employee::getDepartment
             ));
```

Result conceptually:

```text
IT     → [Employee1, Employee2]
HR     → [Employee3]
Finance → [Employee4]
```

Think:

```text
groupingBy → SQL GROUP BY
```

---

# 36. groupingBy() + counting()

Count employees per department.

```java
Map<String, Long> result =
    employees.stream()
             .collect(Collectors.groupingBy(
                 Employee::getDepartment,
                 Collectors.counting()
             ));
```

---

# 37. partitioningBy()

Splits data into exactly two groups:

```text
true
false
```

Example:

```java
Map<Boolean, List<Integer>> result =
    nums.stream()
        .collect(Collectors.partitioningBy(
            x -> x > 3
        ));
```

Concept:

```text
true  → numbers > 3
false → numbers <= 3
```

### Difference

```text
groupingBy     → multiple groups
partitioningBy → two groups
```

---

# 38. Collecting Statistics

For primitive streams:

```java
IntSummaryStatistics stats =
    Arrays.stream(nums)
          .summaryStatistics();
```

Then:

```java
stats.getCount();
stats.getSum();
stats.getMin();
stats.getMax();
stats.getAverage();
```

Useful when you need multiple statistics in one operation.

---

# 39. Stream of Objects → Numeric Calculation

Example:

```java
List<Employee> employees = ...;

double averageSalary =
    employees.stream()
             .mapToDouble(Employee::getSalary)
             .average()
             .orElse(0);
```

Flow:

```text
Stream<Employee>
       ↓
mapToDouble()
       ↓
DoubleStream
       ↓
average()
```

---

# 40. Sorting Objects

```java
employees.stream()
         .sorted(Comparator.comparing(Employee::getSalary))
         .forEach(System.out::println);
```

Descending:

```java
employees.stream()
         .sorted(
             Comparator.comparing(Employee::getSalary)
                       .reversed()
         )
         .forEach(System.out::println);
```

Multiple fields:

```java
employees.stream()
         .sorted(
             Comparator.comparing(Employee::getDepartment)
                       .thenComparing(Employee::getSalary)
         );
```

---

# 41. Multiple Conditions

```java
List<Employee> result =
    employees.stream()
             .filter(e -> e.getSalary() > 50000)
             .filter(e -> e.getDepartment().equals("IT"))
             .toList();
```

Can also combine:

```java
.filter(e ->
    e.getSalary() > 50000 &&
    e.getDepartment().equals("IT")
)
```

---

# 42. Optional with Streams

Many stream operations return `Optional`.

Example:

```java
Optional<Integer> result =
    nums.stream()
        .filter(x -> x > 10)
        .findFirst();
```

Handle safely:

```java
result.ifPresent(System.out::println);
```

Or:

```java
int value = result.orElse(0);
```

---

# 43. Short-Circuit Operations

These may stop processing early.

```text
findFirst()
findAny()
anyMatch()
allMatch()
noneMatch()
limit()
```

Example:

```java
nums.stream()
    .filter(x -> x > 3)
    .findFirst();
```

Once the first matching element is found, processing can stop.

---

# 44. Stream Cannot Normally Be Reused

Wrong:

```java
Stream<Integer> stream = nums.stream();

stream.filter(x -> x > 2).toList();

stream.filter(x -> x < 5).toList(); // Error
```

A stream is consumed after a terminal operation.

Correct:

```java
nums.stream().filter(x -> x > 2).toList();

nums.stream().filter(x -> x < 5).toList();
```

---

# 45. Parallel Stream

Normal:

```java
nums.stream()
```

Parallel:

```java
nums.parallelStream()
```

Parallel streams divide work between multiple threads.

Use carefully.

```java
nums.parallelStream()
    .filter(x -> x > 100)
    .toList();
```

Do not assume:

```text
parallelStream = always faster
```

For small/simple operations, parallel processing can add overhead.

---

# 46. Stream vs Collection

| Collection              | Stream                           |
| ----------------------- | -------------------------------- |
| Stores data             | Processes data                   |
| Can usually be reused   | Cannot normally be reused        |
| Data structure          | Processing pipeline              |
| Can add/remove elements | Does not normally modify source  |
| Eager                   | Intermediate operations are lazy |

---

# 47. Stream vs Primitive Stream

| Stream                   | Primitive Stream                |
| ------------------------ | ------------------------------- |
| `Stream<Integer>`        | `IntStream`                     |
| Objects                  | Primitive values                |
| Boxing required for int  | No boxing                       |
| `collect()` available    | Numeric operations like `sum()` |
| Example: `List<Integer>` | Example: `int[]`                |

Example:

```java
List<Integer> list = List.of(1, 2, 3);

Stream<Integer> s = list.stream();
```

```java
int[] arr = {1, 2, 3};

IntStream s = Arrays.stream(arr);
```

Convert:

```java
IntStream
    .boxed()
    ↓
Stream<Integer>
```

---

# 48. Most Important Methods — Interview Cheat Sheet

## Intermediate

```text
filter()       → select
map()          → transform
flatMap()      → flatten
distinct()     → remove duplicates
sorted()       → sort
limit()        → first N
skip()         → skip N
peek()         → debugging
mapToInt()     → Stream → IntStream
mapToLong()    → Stream → LongStream
mapToDouble()  → Stream → DoubleStream
```

## Terminal

```text
forEach()      → process
toList()       → List
collect()      → collect result
count()        → count
min()          → minimum
max()          → maximum
findFirst()    → first
findAny()      → any
anyMatch()     → at least one
allMatch()     → all
noneMatch()    → none
reduce()       → combine into one
```

## Collectors

```text
toList()
toSet()
toMap()
joining()
groupingBy()
partitioningBy()
counting()
```

## Primitive Streams

```text
IntStream
LongStream
DoubleStream

sum()
average()
min()
max()
count()
boxed()
range()
rangeClosed()
summaryStatistics()
```

---

# 49. Common Interview Patterns

### Filter + Collect

```java
List<Integer> result =
    nums.stream()
        .filter(x -> x % 2 == 0)
        .toList();
```

### Transform + Collect

```java
List<Integer> result =
    nums.stream()
        .map(x -> x * 2)
        .toList();
```

### Filter + Map

```java
List<String> result =
    names.stream()
         .filter(name -> name.length() > 3)
         .map(String::toUpperCase)
         .toList();
```

### Sum

```java
int sum =
    nums.stream()
        .mapToInt(Integer::intValue)
        .sum();
```

### Find Maximum

```java
Optional<Integer> max =
    nums.stream()
        .max(Integer::compareTo);
```

### Remove Duplicates

```java
List<Integer> result =
    nums.stream()
        .distinct()
        .toList();
```

### Group Objects

```java
Map<String, List<Employee>> result =
    employees.stream()
             .collect(
                 Collectors.groupingBy(
                     Employee::getDepartment
                 )
             );
```

### Count by Group

```java
Map<String, Long> result =
    employees.stream()
             .collect(
                 Collectors.groupingBy(
                     Employee::getDepartment,
                     Collectors.counting()
                 )
             );
```

---

# 50. Remember This Flow

For most Stream interview questions, think:

```text
SOURCE
  ↓
filter()      → Do I need to remove/select?
  ↓
map()         → Do I need to transform?
  ↓
flatMap()     → Do I need to flatten?
  ↓
sorted()      → Do I need sorting?
  ↓
distinct()    → Do I need unique values?
  ↓
limit/skip()  → Do I need a subset?
  ↓
collect()     → Do I need List/Set/Map?
  ↓
reduce()      → Do I need one combined value?
```

### Most important distinction

```text
filter()  → keeps/removes elements
map()     → changes each element
flatMap() → flattens nested elements
reduce()  → combines elements into one value
collect() → creates a result collection/map
```

### Primitive rule

```text
Collection<Integer>
      ↓
stream()
      ↓
Stream<Integer>
```

```text
int[]
      ↓
Arrays.stream()
      ↓
IntStream
```

```text
IntStream
      ↓ boxed()
Stream<Integer>
      ↓
collect()
List<Integer>
```
