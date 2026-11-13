# Java Stream API – 55 Interview Practice Questions with Answers

**Level:** Beginner to Advanced  
**Target:** Java Backend Developer – 5+ Years  
**Compatibility:** Java 8+

## Common Employee Class

Use this class for questions 21–35 and 41–43, 45.

```java
class Employee {
    private int id;
    private String name;
    private String department;
    private double salary;
    private int age;

    // Constructor, getters and setters
}
```

Assume `employees` is a `List<Employee>` and `strss` is a `List<String>`.

Common imports:

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.stream.Collectors;
```

---

# Level 1 – Basic (Warm-up)

## 1. Given a list of integers, filter out all even numbers.

**Lambda expression:**

```java
List<Integer> result = numbers.stream()
        .filter(n -> n % 2 != 0)
        .collect(Collectors.toList());
```

**Alternative:**

```java
List<Integer> result = numbers.stream()
        .filter(n -> (n & 1) == 1)
        .collect(Collectors.toList());
```

**Explanation:** Here, we are removing even numbers and retaining odd numbers. `filter()` keeps elements that satisfy the condition.

## 2. Find all numbers greater than 50.

**Lambda expression:**

```java
List<Integer> result = numbers.stream()
        .filter(n -> n > 50)
        .collect(Collectors.toList());
```

**Alternative:**

```java
List<Integer> result = numbers.stream()
        .filter(Predicate.isEqual(50).negate()
                .and(n -> n > 50))
        .collect(Collectors.toList());
```

The first solution is simpler and recommended.

**Explanation:** `filter()` checks each element and retains only numbers greater than 50.

## 3. Convert a list of strings to uppercase.

**Lambda expression:**

```java
List<String> result = strss.stream()
        .map(s -> s.toUpperCase())
        .collect(Collectors.toList());
```

**Method reference:**

```java
List<String> result = strss.stream()
        .map(String::toUpperCase)
        .collect(Collectors.toList());
```

**Explanation:** `map()` transforms every element into another value.

## 4. Find the sum of all numbers in a list.

**Lambda expression:**

```java
int sum = numbers.stream()
        .mapToInt(n -> n)
        .sum();
```

**Method reference:**

```java
int sum = numbers.stream()
        .mapToInt(Integer::intValue)
        .sum();
```

**Alternative using reduce:**

```java
int sum = numbers.stream()
        .reduce(0, (a, b) -> a + b);
```

**Explanation:**
- `mapToInt()` converts the Stream into an IntStream.
- `sum()` calculates the total.
- `reduce()` combines elements into a single result.

## 5. Find the maximum and minimum number from a list.

**Lambda expression:**

```java
Optional<Integer> max = numbers.stream()
        .max((a, b) -> Integer.compare(a, b));

Optional<Integer> min = numbers.stream()
        .min((a, b) -> Integer.compare(a, b));
```

**Method reference / natural ordering:**

```java
Optional<Integer> max = numbers.stream()
        .max(Integer::compareTo);

Optional<Integer> min = numbers.stream()
        .min(Integer::compareTo);
```

**Alternative:**

```java
Optional<Integer> max = numbers.stream()
        .max(Comparator.naturalOrder());

Optional<Integer> min = numbers.stream()
        .min(Comparator.naturalOrder());
```

**Explanation:** `max()` and `min()` return Optional because the input list may be empty.

## 6. Remove duplicate elements from a list.

**Lambda / method chaining:**

```java
List<Integer> result = numbers.stream()
        .distinct()
        .collect(Collectors.toList());
```

**Alternative:**

```java
Set<Integer> result = numbers.stream()
        .collect(Collectors.toSet());
```

**Explanation:**
- `distinct()` removes duplicates while preserving encounter order for an ordered stream.
- `toSet()` collects unique elements but does not guarantee insertion order.

## 7. Sort a list of integers in ascending and descending order.

**Ascending:**

```java
List<Integer> asc = numbers.stream()
        .sorted((a, b) -> Integer.compare(a, b))
        .collect(Collectors.toList());
```

**Method reference / alternative:**

```java
List<Integer> asc = numbers.stream()
        .sorted(Comparator.naturalOrder())
        .collect(Collectors.toList());
```

**Descending:**

```java
List<Integer> desc = numbers.stream()
        .sorted((a, b) -> Integer.compare(b, a))
        .collect(Collectors.toList());
```

**Alternative:**

```java
List<Integer> desc = numbers.stream()
        .sorted(Comparator.reverseOrder())
        .collect(Collectors.toList());
```

**Explanation:** For descending order, reverse the comparison order or use `reverseOrder()`.

## 8. Count how many strings start with the letter 'A'.

**Lambda expression:**

```java
long count = strss.stream()
        .filter(s -> s.startsWith("A"))
        .count();
```

**Alternative:**

```java
long count = strss.stream()
        .filter(Predicate.not(s -> !s.startsWith("A")))
        .count();
```

For Java 8, prefer the first solution because `Predicate.not()` was introduced in Java 11.

**Explanation:** `count()` returns the number of matching elements as a `long`.

## 9. Find the first element in a list that starts with "J".

**Lambda expression:**

```java
Optional<String> result = strss.stream()
        .filter(s -> s.startsWith("J"))
        .findFirst();
```

**Alternative:**

```java
String result = strss.stream()
        .filter(s -> s.startsWith("J"))
        .findFirst()
        .orElse("Not Found");
```

**Explanation:** `findFirst()` returns an Optional containing the first matching element.

## 10. Convert a list of strings into a list containing their lengths.

**Lambda expression:**

```java
List<Integer> result = strss.stream()
        .map(s -> s.length())
        .collect(Collectors.toList());
```

**Method reference:**

```java
List<Integer> result = strss.stream()
        .map(String::length)
        .collect(Collectors.toList());
```

**Explanation:** `map()` transforms String into Integer by extracting its length.

---

# Level 2 – Intermediate

## 11. Find the second-highest number from a list.

**Lambda expression:**

```java
Optional<Integer> result = numbers.stream()
        .distinct()
        .sorted((a, b) -> Integer.compare(b, a))
        .skip(1)
        .findFirst();
```

**Alternative:**

```java
Optional<Integer> result = numbers.stream()
        .distinct()
        .sorted(Comparator.reverseOrder())
        .skip(1)
        .findFirst();
```

**Explanation:**
1. `distinct()` removes duplicate numbers.
2. `sorted()` sorts in descending order.
3. `skip(1)` skips the highest number.
4. `findFirst()` gets the second-highest number.

Example:

```text
Input:  [10, 40, 20, 40, 30]
Output: 30
```

## 12. Find the second-lowest number from a list.

**Lambda expression:**

```java
Optional<Integer> result = numbers.stream()
        .distinct()
        .sorted((a, b) -> Integer.compare(a, b))
        .skip(1)
        .findFirst();
```

**Alternative:**

```java
Optional<Integer> result = numbers.stream()
        .distinct()
        .sorted()
        .skip(1)
        .findFirst();
```

**Explanation:** Sort ascending, skip the smallest element and retrieve the next element.

## 13. Find duplicate elements in a list.

**Lambda expression:**

```java
Set<Integer> seen = new HashSet<>();

Set<Integer> duplicates = numbers.stream()
        .filter(n -> !seen.add(n))
        .collect(Collectors.toSet());
```

**Alternative using frequency:**

```java
Set<Integer> duplicates = numbers.stream()
        .filter(n -> Collections.frequency(numbers, n) > 1)
        .collect(Collectors.toSet());
```

**Explanation:**
- `Set.add()` returns false when the element already exists.
- Therefore, `!seen.add(n)` identifies repeated elements.

Note: The second approach is less efficient because `Collections.frequency()` scans the list for every element.

## 14. Find the frequency of each element in a list.

**Lambda expression:**

```java
Map<Integer, Long> result = numbers.stream()
        .collect(Collectors.groupingBy(
                n -> n,
                Collectors.counting()
        ));
```

**Method reference:**

```java
Map<Integer, Long> result = numbers.stream()
        .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
        ));
```

Example:

```text
Input: [1, 2, 2, 3, 3, 3]

Output:
1 = 1
2 = 2
3 = 3
```

**Explanation:**
- `groupingBy()` groups identical elements.
- `counting()` counts the elements in each group.
- `Function.identity()` returns the original element itself.

## 15. Separate even and odd numbers using partitioningBy().

**Lambda expression:**

```java
Map<Boolean, List<Integer>> result = numbers.stream()
        .collect(Collectors.partitioningBy(
                n -> n % 2 == 0
        ));
```

**Explanation:**

```text
true  = Even numbers
false = Odd numbers
```

Example:

```text
Input: [1, 2, 3, 4, 5, 6]

Output:
true  = [2, 4, 6]
false = [1, 3, 5]
```

## 16. Group strings based on their length.

**Lambda expression:**

```java
Map<Integer, List<String>> result = strss.stream()
        .collect(Collectors.groupingBy(
                s -> s.length()
        ));
```

**Method reference:**

```java
Map<Integer, List<String>> result = strss.stream()
        .collect(Collectors.groupingBy(
                String::length
        ));
```

Example:

```text
Input: ["Java", "SQL", "Spring", "API"]

Output:
4 = [Java]
3 = [SQL, API]
6 = [Spring]
```

**Explanation:** `groupingBy()` uses string length as the grouping key.

## 17. Find the longest string in a list.

**Lambda expression:**

```java
Optional<String> result = strss.stream()
        .max((a, b) ->
                Integer.compare(a.length(), b.length()));
```

**Alternative:**

```java
Optional<String> result = strss.stream()
        .max(Comparator.comparingInt(String::length));
```

**Explanation:** `max()` finds the element with the largest string length.

## 18. Join all strings with a comma separator.

**Lambda expression / method chaining:**

```java
String result = strss.stream()
        .collect(Collectors.joining(","));
```

**Alternative:**

```java
String result = strss.stream()
        .collect(Collectors.joining(", "));
```

Example:

```text
Input: ["Java", "Spring", "SQL"]

Output: Java, Spring, SQL
```

**Explanation:** `joining()` combines strings into one String using the supplied delimiter.

## 19. Find the sum of squares of all even numbers.

**Lambda expression:**

```java
int result = numbers.stream()
        .filter(n -> n % 2 == 0)
        .mapToInt(n -> n * n)
        .sum();
```

**Alternative:**

```java
int result = numbers.stream()
        .filter(n -> n % 2 == 0)
        .map(n -> n * n)
        .reduce(0, Integer::sum);
```

**Explanation:**
1. Filter even numbers.
2. Square each number using `mapToInt()`.
3. Calculate the sum.

For large values, use `long` to reduce the risk of integer overflow.

## 20. Convert a list of strings into a Map where the key is the string and the value is its length.

**Lambda expression:**

```java
Map<String, Integer> result = strss.stream()
        .collect(Collectors.toMap(
                s -> s,
                s -> s.length()
        ));
```

**Method reference:**

```java
Map<String, Integer> result = strss.stream()
        .collect(Collectors.toMap(
                Function.identity(),
                String::length
        ));
```

**Handling duplicate keys:**

```java
Map<String, Integer> result = strss.stream()
        .collect(Collectors.toMap(
                Function.identity(),
                String::length,
                (existing, replacement) -> existing
        ));
```

**Explanation:**
- `Function.identity()` returns the original String.
- `String::length` returns its length.
- The third argument handles duplicate keys.

---

# Level 3 – Employee-Based Questions

## 21. Find employees whose salary is greater than 50,000.

**Lambda expression:**

```java
List<Employee> result = employees.stream()
        .filter(e -> e.getSalary() > 50000)
        .collect(Collectors.toList());
```

**Explanation:** `filter()` retains employees whose salary satisfies the condition.

## 22. Find the employee with the highest salary.

**Lambda expression:**

```java
Optional<Employee> result = employees.stream()
        .max((e1, e2) ->
                Double.compare(e1.getSalary(), e2.getSalary()));
```

**Alternative:**

```java
Optional<Employee> result = employees.stream()
        .max(Comparator.comparingDouble(Employee::getSalary));
```

**Explanation:** `comparingDouble()` extracts salary and compares employees using that value.

## 23. Find the second-highest-paid employee.

**Lambda expression:**

```java
Optional<Employee> result = employees.stream()
        .sorted((e1, e2) ->
                Double.compare(e2.getSalary(), e1.getSalary()))
        .skip(1)
        .findFirst();
```

**Alternative – distinct salary approach:**

```java
Optional<Double> secondSalary = employees.stream()
        .map(Employee::getSalary)
        .distinct()
        .sorted(Comparator.reverseOrder())
        .skip(1)
        .findFirst();

Optional<Employee> result = secondSalary.flatMap(salary ->
        employees.stream()
                .filter(e -> e.getSalary() == salary)
                .findFirst()
);
```

**Important difference:**
- First solution finds the second employee after sorting. Equal salaries can occupy the first two positions.
- Second solution finds the second-highest distinct salary and then an employee earning that salary.

## 24. Group employees by department.

**Lambda expression:**

```java
Map<String, List<Employee>> result = employees.stream()
        .collect(Collectors.groupingBy(
                e -> e.getDepartment()
        ));
```

**Method reference:**

```java
Map<String, List<Employee>> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment
        ));
```

**Explanation:** Employees are grouped using department as the key.

## 25. Find the average salary of employees in each department.

**Lambda expression:**

```java
Map<String, Double> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(
                        e -> e.getSalary()
                )
        ));
```

**Method reference:**

```java
Map<String, Double> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(
                        Employee::getSalary
                )
        ));
```

**Explanation:** `groupingBy()` creates department groups, while `averagingDouble()` calculates the average salary within each group.

## 26. Find the highest-paid employee in each department.

**Lambda expression:**

```java
Map<String, Optional<Employee>> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.maxBy(
                        Comparator.comparingDouble(
                                Employee::getSalary
                        )
                )
        ));
```

**Alternative using collectingAndThen:**

```java
Map<String, Employee> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.collectingAndThen(
                        Collectors.maxBy(
                                Comparator.comparingDouble(
                                        Employee::getSalary
                                )
                        ),
                        Optional::get
                )
        ));
```

**Explanation:**
- `groupingBy()` groups employees by department.
- `maxBy()` finds the employee with the maximum salary in each group.
- `collectingAndThen()` applies a finishing transformation to the collector result.

The second solution assumes each group is non-empty, which is true for groups created from the input stream.

## 27. Find the total salary expenditure of each department.

**Lambda expression:**

```java
Map<String, Double> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.summingDouble(
                        e -> e.getSalary()
                )
        ));
```

**Method reference:**

```java
Map<String, Double> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.summingDouble(
                        Employee::getSalary
                )
        ));
```

**Explanation:** `summingDouble()` calculates the total salary within each department.

## 28. Find employees whose age is greater than 30 and salary is greater than 60,000.

**Lambda expression:**

```java
List<Employee> result = employees.stream()
        .filter(e -> e.getAge() > 30 &&
                     e.getSalary() > 60000)
        .collect(Collectors.toList());
```

**Explanation:** Both conditions must be true because we use the logical AND operator.

## 29. Sort employees based on salary in descending order.

**Lambda expression:**

```java
List<Employee> result = employees.stream()
        .sorted((e1, e2) ->
                Double.compare(e2.getSalary(), e1.getSalary()))
        .collect(Collectors.toList());
```

**Alternative:**

```java
List<Employee> result = employees.stream()
        .sorted(Comparator.comparingDouble(
                Employee::getSalary
        ).reversed())
        .collect(Collectors.toList());
```

**Explanation:** `reversed()` changes the natural ascending comparator into descending order.

## 30. Find the names of all employees working in the IT department.

**Lambda expression:**

```java
List<String> result = employees.stream()
        .filter(e -> e.getDepartment().equals("IT"))
        .map(e -> e.getName())
        .collect(Collectors.toList());
```

**Method reference:**

```java
List<String> result = employees.stream()
        .filter(e -> e.getDepartment().equals("IT"))
        .map(Employee::getName)
        .collect(Collectors.toList());
```

**Explanation:** First filter employees by department, then extract their names using `map()`.

## 31. Find the department having the highest average salary.

**Lambda expression:**

```java
Optional<Map.Entry<String, Double>> result =
        employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(
                        Employee::getSalary
                )
        ))
        .entrySet()
        .stream()
        .max((a, b) ->
                Double.compare(a.getValue(), b.getValue()));
```

**Alternative:**

```java
Optional<Map.Entry<String, Double>> result =
        employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(
                        Employee::getSalary
                )
        ))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue());
```

**Explanation:**
1. Group employees by department.
2. Calculate average salary for each department.
3. Convert the Map into an entry Stream.
4. Find the entry with the maximum average salary.

## 32. Find the youngest employee in each department.

**Lambda expression:**

```java
Map<String, Optional<Employee>> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.minBy(
                        Comparator.comparingInt(
                                e -> e.getAge()
                        )
                )
        ));
```

**Method reference:**

```java
Map<String, Optional<Employee>> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.minBy(
                        Comparator.comparingInt(
                                Employee::getAge
                        )
                )
        ));
```

**Explanation:** `minBy()` finds the employee with the smallest age in each department.

## 33. Count the number of employees in each department.

**Lambda expression:**

```java
Map<String, Long> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.counting()
        ));
```

**Explanation:** `counting()` counts the employees belonging to each department.

## 34. Partition employees into two groups: salary greater than 50,000 and salary less than or equal to 50,000.

**Lambda expression:**

```java
Map<Boolean, List<Employee>> result = employees.stream()
        .collect(Collectors.partitioningBy(
                e -> e.getSalary() > 50000
        ));
```

**Explanation:**

```text
true  = Salary greater than 50,000
false = Salary less than or equal to 50,000
```

Unlike `groupingBy()`, `partitioningBy()` always creates two Boolean partitions.

## 35. Find the top three highest-paid employees.

**Lambda expression:**

```java
List<Employee> result = employees.stream()
        .sorted((e1, e2) ->
                Double.compare(e2.getSalary(), e1.getSalary()))
        .limit(3)
        .collect(Collectors.toList());
```

**Alternative:**

```java
List<Employee> result = employees.stream()
        .sorted(Comparator.comparingDouble(
                Employee::getSalary
        ).reversed())
        .limit(3)
        .collect(Collectors.toList());
```

**Explanation:**
- `sorted()` sorts salary in descending order.
- `limit(3)` retrieves only the first three employees.

Note: This returns three employees, not necessarily three distinct salary levels.

---

# Level 4 – Advanced

## 36. Given a list of lists, flatten it into a single list.

**Lambda expression:**

```java
List<List<Integer>> list = Arrays.asList(
        Arrays.asList(1, 2),
        Arrays.asList(3, 4),
        Arrays.asList(5, 6)
);

List<Integer> result = list.stream()
        .flatMap(l -> l.stream())
        .collect(Collectors.toList());
```

**Method reference:**

```java
List<Integer> result = list.stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
```

Output:

```text
[1, 2, 3, 4, 5, 6]
```

**Explanation:**
- `map()` would return a Stream of lists.
- `flatMap()` converts each inner list into a Stream and flattens them into one Stream.

## 37. Find the third-highest distinct salary.

**Lambda expression:**

```java
Optional<Double> result = employees.stream()
        .map(e -> e.getSalary())
        .distinct()
        .sorted((a, b) -> Double.compare(b, a))
        .skip(2)
        .findFirst();
```

**Method reference:**

```java
Optional<Double> result = employees.stream()
        .map(Employee::getSalary)
        .distinct()
        .sorted(Comparator.reverseOrder())
        .skip(2)
        .findFirst();
```

**Explanation:** Extract salaries, remove duplicates, sort descending, skip the first two and retrieve the third.

## 38. Find duplicate characters in a string using Streams.

**Lambda expression:**

```java
String input = "programming";

Set<Character> result = input.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(
                c -> c,
                Collectors.counting()
        ))
        .entrySet()
        .stream()
        .filter(e -> e.getValue() > 1)
        .map(e -> e.getKey())
        .collect(Collectors.toSet());
```

**Alternative:**

```java
Map<Character, Long> frequency = input.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
        ));

Set<Character> result = frequency.entrySet().stream()
        .filter(e -> e.getValue() > 1)
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
```

**Explanation:**
1. `chars()` converts the String into an IntStream.
2. `mapToObj()` converts each integer into a Character.
3. `groupingBy()` groups identical characters.
4. `counting()` calculates their frequency.
5. Filter characters appearing more than once.

## 39. Find the first non-repeating character in a string.

**Lambda expression:**

```java
String input = "swiss";

Optional<Character> result = input.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(
                c -> c,
                LinkedHashMap::new,
                Collectors.counting()
        ))
        .entrySet()
        .stream()
        .filter(e -> e.getValue() == 1)
        .map(e -> e.getKey())
        .findFirst();
```

**Alternative:**

```java
Map<Character, Long> frequency = input.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(
                Function.identity(),
                LinkedHashMap::new,
                Collectors.counting()
        ));

Optional<Character> result = frequency.entrySet()
        .stream()
        .filter(e -> e.getValue() == 1)
        .map(Map.Entry::getKey)
        .findFirst();
```

**Explanation:** `LinkedHashMap` preserves insertion order, which is important because we need the first non-repeating character.

Example:

```text
Input: swiss
Output: w
```

## 40. Find the first repeating character in a string.

**Lambda expression:**

```java
String input = "programming";

Set<Character> seen = new HashSet<>();

Optional<Character> result = input.chars()
        .mapToObj(c -> (char) c)
        .filter(c -> !seen.add(c))
        .findFirst();
```

**Explanation:**
- `HashSet` stores characters already encountered.
- `add()` returns false when a character is already present.
- `filter()` identifies the repeated character.
- `findFirst()` returns the first repeated occurrence in the original string.

Example:

```text
Input: programming
Output: r
```

## 41. Find the department with the maximum number of employees.

**Lambda expression:**

```java
Optional<Map.Entry<String, Long>> result =
        employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.counting()
        ))
        .entrySet()
        .stream()
        .max((a, b) ->
                Long.compare(a.getValue(), b.getValue()));
```

**Alternative:**

```java
Optional<Map.Entry<String, Long>> result =
        employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.counting()
        ))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue());
```

**Explanation:** Count employees in each department, then find the department with the maximum count.

## 42. Group employees by department and then by age.

**Lambda expression:**

```java
Map<String, Map<Integer, List<Employee>>> result =
        employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.groupingBy(
                        e -> e.getAge()
                )
        ));
```

**Alternative:**

```java
Map<String, Map<Integer, List<Employee>>> result =
        employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.groupingBy(
                        Employee::getAge
                )
        ));
```

**Explanation:** This is nested grouping.

First level: Department.

Second level: Age.

The resulting Map contains a list of employees for each department and age combination.

## 43. Find the top two highest-paid employees in each department.

**Lambda expression:**

```java
Map<String, List<Employee>> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.stream()
                                .sorted((e1, e2) ->
                                        Double.compare(
                                                e2.getSalary(),
                                                e1.getSalary()
                                        ))
                                .limit(2)
                                .collect(Collectors.toList())
                )
        ));
```

**Alternative using comparator chaining:**

```java
Map<String, List<Employee>> result = employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.stream()
                                .sorted(
                                        Comparator.comparingDouble(
                                                Employee::getSalary
                                        ).reversed()
                                )
                                .limit(2)
                                .collect(Collectors.toList())
                )
        ));
```

**Explanation:**
1. Group employees by department.
2. Collect each department's employees into a list.
3. Sort each list by salary descending.
4. Limit each department to two employees.

## 44. Given a list of sentences, find the frequency of each word.

**Lambda expression:**

```java
List<String> sentences = Arrays.asList(
        "Java is good",
        "Java is powerful"
);

Map<String, Long> result = sentences.stream()
        .flatMap(s -> Arrays.stream(s.split("\\s+")))
        .map(s -> s.toLowerCase())
        .collect(Collectors.groupingBy(
                word -> word,
                Collectors.counting()
        ));
```

**Method reference:**

```java
Map<String, Long> result = sentences.stream()
        .flatMap(s -> Arrays.stream(s.split("\\s+")))
        .map(String::toLowerCase)
        .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
        ));
```

Example:

```text
Java = 2
is = 2
good = 1
powerful = 1
```

**Explanation:** `flatMap()` converts multiple sentences into one Stream of words. Then `groupingBy()` and `counting()` calculate word frequency.

Note: This example does not remove punctuation. You can normalize words with `replaceAll()` if needed.

## 45. Convert a list of employees into a Map using employee ID as key and Employee as value. Handle duplicate IDs.

**Lambda expression:**

```java
Map<Integer, Employee> result = employees.stream()
        .collect(Collectors.toMap(
                e -> e.getId(),
                e -> e,
                (existing, replacement) -> existing
        ));
```

**Method reference:**

```java
Map<Integer, Employee> result = employees.stream()
        .collect(Collectors.toMap(
                Employee::getId,
                Function.identity(),
                (existing, replacement) -> replacement
        ));
```

**Explanation:**
- `Employee::getId` extracts the key.
- `Function.identity()` returns the Employee itself.
- The merge function handles duplicate IDs.

The first solution keeps the existing employee. The second keeps the latest employee encountered.

---

# Level 5 – 5+ Years Interview Concepts

## 46. Difference between map() and flatMap()?

### map()

Transforms each element into another element.

```java
List<String> names = Arrays.asList("Java", "Spring");

List<Integer> result = names.stream()
        .map(String::length)
        .collect(Collectors.toList());
```

Output:

```text
[4, 6]
```

### flatMap()

Transforms each element into a Stream and flattens all resulting Streams into one.

```java
List<List<Integer>> numbers = Arrays.asList(
        Arrays.asList(1, 2),
        Arrays.asList(3, 4)
);

List<Integer> result = numbers.stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
```

Output:

```text
[1, 2, 3, 4]
```

**Interview answer:** `map()` performs one-to-one transformation, whereas `flatMap()` performs one-to-many transformation and flattens the results.

## 47. Difference between findFirst() and findAny()?

### findFirst()

Returns the first element according to encounter order.

```java
Optional<Integer> result = numbers.stream()
        .filter(n -> n > 10)
        .findFirst();
```

### findAny()

Returns any matching element.

```java
Optional<Integer> result = numbers.parallelStream()
        .filter(n -> n > 10)
        .findAny();
```

**Interview answer:**
- `findFirst()` respects encounter order.
- `findAny()` does not guarantee which matching element is returned.
- `findAny()` can be useful in parallel processing when a specific first element is not required.

Both return Optional.

## 48. Difference between stream() and parallelStream()?

### stream()

Processes elements sequentially.

```java
numbers.stream()
        .filter(n -> n > 10)
        .forEach(System.out::println);
```

### parallelStream()

Processes elements using parallel execution, typically through the common ForkJoinPool.

```java
numbers.parallelStream()
        .filter(n -> n > 10)
        .forEach(System.out::println);
```

**Interview answer:**

| stream() | parallelStream() |
|---|---|
| Sequential processing | Parallel processing |
| One processing path at a time | Multiple tasks can execute concurrently |
| Predictable execution order for ordered operations | Execution order is not guaranteed by forEach |
| Lower coordination overhead | Additional coordination overhead |

Parallel processing is not automatically faster. It depends on data size, workload, splitting efficiency and available CPU resources.

## 49. Why are Streams lazy? Explain intermediate and terminal operations.

Streams are lazy because intermediate operations are not executed until a terminal operation is invoked.

### Intermediate operations

Examples:
- `filter()`
- `map()`
- `sorted()`
- `distinct()`

### Terminal operations

Examples:
- `collect()`
- `forEach()`
- `count()`
- `reduce()`
- `findFirst()`

Example:

```java
List<Integer> result = numbers.stream()
        .filter(n -> {
            System.out.println("Filtering: " + n);
            return n > 10;
        })
        .map(n -> n * 2)
        .collect(Collectors.toList());
```

The operations execute when `collect()` is called.

**Interview answer:** Intermediate operations build the processing pipeline. Terminal operations trigger execution and produce the final result or side effect.

## 50. Can we reuse a Stream after calling a terminal operation?

No. A Stream cannot be reused after a terminal operation.

Example:

```java
Stream<Integer> stream = numbers.stream();

stream.forEach(System.out::println);

stream.count(); // IllegalStateException
```

**Correct approach:**

```java
long count = numbers.stream().count();

List<Integer> result = numbers.stream()
        .filter(n -> n > 10)
        .collect(Collectors.toList());
```

Create a new Stream from the collection whenever another pipeline is required.

## 51. What happens if you modify the original collection while processing a Stream?

Modifying a collection while its Stream is processing can cause unexpected behaviour and may result in `ConcurrentModificationException`.

Example:

```java
List<Integer> numbers = new ArrayList<>(
        Arrays.asList(1, 2, 3, 4)
);

numbers.stream()
        .forEach(n -> numbers.remove(n));
```

This is unsafe.

**Correct approach:**

```java
List<Integer> result = numbers.stream()
        .filter(n -> n != 2)
        .collect(Collectors.toList());

numbers = result;
```

**Interview answer:** Avoid structural modification of the source collection during Stream processing. Create a new collection or use an appropriate concurrent collection when necessary.

## 52. Difference between reduce() and collect()?

### reduce()

Used to combine Stream elements into one result.

Example: Sum of numbers.

```java
int sum = numbers.stream()
        .reduce(0, (a, b) -> a + b);
```

Alternative:

```java
int sum = numbers.stream()
        .reduce(0, Integer::sum);
```

### collect()

Used to accumulate Stream elements into a mutable result container, such as List, Set or Map.

Example:

```java
List<Integer> result = numbers.stream()
        .filter(n -> n > 10)
        .collect(Collectors.toList());
```

**Interview answer:**

| reduce() | collect() |
|---|---|
| Combines elements into a result | Accumulates elements into a container |
| Commonly used for sum, product and similar aggregation | Commonly used for List, Set and Map |
| Uses an associative reduction operation | Uses a Collector with supplier, accumulator and combiner |

## 53. Difference between groupingBy() and partitioningBy()?

### groupingBy()

Groups elements using a classification function.

```java
Map<Integer, List<Integer>> result = numbers.stream()
        .collect(Collectors.groupingBy(
                n -> n % 2
        ));
```

Output:

```text
0 = Even numbers
1 = Odd numbers
```

### partitioningBy()

Divides elements into two Boolean groups.

```java
Map<Boolean, List<Integer>> result = numbers.stream()
        .collect(Collectors.partitioningBy(
                n -> n % 2 == 0
        ));
```

Output:

```text
true = Even numbers
false = Odd numbers
```

**Interview answer:**
- `groupingBy()` can create multiple groups based on different keys.
- `partitioningBy()` always divides elements into two Boolean partitions: true and false.

## 54. Difference between peek() and map()?

### map()

Transforms elements.

```java
List<Integer> result = numbers.stream()
        .map(n -> n * 2)
        .collect(Collectors.toList());
```

### peek()

Allows you to inspect elements as they pass through the pipeline, commonly for debugging.

```java
List<Integer> result = numbers.stream()
        .peek(n -> System.out.println("Before: " + n))
        .map(n -> n * 2)
        .peek(n -> System.out.println("After: " + n))
        .collect(Collectors.toList());
```

**Interview answer:**
- `map()` changes the elements.
- `peek()` observes elements without transforming them.
- Both are intermediate operations and are lazy.

Do not use `peek()` for important business logic or required side effects.

## 55. When should you avoid parallelStream() in a real-time application?

Avoid or carefully evaluate `parallelStream()` in these situations:

1. Small datasets: Parallel execution overhead may exceed its benefits.
2. Database calls: Parallel database operations can overload connection pools.
3. Network calls: It may create excessive concurrent requests.
4. Shared mutable state: Multiple threads can create race conditions.
5. Order-dependent processing: Parallel execution may not preserve the required processing order.
6. Blocking operations: Blocking tasks can consume shared worker threads.
7. Resource-sensitive applications: Common ForkJoinPool usage can interfere with other work.

Example:

```java
// Sequential
employees.stream()
        .filter(e -> e.getSalary() > 50000)
        .collect(Collectors.toList());

// Parallel
employees.parallelStream()
        .filter(e -> e.getSalary() > 50000)
        .collect(Collectors.toList());
```

**Interview answer:** Use parallel streams only when the workload is suitable, sufficiently large, independent and safe for parallel execution. Always benchmark before choosing parallel processing.

---

# Important Stream API Revision Sheet

| Method | Purpose | Return type / result |
|---|---|---|
| filter() | Filter elements | Stream |
| map() | Transform elements | Stream |
| flatMap() | Flatten nested Streams | Stream |
| distinct() | Remove duplicates | Stream |
| sorted() | Sort elements | Stream |
| limit() | Restrict number of elements | Stream |
| skip() | Skip elements | Stream |
| count() | Count elements | long |
| min() | Find minimum | Optional |
| max() | Find maximum | Optional |
| findFirst() | Find first element | Optional |
| findAny() | Find any element | Optional |
| reduce() | Combine elements | Result |
| collect() | Accumulate elements | Result container |
| groupingBy() | Group elements | Map |
| partitioningBy() | Divide into two groups | Map<Boolean, ...> |
| joining() | Join strings | String |
| counting() | Count grouped elements | Long |
| summingDouble() | Calculate sum | Double |
| averagingDouble() | Calculate average | Double |
| forEach() | Perform an action | void |
| peek() | Inspect pipeline elements | Stream |

# Important Concepts to Remember

### 1. Lambda vs Method Reference

Lambda:

```java
.map(s -> s.length())
```

Method reference:

```java
.map(String::length)
```

Use method references when the lambda simply invokes an existing method with the same arguments.

### 2. Comparator – Ascending vs Descending

Ascending:

```java
.sorted((a, b) -> Integer.compare(a, b))
```

Descending:

```java
.sorted((a, b) -> Integer.compare(b, a))
```

Method reference / comparator alternatives:

```java
.sorted(Comparator.naturalOrder())

.sorted(Comparator.reverseOrder())
```

For object properties:

```java
.sorted(Comparator.comparing(Employee::getName))

.sorted(Comparator.comparingDouble(
        Employee::getSalary
).reversed())
```

### 3. Function.identity()

```java
Function.identity()
```

Returns the same input element.

Equivalent lambda:

```java
n -> n
```

Useful in `toMap()` and `groupingBy()`.

### 4. Optional

Methods to remember:

```java
findFirst()
findAny()
orElse()
orElseGet()
orElseThrow()
ifPresent()
map()
flatMap()
```

Example:

```java
String name = employees.stream()
        .filter(e -> e.getSalary() > 100000)
        .map(Employee::getName)
        .findFirst()
        .orElse("Not Found");
```

### 5. Collector chaining

A powerful interview pattern:

```java
employees.stream()
        .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(
                        Employee::getSalary
                )
        ));
```

Remember the structure:

```java
groupingBy(
    classificationFunction,
    downstreamCollector
)
```

This pattern is useful for grouping, counting, summing, averaging, finding maximum values and many other real-world requirements.

---

# Final Practice Advice

1. First solve using lambda expressions.
2. Then rewrite using method references wherever possible.
3. Understand why you are choosing `map()`, `filter()`, `flatMap()` or `collect()`.
4. Practise nested collectors such as `groupingBy()` with `counting()`, `averagingDouble()` and `maxBy()`.
5. Test empty collections, duplicate values, null values and equal salaries.
6. Practise explaining your solution verbally as if you are in an interview.

**Important:** Don't memorize the solutions. Understand the Stream pipeline and why each operation is used. That is what will help you solve unfamiliar interview questions.