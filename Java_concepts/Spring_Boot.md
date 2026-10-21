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
# Spring Boot Conditional Annotations

## 1. What are Conditional Annotations?

Conditional annotations help Spring Boot **register a bean or apply a configuration only when a specific condition is satisfied**.

They are commonly used in Spring Boot auto-configuration to decide whether a bean should be created based on the classpath, existing beans, application properties, or custom logic.

### Common Conditional Annotations

| Annotation | Purpose |
|---|---|
| `@ConditionalOnClass` | Applies when a specified class is present on the classpath. |
| `@ConditionalOnMissingBean` | Applies when a matching bean is not already available. |
| `@ConditionalOnBean` | Applies when a matching bean is available. |
| `@ConditionalOnProperty` | Applies based on a configuration property's value. |
| `@ConditionalOnExpression` | Applies when a Spring Expression Language (SpEL) expression evaluates to `true`. |

### Example: `@ConditionalOnProperty`

**application.properties**

```properties
app.email.enabled=true
```

**Configuration class**

```java
@Configuration
public class EmailConfig {

    @Bean
    @ConditionalOnProperty(
        name = "app.email.enabled",
        havingValue = "true"
    )
    public EmailService emailService() {
        return new EmailService();
    }
}
```

**Explanation:**

- If `app.email.enabled=true`, Spring registers the `EmailService` bean.
- If the property is missing or has another value, the bean is not registered under this condition's default settings.
- `matchIfMissing = true` can be used if the condition should match when the property is absent.

**When to use:** When a feature should be enabled or disabled through configuration without changing the Java code.

---

## 2. Custom Conditional Annotations

When the built-in conditional annotations do not meet our requirements, we can create a **custom condition** using Spring's `Condition` interface.

### Steps to create a custom condition

1. Create a class that implements the `Condition` interface.
2. Override the `matches()` method and define the condition.
3. Apply `@Conditional` to a `@Bean` method or configuration class.
4. Spring registers the bean only when `matches()` returns `true`.

### Example: Create a bean only when the application runs in a specific environment

**Step 1: Implement the `Condition` interface**

```java
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DevEnvironmentCondition implements Condition {

    @Override
    public boolean matches(
            ConditionContext context,
            AnnotatedTypeMetadata metadata) {

        String environment =
                context.getEnvironment()
                       .getProperty("app.environment");

        return "dev".equalsIgnoreCase(environment);
    }
}
```

**Explanation:**

- `Condition` is the interface used to define custom matching logic.
- `matches()` is called by Spring while evaluating the condition.
- `ConditionContext` provides access to the environment, bean factory, registry, and other configuration facilities.
- `AnnotatedTypeMetadata` provides metadata about the annotated element.
- If `matches()` returns `true`, the conditional bean definition can be registered; otherwise, it is skipped.

**Step 2: Apply `@Conditional` to the bean method**

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Conditional;

@Configuration
public class AppConfig {

    @Bean
    @Conditional(DevEnvironmentCondition.class)
    public EmailService emailService() {
        return new EmailService();
    }
}
```

**Step 3: Configure the environment**

In `application.properties`:

```properties
app.environment=dev
```

**Result:**

- If `app.environment=dev`, Spring registers the `EmailService` bean.
- If `app.environment=prod`, Spring does not register that bean through this configuration method.

---

## 3. Built-in vs Custom Conditions

| Built-in conditions | Custom conditions |
|---|---|
| Use Spring Boot's predefined annotations. | Implement the `Condition` interface. |
| Suitable for common conditions such as classpath, bean existence, and property values. | Suitable for application-specific business or environment checks. |
| Require less code. | Require custom matching logic in `matches()`. |
| Example: `@ConditionalOnProperty`. | Example: `@Conditional(DevEnvironmentCondition.class)`. |

## 4. Interview Points

- **Why are conditional annotations needed?** To register beans only when the required conditions are satisfied.
- **Where are they commonly used?** Spring Boot auto-configuration and feature-specific configuration.
- **What is the purpose of `Condition.matches()`?** It determines whether a condition is satisfied.
- **What happens when `matches()` returns `false`?** The conditional bean definition is not registered through that configuration path.
- **Can `@Conditional` be applied to a configuration class?** Yes. It can conditionally apply the configuration class and its bean definitions.
- **Can custom conditions access application properties?** Yes, through `ConditionContext.getEnvironment()`.

**Remember:** Use built-in conditional annotations whenever they meet the requirement. Implement a custom `Condition` only when the required logic cannot be expressed conveniently using the built-in conditions.

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


# Spring Boot Annotations — Interview Preparation & Reference

## 1. What are Spring Boot Annotations?

Annotations are metadata added to Java classes, methods, fields, and parameters to tell Spring how to configure, create, inject, validate, or manage application components.

**Example:**
```java
@RestController
public class StudentController {
}
```

`@RestController` tells Spring that this class handles HTTP requests and returns response data, usually as JSON.

---

# 2. Core Spring Annotations

## 2.1 `@SpringBootApplication`

**Definition:** The main annotation that enables Spring Boot application configuration, component scanning, and auto-configuration.

**Internally combines:**
- `@SpringBootConfiguration`
- `@EnableAutoConfiguration`
- `@ComponentScan`

**When to use:** On the main class to start a Spring Boot application.

```java
@SpringBootApplication
public class StudentApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentApplication.class, args);
    }
}
```

**Interview question:** What happens when `SpringApplication.run()` is executed?

At a high level, Spring Boot prepares the application environment, creates and refreshes the Spring ApplicationContext, discovers and registers beans, applies auto-configuration, and starts the embedded web server for a web application.

## 2.2 `@Component`

**Definition:** Marks a class as a Spring-managed component so it can be discovered during component scanning.

```java
@Component
public class EmailUtil {
    public void sendEmail() {
        System.out.println("Email sent");
    }
}
```

**When to use:** For general-purpose classes that need to be managed by Spring.

## 2.3 `@Service`

**Definition:** A specialization of `@Component` used to represent the service or business-logic layer.

```java
@Service
public class StudentService {
    public String getStudent() {
        return "Student details";
    }
}
```

**When to use:** For business logic, such as creating students or calculating order totals.

## 2.4 `@Repository`

**Definition:** A specialization of `@Component` used for the persistence or data-access layer.

```java
@Repository
public class StudentRepository {
}
```

**When to use:** For data-access classes. Spring Data repository interfaces commonly use repository infrastructure without requiring an explicit `@Repository` annotation.

**Important:** `@Repository` also participates in persistence exception translation when applicable.

## 2.5 `@Controller`

**Definition:** Marks a class as a Spring MVC controller, commonly used to return views.

```java
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
```

**When to use:** For MVC applications that return views such as Thymeleaf templates.

## 2.6 `@RestController`

**Definition:** A convenience annotation combining `@Controller` and `@ResponseBody`.

```java
@RestController
@RequestMapping("/students")
public class StudentController {

    @GetMapping("/{id}")
    public String getStudent(@PathVariable int id) {
        return "Student ID: " + id;
    }
}
```

**When to use:** For REST APIs that return JSON, text, or other response data.

**Interview question:** What is the difference between `@Controller` and `@RestController`?

- `@Controller`: commonly returns a view name; use `@ResponseBody` when a method should return response data directly.
- `@RestController`: response bodies are the default for its handler methods.

---

# 3. Dependency Injection Annotations

## 3.1 `@Autowired`

**Definition:** Requests that Spring inject a suitable bean into a dependency.

```java
@Autowired
private StudentService studentService;
```

**When to use:** When Spring needs to supply a dependency.

**Recommended approach — constructor injection:**

```java
@Service
public class StudentService {
}

@RestController
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
}
```

When a class has a single constructor, Spring can use it without `@Autowired`.

**Why prefer constructor injection?**
- Makes required dependencies explicit.
- Supports immutable `final` fields.
- Simplifies unit testing.
- Avoids common field-injection problems.

## 3.2 `@Qualifier`

**Definition:** Selects a specific bean when multiple beans match a dependency type.

```java
@Component("emailNotification")
class EmailNotification implements Notification {
}

@Component("smsNotification")
class SmsNotification implements Notification {
}

@Service
class NotificationService {

    private final Notification notification;

    NotificationService(
            @Qualifier("emailNotification")
            Notification notification) {
        this.notification = notification;
    }
}
```

**When to use:** When multiple implementations of the same interface exist.

## 3.3 `@Primary`

**Definition:** Makes one bean the preferred candidate when multiple beans match a single-valued dependency.

```java
@Component
@Primary
class EmailNotification implements Notification {
}
```

**Difference:**
- `@Primary`: declares the default preferred bean.
- `@Qualifier`: explicitly identifies the bean to inject.

## 3.4 `@Value`

**Definition:** Injects a property value into a field, constructor parameter, or method parameter.

```java
@Value("${app.name}")
private String appName;
```

In `application.properties`:

```properties
app.name=StudentApplication
```

**When to use:** For injecting individual configuration values.

## 3.5 `@ConfigurationProperties`

**Definition:** Binds a group of external configuration properties to a Java object.

```java
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name;
    private int timeout;

    // Getters and setters
}
```

```properties
app.name=StudentApplication
app.timeout=30
```

Enable registration, for example, using `@ConfigurationPropertiesScan` on the application class.

**When to use:** When an application has multiple related configuration properties.

**Interview question:** `@Value` vs `@ConfigurationProperties`?

- `@Value`: useful for individual values and supports Spring property expressions.
- `@ConfigurationProperties`: better for structured, type-safe groups of settings.

---

# 4. Configuration Annotations

## 4.1 `@Configuration`

**Definition:** Declares a class as a source of Spring bean definitions.

```java
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

**When to use:** To define application configuration and beans explicitly.

## 4.2 `@Bean`

**Definition:** Marks a method whose returned object should be registered as a Spring bean.

```java
@Configuration
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
```

**When to use:** When you need to register an object, especially a third-party class that you cannot annotate with `@Component`.

**Difference:**
- `@Component`: put it on a class for component scanning.
- `@Bean`: put it on a method to register its returned object.

## 4.3 `@Import`

**Definition:** Imports additional configuration classes or supported component definitions into the Spring configuration.

```java
@Configuration
@Import(DatabaseConfig.class)
public class AppConfig {
}
```

**When to use:** To organize configuration into reusable classes.

## 4.4 `@Profile`

**Definition:** Makes a bean or configuration active only for specified profiles.

```java
@Configuration
@Profile("dev")
public class DevConfig {
}
```

Activate it with:

```properties
spring.profiles.active=dev
```

**When to use:** To separate development, testing, and production configurations.

---

# 5. Auto-Configuration and Conditional Annotations

## 5.1 `@EnableAutoConfiguration`

**Definition:** Enables Spring Boot's auto-configuration mechanism.

Spring Boot examines the classpath, existing beans, and configuration properties to determine which configurations may apply.

**Example:** If Spring MVC dependencies are present, Boot can configure MVC infrastructure when the relevant conditions are satisfied.

**Important:** Auto-configuration backs off in many cases when the application provides its own relevant beans. The exact behavior depends on each configuration's conditions.

## 5.2 `@ConditionalOnClass`

**Definition:** Applies configuration when a specified class is available on the classpath.

```java
@ConditionalOnClass(name = "com.example.SomeLibrary")
@Configuration
class LibraryConfig {
}
```

**When to use:** In conditional configuration, especially library integrations and auto-configuration.

## 5.3 `@ConditionalOnMissingBean`

**Definition:** Applies configuration when a matching bean is not already available.

```java
@Bean
@ConditionalOnMissingBean
public Clock clock() {
    return Clock.systemUTC();
}
```

**When to use:** To provide a default bean that applications can override.

## 5.4 `@EnableConfigurationProperties`

**Definition:** Registers classes annotated with `@ConfigurationProperties` for configuration-property binding.

```java
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {
}
```

**Interview question:** How does Spring Boot reduce configuration?

It provides auto-configuration based on dependencies, existing beans, application properties, and conditional rules. Developers can still override or customize that configuration.

---

# 6. REST API Annotations

## 6.1 `@RequestMapping`

**Definition:** Maps HTTP requests to a controller or handler method.

```java
@RestController
@RequestMapping("/students")
public class StudentController {

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String getAllStudents() {
        return "All students";
    }
}
```

**When to use:** For shared class-level paths or flexible request mapping.

## 6.2 `@GetMapping`

Maps HTTP GET requests.

```java
@GetMapping("/{id}")
public String getStudent(@PathVariable int id) {
    return "Student " + id;
}
```

## 6.3 `@PostMapping`

Maps HTTP POST requests, commonly for creating resources.

```java
@PostMapping
public String createStudent(@RequestBody Student student) {
    return "Student created";
}
```

## 6.4 `@PutMapping`

Maps HTTP PUT requests, commonly for replacing a resource representation.

```java
@PutMapping("/{id}")
public String updateStudent(
        @PathVariable int id,
        @RequestBody Student student) {
    return "Student updated";
}
```

## 6.5 `@PatchMapping`

Maps HTTP PATCH requests, commonly for partial updates.

```java
@PatchMapping("/{id}")
public String updateStudentName(
        @PathVariable int id,
        @RequestBody Map<String, Object> updates) {
    return "Student partially updated";
}
```

## 6.6 `@DeleteMapping`

Maps HTTP DELETE requests.

```java
@DeleteMapping("/{id}")
public String deleteStudent(@PathVariable int id) {
    return "Student deleted";
}
```

**Interview question:** What is the difference between PUT and PATCH?

- PUT commonly replaces the resource representation.
- PATCH applies a partial modification.
- Exact behavior depends on the API contract.

## 6.7 `@PathVariable`

**Definition:** Reads a value from the URI path.

```java
@GetMapping("/{id}")
public String getStudent(@PathVariable("id") int studentId) {
    return "ID: " + studentId;
}
```

Request: `GET /students/10`

Value: `studentId = 10`

## 6.8 `@RequestParam`

**Definition:** Reads a query parameter or form parameter.

```java
@GetMapping("/search")
public String searchStudent(@RequestParam String name) {
    return "Searching: " + name;
}
```

Request: `GET /students/search?name=Ravi`

**Difference:**
- `@PathVariable`: identifies a value in the URI path.
- `@RequestParam`: reads a request parameter.

## 6.9 `@RequestBody`

**Definition:** Binds the HTTP request body to a Java object, typically using an HTTP message converter such as Jackson for JSON.

```java
@PostMapping
public Student createStudent(@RequestBody Student student) {
    return student;
}
```

**When to use:** When the client sends JSON or another supported representation in the request body.

## 6.10 `@ResponseBody`

**Definition:** Writes a handler method's return value directly to the HTTP response body using an appropriate message converter.

It is included by default in `@RestController`.

## 6.11 `@ResponseStatus`

**Definition:** Associates a specified HTTP status with a controller response or exception.

```java
@ResponseStatus(HttpStatus.CREATED)
@PostMapping
public String createStudent() {
    return "Created";
}
```

**When to use:** For a fixed response status. For dynamic status codes and more complex error handling, consider `ResponseEntity`.

## 6.12 `ResponseEntity`

`ResponseEntity` is a class, not an annotation. It lets a controller return a response body, HTTP status, and headers.

```java
@PostMapping
public ResponseEntity<Student> createStudent(
        @RequestBody Student student) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(student);
}
```

---

# 7. Validation Annotations

Validation examples typically use `jakarta.validation` annotations and a validation implementation such as Hibernate Validator.

## 7.1 `@Valid`

**Definition:** Triggers validation of an object according to its declared validation constraints when supported by the method argument processing.

```java
@PostMapping
public String createStudent(@Valid @RequestBody Student student) {
    return "Student created";
}
```

## 7.2 `@NotNull`

The value must not be null.

## 7.3 `@NotBlank`

A string must not be null and must contain at least one non-whitespace character.

## 7.4 `@NotEmpty`

A string, collection, map, or supported array must not be null or empty.

## 7.5 `@Size`

Specifies permitted size or length limits for supported values.

## 7.6 `@Email`

Checks whether a string conforms to the validation provider's email format rules.

**Example model:**

```java
public class Student {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 10, max = 10)
    private String mobileNumber;

    // Getters and setters
}
```

**Important:** Constraints do not validate incoming requests by themselves. Ensure validation is enabled at the appropriate boundary, commonly using `@Valid` on the request body, and configure appropriate error responses.

---

# 8. Spring Data JPA Annotations

## 8.1 `@Entity`

Marks a Java class as a JPA entity.

```java
@Entity
public class Student {
}
```

## 8.2 `@Table`

Specifies the database table mapping.

```java
@Entity
@Table(name = "students")
public class Student {
}
```

## 8.3 `@Id`

Marks the primary-key field or property.

```java
@Id
private Long id;
```

## 8.4 `@GeneratedValue`

Specifies how a primary key is generated.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

## 8.5 `@Column`

Customizes column mapping and selected column properties.

```java
@Column(name = "student_name", nullable = false)
private String name;
```

## 8.6 `@Transient`

Marks an entity field or property that should not be persisted as a database column.

```java
@Transient
private String temporaryMessage;
```

## 8.7 `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`

These annotations define relationships between JPA entities.

Examples:
- `@OneToOne`: one person and one passport.
- `@OneToMany`: one department and many employees.
- `@ManyToOne`: many employees belong to one department.
- `@ManyToMany`: students and courses can have many-to-many relationships.

**When to use:** When modeling relational data and entity associations.

## 8.8 `@Transactional`

**Definition:** Declares that a method or class should execute with transaction semantics managed by Spring.

```java
@Service
public class StudentService {

    @Transactional
    public void createStudent() {
        // Perform related database operations
    }
}
```

**When to use:** When multiple database operations need to succeed or roll back together.

**Important interview points:**
- In the usual proxy-based configuration, calls must pass through the Spring proxy for transaction advice to apply.
- Self-invocation generally bypasses proxy-based transaction interception.
- By default, unchecked exceptions trigger rollback; checked-exception behavior can be customized.
- A transaction does not automatically cover independent remote service calls in a distributed system.

---

# 9. Exception Handling Annotations

## 9.1 `@ExceptionHandler`

Handles specified exceptions in a controller or advice class.

```java
@ExceptionHandler(StudentNotFoundException.class)
public ResponseEntity<String> handleNotFound(
        StudentNotFoundException ex) {
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
}
```

## 9.2 `@ControllerAdvice`

Provides shared advice for controllers, including exception handling.

## 9.3 `@RestControllerAdvice`

Combines `@ControllerAdvice` and `@ResponseBody`, making it useful for centralized REST API exception handling.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<String> handleStudentNotFound(
            StudentNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
```

**When to use:** When multiple controllers need consistent error handling.

---

# 10. Lifecycle and Scope Annotations

## 10.1 `@PostConstruct`

Runs an initialization callback after dependency injection and before the bean is put into normal service.

```java
@PostConstruct
public void initialize() {
    System.out.println("Bean initialized");
}
```

## 10.2 `@PreDestroy`

Runs a destruction callback when Spring destroys the bean, provided the lifecycle is managed appropriately.

```java
@PreDestroy
public void cleanup() {
    System.out.println("Cleaning up resources");
}
```

**Package note:** In modern Spring Boot applications, use `jakarta.annotation.PostConstruct` and `jakarta.annotation.PreDestroy`.

## 10.3 `@Scope`

Defines a bean's scope.

```java
@Component
@Scope("prototype")
public class ReportGenerator {
}
```

Common scopes:
- `singleton`: one bean instance per bean definition per Spring container; this is the default.
- `prototype`: a new instance each time the container is asked to create one.
- `request`: one instance per HTTP request in a web-aware context.
- `session`: one instance per HTTP session in a web-aware context.

**Interview question:** Are Spring singleton beans thread-safe?

No. Singleton scope does not guarantee thread safety. A singleton bean that is accessed concurrently must be designed appropriately.

---

# 11. Scheduling and Asynchronous Execution

## 11.1 `@EnableScheduling` and `@Scheduled`

Enable scheduled task processing and declare a scheduled method.

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

@Component
public class ReportScheduler {

    @Scheduled(fixedRate = 60000)
    public void generateReport() {
        System.out.println("Generating report");
    }
}
```

`fixedRate = 60000` schedules executions at a 60-second rate. Actual execution timing can be affected by task configuration and execution duration.

## 11.2 `@EnableAsync` and `@Async`

Enable Spring's asynchronous method execution and mark a method for asynchronous invocation.

```java
@Configuration
@EnableAsync
public class AsyncConfig {
}

@Service
public class EmailService {

    @Async
    public CompletableFuture<String> sendEmail() {
        return CompletableFuture.completedFuture("Email sent");
    }
}
```

**Important:** Configure an appropriate executor for production workloads. In the usual proxy-based setup, self-invocation does not trigger `@Async` interception.

---

# 12. Caching Annotations

Caching requires an appropriate cache manager and enabled caching.

## 12.1 `@EnableCaching`

Enables Spring's annotation-driven cache management.

## 12.2 `@Cacheable`

Uses a cache to avoid repeatedly executing a method for the same cache key.

```java
@Cacheable(value = "students", key = "#id")
public Student getStudent(Long id) {
    return repository.findById(id).orElseThrow();
}
```

## 12.3 `@CachePut`

Executes the method and updates the cache with the returned value.

## 12.4 `@CacheEvict`

Removes matching cache entries.

```java
@CacheEvict(value = "students", key = "#id")
public void deleteStudent(Long id) {
    repository.deleteById(id);
}
```

**When to use:** When frequently requested data can be cached safely and invalidated correctly.

**Interview question:** `@Cacheable` vs `@CachePut`?

- `@Cacheable`: may skip method execution when a cache hit occurs.
- `@CachePut`: executes the method and updates the cache.

---

# 13. Spring Security Annotations

## 13.1 `@EnableMethodSecurity`

Enables method-level security features.

## 13.2 `@PreAuthorize`

Checks authorization before method execution.

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteStudent(Long id) {
}
```

## 13.3 `@PostAuthorize`

Checks an authorization expression after method execution, before returning the result to the caller.

```java
@PostAuthorize("returnObject.owner == authentication.name")
public Student getStudent(Long id) {
    return repository.findById(id).orElseThrow();
}
```

Use it carefully because the method may already have performed work before the authorization check.

**When to use:** For fine-grained access control at the method level.

---

# 14. Testing Annotations

## 14.1 `@SpringBootTest`

Loads the Spring application context for integration-style testing.

```java
@SpringBootTest
class StudentApplicationTests {
}
```

## 14.2 `@WebMvcTest`

Loads a focused Spring MVC test slice, commonly used to test controllers with MockMvc.

## 14.3 `@DataJpaTest`

Loads a JPA-focused test slice for persistence tests.

## 14.4 `@MockBean` and `@MockitoBean`

These annotations are used to override beans with mocks in Spring tests, depending on the Spring Boot and Spring Framework version.

- `@MockBean` is associated with Spring Boot's test support and is deprecated in newer Spring Boot versions.
- `@MockitoBean` is provided by Spring Framework's test support in modern versions.

**Interview question:** `@SpringBootTest` vs `@WebMvcTest`?

- `@SpringBootTest`: broader application-context testing.
- `@WebMvcTest`: focused testing of the MVC layer, with collaborators typically mocked.

---

# 15. Important Differences — Quick Revision

| Annotations | Main difference |
|---|---|
| `@Component` vs `@Bean` | Class discovered by scanning vs object returned by a configuration method |
| `@Component` vs `@Service` | General component vs business/service-layer specialization |
| `@Repository` vs `@Service` | Persistence-layer specialization vs business-layer specialization |
| `@Controller` vs `@RestController` | MVC controller vs controller with response bodies by default |
| `@Autowired` vs `@Qualifier` | Injects a dependency vs selects a specific matching bean |
| `@Primary` vs `@Qualifier` | Default preferred bean vs explicit bean selection |
| `@Value` vs `@ConfigurationProperties` | Individual property injection vs structured configuration binding |
| `@RequestParam` vs `@PathVariable` | Request parameter vs URI path value |
| `@RequestBody` vs `@ResponseBody` | Request body to Java object vs Java return value to response body |
| `@Valid` vs `@Validated` | Triggers Bean Validation vs Spring validation support with additional features such as validation groups |
| `@Transactional` vs `@Cacheable` | Transaction management vs caching |
| `@PostConstruct` vs `@PreDestroy` | Initialization callback vs destruction callback |
| `@Cacheable` vs `@CachePut` | May reuse cached result vs executes method and updates cache |
| `@SpringBootTest` vs `@WebMvcTest` | Broad integration test vs MVC test slice |

---

# 16. Common Spring Boot Interview Questions

# Spring Boot Interview Questions and Answers

## 1. What is `@SpringBootApplication` and which annotations does it combine?

**Answer:**

`@SpringBootApplication` is the main annotation used to start and configure a Spring Boot application. It combines three annotations:

- `@SpringBootConfiguration` — identifies the class as a source of Spring configuration.
- `@EnableAutoConfiguration` — enables Spring Boot's auto-configuration mechanism.
- `@ComponentScan` — scans packages for Spring-managed components.

**Example:**

```java
@SpringBootApplication
public class StudentApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentApplication.class, args);
    }
}
```

**Interview point:** Place the main application class in a root package so that component scanning can discover components in its subpackages.

---

## 2. What happens internally when `SpringApplication.run()` executes?

**Answer:**

`SpringApplication.run()` bootstraps the Spring Boot application, prepares the environment, creates the application context, loads and configures beans, and starts the embedded web server for a web application.

**Simplified flow:**

1. Creates and prepares the `SpringApplication` runtime.
2. Prepares the application environment and loads configuration properties.
3. Creates the appropriate `ApplicationContext`.
4. Registers bean definitions and processes configuration classes.
5. Performs component scanning and evaluates auto-configuration conditions.
6. Creates and initializes singleton beans, including dependency injection.
7. Refreshes the application context.
8. Starts the embedded web server, such as Tomcat, for a servlet-based web application.
9. Publishes application events and runs applicable startup runners.

**Example:**

```java
public static void main(String[] args) {
    SpringApplication.run(StudentApplication.class, args);
}
```

**Interview point:** The exact internal sequence varies by Spring Boot version and application type. Bean creation and web-server startup involve multiple lifecycle steps, not just one operation.

---

## 3. What is the difference between `@Component`, `@Service`, and `@Repository`?

**Answer:**

All three identify classes as Spring-managed components when component scanning is configured appropriately, but they communicate different responsibilities.

| Annotation | Purpose | Example |
|---|---|---|
| `@Component` | General-purpose component | Utility class |
| `@Service` | Business logic | StudentService |
| `@Repository` | Data-access layer | StudentRepository |

**Example:**

```java
@Component
public class EmailUtil {
}

@Service
public class StudentService {
}

@Repository
public class StudentDao {
}
```

**Why use separate annotations?**

They make the application easier to understand and maintain. `@Repository` also participates in persistence exception translation when the required infrastructure is configured.

**Interview point:** `@Service` and `@Repository` are specialized forms of `@Component`.

---

## 4. What is Dependency Injection? Why is constructor injection recommended?

**Answer:**

Dependency Injection (DI) is a technique in which Spring supplies an object with the dependencies it needs instead of the object creating those dependencies itself.

**Example:**

```java
@Service
public class StudentService {
    public String getStudent() {
        return "Student details";
    }
}

@RestController
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
}
```

Spring creates or obtains the `StudentService` bean and injects it into the controller.

**Why prefer constructor injection?**

- Makes required dependencies explicit.
- Allows fields to be `final`.
- Makes unit testing easier.
- Prevents the object from being constructed without its required dependencies.
- Helps reveal tightly coupled designs.

**Interview point:** With a single constructor, Spring does not require `@Autowired` on that constructor.

---

## 5. What happens when multiple beans of the same type exist?

**Answer:**

If Spring finds multiple candidates for a single-valued dependency, it uses resolution rules such as qualifiers and primary candidates. If the ambiguity cannot be resolved, application startup or dependency resolution can fail.

**Solution 1: `@Qualifier`**

```java
@Component("emailNotification")
class EmailNotification implements Notification {
}

@Component("smsNotification")
class SmsNotification implements Notification {
}

@Service
class NotificationService {

    private final Notification notification;

    NotificationService(
            @Qualifier("emailNotification")
            Notification notification) {
        this.notification = notification;
    }
}
```

**Solution 2: `@Primary`**

```java
@Component
@Primary
class EmailNotification implements Notification {
}
```

**Difference:**

- `@Primary` identifies a preferred candidate.
- `@Qualifier` narrows the selection to a specifically qualified candidate.

**Real-world example:** An application supports email and SMS notifications. Use `@Qualifier` to select the required implementation.

---

## 6. What is the difference between `@Bean` and `@Component`?

**Answer:**

`@Component` marks a class for component scanning, whereas `@Bean` marks a method whose returned object should be registered in the Spring container.

**Using `@Component`:**

```java
@Component
public class EmailService {
}
```

**Using `@Bean`:**

```java
@Configuration
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
```

**When to use each?**

- `@Component`: for classes that you own and want Spring to discover through scanning.
- `@Bean`: for explicit object creation, third-party classes, or customized configuration.

**Interview point:** Both approaches can register Spring-managed beans. They differ in how the bean is declared and created.

---

## 7. How does Spring Boot auto-configuration work?

**Answer:**

Spring Boot auto-configuration attempts to configure application infrastructure based on dependencies available on the classpath, existing beans, configuration properties, and conditional rules.

**Example:**

When Spring MVC dependencies are present and the relevant conditions are satisfied, Spring Boot can configure MVC infrastructure.

When a supported database driver and appropriate configuration are available, Boot can configure a `DataSource`.

**Important annotations:**

- `@EnableAutoConfiguration`
- `@ConditionalOnClass`
- `@ConditionalOnMissingBean`
- `@ConditionalOnProperty`

**Simplified flow:**

1. Spring Boot discovers applicable auto-configuration classes.
2. It evaluates their conditions.
3. Matching configuration classes contribute bean definitions.
4. The application can customize or replace supported defaults.

**Interview point:** Auto-configuration does not mean Spring Boot configures everything unconditionally. Configuration depends on the conditions of each feature.

---

## 8. How can you override Spring Boot's default configuration?

**Answer:**

You can override default behavior using application properties, custom beans, configuration classes, and other supported extension points.

**Method 1: Application properties**

```properties
server.port=9090
spring.application.name=student-service
```

This changes the embedded server port and application name.

**Method 2: Define a custom bean**

```java
@Configuration
public class AppConfig {

    @Bean
    @ConditionalOnMissingBean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
```

This provides a default `Clock` bean if no matching bean exists.

**Method 3: Define an application-specific bean**

When an auto-configuration uses `@ConditionalOnMissingBean`, a suitable application-defined bean can cause the default bean configuration to back off.

**Interview point:** Whether a custom bean replaces a default depends on the specific auto-configuration and its conditions. Not every default can be overridden in the same way.

---

## 9. What is the difference between `@Controller` and `@RestController`?

**Answer:**

`@Controller` is commonly used for Spring MVC controllers that return view names. `@RestController` combines `@Controller` and `@ResponseBody`, so handler return values are written to the response body by default.

**Using `@Controller`:**

```java
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
```

This typically resolves the `home` view.

**Using `@RestController`:**

```java
@RestController
public class StudentController {

    @GetMapping("/student")
    public Student getStudent() {
        return new Student(1, "Ravi");
    }
}
```

The returned object is serialized to the HTTP response body, commonly as JSON.

**Interview point:** Use `@RestController` for REST APIs and `@Controller` for view-based MVC applications. A `@Controller` method can also return a response body when annotated with `@ResponseBody`.

---

## 10. What is the difference between `@RequestParam` and `@PathVariable`?

**Answer:**

`@RequestParam` reads a request parameter, while `@PathVariable` reads a value from the URI path.

**Using `@PathVariable`:**

```java
@GetMapping("/students/{id}")
public String getStudent(@PathVariable Long id) {
    return "Student ID: " + id;
}
```

Request:

```text
GET /students/10
```

**Using `@RequestParam`:**

```java
@GetMapping("/students")
public String searchStudent(@RequestParam String name) {
    return "Student name: " + name;
}
```

Request:

```text
GET /students?name=Ravi
```

**Interview point:**

- Path variable: commonly identifies a particular resource.
- Request parameter: commonly supplies filtering, searching, pagination, or optional request values.

---

## 11. How does `@RequestBody` convert JSON into a Java object?

**Answer:**

`@RequestBody` tells Spring MVC to read the HTTP request body and convert it into a Java object using an appropriate `HttpMessageConverter`. For JSON, a Jackson-based converter is commonly used when Jackson is available and configured.

**Example:**

```java
public class Student {
    private Long id;
    private String name;

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
}
```

Controller:

```java
@PostMapping("/students")
public Student createStudent(@RequestBody Student student) {
    return student;
}
```

Request body:

```json
{
  "id": 1,
  "name": "Ravi"
}
```

**Internal flow:**

1. The client sends a request with a JSON body.
2. Spring MVC selects the appropriate message converter.
3. Jackson deserializes the JSON into a `Student` object.
4. Spring passes the object to the controller method.

**Interview point:** Invalid JSON or incompatible data types can cause a request-body conversion error.

---

## 12. How does validation work with `@Valid`?

**Answer:**

`@Valid` triggers Bean Validation for a supported object argument. Constraints such as `@NotBlank`, `@NotNull`, and `@Size` define the validation rules.

**Example:**

```java
public class Student {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Enter a valid email")
    private String email;

    // Getters and setters
}
```

Controller:

```java
@PostMapping("/students")
public ResponseEntity<Student> createStudent(
        @Valid @RequestBody Student student) {
    return ResponseEntity.status(201).body(student);
}
```

**Internal flow:**

1. Spring converts the request body into a Java object.
2. `@Valid` triggers validation of the object's constraints.
3. If validation fails, Spring MVC normally raises a validation exception.
4. The application can return meaningful validation errors using centralized exception handling.

**Important:** Include a validation implementation in the project, commonly through `spring-boot-starter-validation`.

**Interview point:** `@Valid` triggers validation; the constraint annotations define what is valid.

---

## 13. How do `@ControllerAdvice` and `@ExceptionHandler` work together?

**Answer:**

`@ControllerAdvice` allows exception handling logic to be shared across controllers. `@ExceptionHandler` identifies a method that handles specified exceptions.

For REST APIs, `@RestControllerAdvice` is often convenient because its return values are written to the response body.

**Example:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<String> handleNotFound(
            StudentNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
```

**Internal flow:**

1. A controller or service operation throws an exception.
2. Spring MVC's exception-resolution mechanism looks for a suitable handler.
3. A matching `@ExceptionHandler` method processes the exception.
4. The handler returns an appropriate HTTP response.

**When to use:** To avoid repeating exception-handling code across multiple controllers.

**Interview point:** A global handler can handle common exceptions, while a controller-specific handler can handle exceptions for a particular controller.

---

## 14. What does `@Transactional` do? What are its limitations?

**Answer:**

`@Transactional` declares transaction semantics for a method or class. Spring typically implements declarative transaction management using AOP proxies and a configured transaction manager.

**Example:**

```java
@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void createStudent(Student student) {
        repository.save(student);
        // Other related database operations
    }
}
```

**Why use it?**

If several database operations belong to one business operation, they may need to succeed or roll back together.

**Limitations and interview points:**

1. In the usual proxy-based setup, self-invocation does not trigger transactional advice.
2. By default, unchecked exceptions and errors trigger rollback; checked exceptions do not automatically trigger rollback.
3. The behavior depends on the configured transaction manager and resource.
4. A database transaction does not automatically make remote API calls part of the same atomic transaction.
5. Long-running transactions can hold database resources and locks longer than necessary.
6. Transaction propagation and isolation settings influence transaction behavior.

**Interview point:** `@Transactional` is not a universal guarantee that every operation in a method will be rolled back. Understand rollback rules, proxy behavior, and transaction boundaries.

---

## 15. What is the difference between singleton and prototype scope?

**Answer:**

Singleton scope creates one bean instance per bean definition per Spring container. Prototype scope creates a new instance whenever Spring is asked to resolve that bean for creation.

| Feature | Singleton | Prototype |
|---|---|---|
| Default scope | Yes | No |
| Instance creation | One per bean definition per container | New instance for each container creation request |
| Typical use | Stateless services | Objects that need independent instances |
| Lifecycle management | Spring manages creation and destruction callbacks | Spring manages creation but does not normally manage the full destruction lifecycle |

**Example:**

```java
@Component
@Scope("singleton")
public class StudentService {
}
```

```java
@Component
@Scope("prototype")
public class ReportGenerator {
}
```

**Important:** Singleton beans are not automatically thread-safe. Also, injecting a prototype bean directly into a singleton usually resolves the prototype only when the singleton is created, unless a provider or another suitable mechanism is used.

---

## 16. What are `@PostConstruct` and `@PreDestroy` used for?

**Answer:**

- `@PostConstruct`: executes an initialization callback after dependency injection and before the bean is put into normal service.
- `@PreDestroy`: executes a destruction callback when Spring destroys the bean, provided its lifecycle is managed appropriately.

**Example:**

```java
@Component
public class DatabaseManager {

    @PostConstruct
    public void initialize() {
        System.out.println("Initialize resources");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Clean up resources");
    }
}
```

**When to use:**

- `@PostConstruct`: validate configuration or initialize resources.
- `@PreDestroy`: release resources or perform cleanup.

**Interview point:** These callbacks are part of the bean lifecycle. `@PreDestroy` is not guaranteed to run after abrupt process termination.

**Package note:** In modern Spring Boot applications, use `jakarta.annotation.PostConstruct` and `jakarta.annotation.PreDestroy`.

---

## 17. How do `@Scheduled` and `@Async` work?

**Answer:**

`@Scheduled` runs a method according to a schedule. `@Async` allows a method invocation to execute asynchronously using a configured Spring task executor.

### `@Scheduled` example

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

@Component
public class ReportScheduler {

    @Scheduled(fixedRate = 60000)
    public void generateReport() {
        System.out.println("Generating report");
    }
}
```

The method is scheduled at a 60-second fixed rate.

### `@Async` example

```java
@Configuration
@EnableAsync
public class AsyncConfig {
}

@Service
public class EmailService {

    @Async
    public CompletableFuture<String> sendEmail() {
        return CompletableFuture.completedFuture("Email sent");
    }
}
```

**Differences:**

- `@Scheduled`: determines when a task runs.
- `@Async`: determines that an invocation can run asynchronously.

**Interview points:**

- Enable the corresponding feature with `@EnableScheduling` or `@EnableAsync`.
- Configure a suitable thread pool for production workloads.
- Proxy-based asynchronous execution generally does not apply to self-invocation.
- Handle exceptions appropriately, especially for asynchronous methods without a returned future.

---

## 18. How do `@Cacheable`, `@CachePut`, and `@CacheEvict` differ?

**Answer:**

These annotations support declarative caching through Spring's cache abstraction.

### `@Cacheable`

Uses a cached result when the key matches an existing entry; otherwise, it executes the method and normally stores the result.

```java
@Cacheable(value = "students", key = "#id")
public Student getStudent(Long id) {
    return repository.findById(id).orElseThrow();
}
```

### `@CachePut`

Executes the method and updates the cache with the returned value.

```java
@CachePut(value = "students", key = "#student.id")
public Student updateStudent(Student student) {
    return repository.save(student);
}
```

### `@CacheEvict`

Removes an entry or entries from the cache.

```java
@CacheEvict(value = "students", key = "#id")
public void deleteStudent(Long id) {
    repository.deleteById(id);
}
```

**Quick comparison:**

| Annotation | Behavior |
|---|---|
| `@Cacheable` | May skip method execution on a cache hit |
| `@CachePut` | Executes the method and updates the cache |
| `@CacheEvict` | Removes cached entries |

**Interview point:** Enable caching and configure an appropriate cache manager. Cache invalidation and stale data are important real-world concerns.

---

## 19. How do `@PreAuthorize` and `@PostAuthorize` differ?

**Answer:**

Both support method-level authorization in Spring Security, but they evaluate authorization at different stages.

### `@PreAuthorize`

Checks the authorization expression before method execution.

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteStudent(Long id) {
}
```

The method requires the caller to have the `ADMIN` role.

### `@PostAuthorize`

Checks the authorization expression after method execution and before returning the result to the caller.

```java
@PostAuthorize(
    "returnObject.owner == authentication.name"
)
public Student getStudent(Long id) {
    return repository.findById(id).orElseThrow();
}
```

The expression checks whether the returned student's owner matches the authenticated username.

**Differences:**

- `@PreAuthorize`: authorizes access before the method runs.
- `@PostAuthorize`: checks the result after the method runs.

**Important:** Enable method security using `@EnableMethodSecurity`. With `@PostAuthorize`, the method may already have performed database operations or other side effects before authorization fails.

---

## 20. What is the difference between `@SpringBootTest` and `@WebMvcTest`?

**Answer:**

`@SpringBootTest` loads a broader application context for integration testing, whereas `@WebMvcTest` loads a focused Spring MVC test slice.

### `@SpringBootTest`

```java
@SpringBootTest
class StudentApplicationTests {

    @Test
    void applicationStarts() {
    }
}
```

**When to use:** For tests that require broad application configuration or interactions between multiple layers.

### `@WebMvcTest`

```java
@WebMvcTest(StudentController.class)
class StudentControllerTests {

    @Autowired
    private MockMvc mockMvc;
}
```

**When to use:** For testing controller mappings, HTTP responses, serialization, and MVC behavior without loading the entire application.

**Differences:**

| Feature | `@SpringBootTest` | `@WebMvcTest` |
|---|---|---|
| Context | Broad application context | Focused MVC slice |
| Controller testing | Supported | Main purpose |
| Database/repository | May load if configured and discovered | Usually excluded unless explicitly added |
| Speed | Often slower | Usually faster |
| Dependencies | Real application beans where configured | Collaborators are commonly mocked |

**Interview point:** Use `@WebMvcTest` for focused web-layer tests and `@SpringBootTest` when broader application integration needs to be verified.

---

# Final Interview Preparation Strategy

For each question, prepare these four points:

1. **Definition:** What does it mean?
2. **Purpose:** Why do we need it?
3. **Internal working:** What happens inside Spring?
4. **Real-world example:** Where would you use it in a project?

Do not memorize only annotation definitions. Interviewers often ask follow-up questions about default behavior, configuration conditions, proxy limitations, error handling, and practical use cases.

---

# 17. Recommended Learning Order

Study the annotations in this order to understand the conditions behind them, rather than memorizing names:

1. `@SpringBootApplication` — application startup.
2. `@Component`, `@Service`, `@Repository`, `@Controller`, `@RestController` — bean discovery and layers.
3. `@Autowired`, `@Qualifier`, `@Primary`, `@Bean`, `@Configuration` — dependency injection and configuration.
4. `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PathVariable`, `@RequestParam`, `@RequestBody` — REST APIs.
5. `@Entity`, `@Id`, `@GeneratedValue`, `@Transactional` — database and transactions.
6. `@Valid`, `@NotBlank`, `@ExceptionHandler`, `@RestControllerAdvice` — validation and error handling.
7. `@ConditionalOnMissingBean`, `@ConfigurationProperties`, `@Profile` — auto-configuration and environments.
8. `@PostConstruct`, `@PreDestroy`, `@Scope` — bean lifecycle.
9. `@Scheduled`, `@Async`, caching annotations — background work and performance.
10. Spring Security and testing annotations — authorization and verification.

**Final interview tip:** For every annotation, prepare four things: its definition, why it is needed, when to use it, and what happens internally. Be ready to explain a real project example and at least one limitation or alternative.