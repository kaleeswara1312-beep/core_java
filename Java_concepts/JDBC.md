# JDBC — Exam & Recall Notes

## 1. What is JDBC?

**JDBC (Java Database Connectivity)** is the standard Java API used to connect Java applications with relational databases such as **MySQL, PostgreSQL, Oracle, SQL Server**, etc.

### Simple mental model

```text
Java Application
      |
      | JDBC API
      v
JDBC Driver (MySQL / PostgreSQL / etc.)
      |
      v
RDBMS Database
```

JDBC itself is **not the database driver**. It provides the common Java API, while a vendor-specific JDBC driver translates JDBC calls into the database-specific protocol.

### Typical setup

1. Add the required database JDBC driver dependency.
2. Create a `Connection`.
3. Create a `Statement` / `PreparedStatement`.
4. Execute SQL.
5. Process the result.
6. Close resources.

---

# 2. JDBC Packages

The main package is:

```java
import java.sql.*;
```

Important JDBC interfaces/classes:

| API | Purpose |
|---|---|
| `Connection` | Represents a connection to the database |
| `Statement` | Executes SQL statements directly |
| `PreparedStatement` | Executes parameterized/precompiled SQL |
| `ResultSet` | Holds rows returned by a query |
| `SQLException` | Handles database-related errors |
| `DriverManager` | Helps establish database connections |

---

# 3. JDBC Driver

A database vendor provides a JDBC driver.

Examples:

```text
MySQL       -> MySQL JDBC Driver
PostgreSQL  -> PostgreSQL JDBC Driver
Oracle      -> Oracle JDBC Driver
```

The application adds the appropriate driver dependency.

### Maven example — MySQL

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>YOUR_VERSION</version>
</dependency>
```

### Maven example — PostgreSQL

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>YOUR_VERSION</version>
</dependency>
```

**Exam point:** JDBC API and JDBC driver are different things.

- JDBC API = common Java database API
- JDBC Driver = database-specific implementation

---

# 4. Loading the Driver

Older JDBC code commonly used:

```java
Class.forName("com.mysql.cj.jdbc.Driver");
```

This loads/registers the JDBC driver class.

Modern JDBC drivers generally support **automatic driver loading**, so explicitly calling `Class.forName()` is usually unnecessary when the driver is correctly available on the classpath.

### Exam recall

```text
Class.forName()
       ↓
Load/register JDBC driver
```

**Important:** Do not say that `Class.forName()` is always required in modern JDBC applications.

---

# 5. Establishing a Connection

Example:

```java
Connection connection = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/school",
    "root",
    "password"
);
```

Flow:

```text
Java Application
      ↓
DriverManager
      ↓
JDBC Driver
      ↓
MySQL Database
```

---

# 6. Statement

`Statement` is used to execute SQL directly.

Example:

```java
Statement st = connection.createStatement();

st.executeUpdate(
    "UPDATE students SET name = 'John' WHERE id = 1"
);
```

You can reuse the **same `Statement` object** for different SQL strings:

```java
st.executeUpdate("UPDATE students SET name = 'John' WHERE id = 1");

st.executeUpdate("DELETE FROM students WHERE id = 2");

st.executeQuery("SELECT * FROM students");
```

### Main problem with Statement

If values are dynamically concatenated into SQL:

```java
int id = 10;

Statement st = connection.createStatement();

ResultSet rs = st.executeQuery(
    "SELECT * FROM students WHERE id = " + id
);
```

It can lead to **SQL injection** when untrusted input is included.

---

# 7. PreparedStatement

`PreparedStatement` is used for **parameterized SQL**.

Example:

```java
PreparedStatement ps = connection.prepareStatement(
    "SELECT * FROM students WHERE id = ?"
);

ps.setInt(1, 3);

ResultSet rs = ps.executeQuery();
```

The `?` is a parameter placeholder.

```text
SQL:
SELECT * FROM students WHERE id = ?

                 ↓
          ps.setInt(1, 3)

                 ↓
SELECT * FROM students WHERE id = 3
```

## Why PreparedStatement?

### 1. Helps prevent SQL Injection

Instead of building SQL like:

```java
"SELECT * FROM users WHERE name = '" + userInput + "'"
```

use:

```java
PreparedStatement ps = connection.prepareStatement(
    "SELECT * FROM users WHERE name = ?"
);

ps.setString(1, userInput);
```

The input is treated as a parameter rather than being directly interpreted as SQL syntax.

### 2. Better for repeated execution

The SQL structure can be prepared once and executed multiple times with different parameter values.

Example:

```java
PreparedStatement ps = connection.prepareStatement(
    "INSERT INTO students(name, age) VALUES (?, ?)"
);

ps.setString(1, "John");
ps.setInt(2, 20);
ps.executeUpdate();

ps.setString(1, "David");
ps.setInt(2, 22);
ps.executeUpdate();
```

Here, the same `PreparedStatement` can be reused with different values.

---

# 8. Statement vs PreparedStatement

| Feature | Statement | PreparedStatement |
|---|---|---|
| SQL | Passed directly | Parameterized |
| Placeholder `?` | No | Yes |
| Dynamic values | Usually string concatenation | `setInt`, `setString`, etc. |
| SQL Injection protection | No built-in protection | Helps prevent SQL injection |
| Reuse | Same object can execute different SQL strings | Same prepared SQL can be executed repeatedly |
| Compilation/preparation | SQL is processed for each execution | Prepared SQL can be reused |
| Recommended for user input | No | Yes |

### Important correction

Do **not** memorize:

> "PreparedStatement is compiled only once."

That is an oversimplification.

A better exam/interview answer is:

> `PreparedStatement` allows the SQL statement structure to be prepared separately from its parameter values, and it can be reused efficiently for repeated executions. The exact preparation/caching behavior can depend on the JDBC driver and database.

---

# 9. execute(), executeQuery(), executeUpdate()

These three methods are extremely important for exams.

## execute()

```java
boolean result = st.execute(sql);
```

Returns:

```text
true  → first result is a ResultSet
false → first result is an update count or no result
```

So `execute()` returns a **boolean**, but the boolean does NOT simply mean "SQL succeeded."

### Example

```java
boolean result = st.execute(
    "SELECT * FROM students"
);
```

For a query producing a `ResultSet`, the result is generally `true`.

---

# 10. executeQuery()

Used for statements that return a `ResultSet`, normally `SELECT`.

```java
ResultSet rs = st.executeQuery(
    "SELECT * FROM students"
);
```

Then:

```java
while (rs.next()) {
    System.out.println(rs.getInt("id"));
    System.out.println(rs.getString("name"));
}
```

### Recall

```text
executeQuery()
      ↓
ResultSet
      ↓
Rows
```

**Exam answer:**

> `executeQuery()` is generally used for SQL statements that return a `ResultSet`, such as `SELECT`.

---

# 11. executeUpdate()

Used for SQL statements that modify data.

Typically:

```text
INSERT
UPDATE
DELETE
```

Example:

```java
int count = st.executeUpdate(
    "UPDATE students SET name = 'John' WHERE id = 1"
);
```

`count` represents the number of affected rows, subject to JDBC/database semantics.

Example:

```text
UPDATE → 1 row affected
count = 1
```

Another example:

```java
int count = st.executeUpdate(
    "DELETE FROM students WHERE id = 10"
);
```

### Recall

```text
executeUpdate()
       ↓
affected-row count
```

---

# 12. Quick Method Comparison

```text
execute()
   ↓
boolean
   ↓
Can handle different kinds of results


executeQuery()
   ↓
ResultSet
   ↓
SELECT / result-producing SQL


executeUpdate()
   ↓
int
   ↓
INSERT / UPDATE / DELETE
```

### Memory trick

**Q → Query → ResultSet**

**U → Update → affected rows**

**E → Execute → boolean**

---

# 13. PreparedStatement Execution

With `PreparedStatement`, you normally do not pass the SQL string again.

Example:

```java
PreparedStatement ps = connection.prepareStatement(
    "SELECT * FROM students WHERE id = ?"
);

ps.setInt(1, 3);

ResultSet rs = ps.executeQuery();
```

Notice:

```java
ps.executeQuery();
```

NOT:

```java
ps.executeQuery("SELECT ...");
```

The SQL was already supplied during:

```java
connection.prepareStatement("SELECT ...");
```

### Different operations

```java
ps.execute();
ps.executeQuery();
ps.executeUpdate();
```

The appropriate method depends on what the SQL does.

---

# 14. Why do we create different PreparedStatements?

A `PreparedStatement` represents a **specific SQL statement with its parameter placeholders**.

Example:

```java
PreparedStatement selectPs =
    connection.prepareStatement(
        "SELECT * FROM students WHERE id = ?"
    );

PreparedStatement updatePs =
    connection.prepareStatement(
        "UPDATE students SET name = ? WHERE id = ?"
    );
```

These are two different SQL statements, so they normally have two different `PreparedStatement` objects.

However, the **same PreparedStatement can be reused** for different parameter values:

```java
PreparedStatement ps = connection.prepareStatement(
    "SELECT * FROM students WHERE id = ?"
);

ps.setInt(1, 1);
ps.executeQuery();

ps.setInt(1, 2);
ps.executeQuery();

ps.setInt(1, 3);
ps.executeQuery();
```

---

# 15. Batch Processing

Batching is useful when executing many similar database operations.

Without batching:

```text
Java
 ↓
INSERT
 ↓
Database

Java
 ↓
INSERT
 ↓
Database

Java
 ↓
INSERT
 ↓
Database
```

Many separate executions can create additional network/database overhead.

With batching:

```text
Java
 ↓
Add many operations
 ↓
Batch
 ↓
executeBatch()
 ↓
Database
```

---

# 16. Statement Batch

Example:

```java
Statement st = connection.createStatement();

st.addBatch(
    "INSERT INTO students(name, age) VALUES ('John', 20)"
);

st.addBatch(
    "INSERT INTO students(name, age) VALUES ('David', 21)"
);

st.addBatch(
    "INSERT INTO students(name, age) VALUES ('Mike', 22)"
);

int[] results = st.executeBatch();
```

`executeBatch()` returns an array of update counts:

```java
int[] results
```

Each element corresponds to a command in the batch, subject to JDBC driver/database behavior.

---

# 17. PreparedStatement Batch

For inserting many records with the same SQL structure, `PreparedStatement` is usually the cleaner approach.

```java
PreparedStatement ps = connection.prepareStatement(
    "INSERT INTO students(name, age) VALUES (?, ?)"
);

ps.setString(1, "John");
ps.setInt(2, 20);
ps.addBatch();

ps.setString(1, "David");
ps.setInt(2, 21);
ps.addBatch();

ps.setString(1, "Mike");
ps.setInt(2, 22);
ps.addBatch();

int[] results = ps.executeBatch();
```

### Flow

```text
prepare SQL once
      ↓
set parameters
      ↓
addBatch()
      ↓
set new parameters
      ↓
addBatch()
      ↓
executeBatch()
```

---

# 18. Does executeBatch() mean "only one DB operation"?

Be careful with this statement.

`executeBatch()` sends/executes a group of commands as a batch, reducing per-statement overhead, but it does **not necessarily mean the database performs everything as one single SQL operation**.

Actual behavior depends on the:

- JDBC driver
- Database
- Driver configuration
- SQL type

### Better interview answer

> Batch processing groups multiple JDBC operations so they can be submitted/executed efficiently as a batch, reducing round trips and overhead compared with executing each operation individually.

---

# 19. Batch vs Transaction

These are related but **not the same thing**.

### Batch

Concerned mainly with:

```text
Efficiency
↓
Group multiple operations
```

### Transaction

Concerned mainly with:

```text
Atomicity / consistency
↓
Commit or rollback
```

Example:

```java
connection.setAutoCommit(false);

PreparedStatement ps = connection.prepareStatement(
    "INSERT INTO students(name, age) VALUES (?, ?)"
);

ps.setString(1, "John");
ps.setInt(2, 20);
ps.addBatch();

ps.setString(1, "David");
ps.setInt(2, 21);
ps.addBatch();

ps.executeBatch();

connection.commit();
```

If something fails, application logic may use:

```java
connection.rollback();
```

---

# 20. Resource Closing

Important JDBC resources include:

```text
Connection
Statement / PreparedStatement
ResultSet
```

They should be closed to release database/JDBC resources.

Best practice:

```java
try (
    Connection connection = DriverManager.getConnection(url, user, password);
    PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM students WHERE id = ?"
    )
) {
    ps.setInt(1, 3);

    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
    }
}
```

This uses **try-with-resources**, which automatically closes `AutoCloseable` JDBC resources.

### Why close them?

Closing JDBC resources can release:

- Database connections
- Result-set resources
- Statement resources
- Driver/database-side resources

A `Connection` is especially important because connection pools may reuse connections rather than physically destroying them.

---

# 21. Complete JDBC Flow

```text
1. Add JDBC Driver dependency
             ↓
2. Load/register driver
   (Class.forName in older/common examples)
             ↓
3. Get Connection
             ↓
4. Create Statement /
   PreparedStatement
             ↓
5. Set parameters
   (PreparedStatement)
             ↓
6. Execute SQL
             ↓
   ┌───────────────┬────────────────┬─────────────────┐
   │ execute()     │ executeQuery() │ executeUpdate() │
   │ boolean       │ ResultSet      │ int             │
   └───────────────┴────────────────┴─────────────────┘
             ↓
7. Process ResultSet / update count
             ↓
8. Close resources
```

---

# 22. Interview Example

### Question:
Why would you prefer PreparedStatement over Statement?

### Answer:

> `PreparedStatement` allows us to use parameterized SQL instead of concatenating user input into SQL strings. This helps prevent SQL injection and makes parameter handling cleaner. It can also be reused efficiently for repeated executions of the same SQL structure.

---

# 23. Interview Example: Statement vs PreparedStatement

### Question:
Can one Statement object execute multiple queries?

### Answer:

> Yes. A `Statement` object can execute different SQL strings through methods such as `executeQuery()` and `executeUpdate()`.

### Question:
Can one PreparedStatement execute multiple queries?

### Answer:

> A `PreparedStatement` is associated with a specific SQL statement, so a different SQL statement normally requires a different `PreparedStatement`. However, the same `PreparedStatement` can be reused many times with different parameter values.

---

# 24. Interview Example: execute vs executeQuery

### Question:
What's the difference between `execute()` and `executeQuery()`?

### Answer:

> `execute()` is a general-purpose method that returns a boolean indicating whether the first result is a `ResultSet`. `executeQuery()` is intended for result-producing SQL and returns a `ResultSet`.

---

# 25. Interview Example: executeUpdate

### Question:
What does executeUpdate() return?

### Answer:

> It returns an integer representing the number of rows affected for typical `INSERT`, `UPDATE`, and `DELETE` operations.

---

# 26. Interview Example: Batch

### Question:
Why do we use JDBC batching?

### Answer:

> JDBC batching groups multiple database operations so they can be submitted efficiently together, reducing network round trips and execution overhead. It is especially useful when inserting, updating, or deleting many records.

---

# 27. Common Exam Traps

### Trap 1

❌ `executeQuery()` returns number of rows.

✅ `executeQuery()` returns a `ResultSet`.

---

### Trap 2

❌ `execute()` returns `true` when SQL succeeds.

✅ `execute()` returns `true` when the first result is a `ResultSet`; otherwise it returns `false` for an update count/no result.

---

### Trap 3

❌ `PreparedStatement` always compiles only once.

✅ It separates SQL structure from parameter values and can be reused efficiently; exact preparation/caching depends on driver/database behavior.

---

### Trap 4

❌ Every execution requires a new PreparedStatement.

✅ The same PreparedStatement can be reused with different parameter values.

---

### Trap 5

❌ executeBatch() means the DB always executes everything as one SQL statement.

✅ It submits a group of commands as a batch; exact execution behavior depends on the driver/database.

---

### Trap 6

❌ Class.forName() is mandatory in every modern JDBC application.

✅ Modern JDBC drivers generally support automatic driver loading.

---

### Trap 7

❌ Batch and transaction are the same.

✅ Batch = grouping for efficiency.  
Transaction = atomic unit of work with commit/rollback semantics.

---

# 28. One-Minute Revision

```text
JDBC
 ↓
Java API for relational database connectivity

Driver
 ↓
Database-specific JDBC implementation

Connection
 ↓
Connection between Java and DB

Statement
 ↓
Direct SQL

PreparedStatement
 ↓
Parameterized SQL
 ↓
Helps prevent SQL injection
 ↓
Reusable with different parameter values

execute()
 ↓
boolean

executeQuery()
 ↓
ResultSet

executeUpdate()
 ↓
affected-row count

addBatch()
 ↓
Add operation to batch

executeBatch()
 ↓
Execute/submits batch

try-with-resources
 ↓
Automatically closes JDBC resources
```

# 29. Super-Short Memory Trick

**C → S → P → E → B → C**

```text
C = Connection
S = Statement / PreparedStatement
P = Parameters
E = Execute
B = Batch
C = Close
```

And remember:

```text
Q → ResultSet
U → Update count
E → Boolean
```

---

# 30. Final Exam Cheat Sheet

| Topic | Remember |
|---|---|
| JDBC | Java API for DB connectivity |
| Driver | Database-specific implementation |
| `java.sql.*` | Main JDBC package |
| `Class.forName()` | Traditional explicit driver loading |
| `Connection` | DB connection |
| `Statement` | Direct SQL |
| `PreparedStatement` | Parameterized SQL |
| `?` | Parameter placeholder |
| SQL Injection | PreparedStatement helps prevent it |
| `execute()` | `boolean` |
| `executeQuery()` | `ResultSet` |
| `executeUpdate()` | `int` update count |
| `addBatch()` | Add operation to batch |
| `executeBatch()` | Execute batch; returns update counts |
| Batch | Improves efficiency for many operations |
| Transaction | Commit / rollback / atomicity |
| try-with-resources | Automatic resource closing |

## Golden Rule

> **Use `PreparedStatement` for parameterized SQL, `executeQuery()` for result-producing queries, `executeUpdate()` for typical DML, and batching when performing many similar operations.**
