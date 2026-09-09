# Spring Boot — Interview & Future Recall Notes

## 1. What is Spring Boot?

Spring Boot is a framework built on top of the Spring ecosystem that reduces the amount of configuration and boilerplate required to build Spring applications.

Key benefits:
- Convention over configuration
- Auto-configuration
- Starter dependencies
- Embedded servers such as Tomcat
- Easy creation of REST APIs
- Production-ready features through the Spring ecosystem

Instead of configuring many components manually, Spring Boot can automatically configure them based on the dependencies and application properties present in the project.

---

## 2. Spring MVC and the Front Controller

Spring MVC follows the **Front Controller pattern**.

The main front controller in Spring MVC is:

`DispatcherServlet`

Instead of every request being handled manually, requests first reach the DispatcherServlet.

High-level flow:

Client
  ↓
DispatcherServlet
  ↓
Find matching Controller
  ↓
Controller method
  ↓
Response

The DispatcherServlet works with Spring MVC infrastructure to identify the appropriate controller and method for the incoming request.

### Why is this useful?

We don't need to manually inspect every URL and decide which controller should handle it. Spring manages request routing and other MVC responsibilities.

---

## 3. @Controller vs @RestController

### @Controller

`@Controller` is generally used for MVC applications where a controller method may return a view/page.

Example:

```java
@Controller
public class EmployeeController {

    @GetMapping("/employees")
    public String employees() {
        return "employees";
    }
}
```

Here, the returned value can represent a view name.

### @RestController

`@RestController` is commonly used for REST APIs.

It is effectively:

`@Controller + @ResponseBody`

Example:

```java
@RestController
public class EmployeeController {

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return employeeService.getEmployees();
    }
}
```

The returned object is written directly to the HTTP response body, usually as JSON.

### Important interview point

`@Controller` does NOT always mean "only pages."

You can use:

```java
@Controller
public class EmployeeController {

    @GetMapping("/employees")
    @ResponseBody
    public Employee getEmployee() {
        return employee;
    }
}
```

`@ResponseBody` tells Spring to put the return value directly into the HTTP response body instead of treating it as a view name.

---

## 4. @RequestMapping

`@RequestMapping` is used to map HTTP requests to controller classes or methods.

Example:

```java
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @RequestMapping("/all")
    public List<Employee> getEmployees() {
        return employeeService.getEmployees();
    }
}
```

The resulting path is:

`/api/employees/all`

More specific annotations are commonly used for HTTP methods:

```java
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
@PatchMapping
```

Example:

```java
@GetMapping("/employees")
@PostMapping("/employees")
@PutMapping("/employees/{id}")
@DeleteMapping("/employees/{id}")
```

---

# 5. Spring JDBC

Spring JDBC helps Java applications work with relational databases while reducing the boilerplate code required by raw JDBC.

The main class commonly used is:

`JdbcTemplate`

### Raw JDBC

With raw JDBC, we commonly have to deal with:

```text
Java Code
    ↓
DriverManager / DataSource
    ↓
Connection
    ↓
PreparedStatement
    ↓
ResultSet
    ↓
Database
```

There is more resource-management and exception-handling boilerplate.

### JdbcTemplate

With Spring's JdbcTemplate:

```text
application.properties
        ↓
Spring Boot Auto-Configuration
        ↓
DataSource
        ↓
JdbcTemplate
        ↓
JDBC Driver
        ↓
MySQL
```

JdbcTemplate executes SQL for us while still allowing us to write the SQL ourselves.

Example:

```java
String sql = "SELECT * FROM employees";

List<Employee> employees = jdbcTemplate.query(
        sql,
        (rs, rowNum) -> new Employee(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("department")
        )
);
```

### Important interview point

`JdbcTemplate` does **not** create database connections by itself.

It works through a `DataSource`.

Spring Boot can configure the DataSource based on properties such as:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db
spring.datasource.username=root
spring.datasource.password=your_password
```

The DataSource is responsible for obtaining database connections.

---

# 6. JdbcTemplate vs JPA/Hibernate

This is an important interview comparison.

### JdbcTemplate

JdbcTemplate is a lightweight abstraction over JDBC.

You generally:
- Write SQL yourself
- Decide how rows are mapped to Java objects
- Have more control over SQL
- Avoid much of the raw JDBC boilerplate

Example:

```sql
SELECT * FROM employees WHERE id = ?
```

### JPA/Hibernate

JPA is a Java persistence specification/API, while Hibernate is a popular implementation of JPA.

With JPA/Hibernate:
- Java objects are mapped to database tables
- SQL can be generated automatically
- We work mainly with entities and repositories
- ORM handles much of the object-relational mapping

Example:

```java
@Entity
public class Employee {

    @Id
    private Long id;

    private String name;
    private String email;
}
```

Repository:

```java
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {
}
```

You can then perform operations without writing basic SQL manually.

### Quick comparison

| JdbcTemplate | JPA/Hibernate |
|---|---|
| SQL-centric | Object/ORM-centric |
| Write SQL manually | SQL can be generated |
| Lightweight | More ORM features |
| More control over SQL | Easier object mapping |
| Uses JDBC underneath | Hibernate uses JDBC underneath |

### Interview answer

"JdbcTemplate is a Spring abstraction over JDBC that reduces boilerplate while allowing us to write SQL ourselves. JPA is a persistence specification and Hibernate is a common implementation. JPA/Hibernate provides ORM, where Java objects are mapped to database tables and SQL can be generated automatically."

---

# 7. Scope in Spring

Spring bean scope determines the lifecycle and number of instances of a Spring-managed bean.

The commonly discussed scopes include:

- singleton
- prototype
- request
- session
- application
- websocket

### Singleton

Default Spring bean scope.

```java
@Component
public class EmployeeService {
}
```

By default, Spring creates one bean instance per Spring application context.

### Prototype

```java
@Scope("prototype")
@Component
public class EmployeeService {
}
```

For a prototype bean, Spring creates a new instance whenever the bean is requested from the container.

### Interview point

"Singleton is the default scope in Spring. Prototype creates a new bean instance whenever the container is asked for one."

Important: Spring singleton means **one instance per Spring ApplicationContext**, not necessarily one object for the entire JVM.

---

# 8. application.properties

`application.properties` is a standard place for application configuration in Spring Boot.

Example:

```properties
spring.application.name=demo

spring.datasource.url=jdbc:mysql://localhost:3306/employee_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Spring Boot reads these properties and uses them for configuration and auto-configuration.

For example:

```properties
spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...
```

can be used by Spring Boot to configure the application's DataSource.

### Important clarification

`application.properties` does not magically configure every dependency just because the dependency exists.

Instead:

1. Dependencies are added to the project.
2. Spring Boot detects applicable libraries/classes.
3. Auto-configuration creates/configures appropriate beans.
4. Properties customize that configuration.

---

# 9. Connection Pool

Creating a new database connection repeatedly can be expensive.

A connection pool maintains reusable database connections.

Instead of:

```text
Create connection
    ↓
Use connection
    ↓
Close connection
```

for every request, a pool can work like:

```text
Connection Pool
 ├── Connection 1
 ├── Connection 2
 ├── Connection 3
 └── Connection 4

Application requests a connection
        ↓
Pool gives an available connection
        ↓
Application uses it
        ↓
Connection is returned to pool
```

Spring Boot commonly uses **HikariCP** as the connection pool implementation when JDBC/JPA starters are used.

### Benefits

- Reuses database connections
- Reduces connection creation overhead
- Improves application performance
- Controls the number of simultaneous DB connections

---

# 10. Spring Security

Spring Security is used to secure Spring applications.

It provides mechanisms for:

- Authentication
- Authorization
- Password handling
- Security filters
- Roles and authorities
- Session management
- CSRF protection
- HTTP Basic authentication
- Form login
- Method-level security

---

# 11. SecurityFilterChain

When Spring Security is added, requests are processed through Spring Security's filter chain.

A custom security configuration can be defined using a `SecurityFilterChain` bean.

Example:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(customizer -> customizer.disable())
        .authorizeHttpRequests(request ->
            request.anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

    return http.build();
}
```

### What does this configuration mean?

#### CSRF

```java
.csrf(customizer -> customizer.disable())
```

Disables CSRF protection.

For a stateless REST API using mechanisms such as HTTP Basic or bearer tokens, CSRF handling is often configured differently from a browser-based session application.

Do not say "CSRF is useless." It protects against a specific class of attacks and whether it should be disabled depends on the application's authentication architecture.

#### Authentication requirement

```java
.authorizeHttpRequests(request ->
    request.anyRequest().authenticated()
)
```

Every request must be authenticated.

#### HTTP Basic

```java
.httpBasic(Customizer.withDefaults())
```

Enables HTTP Basic authentication.

The client sends credentials using the HTTP Authorization header.

#### Stateless session

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

The server does not maintain an authenticated HTTP session for each client.

This is commonly used for REST APIs.

---

# 12. Authentication vs Authorization

This is extremely important for interviews.

### Authentication

Authentication answers:

**"Who are you?"**

Example:

```text
Username = Kali
Password = 123
```

Spring Security verifies whether those credentials are valid.

### Authorization

Authorization answers:

**"What are you allowed to do?"**

Example:

```text
User → can read employees
Admin → can read + create + delete employees
```

### HTTP status codes

A useful interview rule:

**401 Unauthorized**

Usually means the request has not been successfully authenticated.

Think:

> "I don't know who you are."

**403 Forbidden**

Usually means the user is authenticated, but does not have sufficient permission.

Think:

> "I know who you are, but you are not allowed to do this."

---

# 13. UserDetailsService

`UserDetailsService` is a Spring Security interface used to retrieve user information during authentication.

It loads user information such as:

- username
- password
- authorities/roles

Example:

```java
@Bean
public UserDetailsService userDetailsService() {

    List<UserDetails> users = new ArrayList<>();

    UserDetails user1 = User
            .withDefaultPasswordEncoder()
            .username("Kali2")
            .password("123")
            .roles("USER")
            .build();

    UserDetails user2 = User
            .withDefaultPasswordEncoder()
            .username("Kali3")
            .password("123")
            .roles("USER")
            .build();

    users.add(user1);
    users.add(user2);

    return new InMemoryUserDetailsManager(users);
}
```

### What happens here?

`InMemoryUserDetailsManager` stores the users in memory.

When a request arrives:

```text
Client
  ↓
Username + Password
  ↓
Spring Security
  ↓
UserDetailsService
  ↓
Find user
  ↓
Verify password
  ↓
Authentication succeeds/fails
```

This is useful for demos and learning.

For a real application, users are usually stored in a database or another identity system rather than hard-coded in memory.

---

# 14. UserDetails

`UserDetails` represents the user's security information.

It can provide:

- Username
- Password
- Authorities
- Account status

A custom implementation can be created:

```java
public class UsersPrincipal implements UserDetails {

    // custom user information

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
```

Spring Security uses `UserDetails` as the security representation of a user.

---

# 15. Roles and Authorities

Spring Security uses authorities to represent permissions.

Example:

```java
.roles("USER")
```

When using `roles("USER")`, Spring Security internally creates an authority with:

```text
ROLE_USER
```

This is why role checks commonly look like:

```java
.hasRole("USER")
```

while direct authority checks can look like:

```java
.hasAuthority("ROLE_USER")
```

### Easy way to remember

```text
roles("USER")
      ↓
ROLE_USER authority
```

---

# 16. BCrypt and Password Hashing

Passwords should not be stored as plain text.

Instead of:

```text
123456
```

the database should store a password hash.

BCrypt is a password-hashing algorithm designed for securely storing passwords.

### Hashing vs Encryption

**Hashing**
- One-way operation
- Intended to be difficult to reverse
- Used for password storage

**Encryption**
- Reversible using a key
- Used when original data needs to be recovered

### Salting

A salt is random data added to the password before hashing.

Conceptually:

```text
Password + Random Salt
        ↓
      BCrypt
        ↓
Password Hash
```

The salt helps prevent attackers from using precomputed hash tables effectively and ensures that the same password does not necessarily produce the same stored hash.

BCrypt hashes commonly contain the salt as part of the encoded result, so the salt does not need to be stored in a separate database column when using standard BCrypt formats.

### Password verification

The application does NOT normally decrypt the stored password.

Instead:

```text
User enters password
        ↓
Password encoder hashes/checks it
        ↓
Compare against stored BCrypt hash
        ↓
Match → authentication succeeds
No match → authentication fails
```

---

# 17. Spring Security Request Flow

A simplified authentication flow:

```text
Client
   ↓
HTTP Request
   ↓
Spring Security Filter Chain
   ↓
Authentication Filter
   ↓
AuthenticationManager
   ↓
UserDetailsService
   ↓
UserDetails
   ↓
PasswordEncoder
   ↓
Authentication successful?
   ├── No → 401
   │
   └── Yes
          ↓
      Authorization
          ↓
      Permission?
       ├── No → 403
       │
       └── Yes
              ↓
          Controller
              ↓
           Response
```

This is a simplified conceptual flow; the exact filters involved depend on the authentication mechanism being used.

---

# 18. Important Corrections to Remember

### Correction 1

Do not say:

"@Controller looks for a page."

Better:

"`@Controller` marks a class as a Spring MVC controller. Its methods may return view names. If a method is annotated with `@ResponseBody`, the return value is written directly to the HTTP response body."

### Correction 2

Do not say:

"Spring JDBC creates the DataSource."

Better:

"Spring Boot can auto-configure a DataSource, and JdbcTemplate uses that DataSource to obtain connections and execute JDBC operations."

### Correction 3

Do not say:

"JPA and Hibernate are the same."

Better:

"JPA is a specification/API for ORM and persistence. Hibernate is a popular implementation of JPA."

### Correction 4

Do not say:

"UserDetailsService verifies the password."

Better:

"UserDetailsService loads the user's security information. The authentication mechanism and PasswordEncoder are involved in validating the credentials."

### Correction 5

Do not say:

"401 means unauthorized and 403 means authenticated."

Better:

"401 generally indicates that authentication is missing or unsuccessful. 403 generally indicates that the request was understood and the user is authenticated but does not have sufficient permission."

---

# 19. Quick Interview Revision

### Spring Boot

**Q: Why Spring Boot?**

Spring Boot reduces configuration and boilerplate through auto-configuration, starter dependencies, convention over configuration, and embedded servers.

### DispatcherServlet

**Q: What is DispatcherServlet?**

It is Spring MVC's front controller. Incoming requests are routed through it, and it coordinates with Spring MVC to find the appropriate handler/controller.

### REST Controller

**Q: Difference between @Controller and @RestController?**

`@Controller` is used for Spring MVC controllers and commonly returns views. `@RestController` is effectively `@Controller + @ResponseBody`, so returned objects are written directly to the response body.

### JdbcTemplate

**Q: Why JdbcTemplate?**

It reduces JDBC boilerplate while allowing us to write SQL ourselves.

### JPA/Hibernate

**Q: What is the difference between JdbcTemplate and JPA/Hibernate?**

JdbcTemplate is SQL-centric. JPA/Hibernate is ORM-centric and maps Java objects to relational database structures.

### DataSource

**Q: Does JdbcTemplate create connections?**

No. JdbcTemplate uses a DataSource to obtain database connections.

### Connection Pool

**Q: Why connection pooling?**

To reuse database connections instead of repeatedly creating and closing them, improving performance and resource utilization.

### Singleton

**Q: What is the default Spring bean scope?**

Singleton.

### Spring Security

**Q: Authentication vs authorization?**

Authentication identifies the user. Authorization determines what the authenticated user is allowed to access.

### 401 vs 403

**Q: Difference?**

401 → authentication is missing/failed.

403 → authentication is successful, but access is forbidden due to insufficient permissions.

### UserDetailsService

**Q: What does UserDetailsService do?**

It loads user information required by Spring Security during authentication.

### BCrypt

**Q: Why BCrypt?**

BCrypt is a password hashing algorithm designed for password storage. It incorporates a salt and is intentionally computationally expensive to make brute-force attacks harder.

---

# 20. One-Page Mental Model

```text
                    SPRING BOOT
                        │
        ┌───────────────┼────────────────┐
        ↓               ↓                ↓
   Spring MVC       Spring JDBC      Spring Security
        │               │                │
        ↓               ↓                ↓
DispatcherServlet   DataSource       Filter Chain
        │               │                │
        ↓               ↓                ↓
   Controller       JdbcTemplate    Authentication
        │               │                │
        ↓               ↓                ↓
 @RestController       SQL          UserDetailsService
        │                                │
        ↓                                ↓
     JSON                         PasswordEncoder
                                         │
                                         ↓
                                  Authorization
                                         │
                                  ┌──────┴──────┐
                                  ↓             ↓
                                 401           403
                           Not authenticated  Forbidden
```

---

## Final Interview Strategy

When explaining Spring Boot, try to connect the concepts rather than memorizing isolated definitions:

```text
Spring Boot
    ↓
Auto-configuration
    ↓
Spring MVC
    ↓
DispatcherServlet
    ↓
Controller
    ↓
Service
    ↓
Repository / JdbcTemplate / JPA
    ↓
DataSource
    ↓
Connection Pool
    ↓
JDBC Driver
    ↓
Database
```

For a secured REST API:

```text
Client
  ↓
Security Filter Chain
  ↓
Authentication
  ↓
UserDetailsService
  ↓
PasswordEncoder
  ↓
Authorization
  ↓
Controller
  ↓
Service
  ↓
Repository / Database
```

These two flows are the most useful mental models to remember for Spring Boot interviews.
