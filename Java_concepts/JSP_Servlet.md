# Java Servlet, JSP & Apache Tomcat — Exam Preparation Notes

## 1. Apache Tomcat

### Definition

**Apache Tomcat** is an open-source Java **web server and Servlet container**.

It is mainly used to run Java web applications that use technologies such as:

* Servlets
* JSP
* Jakarta Servlet APIs

### What does Tomcat do?

When a client sends an HTTP request:

```text
Client / Browser
       |
       | HTTP Request
       v
 Apache Tomcat
       |
       | Finds the appropriate Servlet
       v
   Servlet
       |
       | Business/Application processing
       v
   Response
       |
       v
 Apache Tomcat
       |
       | HTTP Response
       v
Client / Browser
```

### Important Point

Tomcat itself does **not normally contain your business logic**.

Its main responsibilities include:

1. Accept HTTP requests.
2. Determine which web application should handle the request.
3. Manage the Servlet lifecycle.
4. Invoke the appropriate Servlet.
5. Send the HTTP response back to the client.

### Why is Tomcat called a Servlet Container?

Because Tomcat provides the environment in which **Servlets are created, initialized, executed, and destroyed**.

```text
Tomcat
  |
  +-- Servlet Container
       |
       +-- Servlet 1
       +-- Servlet 2
       +-- Servlet 3
```

### Exam Answer

> **Apache Tomcat is a web server and Servlet container used to deploy and execute Java web applications. It receives HTTP requests, maps them to the appropriate Servlet, manages the Servlet lifecycle, and returns HTTP responses to clients.**

---

# 2. Servlet

## Definition

A **Servlet** is a Java class used to handle **HTTP requests and responses** on the server.

It acts as a bridge between:

```text
Client
   |
 HTTP Request
   |
   v
Servlet
   |
Application Logic
   |
   v
HTTP Response
   |
   v
Client
```

### Example

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.getWriter().println("Hello World");
    }
}
```

If the client sends:

```http
GET /hello
```

Tomcat finds:

```java
@WebServlet("/hello")
```

and invokes the Servlet.

---

# 3. Servlet HTTP Methods

A Servlet can handle different HTTP methods.

| HTTP Method | Servlet Method                               |
| ----------- | -------------------------------------------- |
| GET         | `doGet()`                                    |
| POST        | `doPost()`                                   |
| PUT         | `doPut()`                                    |
| DELETE      | `doDelete()`                                 |
| PATCH       | Usually handled manually/framework-dependent |

### Example

```java
@Override
protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response) {

    // Handle GET request
}
```

```java
@Override
protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response) {

    // Handle POST request
}
```

### Memory Trick

```text
GET     → doGet()
POST    → doPost()
PUT     → doPut()
DELETE  → doDelete()
```

---

# 4. Servlet Lifecycle

The Servlet lifecycle is one of the **most important exam topics**.

The basic lifecycle is:

```text
       Servlet Class
            |
            v
         init()
       (Once only)
            |
            v
         service()
      (Many requests)
            |
            v
        destroy()
       (Once only)
```

## Step 1 — `init()`

`init()` is called when the Servlet is initialized.

It is normally called **only once** during the Servlet's lifecycle.

```java
@Override
public void init() {
    System.out.println("Servlet initialized");
}
```

Typical use:

* Initialization
* Loading configuration
* Creating resources

---

## Step 2 — `service()`

`service()` is called for incoming requests.

It can be called **many times**.

```java
@Override
protected void service(
        HttpServletRequest request,
        HttpServletResponse response) {

    // Handle request
}
```

For `HttpServlet`, the service mechanism determines the appropriate HTTP method handler such as:

```text
GET    → doGet()
POST   → doPost()
PUT    → doPut()
DELETE → doDelete()
```

---

## Step 3 — `destroy()`

When the Servlet is being removed from service, Tomcat calls:

```java
@Override
public void destroy() {
    System.out.println("Servlet destroyed");
}
```

It is normally called **once**.

Typical use:

* Closing resources
* Cleanup
* Releasing connections

---

## Complete Lifecycle Example

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    public void init() {
        System.out.println("INIT");
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        System.out.println("GET REQUEST");

        response.getWriter().println("Hello");
    }

    @Override
    public void destroy() {
        System.out.println("DESTROY");
    }
}
```

Execution:

```text
Tomcat starts Servlet
        |
        v
      init()
        |
        v
     doGet()
        |
        v
     doGet()
        |
        v
     doGet()
        |
        v
     destroy()
```

### Important Exam Point

Do **not** say:

> `service()` runs only once.

Instead:

> `init()` is called once, `service()` handles requests repeatedly, and `destroy()` is called once when the Servlet is taken out of service.

---

# 5. Servlet Configuration

There are two common ways to configure/map a Servlet.

## Method 1 — Annotation

Modern applications commonly use annotations.

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
}
```

Here:

```text
/hello
```

is mapped to:

```text
HelloServlet
```

Request:

```http
GET /hello
```

Tomcat → `HelloServlet`

---

# 6. Method 2 — `web.xml`

Servlets can also be configured using the deployment descriptor:

```text
WEB-INF/web.xml
```

Example:

```xml
<servlet>
    <servlet-name>HelloServlet</servlet-name>
    <servlet-class>com.example.HelloServlet</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>HelloServlet</servlet-name>
    <url-pattern>/hello</url-pattern>
</servlet-mapping>
```

### Comparison

| Annotation                      | `web.xml`                           |
| ------------------------------- | ----------------------------------- |
| Configuration inside Java class | External configuration              |
| Easy and concise                | More verbose                        |
| `@WebServlet`                   | `<servlet>` + `<servlet-mapping>`   |
| Common in modern applications   | Common in older/legacy applications |

### Exam Question

**Q: How can you configure a Servlet?**

**Answer:**

> A Servlet can be configured using annotations such as `@WebServlet` or through the deployment descriptor `web.xml`.

---

# 7. RequestDispatcher

## Definition

`RequestDispatcher` is an interface that allows a Servlet to **forward a request to another resource or include another resource's response**.

The resource can be:

* Another Servlet
* JSP
* HTML/resource depending on the environment

Example:

```java
RequestDispatcher dispatcher =
        request.getRequestDispatcher("/second");

dispatcher.forward(request, response);
```

There are two important methods:

```text
RequestDispatcher
       |
       +---- forward()
       |
       +---- include()
```

---

# 8. `forward()`

`forward()` transfers the request from one server-side resource to another.

Example:

```text
Client
  |
  | Request
  v
Servlet 1
  |
  | forward()
  v
Servlet 2
  |
  | Response
  v
Client
```

### Example

Servlet 1:

```java
RequestDispatcher dispatcher =
        request.getRequestDispatcher("/servlet2");

dispatcher.forward(request, response);
```

Servlet 2:

```java
response.getWriter().println("Response from Servlet 2");
```

The final response is generated by Servlet 2.

### Important Point

The forwarding happens **inside the server**.

The browser does not make a new request to Servlet 2.

Conceptually:

```text
Browser
   |
   | Request
   v
Servlet 1
   |
   | Server-side forward
   v
Servlet 2
   |
   v
Browser
```

---

# 9. `include()`

`include()` allows one resource to include the output generated by another resource in the current response.

Example:

```java
RequestDispatcher dispatcher =
        request.getRequestDispatcher("/header");

dispatcher.include(request, response);

response.getWriter().println("Main Content");
```

Conceptually:

```text
Servlet 1
   |
   +---- include() → Servlet 2
   |                    |
   |                    v
   |              Servlet 2 output
   |
   +---- Servlet 1 output
            |
            v
       Combined Response
            |
            v
          Client
```

### Example

Servlet 1:

```java
response.getWriter().println("Main Page");

RequestDispatcher dispatcher =
        request.getRequestDispatcher("/header");

dispatcher.include(request, response);
```

Servlet 2:

```java
response.getWriter().println("Header");
```

The response can contain output from both resources.

---

# 10. `forward()` vs `include()`

| Feature                                 | `forward()`                                  | `include()`                  |
| --------------------------------------- | -------------------------------------------- | ---------------------------- |
| Purpose                                 | Transfer processing                          | Include output               |
| Original resource continues processing? | Generally no after successful forward        | Yes                          |
| Response                                | Target resource generates the final response | Output is combined           |
| Common use                              | Controller → JSP                             | Header/footer/common content |
| Server-side?                            | Yes                                          | Yes                          |

### Memory Trick

```text
FORWARD = "Go there"

INCLUDE = "Bring that here"
```

---

# 11. JSP

## Definition

**JSP = JavaServer Pages**

JSP is a **server-side view technology** used to generate dynamic web pages.

It is primarily used for the **presentation/view layer**.

Example:

```jsp
<html>
<body>

<h1>Welcome ${userName}</h1>

</body>
</html>
```

The server processes the JSP and sends the generated HTML to the browser.

```text
Browser
   |
   | HTTP Request
   v
Tomcat
   |
   v
JSP
   |
   | Server-side processing
   v
HTML
   |
   v
Browser
```

---

# 12. Is JSP Similar to Next.js?

There is a **conceptual similarity**, but they are not the same technology.

You can think of it like this:

```text
Java ecosystem
       |
      JSP
       |
Server-side HTML generation
```

versus:

```text
JavaScript ecosystem
       |
    Next.js
       |
Server-side rendering / web application framework
```

However, **JSP is a view technology**, while **Next.js is a much broader React-based web framework**.

So don't say in an exam:

> JSP is the Java equivalent of Next.js.

Better answer:

> JSP is a Java/Jakarta server-side view technology used to dynamically generate HTML on the server. It is conceptually similar to server-side rendering technologies, but it is not equivalent to Next.js.

---

# 13. JSP Syntax

There are several JSP elements.

## Scriptlet

Older JSP syntax:

```jsp
<%
    String name = "John";
%>
```

### Important

The syntax is:

```text
<% ... %>
```

**Not:**

```text
<% $>
```

---

## JSP Expression

Used to directly output a value:

```jsp
<%= name %>
```

Example:

```jsp
<h1>Hello <%= name %></h1>
```

---

## JSP Declaration

```jsp
<%!
    int count = 0;
%>
```

Declarations define fields/methods at the generated Servlet level.

---

# 14. JSP and Servlet Relationship

This is a **very important exam concept**.

A JSP is ultimately translated/compiled into a **Servlet** by the JSP container.

Conceptually:

```text
JSP File
  |
  | Translation
  v
Servlet Java Source
  |
  | Compilation
  v
Servlet Class
  |
  v
Execution
  |
  v
HTML Response
```

Therefore:

> **JSP is not completely separate from Servlets. A JSP is translated into a Servlet by the container.**

---

# 15. JSP Request Flow

Suppose the client requests:

```http
GET /home.jsp
```

The flow is approximately:

```text
Browser
   |
   | GET /home.jsp
   v
Tomcat
   |
   v
JSP Container
   |
   | JSP → Servlet
   v
Generated Servlet
   |
   | Execute
   v
HTML Response
   |
   v
Browser
```

---

# 16. Servlet + JSP MVC Architecture

A common traditional Java web application architecture is:

```text
                Client
                  |
                  | HTTP Request
                  v
              Servlet
             Controller
                  |
                  v
              Service
            Business Logic
                  |
                  v
             Repository
                  |
                  v
              Database

                  |
                  | Data
                  v

              Servlet
                  |
                  | forward()
                  v
                 JSP
                 View
                  |
                  | HTML
                  v
               Client
```

### Responsibility

| Component      | Responsibility                 |
| -------------- | ------------------------------ |
| Servlet        | Controller / request handling  |
| Service        | Business logic                 |
| Repository/DAO | Database operations            |
| JSP            | Presentation/View              |
| Tomcat         | Web server + Servlet container |

---

# 17. Important Correction: Servlet vs Business Logic

Your original note says:

> Servlet will serve as HTTP request/response handler. This is the business logic area.

The first part is correct, but the second part needs correction.

A Servlet should primarily act as a **controller/request handler**.

Avoid putting all business logic directly inside the Servlet.

### Bad architecture

```java
protected void doGet(...) {

    // HTTP handling

    // Database logic
    // Business calculations
    // Validation
    // Everything here
}
```

### Better architecture

```text
Servlet
   |
   | Request handling
   v
Service
   |
   | Business logic
   v
Repository / DAO
   |
   | Database access
   v
Database
```

This follows separation of concerns.

---

# 18. Tomcat vs Servlet vs JSP

This is one of the most important comparisons.

| Component         | What is it?                    | Main responsibility             |
| ----------------- | ------------------------------ | ------------------------------- |
| Tomcat            | Web server + Servlet container | Runs Java web applications      |
| Servlet           | Java class                     | Handles HTTP requests/responses |
| JSP               | Server-side view technology    | Generates dynamic HTML          |
| RequestDispatcher | Servlet API interface          | Forward/include resources       |

### Easy Memory Trick

```text
Tomcat = Container
Servlet = Controller
JSP = View
```

---

# 19. Complete Example

Imagine we have:

```text
GET /users/101
```

### Step 1 — Browser sends request

```text
Browser
   |
   | GET /users/101
   v
Tomcat
```

### Step 2 — Tomcat finds Servlet

```java
@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
```

Tomcat routes the request to:

```text
UserServlet
```

### Step 3 — Servlet handles request

```java
protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    String userId = request.getPathInfo();

    User user = userService.getUser(userId);

    request.setAttribute("user", user);

    RequestDispatcher dispatcher =
            request.getRequestDispatcher("/user.jsp");

    dispatcher.forward(request, response);
}
```

### Step 4 — JSP displays the data

```jsp
<html>
<body>

<h1>User Details</h1>

<p>Name: ${user.name}</p>
<p>Email: ${user.email}</p>

</body>
</html>
```

### Complete flow

```text
                    HTTP Request
Browser --------------------------------> Tomcat
                                             |
                                             v
                                      UserServlet
                                             |
                                             v
                                       UserService
                                             |
                                             v
                                        Database
                                             |
                                             | User data
                                             v
                                      UserServlet
                                             |
                                      forward()
                                             |
                                             v
                                          JSP
                                             |
                                          HTML
                                             |
                                             v
Browser <-------------------------------- Tomcat
```

---

# 20. Important Exam Questions

## Q1. What is Tomcat?

> Apache Tomcat is a web server and Servlet container used to deploy and run Java web applications. It receives HTTP requests, manages Servlets, and sends HTTP responses back to clients.

---

## Q2. What is a Servlet?

> A Servlet is a Java class that runs on the server and handles client requests and generates responses, commonly for HTTP-based web applications.

---

## Q3. Explain Servlet lifecycle.

> The Servlet lifecycle consists mainly of `init()`, request processing through `service()` and HTTP-specific methods such as `doGet()`/`doPost()`, and `destroy()`. `init()` and `destroy()` are normally called once, while request processing can occur many times.

### Memory:

```text
INIT → SERVICE → DESTROY
Once → Many   → Once
```

---

## Q4. What is JSP?

> JSP, or JavaServer Pages, is a server-side view technology used to dynamically generate HTML. A JSP is translated into a Servlet by the container before execution.

---

## Q5. What is RequestDispatcher?

> RequestDispatcher is a Servlet API interface used to transfer control to another server-side resource using `forward()` or include another resource's output using `include()`.

---

## Q6. Difference between `forward()` and `include()`?

> `forward()` transfers processing to another resource, whereas `include()` incorporates the output of another resource into the current response.

### Memory:

```text
forward() → Go there
include() → Bring it here
```

---

# 21. One-Minute Revision

```text
Tomcat
  ↓
Web Server + Servlet Container

Servlet
  ↓
HTTP Request/Response Handler

Servlet Lifecycle
  ↓
init() → service()/doGet()/doPost() → destroy()
   1              MANY                    1

JSP
  ↓
Server-side View Technology
  ↓
JSP → Servlet → HTML

RequestDispatcher
  ↓
forward() → Transfer control
include() → Include output

Configuration
  ↓
@WebServlet
OR
web.xml

MVC
  ↓
Servlet → Controller
Service → Business Logic
Repository → Database
JSP → View
Tomcat → Container
```

# 22. Golden Rules for the Exam

1. **Tomcat is a web server + Servlet container.**
2. **Servlet handles HTTP requests/responses.**
3. **Don't put all business logic inside the Servlet.**
4. **`init()` → once.**
5. **Request processing → many times.**
6. **`destroy()` → once.**
7. **Servlet mapping can use `@WebServlet` or `web.xml`.**
8. **`RequestDispatcher.forward()` transfers control.**
9. **`RequestDispatcher.include()` includes another resource's output.**
10. **JSP is primarily used for the View/presentation layer.**
11. **JSP is translated into a Servlet by the container.**
12. **JSP syntax for a scriptlet is `<% ... %>`.**
13. **JSP expression is `<%= ... %>`.**
14. **Tomcat runs/manages Servlets; it is not your business-logic layer.**

# Final Memory Diagram

```text
                         CLIENT
                           |
                           | HTTP Request
                           v
                    +---------------+
                    |    TOMCAT     |
                    | Web Server +  |
                    |   Container   |
                    +-------+-------+
                            |
                            v
                       +---------+
                       | Servlet |
                       |Controller|
                       +----+----+
                            |
                            v
                       +---------+
                       | Service |
                       | Business|
                       |  Logic  |
                       +----+----+
                            |
                            v
                       +---------+
                       |   DAO   |
                       |Database |
                       +---------+

                            |
                            | Data
                            v
                       +---------+
                       | Servlet |
                       +----+----+
                            |
                       forward()
                            |
                            v
                       +---------+
                       |   JSP   |
                       |  View   |
                       +----+----+
                            |
                          HTML
                            |
                            v
                         CLIENT
```

### Super-short memory trick

**Tomcat = Container**
**Servlet = Controller**
**Service = Business Logic**
**DAO = Database**
**JSP = View**
**RequestDispatcher = Forward/Include**
