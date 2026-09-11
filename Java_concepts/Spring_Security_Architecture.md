# Spring MVC + Spring Security Architecture — Interview & Recall Notes

## 1. Big Picture

A Spring Boot web application can be understood as two major request-processing layers:

```text
Client
  |
  | HTTP Request
  v
Tomcat / Servlet Container
  |
  |-- If Spring Security is configured:
  |       DelegatingFilterProxy
  |           -> FilterChainProxy
  |               -> SecurityFilterChain
  |                   -> authentication / authorization filters
  |
  v
DispatcherServlet
  |
  | HandlerMapping
  v
Controller
  |
  | Service
  v
Repository
  |
  v
Database
```

**Important interview point:**

Spring Security is a **filter-based security layer** that normally executes before the request reaches `DispatcherServlet`.

Spring MVC is responsible for taking the request that reaches `DispatcherServlet` and finding the appropriate controller method.

---

# 2. What Happens If Spring Security Is NOT Added?

This is the most important flow to understand.

Suppose the client sends:

```http
GET /employees/10
```

and the application contains:

```java
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable int id) {
        return employeeService.getEmployee(id);
    }
}
```

There is **no Spring Security dependency/configuration**.

The simplified flow is:

```text
Browser / Postman
       |
       | GET /employees/10
       v
Tomcat
       |
       | Servlet mapping
       v
DispatcherServlet
       |
       | HandlerMapping
       v
EmployeeController.getEmployee(10)
       |
       v
Service
       |
       v
Repository
       |
       v
Database
```

## 2.1 Step 1 — Client Sends HTTP Request

The client sends:

```http
GET /employees/10
Host: localhost:8080
```

The request first reaches the embedded Tomcat server.

---

# 3. What Does Tomcat Do?

Tomcat is a **Servlet container**.

Its job is to:

- listen for HTTP requests
- create/manage Servlet-related objects
- create `HttpServletRequest` and `HttpServletResponse`
- determine which Servlet should process the request
- invoke that Servlet

In a Spring Boot application, one important Servlet is:

```text
DispatcherServlet
```

`DispatcherServlet` is Spring MVC's **Front Controller**.

---

# 4. How Does Tomcat Know About DispatcherServlet?

Spring Boot automatically registers `DispatcherServlet` with the Servlet container.

Conceptually, the registration looks like:

```text
URL
 |
 +---- / ----> DispatcherServlet
```

So when a request comes to:

```text
GET /employees/10
```

Tomcat sees that the request should be handled by the Spring MVC `DispatcherServlet`.

Conceptually:

```text
Tomcat
   |
   | URL matches DispatcherServlet mapping
   v
DispatcherServlet
```

### Important distinction

Tomcat does **NOT** directly search for:

```text
EmployeeController.getEmployee()
```

Tomcat only gets the request to the appropriate **Servlet**.

The Spring MVC `DispatcherServlet` then finds the appropriate controller.

This distinction is very important in interviews.

---

# 5. DispatcherServlet = Front Controller

`DispatcherServlet` is the central entry point for Spring MVC requests.

It follows the **Front Controller pattern**.

Instead of Tomcat directly calling every controller:

```text
Tomcat
  |
  +--> EmployeeController
  +--> OrderController
  +--> UserController
  +--> ProductController
```

the architecture is:

```text
Tomcat
   |
   v
DispatcherServlet
   |
   +--> EmployeeController
   +--> OrderController
   +--> UserController
   +--> ProductController
```

So:

> Tomcat delegates the request to `DispatcherServlet`, and `DispatcherServlet` delegates the request to the appropriate controller method.

---

# 6. How Does DispatcherServlet Find the Correct Controller?

This is where **HandlerMapping** comes in.

Suppose we have:

```java
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable int id) {
        return employeeService.getEmployee(id);
    }
}
```

The mapping is effectively:

```text
GET /employees/{id}
        |
        v
EmployeeController.getEmployee()
```

Spring MVC maintains mappings between incoming requests and controller handler methods.

`DispatcherServlet` asks a `HandlerMapping`:

```text
"Which handler should process GET /employees/10?"
```

The mapping mechanism finds:

```text
EmployeeController.getEmployee()
```

Then Spring invokes that controller method.

---

# 7. Complete Flow Without Spring Security

For:

```http
GET /employees/10
```

remember this sequence:

```text
1. Client
      |
      v
2. Tomcat / Servlet Container
      |
      v
3. DispatcherServlet
      |
      v
4. HandlerMapping
      |
      v
5. EmployeeController.getEmployee(10)
      |
      v
6. EmployeeService
      |
      v
7. EmployeeRepository
      |
      v
8. Database
```

### One-line interview answer

> When Spring Security is not present, Tomcat receives the HTTP request and routes it to the registered `DispatcherServlet`. The `DispatcherServlet`, acting as Spring MVC's Front Controller, uses `HandlerMapping` to find the controller method matching the request URL and HTTP method, and then invokes that handler.

---

# 8. What Changes When Spring Security Is Added?

Now add Spring Security.

The request becomes:

```text
Client
  |
  v
Tomcat
  |
  v
DelegatingFilterProxy
  |
  v
FilterChainProxy
  |
  v
SecurityFilterChain
  |
  | authentication / authorization
  v
DispatcherServlet
  |
  v
HandlerMapping
  |
  v
Controller
```

Spring Security uses the **Servlet Filter mechanism**.

This means security processing happens before the request reaches Spring MVC's `DispatcherServlet`.

---

# 9. DelegatingFilterProxy

`DelegatingFilterProxy` is a Servlet `Filter` that delegates to a Spring-managed bean.

The important idea is:

```text
Tomcat
   |
   v
DelegatingFilterProxy
   |
   v
Spring Security's FilterChainProxy
```

From there:

```text
FilterChainProxy
      |
      v
SecurityFilterChain
      |
      +--> authentication filters
      +--> authorization filters
      +--> other security filters
```

The uploaded notes describe this architecture as:

```text
Servlet Container
    -> DelegatingFilterProxy
        -> FilterChainProxy
            -> SecurityFilterChain
```

fileciteturn0file0L23-L30

---

# 10. Security Passes the Request to Spring MVC

If the security filters allow the request to continue, the filter chain eventually calls the next component.

Conceptually:

```java
filterChain.doFilter(request, response);
```

The request then reaches:

```text
DispatcherServlet
```

So the complete successful flow becomes:

```text
Client
  |
  v
Tomcat
  |
  v
DelegatingFilterProxy
  |
  v
FilterChainProxy
  |
  v
SecurityFilterChain
  |
  | security checks
  v
DispatcherServlet
  |
  v
HandlerMapping
  |
  v
Controller
```

The security layer therefore does **not** replace `DispatcherServlet`.

It sits before it in the request-processing path.

---

# 11. Authentication Flow

For a username/password-based authentication scenario, the security flow can conceptually become:

```text
Request
  |
  v
Security Filter
  |
  v
AuthenticationManager
  |
  v
ProviderManager
  |
  v
AuthenticationProvider
  |
  v
UserDetailsService
  |
  v
Database
```

The supplied notes identify:

- `AuthenticationManager` as the central authentication interface.
- `ProviderManager` as the standard implementation.
- `AuthenticationProvider` as the component that performs authentication.
- `DaoAuthenticationProvider` as a common provider for database-backed authentication.
- `UserDetailsService` as the component used to retrieve user details.

fileciteturn0file0L32-L35

After successful authentication:

```text
AuthenticationProvider
       |
       v
authenticated Authentication
       |
       v
SecurityContextHolder
```

fileciteturn0file0L37-L40

---

# 12. Security Is Not the Same as DispatcherServlet

This is a common interview confusion.

### Spring Security

Main responsibility:

```text
Security
  |
  +--> Authentication
  +--> Authorization
  +--> Security filters
  +--> Security context
```

### Spring MVC

Main responsibility:

```text
Spring MVC
  |
  +--> DispatcherServlet
  +--> HandlerMapping
  +--> Controller
  +--> Service
  +--> Response handling
```

### Easy memory trick

```text
Security asks:

"Are you allowed to proceed?"

MVC asks:

"Which controller should handle this request?"
```

---

# 13. Authentication vs Authorization

## Authentication

Question:

> Who are you?

Example:

```text
Username: kali
Password: ****
```

The application validates the credentials.

Result:

```text
Authenticated user
```

## Authorization

Question:

> What are you allowed to do?

Example:

```text
ROLE_ADMIN
ROLE_USER
```

A user may be authenticated but still not have permission to access:

```text
DELETE /employees/10
```

---

# 14. What Happens When Security Rejects the Request?

The request may never reach the controller.

Example:

```text
Client
  |
  v
Tomcat
  |
  v
Security Filters
  |
  X
  |
  +--> 401 / 403
```

Therefore:

```text
Security failure
       |
       X
DispatcherServlet
       |
       X
Controller
```

The controller is not necessarily executed when security blocks the request.

---

# 15. ExceptionTranslationFilter

`ExceptionTranslationFilter` is part of the Spring Security filter chain.

Its job is to translate certain Spring Security exceptions into appropriate HTTP responses or authentication redirects.

The supplied notes specifically identify:

```text
AuthenticationException
AccessDeniedException
```

as the relevant exceptions it handles. fileciteturn0file0L85-L110

Conceptually:

```text
ExceptionTranslationFilter
          |
          | try
          v
   downstream filters
          |
          v
     DispatcherServlet
          |
          v
      Controller
```

If an appropriate security exception occurs downstream, it can handle it.

---

# 16. 401 vs 403

## 401 Unauthorized

Usually means:

```text
User is not authenticated
```

Example:

```text
No valid authentication
       |
       v
AuthenticationEntryPoint
       |
       v
401 Unauthorized
```

## 403 Forbidden

Usually means:

```text
User is authenticated
but does not have enough privileges
```

Example:

```text
Authenticated USER
       |
       | tries ADMIN operation
       v
AccessDeniedHandler
       |
       v
403 Forbidden
```

The supplied notes describe this distinction between `AuthenticationEntryPoint` and `AccessDeniedHandler`. fileciteturn0file0L99-L110

---

# 17. The Most Important Comparison

| Without Spring Security | With Spring Security |
|---|---|
| Client | Client |
| ↓ | ↓ |
| Tomcat | Tomcat |
| ↓ | ↓ |
| DispatcherServlet | DelegatingFilterProxy |
| ↓ | ↓ |
| HandlerMapping | FilterChainProxy |
| ↓ | SecurityFilterChain |
| Controller | ↓ |
|  | DispatcherServlet |
|  | ↓ |
|  | HandlerMapping |
|  | ↓ |
|  | Controller |

### Memory shortcut

```text
NO SECURITY:

Client
  ↓
Tomcat
  ↓
DispatcherServlet
  ↓
HandlerMapping
  ↓
Controller
```

```text
WITH SECURITY:

Client
  ↓
Tomcat
  ↓
Security Filters
  ↓
DispatcherServlet
  ↓
HandlerMapping
  ↓
Controller
```

---

# 18. Interview Trap: Does Tomcat Call the Controller?

### Wrong answer

> Tomcat directly calls the controller.

### Better answer

> Tomcat is the Servlet container. It routes the HTTP request to the registered Servlet, which in a Spring MVC application is typically `DispatcherServlet`. `DispatcherServlet` then uses Spring MVC infrastructure such as `HandlerMapping` to identify and invoke the appropriate controller handler method.

This distinction shows that you understand the Servlet layer and Spring MVC layer separately.

---

# 19. Interview Trap: Does DispatcherServlet Search the Controller Class Manually?

Not exactly.

`DispatcherServlet` works with Spring MVC infrastructure.

The important component to remember is:

```text
HandlerMapping
```

Its job is to help map the incoming request to the appropriate handler.

Example:

```text
GET /employees/10
        |
        v
HandlerMapping
        |
        v
EmployeeController.getEmployee()
```

---

# 20. Practical Example

Controller:

```java
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable int id) {
        return new Employee(id, "Kali");
    }
}
```

Request:

```http
GET http://localhost:8080/employees/10
```

### Without Spring Security

```text
Browser/Postman
      |
      v
Tomcat
      |
      v
DispatcherServlet
      |
      v
HandlerMapping
      |
      | GET /employees/10
      v
EmployeeController
      |
      v
getEmployee(10)
```

### With Spring Security

```text
Browser/Postman
      |
      v
Tomcat
      |
      v
DelegatingFilterProxy
      |
      v
FilterChainProxy
      |
      v
SecurityFilterChain
      |
      | authentication / authorization
      |
      v
DispatcherServlet
      |
      v
HandlerMapping
      |
      v
EmployeeController
      |
      v
getEmployee(10)
```

---

# 21. What to Say in an Interview in 30 Seconds

> When a client sends an HTTP request to a Spring Boot application, the request first reaches the embedded Servlet container such as Tomcat. If Spring Security is configured, the request passes through the Servlet filter chain, including `DelegatingFilterProxy`, `FilterChainProxy`, and the configured `SecurityFilterChain`. If security allows the request to continue, it reaches Spring MVC's `DispatcherServlet`, which acts as the Front Controller. The `DispatcherServlet` uses `HandlerMapping` to determine which controller handler method matches the URL and HTTP method, and then invokes that controller method. If Spring Security is not configured, the security filter layer is absent, so Tomcat routes the request to `DispatcherServlet`, and Spring MVC performs the handler mapping directly.

---

# 22. Recall Diagram

Memorize this:

```text
                    HTTP REQUEST
                         |
                         v
                +----------------+
                |     TOMCAT     |
                | Servlet Server |
                +----------------+
                         |
              +----------+----------+
              |                     |
        NO SECURITY           SPRING SECURITY
              |                     |
              |              DelegatingFilterProxy
              |                     |
              |               FilterChainProxy
              |                     |
              |              SecurityFilterChain
              |                     |
              |              Authentication /
              |               Authorization
              |                     |
              +----------+----------+
                         |
                         v
                +------------------+
                | DispatcherServlet|
                |  Front Controller |
                +------------------+
                         |
                         v
                   HandlerMapping
                         |
                         v
                    Controller
                         |
                         v
                      Service
                         |
                         v
                    Repository
                         |
                         v
                     Database
```

---

# 23. Five Things to Remember for Exam

1. **Tomcat = Servlet container**
2. **DispatcherServlet = Spring MVC Front Controller**
3. **DelegatingFilterProxy = bridge from Servlet Filter mechanism to Spring-managed security infrastructure**
4. **FilterChainProxy / SecurityFilterChain = Spring Security request filtering**
5. **HandlerMapping = helps DispatcherServlet find the correct controller handler**

### Golden sequence

```text
Tomcat
  ↓
Security Filters (if configured)
  ↓
DispatcherServlet
  ↓
HandlerMapping
  ↓
Controller
```

---

# 24. Quick Recall Questions

### Q1. Who receives the HTTP request first?

**Answer:** Servlet container such as Tomcat.

### Q2. Does Tomcat directly call the controller?

**Answer:** No. Tomcat routes the request to a Servlet; in Spring MVC that is typically `DispatcherServlet`.

### Q3. What is DispatcherServlet?

**Answer:** Spring MVC's Front Controller.

### Q4. Who helps DispatcherServlet find the controller method?

**Answer:** `HandlerMapping`.

### Q5. Where does Spring Security normally execute?

**Answer:** In the Servlet filter chain, before the request reaches `DispatcherServlet`.

### Q6. What is the flow without Spring Security?

```text
Client
 → Tomcat
 → DispatcherServlet
 → HandlerMapping
 → Controller
```

### Q7. What is the flow with Spring Security?

```text
Client
 → Tomcat
 → DelegatingFilterProxy
 → FilterChainProxy
 → SecurityFilterChain
 → DispatcherServlet
 → HandlerMapping
 → Controller
```

### Q8. What is authentication?

**Answer:** Establishing who the user is.

### Q9. What is authorization?

**Answer:** Determining whether the authenticated user has permission to perform an operation.

### Q10. What is the difference between 401 and 403?

```text
401 → authentication is required / failed
403 → authenticated but insufficient permission
```

---

# 25. Final Mental Model

Think of the application as two gates:

```text
                REQUEST
                   |
                   v
          +----------------+
          |    TOMCAT      |
          +----------------+
                   |
                   v
        +---------------------+
        | SECURITY GATE       |
        | (if configured)     |
        +---------------------+
                   |
              allowed?
                   |
                   v
        +---------------------+
        | DISPATCHER SERVLET  |
        | MVC FRONT CONTROLLER|
        +---------------------+
                   |
                   v
             "Who handles
              this URL?"
                   |
                   v
             HandlerMapping
                   |
                   v
              Controller
```

**One sentence to memorize:**

> **Tomcat gets the request to the Servlet; Spring Security can filter the request before MVC; DispatcherServlet receives the MVC request and uses HandlerMapping to delegate it to the correct controller method.**


# Spring Security – Custom DB Authentication Flow

## 1. Why Custom Authentication?

In a real application, users are not usually hardcoded in Spring Security.

Instead, user information is stored in a database:

```text
USER TABLE

id    username    password       role
1     kali        $2a$...        USER
2     admin       $2b$...        ADMIN
3     john        $2a$...        USER
```

Spring Security needs a way to:

1. Receive the username and password.
2. Find the user from the database.
3. Convert the DB user into `UserDetails`.
4. Verify the entered password.
5. Create an authenticated `Authentication` object.

For this, we commonly customize `UserDetailsService`.

---

# 2. Overall Custom DB Flow

```text
Login Request
     |
     v
UsernamePasswordAuthenticationFilter
     |
     v
AuthenticationManager
     |
     v
AuthenticationProvider
     |
     v
UserDetailsService
     |
     v
UserRepository
     |
     v
Database
     |
     v
UserDetails
     |
     v
PasswordEncoder
     |
     v
Authentication SUCCESS
```

The important point is:

```text
AuthenticationManager
        |
        v
AuthenticationProvider
        |
        v
UserDetailsService
        |
        v
Repository
        |
        v
Database
```

---

# 3. Entity / Database User

Suppose our database contains:

```java
@Entity
public class User {

    @Id
    private Long id;

    private String username;

    private String password;

    private String role;

    // getters and setters
}
```

Example database:

```text
+----+----------+----------+-------+
| id | username | password | role  |
+----+----------+----------+-------+
| 1  | kali     | $2a$...  | USER  |
| 2  | admin    | $2a$...  | ADMIN |
| 3  | john     | $2a$...  | USER  |
+----+----------+----------+-------+
```

Passwords should normally be stored as encoded passwords, not plain text.

---

# 4. Repository

The repository is responsible for communicating with the database.

```java
public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
```

Now we can search:

```java
userRepository.findByUsername("kali");
```

This returns:

```text
Database
    |
    v
User object
```

---

# 5. CustomUserDetails

Spring Security does not directly work with our application's `User` entity.

It expects a `UserDetails` object.

Therefore, we can create our own implementation:

```java
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

The important conversion is:

```text
Database User
     |
     v
CustomUserDetails
     |
     v
Spring Security
```

---

# 6. CustomUserDetailsService

Now we create our custom `UserDetailsService`.

```java
@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "User not found"));

        return new CustomUserDetails(user);
    }
}
```

## Responsibility

`CustomUserDetailsService` has one main responsibility:

> Find the user and return the user as `UserDetails`.

It does NOT normally verify the password itself.

For example:

```text
loadUserByUsername("kali")
            |
            v
       Repository
            |
            v
        Database
            |
            v
       User object
            |
            v
   CustomUserDetails
```

---

# 7. AuthenticationProvider

For database username/password authentication, Spring Security commonly uses:

```java
DaoAuthenticationProvider
```

We configure it with our custom `UserDetailsService` and `PasswordEncoder`.

```java
@Bean
public AuthenticationProvider authenticationProvider(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {

    DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider();

    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);

    return provider;
}
```

The relationship is:

```text
DaoAuthenticationProvider
        |
        +------ UserDetailsService
        |
        +------ PasswordEncoder
```

---

# 8. PasswordEncoder

Example:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Suppose the user enters:

```text
password = "kali123"
```

Database contains:

```text
$2a$10$xxxxxxxxxxxxxxxxxxxxxxxx
```

The `PasswordEncoder` compares them.

Conceptually:

```text
Entered Password
       |
       v
PasswordEncoder
       |
       v
Compare with DB encoded password
       |
       +---- Match ----> SUCCESS
       |
       +---- No Match -> FAILURE
```

---

# 9. AuthenticationManager

`AuthenticationManager` coordinates authentication.

Conceptually:

```text
AuthenticationManager
        |
        v
AuthenticationProvider
```

The `AuthenticationManager` receives an authentication request and delegates it to the appropriate `AuthenticationProvider`.

For username/password authentication:

```text
AuthenticationManager
        |
        v
DaoAuthenticationProvider
```

Then:

```text
DaoAuthenticationProvider
        |
        v
CustomUserDetailsService
        |
        v
Database
```

---

# 10. Complete Configuration

A typical configuration can look like:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin()
                .and()
                .build();
    }
}
```

---

# 11. Multiple Users

A very important point:

We do NOT create a separate `UserDetailsService` for every user.

We create only one:

```text
CustomUserDetailsService
```

It handles all users.

For example:

```text
Database

kali
admin
john
```

When Kali logs in:

```text
username = kali
       |
       v
CustomUserDetailsService
       |
       v
findByUsername("kali")
       |
       v
Kali User
```

When Admin logs in:

```text
username = admin
       |
       v
CustomUserDetailsService
       |
       v
findByUsername("admin")
       |
       v
Admin User
```

When John logs in:

```text
username = john
       |
       v
CustomUserDetailsService
       |
       v
findByUsername("john")
       |
       v
John User
```

So the same service handles every user.

---

# 12. Complete Example – Kali Login

Suppose the database contains:

```text
username = kali
password = encodedPassword
role     = USER
```

Kali sends:

```text
username = kali
password = kali123
```

The flow is:

```text
1. Login Request
       |
       v
2. UsernamePasswordAuthenticationFilter
       |
       v
3. AuthenticationManager
       |
       v
4. DaoAuthenticationProvider
       |
       v
5. CustomUserDetailsService
       |
       v
6. UserRepository
       |
       v
7. Database
       |
       v
8. User found
       |
       v
9. CustomUserDetails
       |
       v
10. PasswordEncoder
       |
       v
11. Password matches
       |
       v
12. Authentication SUCCESS
```

---

# 13. What if User Does Not Exist?

Suppose:

```text
username = xyz
```

But the database does not contain `xyz`.

Then:

```text
CustomUserDetailsService
        |
        v
UserRepository
        |
        v
Database
        |
        v
User NOT FOUND
        |
        v
UsernameNotFoundException
        |
        v
Authentication FAILURE
```

---

# 14. What if Password Is Wrong?

Suppose:

```text
Username = kali
Password = wrongPassword
```

The user is found:

```text
Database
   |
   v
Kali User
```

Then the password is checked:

```text
Entered Password
       |
       v
PasswordEncoder
       |
       X
DB Password
```

If they don't match:

```text
Authentication Failure
```

---

# 15. What Each Component Does

| Component                   | Main Responsibility                                            |
| --------------------------- | -------------------------------------------------------------- |
| `AuthenticationManager`     | Coordinates authentication                                     |
| `AuthenticationProvider`    | Performs a particular authentication mechanism                 |
| `DaoAuthenticationProvider` | Username/password authentication using `UserDetailsService`    |
| `UserDetailsService`        | Loads user information                                         |
| `UserRepository`            | Retrieves user from database                                   |
| `Database`                  | Stores users                                                   |
| `CustomUserDetails`         | Converts application User into Spring Security's `UserDetails` |
| `PasswordEncoder`           | Verifies encoded password                                      |
| `UserDetails`               | Provides user information required by Spring Security          |

---

# 16. Most Important Interview Explanation

If asked:

**"How do you integrate Spring Security authentication with a database?"**

Answer:

```text
I implement UserDetailsService to load the user
from the database using UserRepository.

The User entity is converted into a UserDetails
object, usually through a custom UserDetails implementation.

I configure DaoAuthenticationProvider with my
CustomUserDetailsService and PasswordEncoder.

AuthenticationManager delegates the authentication
request to the AuthenticationProvider.

The provider loads the user through UserDetailsService
and verifies the submitted password using PasswordEncoder.

If the credentials are valid, authentication succeeds.
```

---

# 17. One-Line Mental Model

Remember this:

```text
AuthenticationManager
        ↓
AuthenticationProvider
        ↓
UserDetailsService
        ↓
Repository
        ↓
Database
```

And:

```text
UserDetailsService
      = "Find the user"

AuthenticationProvider
      = "Authenticate the user"

AuthenticationManager
      = "Coordinate the authentication"
```

## Final Flow

```text
                  LOGIN
                    |
                    v
     UsernamePasswordAuthenticationFilter
                    |
                    v
          AuthenticationManager
                    |
                    v
       DaoAuthenticationProvider
              /             \
             v               v
 UserDetailsService    PasswordEncoder
             |
             v
       UserRepository
             |
             v
          Database
             |
             v
          User
             |
             v
     CustomUserDetails
             |
             v
       Password Check
             |
       +-----+-----+
       |           |
     Match       No Match
       |           |
       v           v
   SUCCESS       FAILURE
```

**Key point:** For multiple users, there is still only **one `CustomUserDetailsService` and one `AuthenticationProvider`**. The username from the login request determines which database record is loaded.

# Spring Security – JWT Filter Login & Subsequent Request Flow

## 1. Big Picture

In JWT-based authentication, there are **two different flows**:

```text
1. LOGIN FLOW
   Username + Password
        ↓
   AuthenticationManager
        ↓
   AuthenticationProvider
        ↓
   UserDetailsService
        ↓
   Database
        ↓
   PasswordEncoder
        ↓
   Generate JWT


2. SUBSEQUENT REQUEST FLOW
   JWT
        ↓
   JWT Filter
        ↓
   Validate JWT
        ↓
   Create Authentication
        ↓
   SecurityContext
        ↓
   Authorization
        ↓
   Controller
```

The important difference is:

> `AuthenticationManager` is normally used during **login**, but it is **not required for validating an already-issued JWT**.

---

# 2. Login Flow

Suppose the client sends:

```http
POST /login

{
    "username": "kali",
    "password": "kali123"
}
```

We have a custom login method:

```java
public String verify(Users user) {

    Authentication authentication =
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword()
                    )
            );

    if (authentication.isAuthenticated()) {
        return jwtService.generateToken(user.getUsername());
    }

    return "Login failed";
}
```

The flow is:

```text
Client
  |
  | username + password
  v
/login
  |
  v
verify()
  |
  v
UsernamePasswordAuthenticationToken
  |
  v
AuthenticationManager
  |
  v
AuthenticationProvider
  |
  v
DaoAuthenticationProvider
  |
  v
CustomUserDetailsService
  |
  v
UserRepository
  |
  v
Database
  |
  v
UserDetails
  |
  v
PasswordEncoder
  |
  v
Authentication SUCCESS
  |
  v
jwtService.generateToken()
  |
  v
JWT returned to client
```

---

# 3. Why UsernamePasswordAuthenticationToken?

In the login code:

```java
new UsernamePasswordAuthenticationToken(
        user.getUsername(),
        user.getPassword()
)
```

This object represents the authentication request.

Conceptually:

```text
UsernamePasswordAuthenticationToken

username = kali
password = kali123
```

It does **not** authenticate the user by itself.

It simply carries the credentials to:

```java
authenticationManager.authenticate(...)
```

Then the `AuthenticationManager` delegates the request to an appropriate `AuthenticationProvider`.

---

# 4. AuthenticationManager → AuthenticationProvider

When we write:

```java
authenticationManager.authenticate(token);
```

the flow is:

```text
AuthenticationManager
        |
        v
AuthenticationProvider
        |
        v
DaoAuthenticationProvider
```

The provider was configured earlier in `SecurityConfig`.

Example:

```java
@Bean
public AuthenticationProvider authenticationProvider(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {

    DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider();

    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);

    return provider;
}
```

Important:

> `SecurityConfig` creates/configures the provider during application startup. It is not opened again during every login request.

Runtime:

```text
authenticationManager.authenticate()
              |
              v
     configured Provider
              |
              v
     DaoAuthenticationProvider
```

---

# 5. Provider → UserDetailsService → Database

`DaoAuthenticationProvider` needs user information.

Therefore it calls:

```java
userDetailsService.loadUserByUsername(username);
```

Our custom implementation:

```java
@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "User not found"));

        return new CustomUserDetails(user);
    }
}
```

Flow:

```text
DaoAuthenticationProvider
          |
          v
CustomUserDetailsService
          |
          v
UserRepository
          |
          v
Database
          |
          v
User
          |
          v
CustomUserDetails
```

---

# 6. Password Validation

After loading the user, the provider verifies the password using:

```java
PasswordEncoder
```

Conceptually:

```text
Entered Password
      |
      v
PasswordEncoder
      |
      | compare
      v
Encoded Password from DB
```

If the passwords match:

```text
Authentication SUCCESS
```

If they don't:

```text
Authentication FAILURE
```

---

# 7. Generate JWT

After successful authentication:

```java
if (authentication.isAuthenticated()) {
    return jwtService.generateToken(user.getUsername());
}
```

Now the application generates a JWT.

```text
Username + Password
        |
        v
Authentication SUCCESS
        |
        v
Generate JWT
        |
        v
Return JWT to Client
```

Example response:

```text
eyJhbGciOiJIUzI1NiJ9...
```

The client stores the token and sends it with subsequent requests.

---

# 8. Subsequent Request

Now suppose the user wants:

```http
GET /products
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

The user does **not** send the username and password again.

Instead:

```text
Client
   |
   | Bearer JWT
   v
Server
```

---

# 9. JWT Filter

A custom JWT filter is usually placed in the Spring Security filter chain.

Conceptually:

```text
HTTP Request
     |
     v
JWT Filter
     |
     v
Extract Authorization Header
     |
     v
Extract JWT
     |
     v
Validate JWT
```

Example:

```java
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader != null &&
            authHeader.startsWith("Bearer ")) {

            String token =
                    authHeader.substring(7);

            String username =
                    jwtService.extractUsername(token);

            if (username != null &&
                jwtService.validateToken(token)) {

                UserDetails userDetails = ...;

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

# 10. Important: AuthenticationManager Is NOT Required Here

This is the key concept.

During login:

```text
Username + Password
       ↓
AuthenticationManager
       ↓
AuthenticationProvider
       ↓
Database
```

But during a subsequent JWT request:

```text
JWT
 ↓
JWT Filter
 ↓
JWT Validation
 ↓
Authentication object
 ↓
SecurityContext
```

You normally do **not** do:

```java
authenticationManager.authenticate(...)
```

inside the JWT filter just to validate the JWT.

Why?

Because the user has already authenticated during login.

The JWT is now being used as the user's credential/token.

---

# 11. JWT Validation

The JWT filter extracts the token:

```java
String token =
        authHeader.substring(7);
```

Then the JWT service validates it.

Conceptually:

```text
JWT
 |
 +---- Signature valid?
 |
 +---- Token expired?
 |
 +---- Claims valid?
 |
 +---- Username extracted?
 |
 v
VALID
```

If valid:

```text
JWT Valid
   |
   v
Create Authentication
```

---

# 12. Creating Authentication in JWT Filter

This is an important difference from login.

During login, we create:

```java
new UsernamePasswordAuthenticationToken(
    username,
    password
)
```

This represents:

> "Please authenticate these credentials."

During JWT validation, we create:

```java
new UsernamePasswordAuthenticationToken(
    userDetails,
    null,
    userDetails.getAuthorities()
)
```

This represents:

> "This user has already been authenticated using the valid JWT."

So:

```text
LOGIN

UsernamePasswordAuthenticationToken
(username, password)
        ↓
AuthenticationManager
```

Whereas:

```text
SUBSEQUENT REQUEST

UsernamePasswordAuthenticationToken
(userDetails, null, authorities)
        ↓
SecurityContext
```

The same class can be used for different stages, but the meaning/state is different.

---

# 13. SecurityContext

After validating the JWT, the filter sets the authentication:

```java
SecurityContextHolder
        .getContext()
        .setAuthentication(authentication);
```

Now Spring Security knows:

```text
Current User = kali
Authorities  = ROLE_USER
Authenticated = true
```

Conceptually:

```text
JWT
 ↓
JWT Filter
 ↓
Validate
 ↓
Authentication Object
 ↓
SecurityContext
```

---

# 14. Continue the Filter Chain

Finally:

```java
filterChain.doFilter(request, response);
```

This allows the request to continue through the remaining filters.

Eventually it can reach the controller.

```text
JWT Filter
    |
    v
SecurityContext
    |
    v
Authorization
    |
    v
Controller
```

---

# 15. Complete JWT Login + Subsequent Flow

## First Request – Login

```text
             LOGIN
               |
               v
      Username + Password
               |
               v
            verify()
               |
               v
UsernamePasswordAuthenticationToken
               |
               v
    AuthenticationManager
               |
               v
    AuthenticationProvider
               |
               v
    DaoAuthenticationProvider
               |
               v
    CustomUserDetailsService
               |
               v
        UserRepository
               |
               v
           Database
               |
               v
          UserDetails
               |
               v
        PasswordEncoder
               |
               v
       Authentication
          SUCCESS
               |
               v
      Generate JWT
               |
               v
        Return JWT
```

---

# 16. Second Request – JWT

```text
           GET /products
                 |
                 |
        Authorization: Bearer JWT
                 |
                 v
             JWT Filter
                 |
                 v
          Extract JWT
                 |
                 v
        Validate JWT
                 |
                 v
        Extract Username
                 |
                 v
      Create Authentication
                 |
                 v
         SecurityContext
                 |
                 v
           Authorization
                 |
                 v
            Controller
```

---

# 17. Does JWT Filter Need UserDetailsService?

This depends on the implementation.

A common implementation does:

```text
JWT
 ↓
Extract username
 ↓
UserDetailsService
 ↓
Load user
 ↓
Create Authentication
 ↓
SecurityContext
```

So:

```text
JWT Filter
    |
    v
JwtService
    |
    v
extractUsername()
    |
    v
CustomUserDetailsService
    |
    v
Database
```

However, the JWT itself can contain claims such as username and authorities, and an application can choose to construct the authenticated principal from trusted validated claims instead.

Therefore:

> `UserDetailsService` may be used during JWT requests, but `AuthenticationManager` does not have to be.

---

# 18. Login vs Subsequent Request

|                         | Login                                   | Subsequent Request                    |
| ----------------------- | --------------------------------------- | ------------------------------------- |
| Credential              | Username + Password                     | JWT                                   |
| Custom login controller | Usually yes                             | No                                    |
| JWT Filter              | Not necessarily responsible for login   | Yes                                   |
| AuthenticationManager   | Yes                                     | Usually no                            |
| AuthenticationProvider  | Yes                                     | Usually no                            |
| UserDetailsService      | Yes                                     | Optional, depending on implementation |
| PasswordEncoder         | Yes                                     | No                                    |
| JWT validation          | Generate JWT                            | Validate JWT                          |
| SecurityContext         | Created after successful authentication | Populated by JWT filter               |
| Database                | Usually queried                         | Optional, depending on implementation |

---

# 19. Most Important Mental Model

Remember these two flows separately.

### Login

```text
USERNAME + PASSWORD
        ↓
AuthenticationManager
        ↓
AuthenticationProvider
        ↓
UserDetailsService
        ↓
Database
        ↓
PasswordEncoder
        ↓
SUCCESS
        ↓
JWT
```

### Subsequent Request

```text
JWT
 ↓
JWT Filter
 ↓
Validate JWT
 ↓
Create Authentication
 ↓
SecurityContext
 ↓
Authorization
 ↓
Controller
```

## Final Key Point

> **AuthenticationManager is used to authenticate the username/password during login.**

> **JWT Filter validates the already-issued JWT for subsequent requests and establishes the Authentication in the SecurityContext.**

Therefore:

```text
LOGIN
AuthenticationManager → AuthenticationProvider
```

but:

```text
SUBSEQUENT REQUEST
JWT Filter → JWT Validation → SecurityContext
```

The JWT filter does **not normally need to call `AuthenticationManager` just to validate the token**.


# Authorization Architecture — Spring Security

## 1. What is Authorization?

**Authorization** means checking:

> "Is this authenticated user allowed to access this resource?"

Authentication answers:

> "Who are you?"

Authorization answers:

> "What are you allowed to access?"

---

# 2. Authorization Flow

The basic Spring Security authorization flow is:

```text
Request
   |
   v
SecurityFilterChain
   |
   v
Authentication
   |
   | Authentication successful
   v
AuthorizationFilter
   |
   v
AuthorizationManager
   |
   v
Authorization Decision
   |
   +----------------------+
   |                      |
   | YES                  | NO
   v                      v
Request continues     AccessDeniedException
   |                      |
   v                      v
DispatcherServlet     Exception handling
   |                      |
   v                      v
Controller             403 Forbidden



# Spring Security Method Security

## Enable Method Security

To use method-level authorization:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
}
```

`@EnableMethodSecurity` enables:

* `@PreAuthorize`
* `@PostAuthorize`
* `@PreFilter`
* `@PostFilter`

---

## 1. @PreAuthorize

Checks authorization **before the method executes**.

```java
@GetMapping("/")
@PreAuthorize("hasAuthority('USER_READ')")
public List<Student> getStudents() {
    return studentService.getStudents();
}
```

Flow:

```text
Request
   ↓
@PreAuthorize
   ↓
Allowed?
   ↓ YES
Method executes
```

If not allowed → `403 Forbidden`.

---

## 2. @PostAuthorize

Checks authorization **after the method executes**.

Useful when authorization depends on the **returned object**.

```java
@GetMapping("/{id}")
@PostAuthorize("returnObject.username == authentication.name")
public Student getStudent(@PathVariable int id) {
    return studentService.getStudent(id);
}
```

Flow:

```text
Request
   ↓
Method executes
   ↓
Object returned
   ↓
@PostAuthorize
   ↓
Allow / 403
```

`returnObject` = object returned by the method.

---

## 3. @PreFilter

Filters a **collection input before the method executes**.

```java
@PreFilter("filterObject.username == authentication.name")
public void processStudents(List<Student> students) {
    // Only allowed students reach this method
}
```

```text
Input List
   ↓
@PreFilter
   ↓
Filtered List
   ↓
Method
```

---

## 4. @PostFilter

Filters a **collection returned by the method**.

```java
@GetMapping("/")
@PostFilter("filterObject.username == authentication.name")
public List<Student> getStudents() {
    return studentService.getStudents();
}
```

```text
Method
   ↓
Full List
   ↓
@PostFilter
   ↓
Filtered List
   ↓
Response
```

`filterObject` = current object being checked in the collection.

---

## Quick Difference

| Annotation       | When          | Purpose                    |
| ---------------- | ------------- | -------------------------- |
| `@PreAuthorize`  | Before method | Allow/Deny method          |
| `@PostAuthorize` | After method  | Allow/Deny returned object |
| `@PreFilter`     | Before method | Filter input collection    |
| `@PostFilter`    | After method  | Filter returned collection |

### Easy Memory Trick

```text
PRE  → Before method
POST → After method

AUTHORIZE → Allow / Deny
FILTER    → Remove objects
```
