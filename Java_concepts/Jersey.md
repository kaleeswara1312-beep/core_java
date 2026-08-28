# Jersey REST API – Concepts, Flow, and Servlet Relationship

## 1. What is Jersey?

**Jersey** is an implementation of the **JAX-RS (Jakarta RESTful Web Services)** specification.

It is used to build **REST APIs in Java**.

Instead of manually handling HTTP requests using Servlet APIs such as:

```java
HttpServletRequest
HttpServletResponse
doGet()
doPost()
```

Jersey allows us to define REST endpoints using annotations:

```java
@Path("/users")
public class UserResource {

    @GET
    public List<User> getUsers() {
        // ...
    }
}
```

Jersey takes care of REST-related tasks such as:

* URL routing
* HTTP method mapping
* Path parameters
* Query parameters
* Request body conversion
* Response body conversion
* JSON/XML support
* Content negotiation
* REST resource management

---

# 2. Jersey vs Servlet

The most important concept is:

> **Jersey does not replace the Servlet container. Jersey runs on top of the Servlet infrastructure.**

Think of the architecture as:

```text
Client
   |
   | HTTP Request
   v
Tomcat
   |
   | Servlet Container
   v
Jersey Servlet
   |
   | REST Routing
   v
Resource Class
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

### Servlet

A Servlet is a Java component that handles HTTP requests and responses.

Example:

```java
@WebServlet("/users")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) {

        response.getWriter().write("Users");
    }
}
```

### Jersey

Jersey provides a higher-level REST abstraction.

Instead of:

```java
doGet()
doPost()
HttpServletRequest
HttpServletResponse
```

we can use:

```java
@GET
@POST
@Path()
@PathParam()
@QueryParam()
@Produces()
@Consumes()
```

---

# 3. Why do we need Jersey?

Pure Servlet code can certainly create REST APIs.

For example:

```java
@WebServlet("/users")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) {

        // Handle GET
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) {

        // Handle POST
    }
}
```

This works.

However, as the application grows, manually handling REST routing becomes complicated.

Imagine having:

```text
GET     /users
GET     /users/{id}
POST    /users
PUT     /users/{id}
DELETE  /users/{id}

GET     /products
GET     /products/{id}
POST    /products
PUT     /products/{id}
DELETE  /products/{id}
```

With raw Servlet code, we may need to manually handle:

* HTTP methods
* URL parsing
* Path parameters
* Request body parsing
* JSON conversion
* Response formatting
* Routing logic

Jersey provides this REST infrastructure for us.

---

# 4. Pure Servlet Routing

With a Servlet, Tomcat can map a URL directly to a Servlet.

Example:

```java
@WebServlet("/users")
public class UserServlet extends HttpServlet {
}
```

The flow is:

```text
Client
   |
   | GET /users
   v
Tomcat
   |
   | Servlet mapping
   v
UserServlet
   |
   v
doGet()
```

Tomcat knows:

```text
/users → UserServlet
```

---

# 5. Jersey Routing

With Jersey, Tomcat first sends the request to the **Jersey Servlet**.

Example:

```xml
<servlet>
    <servlet-name>JerseyServlet</servlet-name>

    <servlet-class>
        org.glassfish.jersey.servlet.ServletContainer
    </servlet-class>

    <init-param>
        <param-name>
            jersey.config.server.provider.packages
        </param-name>

        <param-value>com.example</param-value>
    </init-param>
</servlet>

<servlet-mapping>
    <servlet-name>JerseyServlet</servlet-name>
    <url-pattern>/api/*</url-pattern>
</servlet-mapping>
```

The important part is:

```xml
<url-pattern>/api/*</url-pattern>
```

This tells Tomcat:

> Any request starting with `/api/` should be sent to JerseyServlet.

---

# 6. Two Levels of Routing

This is one of the most important concepts.

There are **two routing layers**.

## Layer 1 – Tomcat / Servlet Routing

Tomcat checks:

```text
/api/*
```

and decides:

```text
/api/* → JerseyServlet
```

## Layer 2 – Jersey REST Routing

Jersey then checks:

```java
@Path("/users")
```

and:

```java
@GET
```

and decides:

```text
GET /users → UserResource.getUsers()
```

Therefore:

```text
Client
   |
   | /api/users
   v
Tomcat
   |
   | /api/*
   v
JerseyServlet
   |
   | /users + GET
   v
UserResource.getUsers()
```

---

# 7. Complete Request Flow

Suppose the client sends:

```http
GET http://localhost:8080/jersey-rest-api/api/users
```

The complete flow is:

```text
                 CLIENT
                    |
                    | HTTP GET
                    v
                 TOMCAT
                    |
                    | Servlet Mapping
                    | /api/*
                    v
             JERSEY SERVLET
                    |
                    | Jersey Routing
                    v
             USER RESOURCE
                    |
                    | @GET
                    v
              USER SERVICE
                    |
                    v
          MOCK USER REPOSITORY
                    |
                    v
             IN-MEMORY LIST
```

The response travels back:

```text
In-Memory List
      |
      v
Repository
      |
      v
Service
      |
      v
Resource
      |
      v
Jersey
      |
      v
Tomcat
      |
      v
Client
```

---

# 8. What is JerseyServlet?

Jersey provides a Servlet called:

```java
org.glassfish.jersey.servlet.ServletContainer
```

This is the **entry point from the Servlet container into Jersey**.

Conceptually:

```text
Tomcat
   |
   | HTTP request
   v
ServletContainer
   |
   v
Jersey Framework
   |
   v
REST Resource
```

Therefore:

> **JerseyServlet acts as the bridge between Tomcat's Servlet infrastructure and Jersey's REST framework.**

---

# 9. Resource Class

A Jersey REST resource can look like:

```java
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public Response getUsers() {
        // ...
    }

    @POST
    public Response createUser(User user) {
        // ...
    }
}
```

The annotations tell Jersey how to route requests.

For example:

```java
@Path("/users")
@GET
```

means:

```text
GET /users
```

And:

```java
@Path("/users")
@POST
```

means:

```text
POST /users
```

---

# 10. Path Parameters

Jersey can also handle dynamic URL values.

Example:

```java
@GET
@Path("/{id}")
public Response getUser(
        @PathParam("id") int id) {

    // ...
}
```

Request:

```http
GET /api/users/10
```

Jersey performs:

```text
/api/users/10
       |
       v
/users/{id}
       |
       v
id = 10
       |
       v
getUser(10)
```

We don't need to manually parse the URL.

---

# 11. JSON Conversion

Suppose the client sends:

```json
{
    "id": 3,
    "name": "Kale",
    "email": "kale@gmail.com"
}
```

and the resource has:

```java
@POST
public Response createUser(User user) {
    // ...
}
```

Jersey, with a JSON provider such as Jackson, can convert:

```text
JSON
  |
  v
Jackson
  |
  v
User Java Object
```

So the method receives:

```java
User user
```

instead of requiring us to manually read the request body.

Similarly, if the resource returns:

```java
User
```

Jersey/Jackson can convert:

```text
User Java Object
       |
       v
     JSON
       |
       v
HTTP Response
```

---

# 12. @Produces and @Consumes

### @Produces

Defines the response representation.

```java
@Produces(MediaType.APPLICATION_JSON)
```

Means:

> This resource produces JSON.

### @Consumes

Defines the request representation.

```java
@Consumes(MediaType.APPLICATION_JSON)
```

Means:

> This resource accepts JSON requests.

Example:

```java
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
}
```

---

# 13. Jersey Configuration

We can configure Jersey using:

```java
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        packages("com.example");
    }
}
```

This tells Jersey:

```text
Scan com.example
      |
      v
Find JAX-RS resources
      |
      v
@Path("/users")
@Path("/products")
@Path("/orders")
      |
      v
Register them
```

---

# 14. Why `/api/*` and `/users` are both present

This can initially look confusing.

We have:

```xml
<url-pattern>/api/*</url-pattern>
```

and:

```java
@Path("/users")
```

They serve different purposes.

### `/api/*`

Used by **Tomcat**.

It means:

```text
Which Servlet should receive the request?
```

Answer:

```text
JerseyServlet
```

### `/users`

Used by **Jersey**.

It means:

```text
Which REST resource should handle the request?
```

Answer:

```text
UserResource
```

Therefore:

```text
/api/users
```

is effectively processed as:

```text
/api/*
   ↓
JerseyServlet

/users
   ↓
UserResource
```

---

# 15. Can We Use Annotations Instead of web.xml?

Yes.

Modern Jersey applications can use Java configuration.

For example:

```java
@ApplicationPath("/api")
public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        packages("com.example");
    }
}
```

Then:

```java
@Path("/users")
public class UserResource {

    @GET
    public String getUsers() {
        return "Users";
    }
}
```

The API becomes:

```text
GET /api/users
```

In this approach, we don't need the traditional `web.xml` Jersey Servlet mapping.

So:

```text
Traditional approach:

web.xml
   ↓
JerseyServlet
   ↓
@Path
```

versus:

```text
Java configuration:

@ApplicationPath("/api")
   ↓
Jersey
   ↓
@Path("/users")
```

---

# 16. Why Did Our Example Use Both?

Our example uses:

```text
web.xml
```

to configure the Jersey Servlet for the Servlet container.

Then we use:

```java
@Path("/users")
```

for Jersey's REST routing.

They are **not duplicate configurations**.

They belong to different layers:

```text
                    TOMCAT
                      |
             Servlet Routing
                      |
                 /api/*
                      |
                      v
               JERSEY SERVLET
                      |
              REST Routing
                      |
                 /users
                      |
                      v
                USER RESOURCE
```

---

# 17. Jersey vs Servlet – Comparison

| Feature          | Servlet                          | Jersey                    |
| ---------------- | -------------------------------- | ------------------------- |
| Level            | Lower-level HTTP API             | REST framework            |
| Main abstraction | Servlet                          | JAX-RS Resource           |
| GET handling     | `doGet()`                        | `@GET`                    |
| POST handling    | `doPost()`                       | `@POST`                   |
| URL mapping      | `@WebServlet` / `web.xml`        | `@Path`                   |
| Path parameters  | Manual extraction                | `@PathParam`              |
| Query parameters | Manual extraction                | `@QueryParam`             |
| Request body     | `HttpServletRequest`             | Method parameter          |
| Response         | `HttpServletResponse`            | Return value / `Response` |
| JSON             | Often manual/library integration | Message-body providers    |
| REST routing     | Manual / Servlet mapping         | Built into Jersey         |
| Boilerplate      | More                             | Less                      |

---

# 18. Jersey Does Not Replace Tomcat

This is the key architecture:

```text
                CLIENT
                   |
                   | HTTP
                   v
             +-----------+
             |  TOMCAT   |
             |           |
             | Servlet   |
             | Container |
             +-----------+
                   |
                   | /api/*
                   v
          +------------------+
          |  JerseyServlet   |
          |                  |
          | Jersey Framework |
          +------------------+
                   |
                   | REST Routing
                   v
          +------------------+
          |  UserResource    |
          +------------------+
                   |
                   v
             UserService
                   |
                   v
          MockUserRepository
                   |
                   v
             In-Memory List
```

The relationship is:

```text
Tomcat = Servlet Container
Jersey = REST Framework
JerseyServlet = Bridge/Entry Point
Resource = REST API Handler
```

---

# 19. CRUD Example

Our CRUD API can be represented as:

```text
GET /api/users
        ↓
UserResource.getAllUsers()

GET /api/users/{id}
        ↓
UserResource.getUserById(id)

POST /api/users
        ↓
UserResource.createUser(user)

PUT /api/users/{id}
        ↓
UserResource.updateUser(id, user)

DELETE /api/users/{id}
        ↓
UserResource.deleteUser(id)
```

Then:

```text
Resource
   ↓
Service
   ↓
Repository
   ↓
In-Memory List
```

---

# 20. Complete Architecture

```text
┌──────────────────────────────┐
│          CLIENT              │
│      Browser / Postman       │
└──────────────┬───────────────┘
               │
               │ HTTP Request
               ▼
┌──────────────────────────────┐
│           TOMCAT             │
│      Servlet Container       │
└──────────────┬───────────────┘
               │
               │ /api/*
               ▼
┌──────────────────────────────┐
│       JERSEY SERVLET         │
│     ServletContainer         │
└──────────────┬───────────────┘
               │
               │ REST Routing
               │ @Path + @GET
               ▼
┌──────────────────────────────┐
│       USER RESOURCE          │
│       UserResource           │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│        USER SERVICE          │
│       Business Logic        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│     MOCK REPOSITORY          │
│     Data Access Layer        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│       IN-MEMORY LIST         │
│        ArrayList<User>       │
└──────────────────────────────┘
```

---

# 21. Interview Answer

### Question:

**How does a Jersey REST request reach a particular API method?**

### Answer:

> Jersey runs inside a Servlet container such as Tomcat. When an HTTP request arrives, Tomcat uses the Servlet mapping, such as `/api/*`, to forward the request to Jersey's `ServletContainer`. Jersey then performs REST-specific routing using JAX-RS annotations such as `@Path`, `@GET`, `@POST`, and `@PathParam`. It identifies the matching resource method, extracts parameters, converts the request body if necessary, invokes the method, and converts the returned object into the appropriate response representation such as JSON. The response is then sent back through Tomcat to the client.

---

# 22. Golden Memory Trick

Remember these three statements:

```text
1. TOMCAT RECEIVES
   ↓
   Handles Servlet-level routing

2. JERSEY ROUTES
   ↓
   Handles REST-level routing

3. RESOURCE EXECUTES
   ↓
   Handles the actual API logic
```

Or simply:

```text
Client
  ↓
Tomcat receives
  ↓
Jersey routes
  ↓
Resource executes
  ↓
Service processes
  ↓
Repository accesses data
  ↓
Response comes back
```

**Most important concept:**

> **Tomcat decides "which Servlet?" → Jersey decides "which REST method?"**
