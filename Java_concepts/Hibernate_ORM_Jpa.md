# Hibernate, ORM, JPA & Persistence --- Exam Preparation Notes

## 1. ORM --- Object-Relational Mapping

**ORM (Object-Relational Mapping)** is a technique used to map Java
objects to relational database tables.

Instead of writing SQL for every CRUD operation, we work mainly with
Java objects and ORM APIs. The ORM framework generates the required SQL
behind the scenes.

### Simple example

Suppose we have a Java class:

``` java
public class Student {
    private int id;
    private String name;
    private String email;
}
```

It can be mapped to a database table:

``` text
students
--------------------------------
id       name       email
1        John       john@gmail.com
2        Alex       alex@gmail.com
```

The mapping is approximately:

``` text
Java Class       -> Database Table
Student          -> students

Java Object      -> Table Row
student object   -> one student record

Java Field       -> Table Column
id               -> id
name             -> name
email            -> email
```

### Why ORM?

Without ORM, we may need to write SQL manually:

``` sql
INSERT INTO students (id, name, email)
VALUES (1, 'John', 'john@gmail.com');
```

With Hibernate, we can work with an object:

``` java
Student student = new Student(1, "John", "john@gmail.com");
session.persist(student);
```

Hibernate generates the SQL.

### Important point

ORM does **not** mean SQL disappears completely.

For complex requirements, we may use:

-   JPQL
-   HQL
-   Criteria API
-   Native SQL

------------------------------------------------------------------------

# 2. Hibernate

Hibernate is a popular **ORM framework for Java**.

Its main job is to:

-   Map Java classes to database tables
-   Map Java fields to database columns
-   Generate SQL
-   Execute SQL
-   Manage persistence
-   Manage object states
-   Handle relationships
-   Provide transaction support
-   Reduce boilerplate JDBC code

### Example

``` java
Student student = new Student();
student.setName("John");

session.persist(student);
```

Hibernate may generate something similar to:

``` sql
insert into students (name) values ('John');
```

The exact SQL depends on the Hibernate version and database dialect.

------------------------------------------------------------------------

# 3. JPA

**JPA (Java Persistence API)** is a **specification**, not an ORM
framework itself.

JPA defines standard interfaces, annotations, and rules for persistence.

Examples:

``` java
@Entity
@Id
@GeneratedValue
```

and APIs such as:

``` java
EntityManager
```

Hibernate is one implementation/provider of JPA.

### Easy way to remember

``` text
JPA      = Rules / Specification
Hibernate = Implementation / Framework
```

Other JPA implementations have also existed, such as EclipseLink.

### Important exam question

**Are JPA and Hibernate the same?**

No.

JPA is a specification.

Hibernate is an ORM framework that implements the JPA specification.

------------------------------------------------------------------------

# 4. JDBC vs Hibernate

## JDBC

With JDBC, we generally write SQL ourselves.

``` java
PreparedStatement ps =
    connection.prepareStatement(
        "SELECT * FROM students WHERE id = ?"
    );

ps.setInt(1, 1);

ResultSet rs = ps.executeQuery();
```

## Hibernate

With Hibernate:

``` java
Student student = session.find(Student.class, 1);
```

Hibernate generates and executes the required SQL.

### Comparison

  -----------------------------------------------------------------------
  JDBC                                Hibernate
  ----------------------------------- -----------------------------------
  SQL is commonly written manually    SQL is generated for many
                                      operations

  More boilerplate                    Less boilerplate

  Developer manages ResultSet mapping ORM maps rows to objects

  Lower-level API                     Higher-level ORM abstraction

  Good when direct SQL control is     Good for object-oriented
  needed                              persistence
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# 5. Hibernate Architecture --- Important Components

A basic Hibernate application commonly involves:

``` text
Configuration
     |
     v
SessionFactory
     |
     v
Session
     |
     v
Transaction
     |
     v
Database
```

These components have different responsibilities.

------------------------------------------------------------------------

# 6. Configuration

The `Configuration` object is used in native Hibernate applications to
configure Hibernate.

Example:

``` java
Configuration configuration = new Configuration();

configuration.configure("hibernate.cfg.xml");
```

The configuration can contain:

-   Database URL
-   Username
-   Password
-   JDBC driver
-   Hibernate dialect
-   Schema-generation settings
-   Mapped entity classes

Then:

``` java
SessionFactory sessionFactory =
        configuration.buildSessionFactory();
```

### Important distinction

`hibernate.cfg.xml` is a **traditional/native Hibernate configuration
file**.

In modern Spring Boot applications, configuration is commonly placed in:

``` text
application.properties
```

or:

``` text
application.yml
```

So do not assume every Hibernate application must use
`hibernate.cfg.xml`.

------------------------------------------------------------------------

# 7. hibernate.cfg.xml

A traditional Hibernate configuration can look like:

``` xml
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>

        <property name="hibernate.connection.driver_class">
            com.mysql.cj.jdbc.Driver
        </property>

        <property name="hibernate.connection.url">
            jdbc:mysql://localhost:3306/employee_db
        </property>

        <property name="hibernate.connection.username">
            root
        </property>

        <property name="hibernate.connection.password">
            password
        </property>

        <property name="hibernate.hbm2ddl.auto">
            update
        </property>

        <mapping class="com.example.Student"/>

    </session-factory>
</hibernate-configuration>
```

Do not put real passwords into notes or source code in a real project.

------------------------------------------------------------------------

# 8. SessionFactory

`SessionFactory` is a heavyweight, thread-safe object used to create
Hibernate `Session` objects.

Example:

``` java
SessionFactory sessionFactory =
        configuration.buildSessionFactory();
```

Then:

``` java
Session session = sessionFactory.openSession();
```

### Important points

-   Usually one `SessionFactory` is created for an application/database
    configuration.
-   It is expensive to create.
-   It is designed to be reused.
-   It creates `Session` objects.

### Remember

``` text
SessionFactory -> creates Sessions
```

------------------------------------------------------------------------

# 9. Session

`Session` is the main Hibernate interface used to interact with
persistent objects.

Example:

``` java
Session session = sessionFactory.openSession();
```

Common operations include:

``` java
session.persist(student);
session.find(Student.class, 1);
session.remove(student);
session.merge(student);
```

### Important correction

A `Session` is **not the database itself**.

It represents a persistence context/unit of interaction between the Java
application and Hibernate.

------------------------------------------------------------------------

# 10. Transaction

A transaction represents a group of database operations that should be
treated as one logical unit.

For write operations, we normally use:

``` java
Transaction transaction = session.beginTransaction();

session.persist(student);

transaction.commit();
```

If something goes wrong:

``` java
transaction.rollback();
```

### Basic flow

``` text
beginTransaction()
       |
       v
perform DB operation
       |
       v
commit()
```

If an error occurs:

``` text
beginTransaction()
       |
       v
operation fails
       |
       v
rollback()
```

### Important exam point

A transaction is especially important for operations that modify
database state, such as:

-   INSERT
-   UPDATE
-   DELETE

Read operations may not always require an explicitly started transaction
depending on the environment and operation, but transaction boundaries
are important for consistent database access.

------------------------------------------------------------------------

# 11. Entity

An **Entity** is a Java class whose instances are associated with
persistent database data.

Using JPA:

``` java
@Entity
public class Student {

    @Id
    private int id;

    private String name;
    private String email;
}
```

`@Entity` tells the persistence provider that the class is an entity.

### Primary key

Every entity must have an identifier.

For example:

``` java
@Id
private int id;
```

The identifier uniquely identifies a row/entity.

------------------------------------------------------------------------

# 12. @Table

By default, the persistence provider may derive the table name from the
entity name.

We can explicitly specify it:

``` java
@Entity
@Table(name = "students")
public class Student {
}
```

Now:

``` text
Student class -> students table
```

------------------------------------------------------------------------

# 13. @Id

`@Id` marks the primary-key field.

``` java
@Id
private int id;
```

Database concept:

``` text
PRIMARY KEY
```

Java concept:

``` text
@Id
```

------------------------------------------------------------------------

# 14. @GeneratedValue

Instead of manually assigning IDs, we can configure ID generation.

``` java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

For MySQL auto-increment columns, `IDENTITY` is a commonly used
strategy.

Then:

``` java
Student student = new Student();
student.setName("John");

session.persist(student);
```

The database can generate the ID.

------------------------------------------------------------------------

# 15. Basic Entity Example

``` java
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    public Student() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

### Why the no-argument constructor?

JPA/Hibernate needs a no-argument constructor to instantiate entities.

It can be:

``` java
public Student() {
}
```

------------------------------------------------------------------------

# 16. hbm2ddl.auto

The property:

``` properties
hibernate.hbm2ddl.auto
```

controls schema-generation behavior in Hibernate.

Common values include:

``` text
create
create-drop
update
validate
none
```

## create

Hibernate creates the schema based on the mappings.

Existing schema objects can be dropped/recreated depending on the
database/schema operation.

**Important:** `create` is dangerous for existing data because schema
recreation can cause data loss.

It is mainly useful for learning/testing.

## create-drop

Creates the schema when the SessionFactory starts and drops it when the
SessionFactory closes.

Useful for tests.

## update

Hibernate attempts to update the schema to match the mappings without
intentionally dropping existing data.

Example:

``` properties
hibernate.hbm2ddl.auto=update
```

If you add a new mapped field, Hibernate may attempt to add the
corresponding column.

**Important:** `update` is convenient for development, but it is
generally not the preferred production database migration strategy.

## validate

Hibernate validates that the existing database schema matches the
mappings.

It does not create or modify the schema.

## none

Hibernate does not perform automatic schema generation.

### Important correction to your notes

`create` does **not simply mean "create the table every time the app
runs"** in the sense of preserving existing records. It can recreate
schema objects and therefore can destroy existing data.

Also, `update` does **not mean "update records."**

It means Hibernate updates the **database schema structure** to align
with mappings.

For example:

``` text
Java field added
       |
       v
Hibernate mapping changes
       |
       v
Database column may be added
```

It is about schema, not changing row data.

------------------------------------------------------------------------

# 17. CRUD Operations in Hibernate

CRUD means:

``` text
C -> Create
R -> Read
U -> Update
D -> Delete
```

------------------------------------------------------------------------

## Create / Insert

``` java
Transaction transaction = session.beginTransaction();

Student student = new Student();
student.setName("John");
student.setEmail("john@gmail.com");

session.persist(student);

transaction.commit();
```

Conceptually:

``` sql
INSERT INTO students (name, email)
VALUES ('John', 'john@gmail.com');
```

------------------------------------------------------------------------

# 18. Read / Find

``` java
Student student =
        session.find(Student.class, 1L);
```

Hibernate searches for the entity with ID `1`.

Conceptually:

``` sql
SELECT *
FROM students
WHERE id = 1;
```

### Printing the object

If you do:

``` java
System.out.println(student);
```

and see something like:

``` text
com.example.Student@5e2de80c
```

that is Java's default `toString()` representation.

Override `toString()`:

``` java
@Override
public String toString() {
    return "Student{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            '}';
}
```

Now:

``` java
System.out.println(student);
```

can produce:

``` text
Student{id=1, name='John', email='john@gmail.com'}
```

This is particularly useful while learning Hibernate.

------------------------------------------------------------------------

# 19. Update

A managed entity can be modified and Hibernate can detect the change.

Example:

``` java
Transaction transaction = session.beginTransaction();

Student student =
        session.find(Student.class, 1L);

student.setName("John Updated");

transaction.commit();
```

Hibernate can detect the changed state and issue an update.

Conceptually:

``` sql
UPDATE students
SET name = 'John Updated'
WHERE id = 1;
```

This automatic detection is called **dirty checking**.

------------------------------------------------------------------------

# 20. Delete

``` java
Transaction transaction = session.beginTransaction();

Student student =
        session.find(Student.class, 1L);

session.remove(student);

transaction.commit();
```

Conceptually:

``` sql
DELETE FROM students
WHERE id = 1;
```

------------------------------------------------------------------------

# 21. persist(), find(), merge(), remove()

These are important persistence operations.

### persist()

Makes a new entity persistent.

``` java
session.persist(student);
```

Usually used for new objects.

### find()

Loads an entity by primary key.

``` java
Student student =
        session.find(Student.class, 1L);
```

### remove()

Marks a managed entity for deletion.

``` java
session.remove(student);
```

### merge()

Copies the state of a detached entity into a managed entity.

``` java
Student managedStudent =
        session.merge(student);
```

A common exam distinction:

``` text
persist -> new/transient object becomes managed
merge   -> state is copied into a managed instance
```

------------------------------------------------------------------------

# 22. Entity Lifecycle / Object States

An entity can move through different states.

The main states are:

``` text
Transient
   |
   | persist()
   v
Managed
   |
   | session closes / entity detached
   v
Detached
   |
   | remove()
   v
Removed
```

## Transient

A newly created Java object that Hibernate is not managing.

``` java
Student student = new Student();
student.setName("John");
```

At this point, it is transient.

## Managed / Persistent

After:

``` java
session.persist(student);
```

the entity is associated with the persistence context.

Hibernate tracks it.

## Detached

If the entity is no longer associated with the persistence context:

``` java
session.close();
```

the entity can become detached.

## Removed

When:

``` java
session.remove(student);
```

the managed entity is marked for deletion.

------------------------------------------------------------------------

# 23. Persistence Context

The **persistence context** is a set of managed entity instances
associated with an `EntityManager`/Hibernate `Session`.

Hibernate keeps track of managed objects.

For example:

``` java
Student student =
        session.find(Student.class, 1L);

student.setName("New Name");
```

You do not necessarily have to call an explicit update method.

Hibernate tracks the change and, during flush/transaction processing,
can generate an UPDATE.

This is called **dirty checking**.

------------------------------------------------------------------------

# 24. First-Level Cache

Hibernate has a first-level cache associated with the `Session`.

Example:

``` java
Student s1 =
        session.find(Student.class, 1L);

Student s2 =
        session.find(Student.class, 1L);
```

Within the same session, Hibernate can reuse the managed entity instead
of executing the same database query again.

Conceptually:

``` text
Session
   |
   +-- First-level cache
           |
           +-- Student #1
```

### Important

First-level cache:

-   Exists by default
-   Is associated with a Session/persistence context
-   Is not shared between different sessions

------------------------------------------------------------------------

# 25. Second-Level Cache

Hibernate also supports a second-level cache.

Unlike the first-level cache, it can be shared across sessions through a
configured cache provider.

Conceptually:

``` text
Session 1 ----\
               \
Session 2 ------> Second-Level Cache
               /
Session 3 ----/
```

It is optional and requires configuration.

Common cache providers/solutions include Hibernate-compatible cache
implementations such as Ehcache or Infinispan, depending on the
setup/version.

------------------------------------------------------------------------

# 26. Dirty Checking

Dirty checking means Hibernate detects changes made to managed entities.

Example:

``` java
Student student =
        session.find(Student.class, 1L);

student.setName("Alex");
```

No explicit:

``` java
session.update(student);
```

is required for a managed entity in the normal JPA/Hibernate model.

At flush/commit time, Hibernate compares the current managed state and
can generate:

``` sql
UPDATE students
SET name = 'Alex'
WHERE id = 1;
```

### Exam definition

**Dirty checking is the mechanism by which Hibernate detects changes in
managed entities and synchronizes those changes with the database.**

------------------------------------------------------------------------

# 27. Flush

**Flush** means synchronizing the persistence context with the database.

Example:

``` java
session.flush();
```

Flush does not necessarily mean the transaction is committed.

Think:

``` text
Java object changes
       |
       v
Persistence Context
       |
       | flush
       v
SQL sent to DB
       |
       | commit
       v
Transaction committed
```

### Important distinction

``` text
flush  != commit
```

Flush synchronizes changes.

Commit completes the transaction.

------------------------------------------------------------------------

# 28. JPQL

**JPQL (Java Persistence Query Language)** is an object-oriented query
language defined by JPA.

JPQL queries entities and their fields rather than database tables and
columns.

Example:

``` java
String jpql =
        "SELECT s FROM Student s WHERE s.name = :name";
```

The query refers to:

``` text
Student
s.name
```

not:

``` text
students
name_column
```

Example using `EntityManager`:

``` java
List<Student> students =
        entityManager
            .createQuery(
                "SELECT s FROM Student s WHERE s.name = :name",
                Student.class
            )
            .setParameter("name", "John")
            .getResultList();
```

------------------------------------------------------------------------

# 29. HQL

**HQL (Hibernate Query Language)** is Hibernate's query language.

It is similar to JPQL and works with entities and properties.

Example:

``` java
String hql =
        "FROM Student s WHERE s.name = :name";
```

### JPQL vs HQL

``` text
JPQL -> JPA standard
HQL  -> Hibernate-specific query language
```

Modern HQL and JPQL have significant overlap, but HQL can provide
Hibernate-specific features.

------------------------------------------------------------------------

# 30. Native SQL

Sometimes we want to write actual database SQL.

Example:

``` java
SELECT *
FROM students
WHERE email = 'john@gmail.com';
```

This is native SQL.

Use native SQL when database-specific features or very specific SQL
control are needed.

### Comparison

  Query type   Works mainly with
  ------------ -----------------------------------
  JPQL         Entities and entity attributes
  HQL          Hibernate entities and properties
  Native SQL   Database tables and columns

------------------------------------------------------------------------

# 31. Relationships Between Entities

This is an important Hibernate topic.

Relational databases commonly have relationships such as:

``` text
One-to-One
One-to-Many
Many-to-One
Many-to-Many
```

JPA provides:

``` java
@OneToOne
@OneToMany
@ManyToOne
@ManyToMany
```

------------------------------------------------------------------------

# 32. Many-to-One Example

Suppose many students belong to one department.

``` text
Department
     |
     +---- Student
     +---- Student
     +---- Student
```

Java:

``` java
@ManyToOne
@JoinColumn(name = "department_id")
private Department department;
```

Database:

``` text
students
--------------------------------
id | name | department_id
```

Many students can reference one department.

------------------------------------------------------------------------

# 33. One-to-Many Example

One department has many students.

``` java
@OneToMany(mappedBy = "department")
private List<Student> students;
```

Conceptually:

``` text
Department
    |
    +-- Student
    +-- Student
    +-- Student
```

------------------------------------------------------------------------

# 34. Fetching --- EAGER vs LAZY

Fetching determines when related data is loaded.

## EAGER

Related data is loaded immediately.

Conceptually:

``` text
Load Student
     |
     +-- immediately load Department
```

## LAZY

Related data is loaded when it is accessed, subject to the persistence
context/session and mapping behavior.

Conceptually:

``` text
Load Student
     |
     | Department not immediately loaded
     |
     v
student.getDepartment()
     |
     v
Department may be loaded
```

### Exam point

Lazy loading can improve performance by avoiding unnecessary data
retrieval, but it must be used with awareness of
session/persistence-context boundaries.

------------------------------------------------------------------------

# 35. Cascade

Cascade controls whether certain operations performed on one entity are
propagated to associated entities.

Example:

``` java
@OneToMany(
    mappedBy = "department",
    cascade = CascadeType.ALL
)
private List<Student> students;
```

With cascade configured, certain operations can propagate from the
parent entity to its children.

Common cascade types:

``` text
PERSIST
MERGE
REMOVE
REFRESH
DETACH
ALL
```

------------------------------------------------------------------------

# 36. Orphan Removal

`orphanRemoval = true` can be used for certain parent-child
relationships.

Example:

``` java
@OneToMany(
    mappedBy = "department",
    orphanRemoval = true
)
private List<Student> students;
```

If a child is removed from the parent's managed collection, Hibernate
can remove the orphaned child from the database, depending on the
mapping and lifecycle.

------------------------------------------------------------------------

# 37. @Column

`@Column` allows us to customize column mapping.

``` java
@Column(name = "student_name", nullable = false)
private String name;
```

Now:

``` text
Java field:
name

Database column:
student_name
```

------------------------------------------------------------------------

# 38. @Transient

`@Transient` means a field should not be persisted as a database column.

``` java
@Transient
private String temporaryValue;
```

Hibernate/JPA does not map this field to a persistent column.

Be careful: Java's `transient` keyword and JPA's `@Transient` are
related concepts but are not identical mechanisms.

------------------------------------------------------------------------

# 39. Entity Relationships --- Owning Side

In bidirectional relationships, one side is usually the **owning side**.

Example:

``` java
@ManyToOne
@JoinColumn(name = "department_id")
private Department department;
```

The side containing the foreign-key mapping is typically the owning
side.

The inverse side can use:

``` java
@OneToMany(mappedBy = "department")
private List<Student> students;
```

### Important

`mappedBy` tells JPA that this side is not the owner of the relationship
mapping.

------------------------------------------------------------------------

# 40. Hibernate Dialect

A dialect tells Hibernate about SQL/database-specific behavior.

Historically, configurations often included:

``` properties
hibernate.dialect=...
```

Modern Hibernate versions can often determine the dialect automatically
from the JDBC/database metadata, so manually specifying it may not
always be necessary.

------------------------------------------------------------------------

# 41. Transactions and ACID

Database transactions follow important principles known as **ACID**:

``` text
A -> Atomicity
C -> Consistency
I -> Isolation
D -> Durability
```

## Atomicity

All operations in a transaction succeed or the transaction is rolled
back.

## Consistency

The database moves from one valid state to another valid state.

## Isolation

Concurrent transactions should not incorrectly interfere with one
another.

## Durability

Once committed, changes should survive failures according to the
database's guarantees.

------------------------------------------------------------------------

# 42. Transaction Example

Imagine transferring money:

``` text
Account A: -100
Account B: +100
```

These should happen as one logical transaction.

``` java
Transaction tx = session.beginTransaction();

accountA.setBalance(accountA.getBalance() - 100);
accountB.setBalance(accountB.getBalance() + 100);

tx.commit();
```

If something fails:

``` java
tx.rollback();
```

The transaction prevents a partially completed transfer.

------------------------------------------------------------------------

# 43. Common Hibernate Annotations

Important annotations for exams:

  Annotation          Purpose
  ------------------- ---------------------------------
  `@Entity`           Marks a persistent entity
  `@Table`            Specifies table details
  `@Id`               Specifies primary key
  `@GeneratedValue`   Configures ID generation
  `@Column`           Configures column mapping
  `@Transient`        Excludes field from persistence
  `@OneToOne`         One-to-one relationship
  `@OneToMany`        One-to-many relationship
  `@ManyToOne`        Many-to-one relationship
  `@ManyToMany`       Many-to-many relationship
  `@JoinColumn`       Specifies foreign-key column
  `@Enumerated`       Maps Java enum
  `@Version`          Used for optimistic locking

------------------------------------------------------------------------

# 44. Optimistic Locking

Optimistic locking is used to handle concurrent updates.

Example:

``` java
@Version
private Long version;
```

Hibernate uses the version value to detect whether another transaction
changed the entity.

Conceptually:

``` text
User A reads Student version 1
User B reads Student version 1

User A updates -> version becomes 2

User B tries to update using version 1
       |
       v
Conflict detected
```

This helps prevent one user's changes from silently overwriting another
user's changes.

------------------------------------------------------------------------

# 45. N+1 Query Problem

The **N+1 query problem** is a common ORM performance issue.

Suppose we load:

``` text
100 students
```

and then access each student's department individually.

Hibernate may execute:

``` text
1 query -> load students
100 queries -> load departments
```

Total:

``` text
101 queries
```

Hence:

``` text
N + 1
```

Possible solutions include appropriate fetching strategies, fetch joins,
entity graphs, batch fetching, and query redesign.

------------------------------------------------------------------------

# 46. Hibernate and Spring Boot

In Spring Boot applications, we normally do not manually create:

``` java
Configuration
SessionFactory
Session
```

for every operation.

Spring Boot can configure the persistence infrastructure for us.

For JPA, a typical dependency is:

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Hibernate is commonly the JPA provider used by Spring Boot.

We can then use:

``` java
@Entity
public class Student {
}
```

and a repository:

``` java
public interface StudentRepository
        extends JpaRepository<Student, Long> {
}
```

Then:

``` java
studentRepository.save(student);
```

can persist the entity.

------------------------------------------------------------------------

# 47. Native Hibernate vs Spring Data JPA

## Native Hibernate style

You may write:

``` java
Configuration configuration = new Configuration();
configuration.configure();

SessionFactory sessionFactory =
        configuration.buildSessionFactory();

Session session =
        sessionFactory.openSession();

Transaction tx =
        session.beginTransaction();

session.persist(student);

tx.commit();
```

You manage much of the Hibernate lifecycle yourself.

## Spring Data JPA

You can write:

``` java
public interface StudentRepository
        extends JpaRepository<Student, Long> {
}
```

Then:

``` java
studentRepository.save(student);
```

Spring manages much of the infrastructure and transaction integration.

### Remember

``` text
JPA
 |
 +-- Specification

Hibernate
 |
 +-- JPA implementation/provider

Spring Data JPA
 |
 +-- Repository abstraction built around JPA
```

These are related but different concepts.

------------------------------------------------------------------------

# 48. Important Exam Flow --- Native Hibernate

A simplified native Hibernate flow is:

``` text
1. Create/configure Hibernate configuration
              |
              v
2. Build SessionFactory
              |
              v
3. Open Session
              |
              v
4. Begin Transaction
              |
              v
5. Perform CRUD operation
              |
              v
6. Commit / Rollback
              |
              v
7. Close Session
              |
              v
8. Close SessionFactory
```

Example:

``` java
Configuration configuration = new Configuration();

configuration.configure();

SessionFactory sessionFactory =
        configuration.buildSessionFactory();

Session session =
        sessionFactory.openSession();

Transaction tx =
        session.beginTransaction();

Student student = new Student();
student.setName("John");
student.setEmail("john@gmail.com");

session.persist(student);

tx.commit();

session.close();
sessionFactory.close();
```

------------------------------------------------------------------------

# 49. Important Exam Flow --- JPA

A JPA-based application commonly uses:

``` text
EntityManagerFactory
        |
        v
EntityManager
        |
        v
Transaction
        |
        v
Entity operations
```

Example:

``` java
EntityManagerFactory emf =
        Persistence.createEntityManagerFactory("myPersistenceUnit");

EntityManager em =
        emf.createEntityManager();

EntityTransaction tx =
        em.getTransaction();

tx.begin();

em.persist(student);

tx.commit();

em.close();
emf.close();
```

------------------------------------------------------------------------

# 50. Hibernate Session vs JPA EntityManager

This is an important interview/exam topic.

``` text
JPA:
EntityManager

Hibernate:
Session
```

`EntityManager` is the standard JPA API.

`Session` is Hibernate's native API.

Hibernate's `Session` also provides functionality beyond the basic
standard JPA API.

------------------------------------------------------------------------

# 51. Common Mistakes to Avoid

### Mistake 1

"JPA is a framework."

Better:

> JPA is a specification/API standard for persistence and ORM in Java.

### Mistake 2

"Hibernate and JPA are the same."

Better:

> Hibernate is an ORM framework and a JPA provider/implementation.

### Mistake 3

"`update` updates records."

Better:

> `hibernate.hbm2ddl.auto=update` concerns automatic schema updates, not
> application data updates.

### Mistake 4

"`create` safely creates the table every time."

Better:

> `create` can recreate the schema and may destroy existing data, so it
> is mainly suitable for development/testing.

### Mistake 5

"Session is the database connection."

Better:

> Session is a Hibernate persistence interface/context used to interact
> with persistent entities; Hibernate manages the underlying database
> connectivity.

### Mistake 6

"Every Hibernate operation must use a transaction."

Better:

> Write operations generally require transaction boundaries. Read-only
> operations can have different requirements depending on the API and
> environment.

------------------------------------------------------------------------

# 52. Frequently Asked Exam Questions

## Q1. What is ORM?

ORM is a technique that maps Java objects to relational database tables
so applications can work with objects instead of manually writing SQL
for every operation.

## Q2. What is JPA?

JPA is a Java specification that defines standard APIs and rules for
object-relational persistence.

## Q3. What is Hibernate?

Hibernate is an ORM framework and a popular implementation/provider of
JPA.

## Q4. What is an Entity?

An entity is a Java class whose instances are mapped to persistent
database data, normally using `@Entity`.

## Q5. Why is `@Id` required?

It identifies the primary key of the entity.

## Q6. What is SessionFactory?

It is a heavyweight, thread-safe factory used to create Hibernate
Sessions.

## Q7. What is Session?

It is Hibernate's native API/context used to interact with persistent
entities and the database.

## Q8. What is a transaction?

A transaction is a logical unit of database work that can be committed
or rolled back.

## Q9. What is dirty checking?

Dirty checking is Hibernate's mechanism for detecting changes to managed
entities and synchronizing them with the database.

## Q10. What is first-level cache?

It is the cache associated with a Hibernate Session/persistence context
and is enabled by default.

## Q11. What is JPQL?

JPQL is JPA's object-oriented query language that queries entities and
their attributes.

## Q12. What is HQL?

HQL is Hibernate's query language for querying entities and their
properties.

## Q13. What is native SQL?

Native SQL is database-specific SQL written directly against tables and
columns.

## Q14. What is `hibernate.hbm2ddl.auto`?

It controls Hibernate's automatic schema-generation/validation behavior.

## Q15. What is the difference between `create` and `update`?

``` text
create -> recreate schema based on mappings
update -> attempt to update schema while preserving existing data
```

Neither option should be confused with updating application rows.

------------------------------------------------------------------------

# 53. One Complete Mental Model

For exam preparation, remember this:

``` text
                 JAVA APPLICATION
                        |
                        v
                     ENTITY
                  @Entity class
                        |
                        v
                       ORM
                        |
                        v
                    HIBERNATE
                        |
              +---------+---------+
              |                   |
              v                   v
          Session             JPA APIs
              |              EntityManager
              |                   |
              +---------+---------+
                        |
                        v
                   Persistence
                        |
                        v
                    SQL / JDBC
                        |
                        v
                    DATABASE
                        |
                        v
                     TABLE
```

And the conceptual mapping is:

``` text
Class       -> Table
Object      -> Row
Field       -> Column
@Id         -> Primary Key
Relationship -> Foreign Key / Join
```

------------------------------------------------------------------------

# 54. Quick Revision Sheet

## ORM

``` text
Object <----mapping----> Relational Table
```

## JPA

``` text
Specification
```

## Hibernate

``` text
ORM framework
JPA provider/implementation
```

## Entity

``` java
@Entity
class Student {
    @Id
    Long id;
}
```

## Native Hibernate lifecycle

``` text
Configuration
   ↓
SessionFactory
   ↓
Session
   ↓
Transaction
   ↓
CRUD
   ↓
Commit/Rollback
   ↓
Close
```

## CRUD

``` text
Create -> persist()
Read   -> find()
Update -> change managed entity / merge() when appropriate
Delete -> remove()
```

## Schema settings

``` text
create
create-drop
update
validate
none
```

## Query types

``` text
JPQL  -> JPA standard
HQL   -> Hibernate
SQL   -> Database
```

## Cache

``` text
First-level  -> Session/persistence context
Second-level -> Optional, shared across sessions
```

## Entity states

``` text
Transient -> Managed -> Detached
                 |
                 v
              Removed
```

------------------------------------------------------------------------

# 55. Final Exam Summary

If asked to explain Hibernate from the beginning, you can answer:

> Hibernate is a Java ORM framework that maps Java objects to relational
> database tables and generates SQL for many database operations. JPA is
> a specification that defines standard persistence APIs and ORM rules,
> while Hibernate is a popular implementation/provider of JPA. In native
> Hibernate, we configure Hibernate, build a SessionFactory, open a
> Session, begin a transaction for database modifications, perform
> operations such as persist/find/merge/remove, and then commit or roll
> back the transaction. Entities are Java classes marked with `@Entity`
> and normally contain an `@Id` field representing the primary key.
> Hibernate also provides features such as dirty checking, first-level
> caching, lazy loading, relationships, transaction management, and
> query support through HQL, while JPA provides JPQL and standard
> persistence APIs.

## The most important corrections from the original notes

1.  **JPA and Hibernate are not the same.**
    -   JPA = specification.
    -   Hibernate = ORM framework/JPA provider.
2.  **`hibernate.hbm2ddl.auto=update` does not update table records.**
    -   It concerns updating the database schema.
3.  **`create` can destroy existing data.**
    -   It should not be treated as a safe "create table every time"
        option.
4.  **`hibernate.cfg.xml` is mainly associated with traditional/native
    Hibernate configuration.**
    -   Spring Boot commonly uses
        `application.properties`/`application.yml`.
5.  **A Session is not simply a database connection.**
    -   It is Hibernate's persistence API/context.
6.  **Transactions are especially important for write operations.**
    -   The exact transaction requirement depends on the operation and
        environment.
7.  **A managed entity does not normally require an explicit update
    call.**
    -   Hibernate's dirty checking detects changes and synchronizes them
        during flush/transaction processing.
8.  **`find()` returns an entity object.**
    -   If `System.out.println()` prints a class name plus hash-like
        value, override `toString()` in the entity to display its
        fields.
