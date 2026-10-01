# Java Map — Basic to Advanced

## 1. What is a Map?

A `Map` stores data in **key-value pairs**.

```java
Map<Integer, String> map = new HashMap<>();

map.put(1, "John");
map.put(2, "David");
```

Here:

```text
1 -> John
2 -> David
```

`1` and `2` are keys, and `John` and `David` are values.

---

## 2. Is Map a Collection?

No.

`Map` is part of the Java Collections Framework, but it does **not implement the `Collection` interface**.

```text
Collection
 ├── List
 ├── Set
 └── Queue

Map
 ├── HashMap
 ├── LinkedHashMap
 └── TreeMap
```

---

## 3. Why does Map use Key-Value pairs?

A key helps us **find a value quickly**.

```java
Map<Integer, String> students = new HashMap<>();

students.put(101, "John");
students.put(102, "David");
```

We can directly find:

```java
students.get(101);
```

Result:

```text
John
```

---

## 4. Can Map contain duplicate keys?

No.

A `Map` cannot have duplicate keys.

```java
map.put(1, "John");
map.put(1, "David");
```

The second value replaces the first value.

Result:

```text
1 -> David
```

---

## 5. Can Map contain duplicate values?

Yes.

```java
map.put(1, "John");
map.put(2, "John");
```

Result:

```text
1 -> John
2 -> John
```

Values can be duplicated.

---

## 6. Can Map contain null?

It depends on the Map implementation.

### HashMap

Allows:

* One `null` key
* Multiple `null` values

```java
Map<Integer, String> map = new HashMap<>();

map.put(null, "John");
map.put(1, null);
map.put(2, null);
```

### TreeMap

Normally does not allow a `null` key.

### ConcurrentHashMap

Does not allow `null` keys or `null` values.

---

# HashMap

## 7. What is HashMap?

`HashMap` stores key-value pairs using hashing.

```java
Map<Integer, String> map = new HashMap<>();

map.put(1, "John");
map.put(2, "David");
```

It provides fast lookup in normal cases.

---

## 8. Does HashMap maintain insertion order?

No.

Do not depend on the order in which elements come out of a `HashMap`.

```java
map.put(3, "C");
map.put(1, "A");
map.put(2, "B");
```

The iteration order is **not guaranteed**.

---

## 9. What is the default initial capacity of HashMap?

The default initial capacity is:

```text
16
```

It means the internal table can initially have 16 buckets when the table is initialized.

---

## 10. What is the default load factor?

The default load factor is:

```text
0.75
```

It means HashMap normally resizes when about **75% of its capacity** is reached.

---

## 11. What does put() return?

`put()` returns the **previous value associated with the key**.

```java
Map<Integer, String> map = new HashMap<>();

System.out.println(map.put(1, "John"));
```

Output:

```text
null
```

Now:

```java
System.out.println(map.put(1, "David"));
```

Output:

```text
John
```

Because `John` was the old value.

---

## 12. What does get() return if the key does not exist?

It returns:

```text
null
```

Example:

```java
map.get(100);
```

If key `100` doesn't exist, result is `null`.

---

## 13. get() vs getOrDefault()

### get()

Returns the value or `null`.

```java
map.get(10);
```

### getOrDefault()

Returns the value or a default value.

```java
map.getOrDefault(10, "Not Found");
```

If `10` doesn't exist:

```text
Not Found
```

---

## 14. How do you check whether a key exists?

Use:

```java
map.containsKey(10);
```

Example:

```java
if (map.containsKey(10)) {
    System.out.println("Key exists");
}
```

---

## 15. How do you check whether a value exists?

Use:

```java
map.containsValue("John");
```

---

## 16. How do you remove an entry?

Use:

```java
map.remove(10);
```

It removes the entry whose key is `10`.

---

## 17. How do you get the size?

Use:

```java
map.size();
```

---

# Map Iteration

## 18. What is keySet()?

`keySet()` returns all keys.

```java
for (Integer key : map.keySet()) {
    System.out.println(key);
}
```

---

## 19. What is values()?

`values()` returns all values.

```java
for (String value : map.values()) {
    System.out.println(value);
}
```

---

## 20. What is entrySet()?

`entrySet()` returns both key and value together.

```java
for (Map.Entry<Integer, String> entry : map.entrySet()) {
    System.out.println(entry.getKey());
    System.out.println(entry.getValue());
}
```

For reading both key and value, `entrySet()` is usually the natural choice.

---

# HashMap Internal Working

## 21. How does HashMap work internally?

When we do:

```java
map.put("A", 100);
```

HashMap roughly does:

```text
Key
 ↓
hashCode()
 ↓
Hash calculation
 ↓
Bucket
 ↓
Store key + value
```

For `get()`:

```text
Key
 ↓
hashCode()
 ↓
Find bucket
 ↓
equals()
 ↓
Return value
```

---

## 22. What is hashing?

Hashing is a technique used to convert a key into a value that helps determine where the entry should be stored.

Example:

```java
"A".hashCode();
```

produces an integer hash code.

HashMap uses this information to locate a bucket.

---

## 23. What is hashCode()?

`hashCode()` is a method from `Object`.

It returns an integer representing the object's hash value.

```java
String name = "John";

System.out.println(name.hashCode());
```

HashMap uses this hash information to find the bucket.

---

## 24. Why does HashMap use hashCode()?

Because it helps HashMap find the possible location of a key quickly.

Without hashing, HashMap would need to search through many entries.

---

## 25. Why does HashMap use equals() too?

`hashCode()` helps find the bucket.

`equals()` helps identify the **exact key** inside that bucket.

Simple flow:

```text
hashCode()
    ↓
Find bucket
    ↓
equals()
    ↓
Find exact key
```

---

## 26. What is a hash collision?

A collision happens when different keys end up at the same bucket.

Example:

```text
Key A ──┐
        ├──> Same bucket
Key B ──┘
```

The keys are different, but their calculated bucket location is the same.

---

## 27. How does HashMap handle collisions?

HashMap stores multiple entries in the same bucket.

In modern Java, a bucket can use:

```text
Linked structure
      ↓
Red-Black Tree
```

for heavily-collided buckets.

---

## 28. Can two objects have the same hashCode()?

Yes.

```text
Object A -> hashCode 100
Object B -> hashCode 100
```

They can still be different objects.

This is called a collision.

---

## 29. Can two equal objects have different hashCodes?

No.

The `equals()`/`hashCode()` contract says:

```text
If a.equals(b) is true
then
a.hashCode() == b.hashCode()
```

---

## 30. Can two objects have the same hashCode but not be equal?

Yes.

```text
hashCode() same
equals() false
```

This is completely valid.

---

# equals() and hashCode()

## 31. Why should equals() and hashCode() be overridden together?

Because HashMap uses both.

Suppose:

```java
class Student {
    int id;
    String name;
}
```

If `Student` is used as a key, `equals()` and `hashCode()` should normally be based on the same logical identity fields.

---

## 32. What happens if equals() is overridden but hashCode() is not?

Two logically equal objects may produce different hash codes.

HashMap may put them into different buckets.

This can cause incorrect key lookup behavior.

---

## 33. What happens if hashCode() is overridden but equals() is not?

Objects can have the same hash code but still be considered different by `equals()`.

Therefore, duplicate logical keys may remain.

---

## 34. Why are immutable objects preferred as Map keys?

A key should not change after it is inserted.

For example:

```java
Map<Student, String> map = new HashMap<>();
```

If fields used by `hashCode()` change after insertion, HashMap may no longer find the key correctly.

---

## 35. What happens if a Map key is modified after insertion?

Example:

```text
Insert key
   ↓
hashCode = 100
   ↓
Stored in bucket 100
```

Then the key changes:

```text
hashCode = 200
```

Now HashMap looks in bucket 200 when searching.

The object is still physically in bucket 100.

Therefore:

```java
map.get(key);
```

may fail to find it.

---

# HashMap Capacity

## 36. What is capacity?

Capacity is the number of buckets available internally.

Default initial capacity:

```text
16
```

---

## 37. What is load factor?

Load factor determines when HashMap should resize.

Default:

```text
0.75
```

---

## 38. Why 0.75?

It provides a balance between:

```text
Memory usage
      +
Hash lookup performance
```

A smaller value means more buckets and more memory.

A larger value means fewer buckets but potentially more collisions.

---

## 39. What is threshold?

The threshold determines when HashMap should resize.

Roughly:

```text
threshold = capacity × load factor
```

For example:

```text
capacity = 16
load factor = 0.75

threshold = 16 × 0.75
          = 12
```

---

## 40. What happens when HashMap reaches its threshold?

HashMap increases its internal capacity.

This is called **resizing**.

---

## 41. What is resizing?

Resizing means increasing the number of buckets.

For example:

```text
16 buckets
   ↓
32 buckets
```

---

## 42. What is rehashing?

After resizing, entries may need to be redistributed according to the new table size.

This redistribution is commonly referred to as rehashing.

---

# Java 8 HashMap

## 43. What improvement happened in Java 8 HashMap?

In heavily-collided buckets, HashMap can convert the linked structure into a **Red-Black Tree**.

This helps avoid very slow searches when many entries collide.

---

## 44. What is a tree bin?

A tree bin is a HashMap bucket represented using a Red-Black Tree instead of a linked list.

Conceptually:

```text
Normal bucket:

A -> B -> C -> D

Heavy collision:

       B
      / \
     A   C
          \
           D
```

The actual implementation has additional conditions and details.

---

# LinkedHashMap

## 45. What is LinkedHashMap?

`LinkedHashMap` is a subclass of `HashMap` that additionally maintains a linked ordering of entries.

---

## 46. HashMap vs LinkedHashMap

| HashMap                        | LinkedHashMap                |
| ------------------------------ | ---------------------------- |
| No guaranteed iteration order  | Maintains iteration order    |
| Hash-based                     | Hash-based + linked ordering |
| Usually slightly less overhead | Slightly more overhead       |

---

## 47. Does LinkedHashMap maintain insertion order?

Yes, by default.

```java
map.put(3, "C");
map.put(1, "A");
map.put(2, "B");
```

Iteration gives:

```text
3 -> C
1 -> A
2 -> B
```

---

## 48. What is access order?

LinkedHashMap can also maintain **access order** instead of insertion order.

Example:

```text
A
B
C
```

Access `A`:

```java
map.get(A);
```

With access-order enabled:

```text
B
C
A
```

The recently accessed entry moves toward the end.

---

## 49. Why use LinkedHashMap?

Use it when you need:

```text
Map
+
Predictable iteration order
```

A common use case is implementing an **LRU cache**.

---

## 50. What is removeEldestEntry()?

It is a method in `LinkedHashMap` that can be overridden to automatically remove the oldest entry.

This is useful for implementing an LRU cache.

---

# TreeMap

## 51. What is TreeMap?

`TreeMap` stores key-value pairs in **sorted key order**.

```java
Map<Integer, String> map = new TreeMap<>();

map.put(30, "C");
map.put(10, "A");
map.put(20, "B");
```

Iteration:

```text
10 -> A
20 -> B
30 -> C
```

---

## 52. HashMap vs TreeMap

| HashMap                   | TreeMap                            |
| ------------------------- | ---------------------------------- |
| No guaranteed order       | Sorted by key                      |
| Hash table based          | Red-Black Tree based               |
| Average O(1) lookup       | O(log n) lookup                    |
| Usually faster for lookup | Useful when sorted keys are needed |

---

## 53. Does TreeMap maintain insertion order?

No.

It maintains **sorted key order**.

---

## 54. What data structure does TreeMap use?

TreeMap uses a **Red-Black Tree**.

---

## 55. What is the time complexity of TreeMap?

Typical operations are:

```text
put()    -> O(log n)
get()    -> O(log n)
remove() -> O(log n)
```

---

## 56. Can TreeMap contain null keys?

Normally, no.

With natural ordering, inserting a `null` key results in `NullPointerException`.

---

## 57. What happens if TreeMap keys cannot be compared?

TreeMap needs a way to determine key ordering.

If the keys cannot be compared, an exception such as `ClassCastException` can occur.

---

## 58. Comparable vs Comparator in TreeMap

### Comparable

The class itself defines its natural ordering.

```java
class Student implements Comparable<Student> {
}
```

### Comparator

We provide ordering separately.

```java
Comparator<Student> comparator = ...;
```

Then:

```java
TreeMap<Student, String> map =
        new TreeMap<>(comparator);
```

---

# Map and Custom Objects

## 59. Can a custom object be a HashMap key?

Yes.

```java
Map<Student, String> map = new HashMap<>();
```

But `equals()` and `hashCode()` should be properly implemented when logical equality is required.

---

## 60. Why are equals() and hashCode() important for custom keys?

Suppose:

```java
Student s1 = new Student(1, "John");
Student s2 = new Student(1, "John");
```

You may want:

```text
s1 and s2 = same logical student
```

For HashMap to understand that correctly, `equals()` and `hashCode()` need to represent that same identity.

---

## 61. Can List be used as a HashMap key?

Yes.

```java
Map<List<Integer>, String> map = new HashMap<>();
```

`List` provides `equals()` and `hashCode()` based on its elements.

But the list should not be modified while being used as a key.

---

## 62. Can an array be used as a HashMap key?

Yes, but be careful.

Arrays use `Object`'s identity-based `equals()` and `hashCode()` rather than content-based equality.

So:

```java
int[] a = {1, 2};
int[] b = {1, 2};
```

Normally:

```java
a.equals(b)
```

is `false`.

---

## 63. Can another Map be used as a HashMap key?

Yes.

A `Map` can technically be used as a key if its equality/hash-code behavior is appropriate.

But the key should not be modified after insertion.

---

# Java 8 Map Methods

## 64. What is Map.forEach()?

It allows us to process each key-value pair.

```java
map.forEach((key, value) -> {
    System.out.println(key + " " + value);
});
```

---

## 65. What is putIfAbsent()?

It inserts a value only if the key does not already have a value.

```java
map.putIfAbsent(1, "John");
```

If key `1` already exists, its value is not replaced.

---

## 66. put() vs putIfAbsent()

### put()

Always replaces the existing value.

```java
map.put(1, "David");
```

### putIfAbsent()

Only inserts if the key is absent or mapped appropriately according to the method's contract.

```java
map.putIfAbsent(1, "David");
```

---

## 67. What is compute()?

`compute()` calculates a new value for a key using a function.

```java
map.compute(1, (key, value) -> value + 10);
```

If the old value is `20`, the new value becomes:

```text
30
```

---

## 68. What is computeIfAbsent()?

It calculates a value **only when the key is absent**.

Example:

```java
map.computeIfAbsent(1, key -> 100);
```

If key `1` doesn't exist:

```text
1 -> 100
```

This is especially useful when creating a collection for a key.

---

## 69. What is computeIfPresent()?

It calculates a new value only when the key is already present.

```java
map.computeIfPresent(1, (key, value) -> value + 10);
```

---

## 70. What is merge()?

`merge()` combines an existing value with a new value.

Example:

```java
map.merge("A", 1, Integer::sum);
```

If:

```text
A -> 5
```

then after merge:

```text
A -> 6
```

---

## 71. Why is merge() useful for frequency counting?

Because we can combine the old count and new count.

Conceptually:

```text
First A -> 1
Second A -> 2
Third A -> 3
```

So `merge()` is very useful for frequency maps.

---

# Collectors and Map

## 72. What is Collectors.toMap()?

It converts a Stream into a Map.

Conceptually:

```text
List
 ↓
stream()
 ↓
collect(toMap())
 ↓
Map
```

---

## 73. What happens if duplicate keys occur in toMap()?

This can cause:

```text
IllegalStateException
```

if no merge function is supplied.

Example:

```java
Collectors.toMap(
    Employee::getId,
    Employee::getName
)
```

If two employees have the same ID, Java doesn't know which value to keep.

---

## 74. How can duplicate keys be handled?

Provide a merge function.

Conceptually:

```java
Collectors.toMap(
    Employee::getId,
    Employee::getName,
    (oldValue, newValue) -> newValue
)
```

Now the new value can replace the old value.

---

## 75. What is groupingBy()?

`groupingBy()` groups objects based on a property.

For example:

```text
Employees
   ↓
Department
   ↓
Map<Department, List<Employee>>
```

Example result:

```text
IT       -> [John, David]
HR       -> [Alex, Mike]
Finance  -> [Sam]
```

---

# ConcurrentHashMap

## 76. What is ConcurrentHashMap?

`ConcurrentHashMap` is a Map implementation designed for use by multiple threads concurrently.

```java
Map<Integer, String> map =
        new ConcurrentHashMap<>();
```

---

## 77. Is HashMap thread-safe?

No.

Multiple threads modifying a `HashMap` at the same time can cause unsafe behavior.

---

## 78. Is ConcurrentHashMap thread-safe?

Yes.

It is designed for concurrent access.

---

## 79. HashMap vs ConcurrentHashMap

| HashMap                                 | ConcurrentHashMap                     |
| --------------------------------------- | ------------------------------------- |
| Not thread-safe                         | Thread-safe for concurrent operations |
| Allows null key/value                   | Does not allow null key/value         |
| Suitable for normal single-threaded use | Suitable for concurrent use           |
| No concurrency guarantees               | Designed for concurrent access        |

---

## 80. Can ConcurrentHashMap contain null?

No.

```java
map.put(null, "John");
```

is not allowed.

Likewise:

```java
map.put(1, null);
```

is not allowed.

---

## 81. Why does ConcurrentHashMap not allow null?

Because in concurrent operations, `null` could make it ambiguous whether:

```text
Key does not exist
```

or:

```text
Key exists with null value
```

---

## 82. How does ConcurrentHashMap achieve concurrency?

It allows multiple threads to work with different parts of the map concurrently while coordinating updates safely.

Modern implementations use techniques such as:

```text
CAS
+
fine-grained synchronization
+
volatile operations
```

rather than simply synchronizing the entire map for every operation.

---

# Hashtable

## 83. What is Hashtable?

`Hashtable` is an older, legacy Map implementation.

```java
Map<Integer, String> map = new Hashtable<>();
```

It is synchronized and does not allow `null` keys or values.

---

## 84. Hashtable vs ConcurrentHashMap

`Hashtable` synchronizes its operations more broadly.

`ConcurrentHashMap` was designed for better concurrent scalability.

For new concurrent applications, `ConcurrentHashMap` is generally the modern choice.

---

# Synchronized Map

## 85. What is Collections.synchronizedMap()?

It can wrap a normal Map with synchronization.

```java
Map<Integer, String> map =
    Collections.synchronizedMap(new HashMap<>());
```

It provides synchronized access to the map's methods.

---

## 86. HashMap vs synchronizedMap vs ConcurrentHashMap

### HashMap

```text
Not thread-safe
```

### synchronizedMap

```text
Thread-safe wrapper
```

### ConcurrentHashMap

```text
Designed specifically for concurrent access
```

---

# Iterators and Modification

## 87. What is a fail-fast iterator?

A fail-fast iterator detects certain structural modifications to a collection while it is being iterated and may throw:

```text
ConcurrentModificationException
```

Example concept:

```java
for (Integer key : map.keySet()) {
    map.remove(key);
}
```

This can result in `ConcurrentModificationException`.

---

## 88. What is ConcurrentModificationException?

It can occur when a collection is structurally modified while using an iterator in a way the iterator does not permit.

It is not specifically a "multiple threads only" exception.

It can happen in a single thread too.

---

# Tricky Map Concepts

## 89. What happens here?

```java
map.put("A", 10);
map.put("A", 20);
```

Result:

```text
A -> 20
```

The second `put()` replaces the old value.

---

## 90. What happens here?

```java
map.put(null, 10);
map.put(null, 20);
```

For `HashMap`:

```text
null -> 20
```

Only one `null` key exists.

---

## 91. Can HashMap contain multiple null values?

Yes.

```java
map.put(1, null);
map.put(2, null);
map.put(3, null);
```

Result:

```text
1 -> null
2 -> null
3 -> null
```

---

## 92. Can HashMap contain two entries with the same hash code?

Yes.

Two different keys can have the same hash code.

HashMap handles this using collision handling.

---

## 93. Can HashMap contain two equal keys?

No.

If two keys are considered equal:

```java
key1.equals(key2) == true
```

they represent the same logical key.

The newer value replaces the old value.

---

## 94. Why shouldn't we depend on HashMap iteration order?

Because HashMap does not promise a particular iteration order.

Even if your current program appears to return:

```text
1
2
3
```

you should not assume it will always do so.

---

## 95. Why can HashMap sometimes appear sorted?

It can happen because of the current hash distribution and internal structure.

But that is **an implementation behavior, not a guarantee**.

---

## 96. containsKey() vs get()

Consider:

```java
map.put("A", null);
```

Now:

```java
map.get("A");
```

returns:

```text
null
```

But the key actually exists.

Therefore:

```java
map.containsKey("A");
```

returns:

```text
true
```

So use `containsKey()` when you specifically need to know whether the key exists.

---

# Important Map Comparison

## 97. HashMap vs LinkedHashMap vs TreeMap

| Feature            | HashMap             | LinkedHashMap                | TreeMap          |
| ------------------ | ------------------- | ---------------------------- | ---------------- |
| Ordering           | No guaranteed order | Insertion/access order       | Sorted key order |
| Internal structure | Hash table          | Hash table + linked ordering | Red-Black Tree   |
| Average get        | O(1)                | O(1)                         | O(log n)         |
| Allows null key    | Yes, one            | Yes, one                     | Normally no      |
| Duplicate keys     | No                  | No                           | No               |
| Duplicate values   | Yes                 | Yes                          | Yes              |
| Main use           | Fast lookup         | Lookup + order               | Sorted keys      |

---

# Quick Interview Revision

## 98. Most important points to remember

```text
Map
 ↓
Key + Value

Key
 ↓
Cannot be duplicated

Value
 ↓
Can be duplicated
```

### HashMap

```text
Fast lookup
No guaranteed order
Allows one null key
Allows null values
Not thread-safe
```

### LinkedHashMap

```text
HashMap + ordering
Maintains insertion order by default
Can maintain access order
Useful for LRU cache
```

### TreeMap

```text
Sorted keys
Red-Black Tree
O(log n)
Normally no null key
```

### ConcurrentHashMap

```text
Thread-safe concurrent Map
No null keys
No null values
Useful for multi-threaded applications
```

### HashMap internal flow

```text
put(key, value)
      ↓
  hashCode()
      ↓
   bucket
      ↓
 collision?
      ↓
equals()
      ↓
store/find entry
```

### equals/hashCode rule

```text
If a.equals(b) == true

then

a.hashCode() == b.hashCode()
```

### Simple rule for choosing Map

```text
Need fast lookup?
        ↓
    HashMap

Need insertion/access order?
        ↓
  LinkedHashMap

Need sorted keys?
        ↓
    TreeMap

Need concurrent access?
        ↓
ConcurrentHashMap
```
