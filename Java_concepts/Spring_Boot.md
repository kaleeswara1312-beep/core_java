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


# How Spring Boot Works Internally — Interview Preparation

## 1. What is Spring Boot?

Spring Boot is built on top of the Spring Framework. It simplifies application development by providing **auto-configuration, starter dependencies, embedded servers, and production-ready features**.

Without Spring Boot, developers typically configure many Spring components and external servers manually. Spring Boot reduces this configuration through sensible defaults.

**Interview definition:** Spring Boot simplifies Spring application development by automatically configuring components based on the dependencies available in the project.

---

## 2. How Spring Boot Works Internally

When we start a Spring Boot application, the following process takes place:

1. JVM starts and executes the `main()` method.
2. `SpringApplication.run()` is invoked.
3. Spring Boot prepares the application environment.
4. An appropriate `ApplicationContext` is created.
5. Component scanning identifies Spring-managed classes.
6. Auto-configuration configures required beans.
7. Spring creates and initializes beans and injects dependencies.
8. The embedded server, such as Tomcat, starts for a servlet-based web application.
9. The application becomes ready to handle requests.

### Internal flow

```text
JVM Starts
    |
    v
main() Method
    |
    v
SpringApplication.run()
    |
    v
Prepare Environment
(Properties, Profiles, Arguments)
    |
    v
Create ApplicationContext
    |
    v
Load Bean Definitions
    |
    +--------------------------+
    |                          |
    v                          v
Component Scanning       Auto-Configuration
    |                          |
    +------------+-------------+
                 |
                 v
        Create and Initialize Beans
                 |
                 v
        Dependency Injection
                 |
                 v
        Start Embedded Tomcat
                 |
                 v
        Application Ready
                 |
                 v
        Handle HTTP Requests
```

**Important:** The diagram is a simplified conceptual flow. Internally, Spring Boot uses several coordinated lifecycle steps, and web-server initialization and bean creation can interact during context refresh.

---

## 3. Understanding the Main() Method

Example:

```java
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

### What happens here?

- `@SpringBootApplication` enables the main Spring Boot configuration mechanisms.
- `main()` is the normal Java application entry point.
- `SpringApplication.run()` bootstraps the Spring application.
- Spring Boot creates and refreshes the application context and starts the web server when required.

The `main()` method itself does not create every bean or start Tomcat directly. Those operations are coordinated by Spring Boot.

---

## 4. What Is `@SpringBootApplication`?

`@SpringBootApplication` is a composite annotation that combines three important annotations.

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

### A. `@SpringBootConfiguration`

- A specialized form of `@Configuration`.
- Indicates that the class provides Spring configuration.
- Allows bean definitions through `@Bean` methods.

### B. `@EnableAutoConfiguration`

- Enables Spring Boot's auto-configuration mechanism.
- Configures beans based on classpath dependencies, existing beans, properties, and conditional rules.

Example: If Spring MVC is available and the application is configured as a servlet web application, Spring Boot can configure the infrastructure needed to handle HTTP requests.

### C. `@ComponentScan`

- Searches for components in the package containing the application class and its subpackages by default.
- Identifies classes annotated with `@Component`, `@Service`, `@Repository`, and `@Controller`, among others.

**Interview point:** Place the main application class in a suitable root package so component scanning can find the application's components.

---

## 5. What Is Auto-Configuration?

Auto-configuration is a mechanism through which Spring Boot configures application infrastructure based on the application's environment and dependencies.

For example, when we add:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Spring Boot can configure relevant web infrastructure, including Spring MVC and an embedded servlet server when the required dependencies and conditions are satisfied.

### How does auto-configuration work internally?

1. `@EnableAutoConfiguration` enables the auto-configuration process.
2. Spring Boot discovers candidate auto-configuration classes through its auto-configuration imports mechanism.
3. Auto-configuration classes contain bean definitions.
4. Conditions determine whether particular configurations should apply.
5. Applicable bean definitions are registered with the application context.

Common conditional annotations include:

| Annotation | Purpose |
|---|---|
| `@ConditionalOnClass` | Applies when a specified class is present on the classpath. |
| `@ConditionalOnMissingBean` | Applies when a matching bean is not already available. |
| `@ConditionalOnBean` | Applies when a matching bean is available. |
| `@ConditionalOnProperty` | Applies based on a configuration property's value. |

### Example

Suppose Spring Boot can configure a bean when a particular library is available, but the application has not provided its own matching bean.

```java
@Configuration
public class PaymentConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PaymentService paymentService() {
        return new PaymentService();
    }
}
```

If no matching `PaymentService` bean exists, Spring can register this bean. If an appropriate bean already exists, the condition normally prevents this method from supplying another one.

**Interview point:** Auto-configuration is conditional configuration, not magic. It is based on classes, beans, properties, and other conditions.

---

## 6. What Are Starter Dependencies?

Starter dependencies are convenient dependency bundles that simplify dependency management.

Example:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

This starter brings in the dependencies needed for common Spring Data JPA functionality, subject to its dependency configuration.

Other examples:

- `spring-boot-starter-web` — servlet-based web applications.
- `spring-boot-starter-data-jpa` — JPA-based database access.
- `spring-boot-starter-security` — Spring Security integration.
- `spring-boot-starter-test` — common testing dependencies.

### Starter vs. auto-configuration

- **Starter:** Helps bring the required libraries into the project.
- **Auto-configuration:** Uses classpath and configuration conditions to configure suitable beans.

Adding a starter does not mean every possible feature is automatically configured; the necessary conditions must also be satisfied.

---

## 7. What Is the Spring IoC Container?

IoC stands for **Inversion of Control**.

Instead of application classes manually creating and managing all their dependencies, the Spring container manages configured objects and their relationships.

These managed objects are called **Spring beans**.

Example:

```java
@Service
public class OrderService {

    public String createOrder() {
        return "Order created";
    }
}
```

Spring can detect `OrderService` during component scanning and register it as a bean.

### What is `ApplicationContext`?

`ApplicationContext` is Spring's central container interface. It provides functionality for managing beans and accessing application-level facilities.

Spring Boot creates a suitable application context for the application type. A typical servlet-based web application uses an `AnnotationConfigServletWebServerApplicationContext` or a related context implementation, depending on the configuration and Spring Boot version.

### What happens during bean initialization?

A simplified lifecycle is:

1. Spring loads bean definitions.
2. Spring creates bean instances when required by the bean's scope and lifecycle.
3. Dependencies are injected.
4. Initialization callbacks and applicable bean post-processors run.
5. The initialized beans are available for use.

The exact lifecycle depends on bean type, scope, lazy initialization, and configuration.

---

## 8. How Does Dependency Injection Work?

Dependency Injection (DI) means that a class receives its dependencies from an external container rather than constructing them itself.

Example:

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public String placeOrder() {
        return paymentService.processPayment();
    }
}
```

```java
@Service
public class PaymentService {

    public String processPayment() {
        return "Payment successful";
    }
}
```

Spring detects the beans and injects `PaymentService` into `OrderService`.

### Why is DI useful?

- Reduces tight coupling.
- Improves unit testing.
- Makes implementations easier to replace.
- Centralizes object configuration and lifecycle management.

**Interview point:** Constructor injection is generally preferred for required dependencies because it makes dependencies explicit and supports immutable fields.

---

## 9. How Does Embedded Tomcat Work?

In a traditional deployment, a web application might be packaged as a WAR file and deployed to an externally installed Tomcat server.

With a typical Spring Boot web application, an embedded server can be included in the application dependencies.

When the application starts:

1. Spring Boot identifies the servlet web application type.
2. The appropriate web application context is created.
3. The server factory and related web infrastructure are configured.
4. During application-context initialization, the embedded server is created and started.
5. The server listens on the configured port, commonly `8080` by default.

Example:

```properties
server.port=9090
```

The application will normally listen on port `9090`, provided the port is available and no other configuration overrides it.

### Does Spring Boot always use Tomcat?

No.

- The standard Spring MVC web starter typically uses embedded Tomcat.
- Jetty or Undertow can be used with appropriate dependencies and configuration.
- Spring WebFlux commonly uses a reactive server such as Reactor Netty.
- A non-web application may not start an embedded web server at all.

---

## 10. How Does Spring Boot Handle an HTTP Request?

Suppose a client sends:

```http
GET /orders/101
```

A typical Spring MVC request flow is:

```text
Client
  |
  v
Embedded Tomcat
  |
  v
Servlet Filters
(e.g., Spring Security filters)
  |
  v
DispatcherServlet
  |
  v
HandlerMapping
  |
  v
HandlerAdapter
  |
  v
Controller
  |
  v
Service Layer
  |
  v
Repository Layer
  |
  v
Database
  |
  v
Response Returned
```

### Step-by-step explanation

1. **Tomcat:** Accepts the HTTP connection and processes the request at the servlet-container level.
2. **Filters:** Applicable servlet filters execute. Spring Security commonly uses a filter chain here.
3. **DispatcherServlet:** Acts as the front controller for Spring MVC.
4. **HandlerMapping:** Identifies the controller handler that matches the request.
5. **HandlerAdapter:** Invokes the selected handler.
6. **Controller:** Processes the web request and calls the service layer.
7. **Service:** Contains business logic.
8. **Repository:** Performs database operations when needed.
9. **Response handling:** Spring MVC serializes the result, often to JSON through an HTTP message converter, and the response returns to the client.

The exact path varies with filters, interceptors, exception handlers, asynchronous processing, and the type of response.

---

## 11. How Does `application.properties` Work?

Spring Boot uses externalized configuration so application settings can be changed without modifying Java code.

Example:

```properties
server.port=9090
spring.application.name=order-service
spring.datasource.url=jdbc:mysql://localhost:3306/orders
```

During startup, Spring Boot prepares an `Environment` containing configuration properties from applicable property sources.

These values can be accessed through mechanisms such as:

```java
@Value("${spring.application.name}")
private String applicationName;
```

For grouped and type-safe configuration, use `@ConfigurationProperties`.

Configuration may come from several sources, including configuration files, environment variables, command-line arguments, and other supported property sources.

**Interview point:** Spring Boot supports externalized configuration and profiles, allowing different settings for development, testing, and production.

---

## 12. What Are Spring Profiles?

Profiles allow different beans or configuration settings to be activated for different environments.

Example:

```properties
# application-dev.properties
server.port=8081
```

```properties
# application-prod.properties
server.port=8080
```

Activate a profile with:

```properties
spring.profiles.active=dev
```

Or through an appropriate environment variable or command-line argument.

A bean can also be restricted to a profile:

```java
@Service
@Profile("dev")
public class MockPaymentService {
}
```

When the `dev` profile is active, Spring can register this bean if the relevant configuration is loaded.

---

## 13. What Happens When We Add a New Dependency?

Suppose we add Spring Data JPA to a project.

```text
Add JPA Starter
      |
      v
Dependency Management Resolves Libraries
      |
      v
Classes Become Available on Classpath
      |
      v
Spring Boot Evaluates Auto-Configuration
      |
      v
Required Conditions Are Checked
      |
      v
Applicable Bean Definitions Are Registered
      |
      v
Beans Are Initialized as Required
```

For database auto-configuration to succeed, the necessary conditions must be met. For example, a suitable JDBC driver and database configuration are normally required for a real database connection.

**Important:** A dependency being present on the classpath does not guarantee that its configuration will activate or that the application will start successfully.

---

## 14. What Is the Difference Between Spring and Spring Boot?

| Spring Framework | Spring Boot |
|---|---|
| Provides core features such as IoC, DI, and Spring MVC. | Builds on Spring and simplifies application setup. |
| May require more explicit configuration. | Provides conditional auto-configuration. |
| Does not itself require an embedded server in every deployment model. | Supports convenient embedded-server deployments. |
| Dependencies and versions may need more manual management. | Starters and dependency management simplify this work. |
| Provides the underlying application framework. | Adds conventions and operational features around Spring applications. |

Spring Boot does not replace the Spring Framework. It builds on it.

---

## 15. How to Debug Auto-Configuration

If a bean is not created or an unexpected configuration is applied, useful troubleshooting steps include:

- Check whether the required dependency is present.
- Verify the component's package and component-scan boundaries.
- Check active profiles and configuration properties.
- Look for missing-bean or property conditions.
- Check whether another bean causes `@ConditionalOnMissingBean` to back off.
- Review the startup logs and exception messages.
- Use the Actuator `conditions` endpoint when Actuator is configured and that endpoint is appropriately exposed and secured.
- Start the application with the `--debug` option to see the auto-configuration condition evaluation report.

Example:

```bash
java -jar order-service.jar --debug
```

The condition evaluation report helps explain which auto-configurations matched and which did not.

---

## 16. Common Spring Boot Interview Questions

### Q1. What happens when `SpringApplication.run()` is called?

It bootstraps Spring Boot, prepares the environment, creates the application context, loads configuration and bean definitions, refreshes the context, and starts the embedded server for a suitable web application.

### Q2. What is the purpose of `@SpringBootApplication`?

It combines `@SpringBootConfiguration`, `@EnableAutoConfiguration`, and `@ComponentScan`.

### Q3. What is auto-configuration?

It conditionally configures Spring beans based on classpath dependencies, existing beans, properties, and other conditions.

### Q4. What is the difference between a bean and a normal Java object?

A bean is an object managed by the Spring container. A normal Java object is not automatically Spring-managed merely because it is instantiated with `new`.

### Q5. What is the role of `ApplicationContext`?

It is the central Spring container that manages beans and provides access to application-level facilities.

### Q6. How does Spring Boot inject dependencies?

Spring resolves dependencies from bean definitions and injects them through constructors, setters, or fields, depending on how the class is configured.

### Q7. Does Spring Boot always start Tomcat?

No. A typical servlet web application uses Tomcat by default, but another server may be configured, and non-web applications do not need an embedded web server.

### Q8. What is `DispatcherServlet`?

It is Spring MVC's front controller. It coordinates request mapping, handler invocation, and response processing.

### Q9. Why does Spring Boot use conditional annotations?

They allow auto-configuration to apply only when the required classes, beans, properties, or other conditions are present.

### Q10. What happens if two beans of the same type exist?

If Spring needs to resolve a single bean and multiple candidates match, it may report ambiguity unless the injection point or configuration identifies a suitable candidate using mechanisms such as `@Qualifier` or `@Primary`.

### Q11. What happens if a required bean is missing?

The application may fail during context initialization with a dependency-resolution or configuration error, unless the dependency is optional or the relevant configuration does not require it.

### Q12. What is the difference between component scanning and auto-configuration?

Component scanning discovers application components, such as your `@Service` and `@Controller` classes. Auto-configuration conditionally supplies infrastructure and other bean definitions based on the application's setup.

---

## 17. Final Interview Summary

Remember these key points:

1. `main()` starts the Java application.
2. `SpringApplication.run()` bootstraps Spring Boot.
3. `@SpringBootApplication` combines configuration, auto-configuration, and component scanning.
4. Starter dependencies simplify dependency management.
5. Auto-configuration uses conditional rules to register appropriate bean definitions.
6. `ApplicationContext` manages Spring beans.
7. Dependency Injection supplies required dependencies.
8. A typical servlet application starts an embedded server such as Tomcat.
9. Spring MVC uses `DispatcherServlet` to coordinate HTTP request handling.
10. Externalized configuration and profiles help manage environment-specific settings.

**One-line interview answer:**

Spring Boot starts by bootstrapping the application context, loading configuration and bean definitions, applying conditional auto-configuration, initializing Spring-managed components, and starting an embedded web server when required, after which the application can process requests through its configured web stack.
