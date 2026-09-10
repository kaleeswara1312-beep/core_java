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