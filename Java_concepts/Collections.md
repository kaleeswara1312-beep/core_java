# Java Collections — Short Exam Revision Notes

## 1. List Interface

`List` is an **ordered collection** that:
- Allows **duplicates**
- Preserves **insertion order**
- Supports **index-based access**

### ArrayList

**Definition:** `ArrayList` is a `List` implementation backed by a **dynamic array**.

**Data Structure:** Dynamic Array

**Example:**
```java
List<String> names = new ArrayList<>();
names.add("A");
names.add("B");
names.add("A");

System.out.println(names.get(1)); // B
```

**Key points**
- Fast random access: `get(index)` → **O(1)**
- Add at end → usually **O(1) amortized**
- Insert/delete in middle or beginning → **O(n)** because elements may need shifting
- Preserves insertion order
- Allows duplicates and multiple `null` values

### LinkedList

**Definition:** `LinkedList` implements `List` and `Deque` using a **doubly linked list**.

**Data Structure:** Doubly Linked List

**Example:**
```java
LinkedList<String> names = new LinkedList<>();
names.add("A");
names.add("B");
names.addFirst("Start");
names.addLast("End");
```

**Key points**
- Access by index → **O(n)** because nodes must be traversed
- Add/remove at the ends → **O(1)**
- Insert/delete is efficient **when the node position is already known**
- Preserves insertion order
- Allows duplicates and `null`

> **Exam trap:** LinkedList is not automatically faster than ArrayList for every insertion/deletion. Finding the middle node still takes **O(n)**.

---

## 2. Size vs Capacity

### ArrayList

- **Size** = number of elements currently stored.
- **Capacity** = amount of internal array space currently available before resizing.

```java
ArrayList<Integer> list = new ArrayList<>(10);

list.add(100);
list.add(200);
```

Here:
- Size = `2`
- Initial capacity = `10`

When capacity becomes insufficient, ArrayList creates a **larger internal array and copies elements**.

---

## 3. Homogeneous vs Heterogeneous

### Homogeneous
A collection contains values of the **same type**.

```java
List<String> names = new ArrayList<>();
```

### Heterogeneous
A collection can contain values of **different types**.

```java
List<Object> data = new ArrayList<>();
data.add("Java");
data.add(100);
data.add(true);
```

**Exam point:** Java generics normally provide **compile-time type safety**, so collections such as `List<String>` are preferred over raw collections.

---

## 4. Generics

Generics specify the **type of elements** a collection can store.

```java
List<Integer> numbers = new ArrayList<>();
numbers.add(10);
// numbers.add("Java"); // Compile-time error
```

**Memory trick:**  
`<T>` = **Type**

---

# 5. Stack

**Principle:** **LIFO — Last In, First Out**

Example:

```text
Push: 10 → 20 → 30

Top
 ↓
30  ← pop()
20
10
```

`Stack` is a legacy class. In modern Java, `Deque`/`ArrayDeque` is generally preferred for stack operations.

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(10);
stack.push(20);
stack.pop(); // 20
```

**Memory trick:** Stack = **LIFO**

---

# 6. Queue

**Principle:** **FIFO — First In, First Out**

```text
Front                         Rear
 ↓                             ↓
10 → 20 → 30 → 40
 ↑
poll() removes 10
```

Common operations:

| Operation | Meaning |
|---|---|
| `add()` / `offer()` | Insert |
| `remove()` / `poll()` | Remove front |
| `element()` / `peek()` | View front |

**Memory trick:** Queue = **FIFO**

---

# 7. PriorityQueue

`PriorityQueue` implements the `Queue` interface.

**Main idea:** The element with the **highest priority** is at the head. With natural ordering, the **smallest element** has the highest priority.

**Data Structure:** **Heap** (typically a binary heap / min-heap for natural ordering)

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();

pq.add(30);
pq.add(10);
pq.add(20);

System.out.println(pq.poll()); // 10
```

**Key points**
- Duplicates allowed
- `null` not allowed
- Natural ordering gives smallest element at the head
- `poll()` removes the head
- `peek()` views the head
- **Do not assume iteration prints all elements in sorted order**

> **Exam trap:** PriorityQueue is **not a sorted list**. It guarantees priority at the head, not sorted iteration.

---

# 8. ArrayDeque

`ArrayDeque` is a **double-ended queue (Deque)**.

**Data Structure:** Resizable circular array / array-based deque

It allows insertion and deletion at **both ends**.

```java
Deque<Integer> dq = new ArrayDeque<>();

dq.addFirst(10);
dq.addLast(20);

dq.removeFirst(); // 10
dq.removeLast();  // 20
```

**Key points**
- FIFO behavior → can be used as a queue
- LIFO behavior → can be used as a stack
- Duplicates allowed
- `null` not allowed
- Efficient operations at both ends

---

# 9. Set Interface

A `Set` represents a collection where **duplicate elements are not allowed**.

| Class | Duplicate | Order |
|---|---|---|
| HashSet | ❌ | No guaranteed order |
| LinkedHashSet | ❌ | Insertion order |
| TreeSet | ❌ | Sorted order |

---

## HashSet

**Data Structure:** **Hash table** (implemented using hash-based buckets; modern Java can treeify heavily-colliding buckets)

```java
Set<String> set = new HashSet<>();

set.add("B");
set.add("A");
set.add("B");

System.out.println(set); // B appears only once
```

**Key points**
- Duplicates not allowed
- No guaranteed insertion order
- Allows one `null`
- Fast average `add`, `remove`, `contains`: **O(1)**

---

## LinkedHashSet

**Data Structure:** **Hash table + doubly linked list**

```java
Set<String> set = new LinkedHashSet<>();

set.add("B");
set.add("A");
set.add("C");

System.out.println(set); // [B, A, C]
```

**Key points**
- No duplicates
- Preserves insertion order
- Allows one `null`
- Slightly more memory than HashSet because it maintains links between entries

---

## TreeSet

`TreeSet` implements `SortedSet` and `NavigableSet`.

**Data Structure:** **Red-Black Tree** (self-balancing binary search tree)

```java
Set<Integer> set = new TreeSet<>();

set.add(30);
set.add(10);
set.add(20);

System.out.println(set); // [10, 20, 30]
```

**Key points**
- No duplicates
- Maintains sorted order
- Natural ordering or supplied `Comparator`
- `add`, `remove`, `contains` → **O(log n)**
- `null` is generally not allowed with natural ordering

---

# 10. Map

A `Map` stores data as:

```text
Key → Value
```

**Important:** Map is separate from the `Collection` interface.

---

## HashMap

**Data Structure:** **Hash table** with buckets; modern Java may use balanced tree structures for heavily-colliding buckets.

```java
Map<Integer, String> map = new HashMap<>();

map.put(1, "Java");
map.put(2, "Spring");
map.put(1, "Python");

System.out.println(map.get(1)); // Python
```

**Key points**
- Key-value pairs
- Duplicate keys **not allowed**
- If the same key is inserted again, its value is replaced
- Multiple keys can have the same value
- Allows one `null` key and multiple `null` values
- No guaranteed insertion order
- Average `put/get/remove` → **O(1)**

---

## LinkedHashMap

**Data Structure:** **Hash table + doubly linked list**

```java
Map<Integer, String> map = new LinkedHashMap<>();

map.put(1, "A");
map.put(2, "B");
map.put(3, "C");

System.out.println(map); // {1=A, 2=B, 3=C}
```

**Key points**
- Same basic key/value rules as HashMap
- Preserves **insertion order by default**
- Can also be configured for **access-order**
- Allows `null` key and `null` values

---

## TreeMap

> The correct class name is **TreeMap**, not `TreeHashMap`.

**Data Structure:** **Red-Black Tree**

```java
Map<Integer, String> map = new TreeMap<>();

map.put(30, "C");
map.put(10, "A");
map.put(20, "B");

System.out.println(map); // {10=A, 20=B, 30=C}
```

**Key points**
- Keys are maintained in sorted order
- Natural ordering or supplied `Comparator`
- Duplicate keys not allowed
- `put`, `get`, `remove` → **O(log n)**
- `null` key is not allowed with natural ordering
- Values can be `null`

---

# 11. What Is Insertion Order?

**Insertion order** means the order in which elements were successfully added to the collection.

Example:

```java
Set<String> set = new LinkedHashSet<>();

set.add("Java");
set.add("Python");
set.add("JavaScript");
```

Insertion order:

```text
1. Java
2. Python
3. JavaScript
```

When iterated, `LinkedHashSet` returns:

```text
Java → Python → JavaScript
```

### Why is insertion order important?

Order matters when the application needs **predictable iteration**, for example:
- Displaying items in the same order the user entered them
- Generating predictable reports
- Maintaining processing sequences
- Producing stable output for testing/debugging

### How does it work internally?

`LinkedHashSet` and `LinkedHashMap` combine:

```text
Hash table
     +
Doubly linked list
```

The hash table provides fast lookup, while the linked list connects entries in insertion sequence.

```text
Hash table lookup
      ↓
 [A] [B] [C]
  ↓   ↓   ↓

Doubly linked order:
A ↔ B ↔ C
```

So:
- **Hash table** → fast search
- **Linked list** → remembers order

> **Important:** HashSet and HashMap do **not** guarantee insertion order. If order matters, use LinkedHashSet or LinkedHashMap.

---

# 12. Quick Data Structure Map

| Java Class | Main Data Structure | Order | Duplicate | Null |
|---|---|---|---|---|
| `ArrayList` | Dynamic Array | Insertion | ✅ | ✅ |
| `LinkedList` | Doubly Linked List | Insertion | ✅ | ✅ |
| `ArrayDeque` | Resizable Array / Deque | Ends | ✅ | ❌ |
| `PriorityQueue` | Heap | Priority at head | ✅ | ❌ |
| `HashSet` | Hash Table | No guarantee | ❌ | One `null` |
| `LinkedHashSet` | Hash Table + Doubly Linked List | Insertion | ❌ | One `null` |
| `TreeSet` | Red-Black Tree | Sorted | ❌ | Generally ❌ |
| `HashMap` | Hash Table | No guarantee | Key ❌ | One `null` key |
| `LinkedHashMap` | Hash Table + Doubly Linked List | Insertion* | Key ❌ | One `null` key |
| `TreeMap` | Red-Black Tree | Sorted by key | Key ❌ | Natural ordering: ❌ key |

`*` LinkedHashMap can also use access-order when configured.

---

# 13. Exam Memory Tricks

```text
LIST
├── ArrayList     → Array → Fast index
└── LinkedList    → Links → Fast ends

QUEUE
├── Queue         → FIFO
├── PriorityQueue → Heap → Priority
└── ArrayDeque    → Double End

SET
├── HashSet       → Hash → No order
├── LinkedHashSet → Hash + Links → Insertion order
└── TreeSet       → Tree → Sorted

MAP
├── HashMap       → Hash → No order
├── LinkedHashMap → Hash + Links → Insertion order
└── TreeMap       → Tree → Sorted
```

### One-line interview answers

- **ArrayList:** Dynamic array; fast index access, slower middle insertion/deletion due to shifting.
- **LinkedList:** Doubly linked list; efficient end operations, but index traversal is slower.
- **HashSet:** Hash-based set with no guaranteed iteration order.
- **LinkedHashSet:** HashSet + linked structure to preserve insertion order.
- **TreeSet:** Sorted set backed by a Red-Black Tree.
- **HashMap:** Hash-based key-value store with no guaranteed order.
- **LinkedHashMap:** HashMap + linked structure to preserve insertion/access order.
- **TreeMap:** Sorted map backed by a Red-Black Tree.
- **PriorityQueue:** Heap-based queue where the highest-priority element is at the head.
