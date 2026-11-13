# Monolithic vs Microservices Architecture

## 1. Monolithic Architecture

Monolithic architecture is a **tightly coupled architecture** where all modules or functionalities are bundled into a single application.

For example, an e-commerce application contains Product, User, Order, and Payment modules in one application.

### Problems with Monolithic Architecture

* **Hard to scale:** If the Product module receives more traffic, we may need to scale the entire application instead of scaling only the Product module.
* **Hard to deploy:** Even if we change a single module, we generally need to rebuild and redeploy the complete application.
* **Application breakdown:** A critical failure can affect the entire application, depending on how the application handles failures.
* **Hard to maintain:** When multiple developers work on different modules in the same application, code changes and dependencies can become difficult to manage.

## 2. Microservices Architecture

Microservices architecture divides a single application into multiple **independently deployable services**, with each service handling a specific business functionality.

For example, an e-commerce application can have separate Product, User, Order, and Payment services.

### Advantages of Microservices

* **Easy to scale:** We can scale only the Product service if it receives more traffic.
* **Easy to deploy:** We can deploy the Order service independently when its code changes.
* **Better fault isolation:** If the Product service fails, other services may continue working, provided they can handle the failure.
* **Easy to maintain:** Different teams can develop and maintain individual services independently.

## 3. Inter-Service Communication

Microservices communicate with one another in two main ways.

### A. Synchronous Communication

In synchronous communication, one service sends a request to another service and waits for its response.

It commonly uses REST APIs with clients such as `RestClient` or `WebClient` (when used in a non blocking manner).

**Real-time example: E-commerce Order**

1. The user places an order.
2. The Order service calls the Product service to check product availability.
3. The Product service returns the availability status.
4. The Order service continues processing based on the response.

**Key point:** The calling service waits for the response before proceeding with that operation.

### B. Asynchronous Communication

In asynchronous communication, one service sends a message to a message broker, and another service processes that message independently.

Examples of message brokers include Apache Kafka and RabbitMQ.

**Real-time example: E-commerce Order Notification**

1. The user places an order.
2. The Order service creates the order and publishes an `OrderCreated` event to the message broker.
3. The Notification service receives the event and sends an email or SMS to the user.
4. The Order service does not need to wait for the notification to finish.

**Key point:** The sender and receiver can work independently, and the message broker helps deliver messages between them.

## Quick Difference

| Synchronous                         | Asynchronous                                |
| ----------------------------------- | ------------------------------------------- |
| Request-response communication      | Message/event-based communication           |
| Caller waits for the response       | Caller does not need to wait for processing |
| Example: Check product availability | Example: Send order confirmation email      |
| Commonly uses REST APIs             | Commonly uses Kafka or RabbitMQ             |

# Spring Boot Microservices Communication: RestTemplate vs RestClient vs FeignClient

## 1. RestTemplate

`RestTemplate` is a synchronous, blocking HTTP client used to communicate between microservices. It is an older Spring approach and is now considered a legacy choice for new applications.

### How it works

1. Create a `RestTemplate` bean in a configuration class.
2. Inject it into the service class.
3. Use methods such as `getForObject()`, `postForObject()`, `exchange()`, and `delete()` to communicate with another service.

### Configuration

```java
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder builder) {
        return builder.build();
    }
}
```

### Service example

```java
@Service
public class OrderService {

    private final RestTemplate restTemplate;

    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getProduct(Long id) {
        return restTemplate.getForObject(
            "http://localhost:8001/products/{id}",
            String.class,
            id
        );
    }
}
```

### Why is RestTemplate considered legacy?

- It uses a traditional, synchronous, blocking programming model.
- Its API is less fluent than the newer RestClient API.
- Spring recommends RestClient for new synchronous HTTP communication in modern Spring applications.

**Important:** RestTemplate is not deprecated in the sense that all existing applications must stop using it. It remains available, but Spring Framework 7 deprecates it in favor of RestClient.

### Real-time production example

An existing e-commerce application uses RestTemplate to fetch product details from the Inventory Service. The existing implementation works correctly, so the team continues maintaining it rather than rewriting the entire integration immediately.

---

## 2. RestClient

`RestClient` is the modern synchronous HTTP client introduced in Spring Framework 6.1. It provides a fluent API, which means multiple method calls can be chained together.

It is still blocking, unlike WebClient's non-blocking model.

### Configuration

```java
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(
            RestClient.Builder builder) {
        return builder.build();
    }
}
```

### Service example

```java
@Service
public class ProductService {

    private final RestClient restClient;

    public ProductService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String getProduct(Long id) {
        return restClient.get()
                .uri("http://localhost:8001/products/{id}", id)
                .retrieve()
                .body(String.class);
    }
}
```

### RestClient flow

```text
RestClient
    |
    v
HTTP Method (GET / POST / PUT / DELETE)
    |
    v
URI
    |
    v
Headers / Body (optional)
    |
    v
retrieve() or exchange()
    |
    v
Response handling
    |
    v
Java response object
```

### Important methods

- `get()`, `post()`, `put()`, `delete()` — select the HTTP method.
- `uri()` — specify the target URL.
- `header()` — add HTTP headers.
- `body()` — provide the request body.
- `retrieve()` — retrieve and process the response.
- `body()` on the response spec — convert the response body into the requested Java type.
- `exchange()` — access the full request/response interaction for custom handling.

### Benefits

- Fluent, readable API.
- Supports custom headers, request bodies, and error handling.
- Integrates with Spring's HTTP message converters.
- A good replacement for RestTemplate in synchronous applications.

### Real-time production example

An Order Service calls a Payment Service to retrieve a payment status. The call is synchronous because the Order Service needs the payment result before returning a response to the caller.

---

## 3. OpenFeign Client

Spring Cloud OpenFeign is a declarative HTTP client. Instead of writing HTTP request implementation code in each service method, you define an interface and annotate its methods.

Spring Cloud OpenFeign creates a runtime proxy that executes the HTTP requests.

### Feign client example

```java
@FeignClient(
    name = "inventory-service",
    url = "${inventory.url}"
)
public interface InventoryClient {

    @GetMapping("/products/{id}")
    String getProduct(@PathVariable Long id);
}
```

### application.yml

```yaml
inventory:
  url: http://localhost:8001
```

### Service class

```java
@Service
public class OrderService {

    private final InventoryClient inventoryClient;

    public OrderService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    public String getProduct(Long id) {
        return inventoryClient.getProduct(id);
    }
}
```

You do not implement the HTTP request logic inside `InventoryClient`. OpenFeign handles it.

### Feign flow

```text
Feign Interface
      |
      v
Dynamic Proxy
      |
      v
Request Template
      |
      v
Encoder (if request body needs encoding)
      |
      v
HTTP Client
      |
      v
Remote Service
      |
      v
Decoder (for successful response bodies)
      |
      v
Java Return Value
```

For non-2xx HTTP responses, Feign generally uses `ErrorDecoder` rather than the normal response decoder.

### Common Feign customizations

- `RequestInterceptor` — add headers, tokens, or correlation IDs.
- `ErrorDecoder` — convert HTTP error responses into custom exceptions.
- `Retryer` — configure retries for retryable failures.
- `Logger.Level` — configure request and response logging.
- `Request.Options` or properties — configure connection and read timeouts.
- Custom `Encoder` and `Decoder` — change request serialization or response deserialization when necessary.

### Per-client Feign logger

When multiple Feign clients need different logging levels, configure them individually in `application.yml`.

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          inventory-service:
            loggerLevel: full
          payment-service:
            loggerLevel: basic

logging:
  level:
    com.example.InventoryClient: DEBUG
    com.example.PaymentClient: DEBUG
```

Replace the logger names with the fully qualified interface names in your application.

Available Feign logger levels:

- `NONE` — no Feign logging.
- `BASIC` — method, URL, response status, and execution time.
- `HEADERS` — basic information plus request and response headers.
- `FULL` — headers, body, and metadata.

**Production caution:** `FULL` logging can expose sensitive data, such as authorization headers or personal information. Use it selectively and sanitize sensitive values.

### Real-time production example

An e-commerce Order Service calls the Inventory Service to check stock and the Payment Service to retrieve payment status. Separate Feign interfaces keep the communication code clean, while each client can have its own timeout, logging, and error-handling configuration.

---

## 4. Differences between RestTemplate, RestClient, and FeignClient

| Feature | RestTemplate | RestClient | OpenFeign |
|---|---|---|---|
| Programming style | Imperative | Fluent | Declarative |
| Communication | Synchronous | Synchronous | Synchronous by default |
| Blocking | Yes | Yes | Yes with the usual HTTP clients |
| Request implementation | Written in Java | Written using method chaining | Generated through a proxy |
| Interface required | No | No | Yes, typically |
| Request boilerplate | More | Less | Least |
| Configuration | Spring bean | Spring bean | Client interface and configuration |
| Custom headers | Supported | Supported | Often through interceptors |
| Error handling | Exceptions and handlers | Status handlers and exceptions | `ErrorDecoder` for HTTP errors |
| Best fit | Existing applications | New synchronous HTTP integrations | Declarative service-to-service calls |

---

## 5. When should we choose each one?

### Choose RestTemplate when

- You are maintaining an existing application that already uses it.
- The current implementation is stable and there is no immediate reason to migrate.
- You need to make incremental changes without introducing unnecessary refactoring.

For new synchronous integrations, consider RestClient instead.

### Choose RestClient when

- You want a modern Spring API for synchronous HTTP calls.
- You prefer to write request logic directly in Java.
- You need straightforward control over URIs, headers, request bodies, and response handling.
- Your application does not need Feign's declarative interface model.

### Choose OpenFeign when

- Your microservices communicate through many HTTP endpoints.
- You want to define each downstream service through an interface.
- You want client-specific timeouts, logging, interceptors, and error handling.
- You are using Spring Cloud features such as service discovery and load balancing, when configured appropriately.

Feign is especially useful when you want to avoid repeating HTTP request construction across many service integrations.

---

## 6. Real-world e-commerce example

Imagine an e-commerce platform with these services:

- **Order Service** — creates orders.
- **Inventory Service** — checks available stock.
- **Payment Service** — processes or retrieves payment status.
- **Notification Service** — sends email or SMS notifications.

| Scenario | Possible choice | Reason |
|---|---|---|
| Existing legacy Order Service calls Inventory | RestTemplate | Continue maintaining existing integration |
| A new service makes one or two synchronous HTTP calls | RestClient | Simple, fluent request code |
| Order Service has many downstream HTTP integrations | OpenFeign | Centralized, declarative clients |
| Notifications must be processed asynchronously through messaging | A message broker, such as Kafka or RabbitMQ | Decouples the producer from immediate HTTP responses |
| A service requires non-blocking reactive HTTP communication | WebClient | Supports reactive, non-blocking I/O |

These are design options, not strict rules. The best choice depends on the existing architecture, operational requirements, and team conventions.

---

## 7. Important production considerations

Regardless of which synchronous HTTP client you choose:

1. **Timeouts:** Set connection and response timeouts so calls do not wait indefinitely.
2. **Retries:** Retry only appropriate transient failures. Avoid blindly retrying operations that might create duplicate payments or orders.
3. **Circuit breakers:** Consider Resilience4j or Spring Cloud CircuitBreaker to help prevent repeated calls to an unhealthy downstream service.
4. **Observability:** Use structured logs, correlation IDs, metrics, and distributed tracing.
5. **Error handling:** Translate downstream errors into meaningful application-level responses.
6. **Security:** Forward authentication credentials only when appropriate, and avoid logging tokens or other secrets.
7. **Service discovery and load balancing:** Configure these explicitly when services are addressed by logical names rather than fixed URLs.

## Final interview summary

- **RestTemplate:** Older, imperative, blocking HTTP client, mainly relevant for existing code.
- **RestClient:** Modern, fluent, blocking HTTP client for synchronous communication.
- **OpenFeign:** Declarative HTTP client that generates request behavior from annotated interfaces.

For a new Spring Boot microservices application, RestClient and OpenFeign are both reasonable options. Choose RestClient when you prefer explicit, fluent HTTP code; choose OpenFeign when you prefer declarative interfaces and centralized per-service client configuration.

# Eureka Service Discovery in Microservices

## 1. What is Service Discovery?

In a microservices architecture, one service often needs to communicate with another service.

Without service discovery, we may hardcode the target service URL in our code. For example:

```java
@FeignClient(name = "inventory", url = "http://localhost:8001")
```

If the inventory service changes its port or host, we may need to update the configuration.

To avoid hardcoding the target instance's address, we use **Eureka Service Discovery**.

Eureka maintains a registry of service instances. Instead of specifying the actual URL in Feign, we specify the service name.

```java
@FeignClient(name = "ecom-inventory-service")
```

Eureka-based discovery, together with Spring Cloud LoadBalancer, helps locate an available instance dynamically.

## 2. Service Registration Flow

When an Order Service or Inventory Service starts, the following process generally occurs:

1. The Spring Boot application starts.
2. The Eureka client initializes and obtains information about its service instance.
3. The client creates or maintains its instance information, such as application name, host, and port.
4. The client registers the instance with the Eureka Server through an HTTP request.
5. Eureka updates its in-memory service registry.
6. The client periodically sends heartbeats to renew its lease.
7. The client periodically fetches registry information to refresh its local cache.

**Flow:**

`Spring Boot Application → Eureka Client → Instance Information → Registration Request → Eureka Registry Updated`

Heartbeats and registry fetching are periodic processes; they do not necessarily start in the exact sequence shown above.

## 3. Service Registry and Local Cache

Eureka Server maintains the registry of registered service instances, including their addresses and ports.

Eureka clients can maintain a local cache of service registry information.

For example, suppose Eureka has registered three instances:

| Service name | Host | Port |
|---|---|---|
| ecom-inventory-service | localhost | 8001 |
| ecom-inventory-service | localhost | 8002 |
| ecom-inventory-service | localhost | 8003 |

When Order Service needs to call Inventory Service, it can use the local registry information instead of querying Eureka for every request.

The default registry fetch interval is commonly **30 seconds**.

### Configure the registry refresh interval

In the Eureka client's `application.yml`:

```yaml
eureka:
  client:
    registry-fetch-interval-seconds: 10
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

Here, `registry-fetch-interval-seconds: 10` configures the client to refresh its registry information every 10 seconds.

**Important:** The first service call does not necessarily contact Eureka. The local cache may already be populated during application startup.

## 4. Heartbeats and Health Checks

Eureka needs to know whether registered service instances are still available.

### Heartbeat

A Eureka client periodically sends a heartbeat to renew its lease.

The default heartbeat interval is commonly **30 seconds**.

### Unexpected shutdown

If a service crashes or becomes unreachable, Eureka stops receiving heartbeats. The instance may be removed after its lease expires, depending on eviction behavior and self-preservation settings.

### Graceful shutdown

When an application shuts down gracefully, its Eureka client normally attempts to unregister the instance from Eureka rather than waiting for lease expiration.

### Configure heartbeat and lease expiration

```yaml
eureka:
  instance:
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

- `lease-renewal-interval-in-seconds`: Sends heartbeats every 10 seconds.
- `lease-expiration-duration-in-seconds`: The lease expires after 30 seconds without a successful renewal, subject to Eureka's eviction behavior.

These values are examples for learning and are not necessarily suitable for production.

### Heartbeat vs. Health Check

- **Heartbeat:** Indicates that the instance is communicating with Eureka.
- **Health check:** Determines whether the application is healthy enough to handle requests.

A heartbeat alone does not guarantee that the application's database or business APIs are functioning correctly.

## 5. How Does Eureka Identify a Service Without a Hardcoded URL?

Consider this Feign client in Order Service:

```java
@FeignClient(name = "ecom-inventory-service")
public interface InventoryClient {

    @GetMapping("/inventory/{productId}")
    String getInventory(@PathVariable String productId);
}
```

The name `ecom-inventory-service` must match the service's registered application name.

For example, the Inventory Service has this configuration:

```yaml
spring:
  application:
    name: ecom-inventory-service

server:
  port: 8001

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

When Inventory Service starts, it registers its instance information with Eureka.

When Order Service calls the Feign method, the service name is resolved through Spring Cloud's discovery and load-balancing components.

### Request flow

`Feign → FeignBlockingLoadBalancerClient → Service Instance Selection → HTTP Request → Inventory Service`

More precisely:

1. **Feign:** Creates the HTTP request using the configured service name and endpoint path.
2. **FeignBlockingLoadBalancerClient:** Integrates Feign with Spring Cloud LoadBalancer when the load-balancer integration is configured.
3. **DiscoveryClient / Eureka client:** Provides registered instance information, commonly from the local registry cache.
4. **LoadBalancer:** Selects an available instance.
5. **URL resolution:** The service name is resolved to the selected instance's actual host and port.
6. **HTTP request:** The request goes directly to that Inventory Service instance.
7. **Response:** Inventory Service processes the request and returns the response to Order Service.

Eureka does not proxy or redirect every HTTP request. It provides the service registry; the calling service sends the request to the selected instance.

## 6. Important Interview Points

- **Eureka Server:** Maintains the service registry.
- **Eureka Client:** Registers its instance and obtains registry information.
- **Service Registry:** Stores registered service-instance information.
- **Local Cache:** Reduces the need to fetch registry information for every request.
- **Heartbeat:** Renews the service instance's lease.
- **Lease Expiration:** Allows stale instances to be removed after missed renewals, subject to eviction behavior.
- **OpenFeign:** Makes declarative HTTP calls using a service name.
- **Spring Cloud LoadBalancer:** Selects an instance from the discovered instances.
- **Service Discovery:** Avoids hardcoding the target instance's host and port in each Feign client.

**One-line interview answer:** Eureka maintains a registry of service instances, and Spring Cloud LoadBalancer uses discovery information to select an instance so that OpenFeign can call it without hardcoding its host and port.

# API Gateway, Resilience and Fault Tolerance in Microservices

## 1. API Gateway

### Definition
An API Gateway acts as a **single entry point** for clients to access multiple microservices. It routes incoming requests to the appropriate backend service.

### Problem
- Clients need to know multiple microservice URLs.
- Service hostnames and ports may change.
- Authentication, logging, and rate limiting may be duplicated across services.

### Example
Without an API Gateway:
- Order Service: `http://localhost:8001`
- Payment Service: `http://localhost:8002`
- User Service: `http://localhost:8003`

With an API Gateway:
- Orders: `http://localhost:9001/orders/**`
- Payments: `http://localhost:9001/payments/**`
- Users: `http://localhost:9001/users/**`

### Explanation
The client sends a request to the gateway, and the gateway forwards it to the appropriate microservice. Clients do not need to know the backend service's address.

### Responsibilities
- Request routing
- Authentication and token validation
- Logging and monitoring
- Rate limiting
- Request and response filtering

**Note:** Business logic and resource-level authorization should remain in the appropriate backend services.

---

## 2. Spring Cloud Gateway

### Definition
Spring Cloud Gateway is a Spring-based API Gateway that provides routing, predicates, and filters for handling client requests.

### Example
A request to `/orders/101` is routed to the Order Service.

```yaml
server:
  port: 9001

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: order-service
              uri: http://localhost:8001
              predicates:
                - Path=/orders/**
```

### Explanation
- `id`: Identifies the route.
- `uri`: Specifies the backend service address.
- `Path=/orders/**`: Matches requests beginning with `/orders/`.
- The gateway forwards matching requests to the configured destination.

### Why is Spring Cloud Gateway reactive?
Spring Cloud Gateway Server WebFlux uses Spring WebFlux and a non-blocking processing model.

**Example:** While waiting for a backend HTTP response, the gateway can avoid blocking a dedicated thread for that entire wait.

**Note:** It can route requests to traditional Spring MVC microservices. However, the gateway itself should use a compatible WebFlux setup rather than mixing in Spring MVC server dependencies.

---

## 3. Static Routing

### Definition
Static routing specifies the backend service URL directly in the gateway configuration.

### Example
```yaml
uri: http://localhost:8001
```

### Explanation
Requests matching the configured route are forwarded to that fixed address.

### Advantages
- Simple to configure.
- Easy to understand and debug.

### Limitation
If the backend service's host or port changes, the gateway configuration may need to be updated.

---

## 4. Dynamic Routing with Eureka

### Definition
Dynamic routing uses service discovery to find registered service instances instead of hardcoding their addresses.

### Example
```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: order-service
              uri: lb://ORDER-SERVICE
              predicates:
                - Path=/orders/**
```

### Explanation
- `lb://` tells the gateway to use load-balanced service resolution.
- `ORDER-SERVICE` is the service ID registered with Eureka.
- Eureka maintains information about registered service instances.
- Spring Cloud LoadBalancer selects an available instance.

### Request flow
```text
Client
  |
  v
API Gateway
  |
  v
Eureka / Service Discovery
  |
  v
Available Order Service Instance
```

### Advantages
- Avoids hardcoding service addresses.
- Supports multiple instances.
- Helps the application adapt when service instances change.

**Required setup:** Eureka Server, Eureka Client configuration for backend services, and the appropriate Eureka Client and Spring Cloud LoadBalancer dependencies in the gateway.

---

# Resilience and Fault Tolerance

## 5. Resilience

### Definition
Resilience is the ability of a system to handle failures, recover from them, and continue providing service.

### Example
The Order Service calls the Inventory Service. If Inventory temporarily fails, the Order Service retries the call or returns a controlled fallback response.

### Explanation
Resilience reduces the impact of temporary failures and helps prevent one service's problem from affecting the entire application.

---

## 6. Fault Tolerance

### Definition
Fault tolerance is the ability of a system to continue providing its required functionality even when one or more components fail.

### Example
One Order Service instance goes down, but another healthy instance continues handling requests.

### Explanation
This requires appropriate redundancy, service discovery or load balancing, and healthy alternative instances.

### Resilience vs Fault Tolerance
- **Resilience:** Focuses on handling failures and recovering.
- **Fault tolerance:** Focuses on continuing required operation despite failures.

---

## 7. Resilience4j

### Definition
Resilience4j is a Java library that provides fault-tolerance patterns such as Retry, Circuit Breaker, Rate Limiter, Time Limiter, and Bulkhead.

### Example
```java
@CircuitBreaker(
    name = "circuitOrderService",
    fallbackMethod = "orderCircuitFallback"
)
public String placeOrder(String productId) {
    return orderService.placeOrder(productId);
}
```

### Explanation
Resilience4j applies the configured behavior around the protected method. In Spring applications, annotation-based behavior commonly uses Spring AOP proxies.

### Important requirements
- Add the appropriate Resilience4j Spring Boot dependency.
- Configure the matching instance name in `application.yml`.
- Ensure calls pass through the Spring-managed proxy.
- Calling an annotated method from another method in the same class can bypass the proxy.

---

# Resilience4j Patterns

## 8. Retry Pattern

### Definition
The Retry pattern repeats a failed operation a limited number of times when the failure may be temporary.

### Problem
A service may fail temporarily because of a network interruption or a short-lived dependency issue.

### Example
The Order Service calls the Inventory Service. The first call fails, but a subsequent attempt succeeds.

```java
@Retryable(
    retryFor = RuntimeException.class,
    maxAttempts = 3,
    backoff = @Backoff(delay = 2000)
)
public String placeOrder(String productId) {
    return orderService.placeOrder(productId);
}
```

### Explanation
- `retryFor`: Specifies which exceptions trigger retries.
- `maxAttempts = 3`: Allows at most three total attempts, including the initial attempt.
- `delay = 2000`: Waits two seconds between retry attempts.

### Implementation flow
```text
Order Service
     |
     v
Retry Interceptor / Proxy
     |
     v
Call the Protected Method
     |
     +---- Success ----> Return Result
     |
     +---- Failure ---> Retry if Eligible
```

### Best practices
- Retry only failures that may be temporary.
- Use bounded attempts and backoff.
- Ensure repeated operations are safe, particularly for order creation and payments.
- Use idempotency keys where appropriate to avoid duplicate business operations.

### Implementation options
- Spring Retry: `@Retryable`
- Resilience4j Retry: `@Retry`

Use the configuration and annotations appropriate to the library you have installed.

---

## 9. Rate Limiter Pattern

### Definition
A Rate Limiter restricts how many requests or method calls are permitted within a configured time period.

### Problem
Too many requests can overload a service, consume resources, or affect other clients.

### Example
Allow two calls every ten seconds.

```yaml
resilience4j:
  ratelimiter:
    instances:
      orderService:
        limitForPeriod: 2
        limitRefreshPeriod: 10s
        timeoutDuration: 0s
```

```java
@RateLimiter(
    name = "orderService",
    fallbackMethod = "orderRateLimitFallback"
)
public String placeOrder(String productId) {
    return orderService.placeOrder(productId);
}
```

### Explanation
- `limitForPeriod`: Number of permitted calls per refresh period.
- `limitRefreshPeriod`: Time interval after which the permission limit refreshes.
- `timeoutDuration`: Maximum time a caller waits for permission. With `0s`, it does not wait.

When the permitted calls are exhausted, additional calls are rejected and the fallback can handle the rejection.

### Where can it be implemented?
- API Gateway: Protect public endpoints.
- Individual microservices: Protect expensive operations.
- Inter-service calls: Limit calls to a dependency.

### Common use cases
- Traffic spikes
- Expensive APIs
- Subscription-based request limits
- Preventing excessive requests from clients

**Note:** Rate limiting is not a complete DDoS protection solution by itself.

---

## 10. Circuit Breaker Pattern

### Definition
A Circuit Breaker stops calls to a dependency temporarily when failures exceed a configured threshold.

### Problem
Repeated calls to an unhealthy service can waste resources and contribute to cascading failures.

### Example
The Order Service repeatedly receives failures from the Inventory Service. Once the failure threshold is reached, the circuit opens and new calls are rejected.

```yaml
resilience4j:
  circuitbreaker:
    instances:
      circuitOrderService:
        registerHealthIndicator: true
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 5
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 2
        automaticTransitionFromOpenToHalfOpenEnabled: true
        recordExceptions:
          - feign.FeignException
          - java.io.IOException
          - java.lang.RuntimeException
        ignoreExceptions:
          - java.lang.IllegalArgumentException
```

### Explanation
- `slidingWindowSize`: Examines the last five calls.
- `minimumNumberOfCalls`: Requires five calls before evaluating the failure rate.
- `failureRateThreshold`: Opens the circuit when the failure rate reaches 50%.
- `waitDurationInOpenState`: Keeps the circuit open for ten seconds.
- `permittedNumberOfCallsInHalfOpenState`: Allows two trial calls in Half-Open.
- `recordExceptions`: Exceptions counted as failures.
- `ignoreExceptions`: Exceptions excluded from failure and success calculations.

### Three states

**Closed**
- Calls are allowed.
- Failures are monitored.
- The circuit opens if the configured failure threshold is reached.

**Open**
- Calls are rejected without invoking the protected dependency.
- The service gets time to recover.

**Half-Open**
- A limited number of trial calls are allowed.
- Successful trials can close the circuit.
- Continued failures reopen it.

### Implementation flow
```text
Order Service
     |
     v
Circuit Breaker
     |
     +---- Closed ------> Call Inventory Service
     |
     +---- Open --------> Reject Call / Fallback
     |
     +---- Half-Open ---> Test Recovery
```

**Note:** Exception recording rules must match the exceptions your application actually throws. A Circuit Breaker does not automatically fix the failed service.

---

## 11. Fallback Pattern

### Definition
A fallback provides an alternative response or action when the main operation fails or is rejected.

### Example
```java
public String orderCircuitFallback(
        String productId,
        Throwable ex) {

    System.out.println("Circuit Breaker Invoked!");
    System.out.println("Reason: " + ex.getMessage());

    return "Inventory service is unavailable. Please try again later.";
}
```

### Explanation
If the protected call fails in a way handled by the Circuit Breaker, the fallback can return a controlled message rather than exposing an unhandled error to the caller.

### Important points
- The fallback parameters must match the original method's parameters, with an optional compatible exception parameter.
- The fallback return type must be compatible with the original method's return type.
- A fallback can also be used with Retry, Rate Limiter, Time Limiter, and Bulkhead.
- Do not return a success message when the order or payment has actually failed.

**Fallback is not a standalone Resilience4j module in the same sense as Circuit Breaker or Retry; it is a fallback mechanism used with these patterns.**

---

## 12. Timeout

### Definition
A timeout specifies the maximum time a client or operation waits for a response.

### Example
```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          inventory-service:
            connectTimeout: 3000
            readTimeout: 20000
```

### Explanation
- `connectTimeout`: Maximum time allowed to establish a connection.
- `readTimeout`: Maximum time allowed while waiting for response data after the connection is established, according to the HTTP client's timeout behavior.

If the relevant timeout expires, the call fails instead of waiting indefinitely.

### Why use timeouts?
- Prevent excessively long waits.
- Reduce resource exhaustion.
- Improve responsiveness when a dependency is slow.

---

## 13. Time Limiter Pattern

### Definition
Resilience4j TimeLimiter limits the time allowed for a supported asynchronous operation.

### Example
```yaml
resilience4j:
  timelimiter:
    instances:
      timeLimitOrderService:
        timeoutDuration: 2s
        cancelRunningFuture: true
```

```java
@TimeLimiter(
    name = "timeLimitOrderService",
    fallbackMethod = "orderTimeLimiterFallback"
)
public CompletableFuture<String> placeOrder(String productId) {
    return CompletableFuture.supplyAsync(
        () -> orderService.placeOrder(productId)
    );
}
```

### Explanation
- `timeoutDuration`: Maximum time allowed before the TimeLimiter times out the operation.
- `cancelRunningFuture`: Requests cancellation of the running future when the timeout occurs, where supported.

If the time limit expires, the operation completes exceptionally and the fallback can return an alternative result.

### Why is an asynchronous return type used?
Resilience4j TimeLimiter works with supported asynchronous types such as `CompletionStage` and `Future`. A `CompletableFuture` is commonly used in Spring applications.

### Timeout vs TimeLimiter
- **Timeout:** Usually configures how long a client waits for a connection or response, such as an OpenFeign read timeout.
- **TimeLimiter:** Limits the duration of a supported asynchronous operation at the application level.

**Important:** Cancelling a future does not guarantee that the underlying HTTP request or blocking work has stopped. Configure client timeouts as well.

---

## 14. Bulkhead Pattern

### Definition
The Bulkhead pattern isolates workloads or limits concurrent operations so that excessive activity in one area does not exhaust all available resources.

### Problem
If too many requests execute concurrently, a service may run out of threads, connections, or other resources.

### Example 1: Semaphore Bulkhead

```yaml
resilience4j:
  bulkhead:
    instances:
      bulkHeadOrderService:
        maxConcurrentCalls: 2
        maxWaitDuration: 0s
```

```java
@Bulkhead(
    name = "bulkHeadOrderService",
    type = Bulkhead.Type.SEMAPHORE,
    fallbackMethod = "orderBulkHeadFallback"
)
public String placeOrder(String productId) {
    return orderService.placeOrder(productId);
}
```

### Explanation
- `maxConcurrentCalls: 2`: Allows at most two simultaneous executions of the protected method.
- `maxWaitDuration: 0s`: Does not wait for an available permit.
- Additional calls are rejected when all permits are occupied.

A Semaphore Bulkhead does not create a separate thread pool; the method executes on the caller's thread.

### Example 2: ThreadPool Bulkhead

```yaml
resilience4j:
  thread-pool-bulkhead:
    instances:
      bulkHeadOrderService:
        maxThreadPoolSize: 2
        coreThreadPoolSize: 2
        queueCapacity: 0
```

```java
@Bulkhead(
    name = "bulkHeadOrderService",
    type = Bulkhead.Type.THREADPOOL,
    fallbackMethod = "orderBulkHeadFallback"
)
public CompletableFuture<String> placeOrder(String productId) {
    return CompletableFuture.completedFuture(
        orderService.placeOrder(productId)
    );
}
```

### Explanation
- `maxThreadPoolSize`: Maximum worker-thread count.
- `coreThreadPoolSize`: Core worker-thread count.
- `queueCapacity: 0`: Does not allow tasks to wait in a queue.
- Additional tasks can be rejected when no worker is available.

The ThreadPool Bulkhead runs the protected method on its dedicated executor. Its protected method should use a compatible asynchronous return type, such as `CompletableFuture`.

### Semaphore vs ThreadPool

| Feature | Semaphore | ThreadPool |
|---|---|---|
| Limits concurrent work | Yes | Yes |
| Uses a dedicated executor | No | Yes |
| Typical return type | Synchronous, such as `String` | Asynchronous, such as `CompletableFuture` |
| Configuration | `resilience4j.bulkhead` | `resilience4j.thread-pool-bulkhead` |

**Important:** For ThreadPool Bulkhead, do not wrap the method body in an unrelated `CompletableFuture.supplyAsync()` without a specific reason. The bulkhead already schedules the protected method on its own executor.

---

# 15. Quick Comparison of Resilience Patterns

| Pattern | Main purpose | Example |
|---|---|---|
| Retry | Handle temporary failures | Retry a failed Inventory call |
| Rate Limiter | Control request frequency | Allow two calls every ten seconds |
| Circuit Breaker | Prevent repeated calls to an unhealthy dependency | Open the circuit after repeated failures |
| Fallback | Provide an alternative result | Return an Inventory unavailable message |
| Timeout | Limit client waiting time | Feign read timeout of three seconds |
| Time Limiter | Limit asynchronous operation duration | Time out a future after two seconds |
| Bulkhead | Limit or isolate concurrent work | Permit only two simultaneous executions |

---

# 16. Recommended Implementation Order

1. **API Gateway:** Configure routes for the backend microservices.
2. **Eureka:** Register services and configure discovery-based routing.
3. **Timeouts:** Configure connection and response timeouts for remote calls.
4. **Retry:** Retry suitable temporary failures with bounded attempts and backoff.
5. **Circuit Breaker:** Stop repeatedly calling an unhealthy dependency.
6. **Fallback:** Return an appropriate alternative response when necessary.
7. **Rate Limiter:** Control request frequency.
8. **Bulkhead:** Limit concurrent work and isolate resources.
9. **Time Limiter:** Limit supported asynchronous operations.
10. **Testing and monitoring:** Test success, failure, timeout, rejection, and recovery scenarios.

**Final takeaway:** An API Gateway manages client entry and routing, while resilience patterns protect service-to-service calls and resource usage. They complement one another but solve different problems.

# Saga Pattern in Microservices

## 1. What Is the Saga Pattern?

**Definition:** The Saga Pattern manages a business transaction across multiple microservices by splitting it into a sequence of local transactions, each with a corresponding compensating action if a later step fails.

## 2. Why Do We Need It?

In microservices, each service usually manages its own database. A single ACID transaction across all services is generally not practical.

**Example:** Placing an order requires:
1. Order Service creates an order.
2. Payment Service processes payment.
3. Inventory Service reserves stock.

If inventory reservation fails after payment succeeds, the system must handle the payment and order consistently.

**Without Saga:** One service may complete its transaction while another fails, leaving inconsistent business state.

**With Saga:** The system executes compensating actions to undo or offset previously completed steps.

## 3. Example Flow

**Successful transaction**

```text
Create Order
    |
    v
Process Payment
    |
    v
Reserve Inventory
    |
    v
Order Confirmed
```

**Failure and compensation**

```text
Create Order          SUCCESS
    |
    v
Process Payment       SUCCESS
    |
    v
Reserve Inventory     FAILED
    |
    v
Refund Payment        COMPENSATION
    |
    v
Cancel Order          COMPENSATION
```

Compensation is a new business operation, not necessarily a literal database rollback.

## 4. Two Saga Implementation Approaches

### A. Choreography

**Definition:** Services coordinate through events without a central controller.

**Example:**
- Order Service publishes `OrderCreated`.
- Payment Service processes payment and publishes `PaymentCompleted`.
- Inventory Service reserves stock and publishes `InventoryReserved`.
- If inventory fails, events trigger payment refund and order cancellation.

**Suitable for:** Smaller workflows with a limited number of services.

### B. Orchestration

**Definition:** A central orchestrator controls the workflow and tells each service which operation to perform.

**Example:**

```text
Saga Orchestrator
    |
    +--> Order Service: Create Order
    |
    +--> Payment Service: Process Payment
    |
    +--> Inventory Service: Reserve Stock
                     |
                     +--> Failure
                            |
                            v
                     Refund Payment
                            |
                            v
                     Cancel Order
```

**Suitable for:** Complex workflows with multiple steps, conditional decisions, and compensating actions.

## 5. How to Implement Saga in Java

You can implement Saga using Spring Boot with either event-driven messaging or a workflow orchestrator.

| Technology | Purpose |
|---|---|
| Spring Boot | Build microservices and REST APIs |
| Apache Kafka or RabbitMQ | Exchange events between services |
| Spring Kafka or Spring AMQP | Integrate messaging with Spring |
| Orchestration framework (optional) | Manage workflow steps and compensation |

### Example using Spring Boot and Kafka

1. Order Service creates an order and publishes `OrderCreated`.
2. Payment Service consumes the event and processes payment.
3. Payment Service publishes `PaymentCompleted` or `PaymentFailed`.
4. Inventory Service reserves stock after payment succeeds.
5. If inventory reservation fails, the workflow triggers `RefundPayment` and `CancelOrder`.

Each service commits its own local database transaction.

## 6. Which Package Should We Use?

There is no single mandatory Java package for the Saga Pattern.

- **For learning or a simple implementation:** Spring Boot + Spring Kafka or Spring AMQP.
- **For a complex orchestrated workflow:** Consider Temporal's Java SDK or a suitable workflow orchestration framework.
- **For event-driven choreography:** Use Kafka or RabbitMQ with Spring's messaging integration.

Typical Maven dependencies for a Kafka-based implementation:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

Use a compatible Spring Boot and Spring Kafka version.

## 7. Important Best Practices

- Make event processing idempotent to avoid duplicate operations.
- Use retries for transient failures.
- Implement compensating transactions for completed steps.
- Handle duplicate and out-of-order events.
- Consider the transactional outbox pattern to reliably publish events alongside database changes.
- Monitor workflow status and provide recovery for failed compensations.

## Summary

**Saga Pattern = Local transactions + Coordination + Compensating actions.**

It helps maintain business consistency across multiple microservices without requiring one distributed database transaction.


# Idempotency in Microservices

## Definition
Idempotency means executing the same operation multiple times produces the same final business result as executing it once.

## When Does It Happen?
Duplicate requests can occur when the original request succeeds but the client does not receive the response, leaving the operation's outcome uncertain.

## Ways Duplicate Requests Can Happen
1. **Network Timeout:** Backend completes the operation, but the response is lost.
2. **Frontend Retry:** Frontend retries a request after a timeout or network failure.
3. **Multiple Button Clicks:** User clicks Submit, Pay, or Place Order multiple times.
4. **API Gateway/HTTP Client Retry:** Configured gateways or clients resend requests after certain failures.
5. **Service Crash:** Service commits a database transaction but crashes before responding or acknowledging.
6. **Kafka/RabbitMQ Redelivery:** Consumer processes a message but fails before acknowledgment, so the broker delivers it again.
7. **Saga Retry:** A microservice workflow retries a previously executed step after a failure.
8. **Browser Refresh/Resubmission:** User reloads the page or resubmits an operation whose outcome is unknown.

## How to Prevent Duplicate Business Operations
1. **Idempotency Key:** Assign a unique key to each logical operation and reuse the same key for every retry.
2. **Database Unique Constraint:** Enforce uniqueness on the idempotency key within the appropriate scope.
3. **Store Operation Status:** Persist the key, request hash, status, and result/resource ID.
4. **Safe Concurrency Handling:** Ensure simultaneous requests with the same key cannot both execute the business operation.
5. **Frontend Protection:** Disable submit buttons while processing, but do not rely on this alone.
6. **Retry Policy:** Retry only appropriate failures, use backoff where needed, and preserve the original key.
7. **Message Deduplication:** Use stable event/message IDs and an inbox table for Kafka/RabbitMQ consumers.
8. **Saga Operation Identity:** Reuse the same operation ID when retrying the same Saga step.
9. **Durable Business Constraints:** Preserve payment/order records and enforce business-level uniqueness where necessary.
10. **External Service Protection:** Propagate the operation identity or use the external provider's idempotency mechanism.

## Where to Store Idempotency Data
- **PostgreSQL/MySQL:** Recommended for durable records and critical business operations.
- **Redis:** Useful for fast, temporary deduplication with TTL; not necessarily sufficient alone for critical transactions.
- **Inbox Table:** Useful for deduplicating messages consumed from Kafka/RabbitMQ.

## How Long Should It Live?
Retention depends on the retry window and business requirements. Temporary API keys may live for hours or days, while critical payment and business-operation records may need to be retained much longer. Never rely solely on a short TTL to prevent duplicate financial transactions.

## Key Rules
- Same logical operation = same idempotency key.
- New logical operation = new idempotency key.
- Same key with a different payload = reject the request.
- Concurrent duplicates = protect with a unique constraint and safe transaction/concurrency handling.
- A timeout does not necessarily mean the operation failed.
- Idempotency prevents duplicate business effects; it does not guarantee exactly-once message delivery.


# Transactional Outbox Pattern — Microservices Interview Notes

## 1. Definition
The **Transactional Outbox Pattern** ensures that a service's database changes and the event describing those changes are saved atomically in the same local database transaction. A separate publisher then sends the event to a message broker.

## 2. Why Do We Need It?
In microservices, a service often needs to:
1. Save business data in its database.
2. Publish an event to Kafka or RabbitMQ.

These two operations can fail independently, creating inconsistency. This is called the **dual-write problem**.

### Without Outbox
- **Database succeeds, publishing fails:** Order exists, but other services never receive the event.
- **Publishing succeeds, database fails:** Other services receive an event for data that was never committed.
- **Service crashes between operations:** Database and message broker become inconsistent.

## 3. How Does It Work?
1. Client sends a request to the service.
2. Service starts a database transaction.
3. Saves the business data, such as an order.
4. Saves the corresponding event in the `outbox` table within the same transaction.
5. Transaction commits: both records are saved, or neither is.
6. A separate publisher reads the outbox and sends events to Kafka/RabbitMQ.
7. Publisher records successful publication or tracks delivery state.
8. Consumers process events idempotently because duplicate delivery is possible.

## 4. Architecture Flow

```text
Client
  |
  v
Order Service
  |
  | One database transaction
  v
+----------------------------+
| Orders Table               |
| Outbox Table               |
+----------------------------+
  |
  | Transaction commits
  v
Outbox Publisher / CDC
  |
  v
Kafka / RabbitMQ
  |
  +--> Inventory Service
  |
  +--> Notification Service
```

## 5. Example Scenario
A customer places an order.

- `orders` table stores order `ORD-101`.
- `outbox` table stores an `OrderCreated` event.
- Publisher sends the event to Kafka.
- Inventory Service reserves stock.
- Notification Service sends an order confirmation.

If Kafka is unavailable, the committed outbox event remains available for retry.

## 6. Example Database Tables

```sql
CREATE TABLE orders (
    id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox (
    event_id VARCHAR(100) PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(150) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP
);
```

The `event_id` must be unique and stable. Production implementations may also store retry count, delivery status, event version, and routing information.

## 7. Spring Boot / Java Example

The order and outbox event must be saved in the same database transaction.

```java
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderService(
            OrderRepository orderRepository,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void createOrder(CreateOrderRequest request)
            throws JsonProcessingException {

        Order order = new Order(
            request.orderId(),
            request.customerId(),
            "CREATED"
        );

        orderRepository.save(order);

        OutboxEvent event = new OutboxEvent(
            UUID.randomUUID().toString(),
            "Order",
            order.getId(),
            "OrderCreated",
            objectMapper.writeValueAsString(order)
        );

        outboxRepository.save(event);
    }
}
```

This is illustrative code; entity constructors and repository methods must match your application.

### Why Is `@Transactional` Important?
Both repositories must participate in the same local database transaction.

- If saving the order fails, the outbox insert rolls back.
- If saving the event fails, the order insert rolls back.
- If the transaction commits, both records are persisted.

**Important:** A regular `@Transactional` database transaction does not automatically make a separate Kafka publish atomic with the database transaction.

## 8. How Is the Event Published?

### A. Polling Publisher
A scheduled worker queries unpublished outbox records, sends them to the broker, and marks them as published.

**Advantages**
- Simple to understand and implement.
- Does not require CDC infrastructure.

**Disadvantages**
- Repeated database polling adds load.
- Multiple publisher instances require safe claiming or locking.
- A crash after publishing but before marking the event published can cause duplicate publication.

### B. Change Data Capture (CDC)
A tool such as Debezium reads database transaction-log changes and publishes outbox events to Kafka.

**Advantages**
- Avoids frequent application-level polling.
- Supports scalable, low-latency event publishing.

**Disadvantages**
- Requires additional infrastructure and monitoring.
- Duplicate delivery can still occur.
- Connector and schema configuration need care.

## 9. When Should We Use It?
Use the Outbox Pattern when a service must update its database and reliably publish an event describing that update.

Common examples:
- **Order Service:** Save order and publish `OrderCreated`.
- **Payment Service:** Record payment result and publish `PaymentCompleted`.
- **Inventory Service:** Update stock and publish `StockReserved`.
- **Saga workflows:** Reliably publish events or commands after local transactions.
- **Customer Service:** Save customer changes and notify downstream services.

It is particularly useful when losing an event would leave other services with stale or inconsistent state.

## 10. When Might We Not Need It?
- No event needs to be published after the database update.
- The operation is read-only.
- Losing a notification is acceptable under the business requirements.
- Another architecture already provides the required reliability and consistency.

Do not add an outbox to every service automatically. Use it where database changes and event publication must remain consistent.

## 11. Advantages
- Prevents the dual-write inconsistency.
- Saves business data and publication intent atomically.
- Supports retries after broker outages.
- Fits event-driven microservices and Saga workflows.
- Provides visibility into pending events and recovery.

## 12. Limitations and Important Considerations
- **Duplicate publication:** Consumers must be idempotent.
- **Ordering:** Preserve event order for an aggregate when business logic requires it.
- **Cleanup:** Define a retention policy for published events.
- **Retries:** Use backoff, monitoring, and recovery handling for persistent failures.
- **Concurrency:** Multiple publishers must safely claim records, or use CDC.
- **Schema evolution:** Version events and maintain compatibility.
- **Consumer reliability:** Commit the deduplication record and business update atomically where possible.
- **External side effects:** Outbox does not make payments, emails, or another service's database part of the same local transaction.

## 13. Outbox vs Saga vs Two-Phase Commit

| Pattern | Main Purpose |
|---|---|
| Transactional Outbox | Atomically save local business data and the event to publish |
| Saga | Coordinate a multi-service workflow using local transactions and compensations |
| Two-Phase Commit (2PC) | Coordinate a distributed transaction through prepare and commit/abort phases |

These patterns solve different problems and can be used together. For example, each Saga participant can use an outbox to publish events reliably.

## 14. Common Interview Questions

### Q1. What problem does the Outbox Pattern solve?
It solves the dual-write problem where database updates and message publication can fail independently.

### Q2. Does the Outbox Pattern guarantee exactly-once delivery?
No. Duplicate publication is possible. Consumers should deduplicate events using a stable event ID or implement idempotent business operations.

### Q3. Why not publish to Kafka inside `@Transactional`?
A normal Spring database transaction generally covers the database, not the Kafka broker. One operation may succeed while the other fails.

### Q4. How does the pattern handle Kafka downtime?
The event remains in the outbox after the database transaction commits. The publisher retries when Kafka becomes available.

### Q5. What is the difference between polling and CDC?
Polling periodically queries the outbox table. CDC reads database transaction-log changes, often through Debezium.

### Q6. How does Outbox relate to Saga?
Saga coordinates a multi-step business workflow. Outbox helps each participating service publish its local transaction's outcome reliably.

### Q7. What if the publisher crashes after sending an event?
The event may be sent again after recovery. Consumers must handle duplicate events safely.

### Q8. Where should the outbox table be stored?
Usually in the same database as the business data whose change the event describes, allowing both inserts to participate in one local transaction.

## 15. One-Line Interview Answer
The **Transactional Outbox Pattern** solves the dual-write problem by saving business data and its corresponding event in the same local database transaction, then publishing the event asynchronously with retry and duplicate-handling mechanisms.

## Quick Revision
- **Problem:** Database update and broker publish can fail independently.
- **Solution:** Save business data and outbox event in one local transaction.
- **Publisher:** Poll the outbox or use CDC/Debezium.
- **Delivery:** Duplicate publication can happen.
- **Consumer:** Use idempotency/deduplication.
- **Used with:** Event-driven microservices and Saga workflows.
- **Remember:** Outbox ensures reliable publication intent, not automatic exactly-once end-to-end processing.



# Observability and Distributed Tracing in Microservices

## 1. What is Observability?

Observability helps us understand what is happening inside an application or distributed system by analyzing the information generated while the application is running.

In a microservices architecture, a single user request may travel through multiple services. If something goes wrong, we need to identify:

- Which service received the request?
- Which services were called?
- Where did the request fail?
- How much time did each service take?
- What was the actual error?
- Is the system experiencing high CPU or memory usage?

Observability helps us answer these questions.

There are three major pillars of observability:

1. Logs
2. Metrics
3. Traces

---

## 2. Three Pillars of Observability

### 2.1 Logs

Logs provide detailed event information about what is happening inside an application.

For example:

```text
2026-11-06 10:30:00 INFO  OrderController - Order request received
2026-11-06 10:30:01 INFO  OrderService - Validating product ID
2026-11-06 10:30:02 ERROR InventoryService - Product not available
```

Using logs, we can identify:

- What happened?
- When did it happen?
- Which operation generated the event?
- What error occurred?
- What information was available during execution?

**Real-time use case:**

Suppose an order fails because the inventory service returns an error. Logs help us identify the actual error and understand what happened during execution.

### 2.2 Metrics

Metrics represent numerical measurements collected over time.

They help us understand application and infrastructure performance.

Examples:

| Metric | Purpose |
|---|---|
| CPU Usage | Measures processor utilization |
| Memory Usage | Measures memory consumption |
| Request Count | Number of requests received |
| Requests Per Second | Request throughput |
| Response Time | Time taken to process requests |
| Error Rate | Percentage of failed requests |
| JVM Heap Usage | Java heap memory utilization |
| Thread Count | Number of active application threads |

**Real-time use case:**

Suppose your Order Service receives 10,000 requests per second.

Metrics help identify:
- CPU usage is increasing.
- Memory consumption is increasing.
- API response time is increasing.
- Error rate is increasing.

This helps us identify performance bottlenecks.

### 2.3 Traces

Distributed tracing helps us track a single request as it travels through multiple microservices.

For example:

```text
Client
  |
  v
API Gateway
  |
  v
Order Service
  |
  v
Inventory Service
  |
  v
Payment Service
  |
  v
Notification Service
```

Suppose the user places an order, but the request fails.

Distributed tracing helps identify:

- Which services received the request?
- How much time did each service take?
- Which service failed?
- Where did the request spend most of its time?
- Was the request successful or unsuccessful?

#### Trace ID

A Trace ID identifies one complete distributed request.

For example:

```text
Trace ID: abc123
```

The same Trace ID is propagated across the participating services.

#### Span ID

A Span ID identifies an individual operation within a trace.

For example:

```text
Trace ID: abc123

Order Service:
  Span ID: 111

Inventory Service:
  Span ID: 222

Payment Service:
  Span ID: 333
```

Each span has its own Span ID.

A span can also contain a Parent Span ID to establish the relationship between operations.

**Important interview point:**

- Trace ID identifies the complete request across services.
- Span ID identifies an individual operation within that request.
- Parent Span ID identifies the operation that initiated the current span.

---

## 3. Instrumentation

Instrumentation means adding the ability to collect observability information from an application.

In simple terms, it helps an application generate telemetry data.

There are two common approaches.

### 3.1 Automatic Instrumentation

The instrumentation library automatically captures information about supported operations.

For example:
- Incoming HTTP requests
- Outgoing HTTP requests
- Database calls
- Framework operations

We do not need to manually create every span.

### 3.2 Manual Instrumentation

We explicitly add instrumentation code to capture custom business operations.

For example:

```java
Span span = tracer.spanBuilder("place-order").startSpan();

try (Scope scope = span.makeCurrent()) {

    orderService.placeOrder();

} catch (Exception ex) {

    span.recordException(ex);
    span.setStatus(StatusCode.ERROR);

    throw ex;

} finally {

    span.end();
}
```

Manual instrumentation is useful when we want to monitor custom business operations that automatic instrumentation does not capture.

---

## 4. What is Telemetry?

Telemetry is the information collected from an application or system and transmitted to an observability platform.

The three main types of telemetry are:

1. Logs
2. Metrics
3. Traces

For example:

```text
Spring Boot Application
       |
       |---- Logs
       |
       |---- Metrics
       |
       |---- Traces
       |
       v
Telemetry Collection
       |
       v
Observability Backend
```

Instrumentation generates or captures telemetry, and telemetry pipelines transport it to monitoring and tracing systems.

---

## 5. What is Micrometer?

Micrometer is an application metrics instrumentation and facade library for Java applications.

It provides a common API to collect application metrics without tightly coupling the application to one monitoring backend.

For example:

```java
Counter counter = Counter.builder("orders.created")
        .description("Total orders created")
        .register(meterRegistry);

counter.increment();
```

Micrometer can export metrics to monitoring systems such as Prometheus.

### Micrometer vs OpenTelemetry

| Micrometer | OpenTelemetry |
|---|---|
| Primarily known for application metrics instrumentation | Provides APIs, SDKs and instrumentation for traces, metrics and logs |
| Commonly used in Spring Boot applications | Vendor-neutral observability framework |
| Supports multiple metrics registries | Supports multiple telemetry exporters and protocols |
| Provides a common metrics facade | Provides a broader observability ecosystem |

**Important:** Micrometer and OpenTelemetry are not competitors. They can work together.

Micrometer Observation can also represent an observation that produces metrics and, with appropriate tracing integration, spans.

---

## 6. What is OpenTelemetry?

OpenTelemetry (OTel) is an open-source, vendor-neutral observability framework.

It provides APIs, SDKs, instrumentation libraries and tools to generate, collect and export telemetry.

It supports:

- Distributed tracing
- Metrics
- Logs
- Context propagation
- Automatic instrumentation
- Manual instrumentation

### Why do we need OpenTelemetry?

Suppose we have multiple microservices.

Without a common instrumentation approach, each service might use different libraries and monitoring systems.

OpenTelemetry provides a standardized way to generate and export telemetry.

For example:

```text
Order Service
      |
      | OpenTelemetry
      |
Inventory Service
      |
      | OpenTelemetry
      |
Payment Service
```

All services can export telemetry through a common collection pipeline.

### OpenTelemetry Spring Boot Starter

For Spring Boot applications, we can use the OpenTelemetry Spring Boot Starter.

Maven dependency:

```xml
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
</dependency>
```

The instrumentation BOM manages the instrumentation dependency versions.

Example:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.opentelemetry.instrumentation</groupId>
            <artifactId>opentelemetry-instrumentation-bom</artifactId>
            <version>2.33.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Keep the OpenTelemetry instrumentation BOM version separate from Spring Boot's `opentelemetry.version` property.

---

## 7. What is OTLP?

OTLP stands for OpenTelemetry Protocol.

It is the standard protocol used to transmit telemetry data between OpenTelemetry components.

It supports:

- Traces
- Metrics
- Logs

Two commonly used transport protocols are:

| Protocol | Port | Purpose |
|---|---:|---|
| OTLP gRPC | 4317 | Sends telemetry using gRPC |
| OTLP HTTP | 4318 | Sends telemetry using HTTP |

Example:

```text
Spring Boot Application
        |
        | OTLP HTTP
        | localhost:4318
        |
        v
OpenTelemetry Collector
```

**Interview correction:** It is OTLP, not OLTP. OLTP generally means Online Transaction Processing in database terminology.

---

## 8. What is the OpenTelemetry Collector?

The OpenTelemetry Collector is a separate service that receives, processes and exports telemetry data.

It acts as a central telemetry pipeline.

Instead of configuring every microservice to communicate with different observability backends, we can send telemetry to one Collector.

The Collector has three major components.

### 8.1 Receivers

Receivers accept telemetry data from applications or other sources.

Examples:

- OTLP receiver
- Prometheus receiver
- Jaeger receiver

### 8.2 Processors

Processors modify or prepare telemetry before exporting it.

Examples:

- Batch processor
- Memory limiter
- Attributes processor
- Resource processor

### 8.3 Exporters

Exporters send telemetry data to observability backends.

Examples:

- Zipkin exporter
- OTLP exporter
- Prometheus exporter
- Debug exporter

### Collector Architecture

```text
Spring Boot Services
        |
        | OTLP
        v
+---------------------------+
| OpenTelemetry Collector   |
|                           |
|  1. Receivers             |
|  2. Processors            |
|  3. Exporters             |
+---------------------------+
        |
        v
Observability Backend
```

The Collector can also be deployed as an agent near applications or as a centralized gateway.

---

# 9. Tracing Backend Systems

A tracing backend stores, indexes and allows us to search and visualize distributed traces.

Some popular tracing backends are:

### 9.1 Zipkin

Zipkin is a distributed tracing system.

It helps us:
- Search traces.
- View spans.
- Understand service dependencies.
- Identify latency.
- Debug distributed requests.

UI:

```text
http://localhost:9411
```

### 9.2 Jaeger

Jaeger is another distributed tracing platform.

It provides:
- Trace search
- Span visualization
- Service dependency analysis
- Latency analysis
- Distributed request debugging

UI:

```text
http://localhost:16686
```

### 9.3 Grafana Tempo

Tempo is Grafana's distributed tracing backend.

It stores and queries traces and integrates with Grafana dashboards.

It can receive traces through OTLP.

Tempo is useful when building a Grafana-based observability platform.

**Important:** Grafana and Tempo are different.

- Tempo is the tracing backend.
- Grafana is the visualization and dashboard platform.

---

# 10. End-to-End Configuration: Spring Boot + OpenTelemetry + Collector + Jaeger

Now let's understand how to configure distributed tracing in a Spring Boot microservices project.

## Step 1: Understand the architecture

Consider two microservices:

- Order Service: Port 8003
- Inventory Service: Port 8001

Order Service calls Inventory Service using OpenFeign.

Our architecture:

```text
             Client
                |
                v
         Order Service
             :8003
                |
                | Feign HTTP Call
                |
                v
        Inventory Service
             :8001
                |
                |
                v
       OpenTelemetry Collector
                |
                | OTLP
                |
                v
              Jaeger
                |
                v
            Jaeger UI
```

The important point is that both services generate telemetry and the Collector exports it to Jaeger.

## Step 2: Add OpenTelemetry dependencies

Add the OpenTelemetry Spring Boot Starter to both services.

```xml
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
</dependency>
```

For the instrumentation BOM, use:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.opentelemetry.instrumentation</groupId>
            <artifactId>opentelemetry-instrumentation-bom</artifactId>
            <version>2.33.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

For the Order Service, if using Spring Cloud OpenFeign with Micrometer observations, add:

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-micrometer</artifactId>
</dependency>
```

The Feign Micrometer observation integration and OpenTelemetry Micrometer instrumentation must also be configured and available at runtime.

## Step 3: Configure Order Service

Example `application.yml`:

```yaml
spring:
  application:
    name: ecom-order-service

  cloud:
    openfeign:
      micrometer:
        enabled: true

server:
  port: 8003

otel:
  file_format: "1.0"

  resource:
    attributes:
      - name: service.name
        value: ecom-order-service

  propagator:
    composite:
      - tracecontext:
      - baggage:

  tracer_provider:
    processors:
      - batch:
          exporter:
            otlp_http:
              endpoint: http://localhost:4318/v1/traces
```

## Step 4: Configure Inventory Service

Example `application.yml`:

```yaml
spring:
  application:
    name: ecom-inventory-service

server:
  port: 8001

otel:
  file_format: "1.0"

  resource:
    attributes:
      - name: service.name
        value: ecom-inventory-service

  propagator:
    composite:
      - tracecontext:
      - baggage:

  tracer_provider:
    processors:
      - batch:
          exporter:
            otlp_http:
              endpoint: http://localhost:4318/v1/traces
```

The service names must be unique so that we can identify each service in the tracing backend.

Note: The configuration above uses the declarative OpenTelemetry Spring Boot Starter configuration format. Verify that the selected starter version supports your Spring Boot version and that your runtime instrumentation includes the HTTP client being used.

## Step 5: Start the OpenTelemetry Collector

Create a file named:

```text
otel-collector-config.yaml
```

Example configuration:

```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318

processors:
  batch:

exporters:
  otlp/jaeger:
    endpoint: jaeger:4317
    tls:
      insecure: true

  debug:
    verbosity: detailed

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlp/jaeger, debug]

    metrics:
      receivers: [otlp]
      processors: [batch]
      exporters: [debug]

    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [debug]
```

Explanation:

- Receiver: Accepts telemetry from both services.
- Processor: Batches telemetry before export.
- Exporter: Sends traces to Jaeger.
- Debug exporter: Prints telemetry information in Collector logs.

This example sends traces to Jaeger, while metrics and logs are printed through the debug exporter. They are not yet stored in a metrics or logs backend.

## Step 6: Start Jaeger using Docker

Create a Jaeger container:

```powershell
docker run -d `
  --name jaeger `
  --network observability `
  -p 16686:16686 `
  jaegertracing/jaeger:latest
```

The Collector and Jaeger must share a Docker network so that the Collector can resolve `jaeger`.

Start the OpenTelemetry Collector with the configuration file mounted into the container:

```powershell
docker run -d `
  --name otel-collector `
  --network observability `
  -p 4317:4317 `
  -p 4318:4318 `
  -v "${PWD}/otel-collector-config.yaml:/etc/otelcol-contrib/config.yaml" `
  otel/opentelemetry-collector-contrib:latest `
  --config=/etc/otelcol-contrib/config.yaml
```

If you already have an `otel-collector` container, update its existing configuration and restart it instead of creating another container with the same name.

## Step 7: Start both microservices

Start:

```text
Inventory Service -> localhost:8001
Order Service     -> localhost:8003
```

Call:

```http
GET http://localhost:8003/orders/1
```

This should trigger the following flow:

```text
Order Service
     |
     | Feign HTTP request
     v
Inventory Service
```

## Step 8: Verify traces in Jaeger

Open:

```text
http://localhost:16686
```

Select:

```text
Service: ecom-order-service
```

Click Find Traces.

Open a trace and verify:

```text
Order Service
      |
      v
Inventory Service
```

Both spans should have the same Trace ID.

If only the Order Service appears, check:

- Whether the Feign request actually reaches Inventory.
- Whether the Feign HTTP client is instrumented.
- Whether the trace context is propagated.
- Whether Inventory successfully exports its spans.

---

# 11. Zipkin vs Jaeger vs Grafana Tempo

| Feature | Zipkin | Jaeger | Grafana Tempo |
|---|---|---|---|
| Purpose | Distributed tracing | Distributed tracing | Distributed tracing |
| Trace search | Yes | Yes | Yes, through Grafana |
| Span visualization | Yes | Yes | Yes |
| OTLP support | Through Collector/exporter integrations | Native OTLP ingestion | Native OTLP ingestion |
| Grafana integration | Supported | Supported | Native integration |
| Typical use | Simple tracing setup | Distributed tracing analysis | Grafana observability stack |

They are alternative tracing backends. We generally don't need all three in one production architecture.

---

# 12. Important Interview Questions

### Q1. What is observability?

Observability is the ability to understand the internal state of a system using the telemetry it generates, mainly logs, metrics and traces.

### Q2. What is the difference between monitoring and observability?

Monitoring tells us whether a system is working as expected.

Observability helps us investigate why something is happening inside the system.

### Q3. What is distributed tracing?

Distributed tracing tracks a single request across multiple services using Trace IDs and Span IDs.

### Q4. What is the difference between Trace ID and Span ID?

Trace ID identifies the complete distributed request.

Span ID identifies one individual operation within that request.

### Q5. What is instrumentation?

Instrumentation is the process of adding the ability to capture telemetry from an application, either automatically or manually.

### Q6. What is OpenTelemetry?

OpenTelemetry is a vendor-neutral framework for generating, collecting and exporting traces, metrics and logs.

### Q7. What is OTLP?

OTLP is the OpenTelemetry Protocol used to transport telemetry between applications, Collectors and backends.

### Q8. Why do we need an OpenTelemetry Collector?

The Collector provides a centralized pipeline to receive, process and export telemetry without tightly coupling applications to individual backends.

### Q9. What are the three major components of the Collector?

1. Receivers
2. Processors
3. Exporters

### Q10. What is the difference between Micrometer and OpenTelemetry?

Micrometer provides a common instrumentation API, especially for application metrics and observations in Spring applications.

OpenTelemetry provides a broader vendor-neutral observability framework covering traces, metrics and logs.

### Q11. Can we use Jaeger and Zipkin together?

Yes. The Collector can export traces to multiple backends simultaneously, provided the appropriate exporters are configured.

However, this is not mandatory. Usually one tracing backend is sufficient.

### Q12. Is Grafana a tracing backend?

Grafana is primarily an observability visualization and dashboard platform. It integrates with tracing backends such as Tempo, Jaeger and Zipkin.

---

# 13. Final Architecture to Remember

```text
             Spring Boot Microservices
                       |
          +------------+------------+
          |            |            |
          v            v            v
         Logs        Metrics       Traces
          |            |            |
          +------------+------------+
                       |
                       v
              OpenTelemetry
              Instrumentation
                       |
                       v
                OTLP Protocol
                       |
                       v
           OpenTelemetry Collector
                       |
          +------------+------------+
          |            |            |
          v            v            v
       Loki       Prometheus      Tempo
       Logs        Metrics       Traces
          |            |            |
          +------------+------------+
                       |
                       v
                    Grafana
                       |
                       v
              Unified Dashboard
```

**Remember this sequence for interviews:**

1. Instrumentation captures telemetry.
2. OpenTelemetry provides the APIs, SDKs and instrumentation ecosystem.
3. OTLP transports telemetry.
4. OpenTelemetry Collector receives, processes and exports telemetry.
5. Backend systems store and make telemetry searchable.
6. Grafana visualizes the information.

This is the overall observability architecture used to monitor and troubleshoot distributed microservices.

# Event-Driven Architecture and RabbitMQ

## 1. Event-Driven Architecture (EDA)

Event-Driven Architecture is a software architecture in which services communicate with each other by publishing and consuming events.

An event represents something that has already happened in the system.

Examples:

- OrderCreated
- PaymentCompleted
- ProductUpdated
- UserRegistered
- OrderCancelled

### Why do we need Event-Driven Architecture?

In a traditional synchronous architecture, one service directly communicates with another service and waits for its response.

For example:

Customer → Order Service → Inventory Service → Payment Service → Notification Service

Problems:

- The user may need to wait for multiple services to respond.
- If one downstream service is slow, the entire request can become slow.
- If one service is unavailable, it can affect the calling service.
- Services become tightly coupled.

To overcome these problems, we can use asynchronous communication through messaging systems such as:

- RabbitMQ
- Apache Kafka
- AWS SNS
- AWS SQS

Instead of directly calling another service, the publisher publishes an event to a messaging system. Other services consume that event independently.

### Main components of EDA

**1. Publisher / Producer**

- Responsible for publishing events.
- Does not need to know which services will consume the event.
- Can continue its own processing without waiting for consumers to finish.

**2. Event Broker / Messaging System**

- Receives messages from publishers.
- Routes messages to appropriate queues or subscribers.
- Temporarily stores messages until they are delivered and acknowledged, depending on the messaging system and configuration.

Examples: RabbitMQ and Kafka.

**3. Subscriber / Consumer**

- Receives and processes published events.
- Can perform its own business logic independently.
- Acknowledges successful message processing where the messaging system supports acknowledgements.

### Event-Driven Architecture flow

Publisher → Event Broker → Consumer

Example:

1. Customer places an order.
2. Order Service publishes an `OrderCreated` event.
3. RabbitMQ receives the event.
4. Inventory Service consumes the event.
5. Inventory Service reserves the requested product quantity.

**Important:** Asynchronous communication does not automatically make every operation faster. It helps reduce waiting between services and allows background processing.

---

## 2. Synchronous vs Asynchronous Communication

| Synchronous                     | Asynchronous                                 |
| ------------------------------- | -------------------------------------------- |
| Caller waits for response       | Caller does not wait for consumer processing |
| Usually direct communication    | Usually broker-based communication           |
| Tighter runtime dependency      | Looser coupling                              |
| Can increase request latency    | Can reduce request latency                   |
| Example: REST API, Feign Client | Example: RabbitMQ, Kafka                     |

### When to use synchronous communication?

Use synchronous communication when:

- An immediate response is required.
- The next operation depends on the response.
- You need to validate information before continuing.

Example: Checking whether a product exists before creating an order.

### When to use asynchronous communication?

Use asynchronous communication when:

- Processing can happen in the background.
- Multiple services need to react to an event.
- The publisher should not wait for downstream processing.

Example: Sending order confirmation emails after an order is placed.

---

# 3. RabbitMQ

RabbitMQ is a message broker that implements messaging protocols such as AMQP (Advanced Message Queuing Protocol).

It allows different applications and microservices to communicate asynchronously through messages.

### Why RabbitMQ?

- Supports asynchronous communication.
- Decouples services.
- Supports message acknowledgement.
- Supports message routing.
- Supports retry and dead-letter queues.
- Supports multiple consumers.
- Supports durable queues and persistent messages.

## 4. RabbitMQ Big Picture Architecture

The important components are:

1. Publisher
2. Exchange
3. Binding
4. Queue
5. Consumer
6. Message

### RabbitMQ architecture flow

Publisher → Exchange → Binding → Queue → Consumer

**Publisher**

Publishes a message to an exchange.

**Exchange**

Receives messages from publishers and decides where to route them.

**Binding**

Defines the relationship between an exchange and a queue. It can include a routing key or binding arguments.

**Queue**

Stores messages until they are delivered to consumers and acknowledged.

**Consumer**

Receives messages from the queue and processes them.

**Important:** The publisher generally publishes to an exchange, not directly to a queue (except when using the default exchange).

---

## 5. AMQP

AMQP stands for Advanced Message Queuing Protocol.

It is a messaging protocol used for communication between applications and message brokers.

RabbitMQ supports AMQP 0-9-1, which is commonly used with Spring AMQP.

Spring Boot provides the following important components:

- `RabbitTemplate` – used to publish messages.
- `@RabbitListener` – used to consume messages.
- `RabbitAdmin` – used to declare exchanges, queues and bindings.
- `MessageConverter` – converts Java objects to message payloads and vice versa.

---

## 6. RabbitMQ Exchange Types

An exchange determines how messages are routed to queues.

RabbitMQ supports four commonly used exchange types.

### 1. Direct Exchange

Routes messages to queues based on an exact routing key match.

Example:

Exchange: `order.exchange`

Routing key: `order.created`

Queue: `inventory.order.queue`

If the message routing key matches the binding routing key, RabbitMQ routes the message to that queue.

Use cases:

- Order processing.
- Payment processing.
- Specific event routing.

### 2. Fanout Exchange

Routes a message to all queues bound to the exchange.

It ignores the routing key.

Example:

A customer registers.

The User Service publishes `UserRegistered`.

The following services receive the event:

- Email Service
- Notification Service
- Analytics Service

Each service should have its own queue.

Use cases:

- Notifications.
- Broadcasting events.
- Multiple independent subscribers.

### 3. Topic Exchange

Routes messages based on routing key patterns.

Special characters:

- `*` – matches exactly one word.
- `#` – matches zero or more words.

Example routing keys:

- `order.created`
- `order.cancelled`
- `payment.completed`

Example bindings:

| Binding key | Messages received               |
| ----------- | ------------------------------- |
| `order.*`   | All order events with two words |
| `order.#`   | All events starting with order  |
| `*.created` | All events ending with created  |

Use cases:

- Event-based routing.
- Multiple event categories.
- Flexible routing requirements.

### 4. Headers Exchange

Routes messages based on message headers instead of routing keys.

It uses header values and matching rules such as `all` or `any`.

Use cases:

- Routing based on message metadata.
- Complex routing conditions.

### Exchange comparison

| Exchange | Routing mechanism    |
| -------- | -------------------- |
| Direct   | Exact routing key    |
| Fanout   | All bound queues     |
| Topic    | Routing key patterns |
| Headers  | Message headers      |

---

# 7. RabbitMQ Message Structure

A RabbitMQ message contains three important parts.

### 1. Message Payload

The actual business data being transmitted.

Example:

```json
{
  "eventId": "12345",
  "productId": "101",
  "quantity": 2,
  "status": "CREATED"
}
```

### 2. Message Properties and Headers

Additional information associated with the message.

Examples:

- Content type
- Content encoding
- Message ID
- Correlation ID
- Timestamp
- Delivery mode
- Custom headers

### 3. Exchange and Routing Key

The exchange determines how the message is routed.

The routing key provides routing information to the exchange.

Example:

Exchange: `order.exchange`

Routing key: `order.created`

---

# 8. Message Converter

A message converter converts Java objects into message payloads and converts received payloads back into Java objects.

Without a JSON message converter, Spring AMQP's default converter generally uses Java object serialization for supported serializable objects.

For microservices, JSON is commonly preferred because it is language-independent.

### Java object to JSON

Publisher:

```java
rabbitTemplate.convertAndSend(
    "order.exchange",
    "order.created",
    orderCreatedEvent
);
```

The Jackson JSON message converter converts the Java object into JSON.

### JSON to Java object

Consumer:

```java
@RabbitListener(queues = "inventory.order.queue")
public void consumeOrder(OrderCreatedEvent event) {

    System.out.println(event.getProductId());
}
```

Spring converts the JSON payload into the Java DTO.

For Spring AMQP 4, use:

```java
@Bean
public MessageConverter jacksonMessageConverter() {
    return new JacksonJsonMessageConverter();
}
```

Use the corresponding converter in both publisher and consumer applications.

---

# 9. Message Acknowledgement

Acknowledgement tells RabbitMQ whether a consumer successfully processed a message.

RabbitMQ supports different acknowledgement behaviours.

### 1. ACK (Acknowledgement)

The consumer successfully processes the message.

RabbitMQ removes the message from the queue.

### 2. NACK (Negative Acknowledgement)

The consumer indicates that it could not successfully process the message.

Depending on the configuration, RabbitMQ can:

- Requeue the message.
- Discard it.
- Route it to a dead-letter exchange if configured.

### 3. Reject

Rejects an individual message.

It can also requeue or discard the message.

### Acknowledgement flow

Message delivered → Consumer processes message → ACK → Message removed.

If processing fails:

Message delivered → Consumer processing fails → NACK → Requeue or Dead Letter Queue.

### Acknowledgement modes in Spring AMQP

**AUTO**

Spring acknowledges the message when the listener successfully completes. If the listener throws an exception, the container applies its configured error and retry behaviour.

**MANUAL**

The application explicitly acknowledges or rejects the message using the channel.

**NONE**

Messages are automatically acknowledged by the broker when delivered. This provides weaker delivery guarantees because the consumer may fail after delivery without processing the message.

For most initial Spring Boot consumer implementations, AUTO acknowledgement is a suitable starting point.

---

# 10. Retry Mechanism and Redelivery

Sometimes a consumer fails to process a message because of temporary issues.

Examples:

- Database temporarily unavailable.
- External API timeout.
- Network failure.

Instead of immediately losing the message, we can retry processing.

### Retry

Retry means attempting to process the same message again after a failure.

Example:

1. Consumer receives the message.
2. Database operation fails.
3. Consumer retries processing.
4. If processing succeeds, the message is acknowledged.
5. If retries are exhausted, the message can be sent to a Dead Letter Queue.

### Redelivery

Redelivery means RabbitMQ delivers a previously delivered but unacknowledged or requeued message again.

A message can be redelivered after:

- Consumer rejection with requeue enabled.
- Consumer connection failure before acknowledgement.
- Consumer channel or connection closure while processing.

RabbitMQ provides a redelivery indicator in message delivery metadata.

**Important:** Redelivery does not necessarily mean your business logic was executed. It means the message was delivered previously.

---

# 11. Dead Letter Queue (DLQ)

A Dead Letter Queue stores messages that cannot be processed successfully under the configured dead-letter rules.

Messages can be dead-lettered when:

- A consumer rejects a message without requeueing.
- A message expires.
- A queue exceeds its configured maximum length.
- A quorum queue exceeds its delivery limit.

### Why do we need DLQ?

Suppose an order event repeatedly fails because its product information is invalid.

If we continuously retry the same message, it can consume resources and block useful processing.

Instead, after a defined retry policy, we can route the message to a DLQ.

A developer can inspect the failed message and investigate the reason.

### DLQ architecture

Main Exchange → Main Queue → Consumer

If processing repeatedly fails:

Consumer → Retry Mechanism → DLQ Exchange → DLQ Queue

### Production recommendation

- Configure a limited retry count.
- Use a delay between retries.
- Configure a DLQ.
- Monitor failed messages.
- Provide a mechanism to inspect and replay messages safely.

Avoid infinite immediate retries.

---

# 12. Concurrency and Multiple Consumers

RabbitMQ supports multiple consumers for the same queue.

Example:

`inventory.order.queue`

Consumers:

- Inventory Consumer 1
- Inventory Consumer 2
- Inventory Consumer 3

RabbitMQ distributes messages among available consumers.

This allows parallel processing and improves throughput.

However, messages from a single queue are distributed among consumers; they are not broadcast to every consumer.

### Important distinction

If three different services need the same event, create three separate queues bound to the same exchange.

Example:

`order.exchange`

- `inventory.order.queue`
- `notification.order.queue`
- `analytics.order.queue`

Each service receives its own copy of the event.

---

# 13. Production Example: E-Commerce Order Processing

Let's understand how RabbitMQ is used in a real microservices architecture.

### Services

1. Order Service
2. Inventory Service
3. Payment Service
4. Notification Service

### Scenario: Customer places an order

**Step 1: Order Service**

Customer places an order.

Order Service:

- Validates the order.
- Saves the order in its database.
- Publishes an `OrderCreated` event.

**Step 2: RabbitMQ**

RabbitMQ receives the event and routes it to the appropriate queues.

**Step 3: Inventory Service**

Inventory Service consumes `OrderCreated`.

It:

- Checks product availability.
- Reserves the requested quantity.
- Publishes an `InventoryReserved` or `InventoryRejected` event.

**Step 4: Payment Service**

Payment Service consumes the appropriate event.

It:

- Initiates payment.
- Processes the payment.
- Publishes `PaymentCompleted` or `PaymentFailed`.

**Step 5: Notification Service**

Notification Service consumes the relevant events.

It:

- Sends an order confirmation.
- Sends payment status updates.
- Notifies the customer.

### Architecture

```text
                 Order Service
                       |
                       |
                 OrderCreated
                       |
                       v
                RabbitMQ Exchange
                       |
          +------------+-------------+
          |            |             |
          v            v             v
     Inventory     Analytics    Notification
       Queue         Queue         Queue
          |            |             |
          v            v             v
     Inventory     Analytics    Notification
      Service       Service       Service
          |
          |
    InventoryReserved
          |
          v
    RabbitMQ Exchange
          |
          v
     Payment Queue
          |
          v
     Payment Service
```

This architecture allows services to react to events independently.

### Important production concept: Saga Pattern

When a business transaction spans multiple microservices, a single database transaction usually cannot cover all services.

For example:

1. Order created.
2. Inventory reserved.
3. Payment failed.

We need to release the reserved inventory and update the order status.

This is where the Saga Pattern becomes useful.

A Saga coordinates a sequence of local transactions and uses compensating actions when something fails.

Example:

- Reserve inventory.
- Process payment.
- If payment fails, release inventory.
- Cancel or update the order.

This is useful for distributed business transactions.

---

# 14. Important Production Considerations

Understanding RabbitMQ is not only about publishing and consuming messages. Reliability is equally important.

### 1. Durable Queue

A durable queue survives a RabbitMQ broker restart.

Example:

```java
@Bean
public Queue orderQueue() {
    return QueueBuilder.durable("inventory.order.queue").build();
}
```

### 2. Persistent Messages

Persistent messages are stored with delivery mode set to persistent.

For example, Spring AMQP can publish messages with persistent delivery mode.

Durable queues and persistent messages together improve message survival across broker restarts, but production reliability also depends on broker configuration and storage.

### 3. Publisher Confirms

Publisher confirms allow the producer to know whether RabbitMQ has accepted responsibility for a published message.

Important: A publisher confirm does not mean the consumer has processed the message.

### 4. Idempotency

A message may be delivered more than once.

Therefore, consumers should be designed to handle duplicate messages safely.

Example:

If the same `OrderCreated` event arrives twice, Inventory Service should not reserve the same product quantity twice.

A common approach is to maintain a table of processed event IDs.

Before processing:

1. Check whether the event ID has already been processed.
2. If yes, skip duplicate processing.
3. Otherwise, process the event and record the event ID.

### 5. Transactional Outbox Pattern

Consider this scenario:

1. Order Service saves an order in its database.
2. It publishes an event to RabbitMQ.
3. RabbitMQ publishing fails.

Now the order exists in the database, but the event was never published.

The Transactional Outbox Pattern solves this problem.

In the same database transaction:

- Save the order.
- Save the event in an outbox table.

A separate publisher reads the outbox table and publishes pending events to RabbitMQ.

After successful publication, the event is marked as published.

This helps avoid losing events between database persistence and message publishing.

### 6. Message Ordering

RabbitMQ queues generally preserve enqueue order, but multiple consumers, requeues, priorities and parallel processing can affect the order in which messages are processed.

If strict ordering is required, design the queue and consumer concurrency accordingly.

### 7. Monitoring

Monitor:

- Ready messages.
- Unacknowledged messages.
- Consumer count.
- Queue depth.
- Message publishing rate.
- Message acknowledgement rate.
- Dead-lettered messages.
- Consumer failures.

---

# 15. RabbitMQ vs Kafka vs AWS SNS vs AWS SQS

| Feature             | RabbitMQ                                | Kafka                                | SNS                              | SQS                             |
| ------------------- | --------------------------------------- | ------------------------------------ | -------------------------------- | ------------------------------- |
| Type                | Message broker                          | Distributed event streaming platform | Pub/sub messaging service        | Managed message queue           |
| Main concept        | Exchanges and queues                    | Topics and partitions                | Topics and subscriptions         | Queues                          |
| Message consumption | Consumer acknowledgement                | Consumer offsets                     | Push/fanout delivery             | Consumer polling                |
| Message retention   | Generally until acknowledged or removed | Configurable retention               | Depends on subscription delivery | Configurable retention          |
| Routing             | Exchange types and bindings             | Topic partitions                     | Topic subscriptions and filters  | Queue-based                     |
| Common use case     | Task and event messaging                | High-throughput event streaming      | Event broadcasting               | Decoupled background processing |

### When to use RabbitMQ?

- Background job processing.
- Task distribution.
- Message routing.
- Microservice communication.
- Request/reply messaging.

### When to use Kafka?

- High-throughput event streaming.
- Event history and replay.
- Real-time data pipelines.
- Analytics and stream processing.
- Multiple independent consumers reading retained events.

### When to use SNS?

- Broadcasting events to multiple subscribers.
- AWS-based pub/sub architectures.
- Fanout to SQS queues, Lambda and other supported endpoints.

### When to use SQS?

- Managed asynchronous task queues.
- Decoupling AWS services.
- Background job processing.
- Reliable queue-based workloads without managing a broker.

---

# 16. Important Interview Questions

**1. What is Event-Driven Architecture?**

It is an architecture in which services communicate by publishing and consuming events instead of depending entirely on direct synchronous calls.

**2. What is the difference between a producer and a consumer?**

A producer publishes messages, whereas a consumer receives and processes messages.

**3. What is RabbitMQ?**

RabbitMQ is a message broker that supports asynchronous communication using messaging protocols such as AMQP.

**4. What is an exchange?**

An exchange receives messages from publishers and routes them to queues based on its exchange type and binding rules.

**5. What is a binding?**

A binding connects an exchange to a queue and defines routing conditions.

**6. What is a routing key?**

A routing key is a message property used by exchanges such as Direct and Topic exchanges to determine the destination queue.

**7. What is the difference between Direct and Topic exchanges?**

Direct exchanges use exact routing key matching, whereas Topic exchanges use pattern-based matching.

**8. What is the difference between ACK and NACK?**

ACK indicates successful message processing. NACK indicates unsuccessful processing and allows the message to be requeued or rejected.

**9. What is a DLQ?**

A Dead Letter Queue stores messages that have been dead-lettered because of rejection, expiration, queue limits or other configured conditions.

**10. What is idempotency?**

Idempotency means processing the same message multiple times should not cause duplicate business effects.

**11. What happens if a consumer crashes before acknowledging a message?**

If the message has not been acknowledged, RabbitMQ can redeliver it after the consumer connection or channel closes.

**12. Can multiple consumers consume from the same queue?**

Yes. RabbitMQ distributes messages among consumers. It does not broadcast every message to all consumers on the same queue.

**13. How can multiple services receive the same event?**

Create separate queues for each service and bind all those queues to the same exchange.

**14. What is a publisher confirm?**

It allows the producer to know whether RabbitMQ has accepted a published message. It does not confirm consumer processing.

**15. What is the Transactional Outbox Pattern?**

It ensures business data and the event to be published are saved in the same database transaction. A separate publisher then sends the event to the broker.

---

# 17. My Learning Checklist

- [ ] Understand synchronous vs asynchronous communication.
- [ ] Understand Event-Driven Architecture.
- [ ] Understand Publisher, Broker and Consumer.
- [ ] Understand AMQP.
- [ ] Understand RabbitMQ architecture.
- [ ] Understand Exchange, Queue, Binding and Routing Key.
- [ ] Understand Direct, Fanout, Topic and Headers exchanges.
- [ ] Understand Message Payload, Headers and Properties.
- [ ] Understand MessageConverter.
- [ ] Implement a RabbitMQ publisher using Spring Boot.
- [ ] Implement a RabbitMQ consumer using `@RabbitListener`.
- [ ] Understand ACK, NACK and message redelivery.
- [ ] Implement retry and DLQ.
- [ ] Understand multiple consumers and concurrency.
- [ ] Understand publisher confirms.
- [ ] Understand idempotency.
- [ ] Understand Transactional Outbox Pattern.
- [ ] Understand Saga Pattern.
- [ ] Understand RabbitMQ vs Kafka vs SNS vs SQS.

---

## Final Summary

Event-Driven Architecture helps us build loosely coupled microservices that communicate through events.

RabbitMQ is one of the messaging systems used to implement this architecture.

The important RabbitMQ flow is:

**Publisher → Exchange → Binding → Queue → Consumer**

For production systems, understanding message delivery, acknowledgement, retry, DLQ, idempotency, publisher confirms and the Transactional Outbox Pattern is equally important.

Start with a simple publisher and consumer implementation, then gradually add production reliability features.

# Apache Kafka – Complete Reference Notes

## 1. What is Kafka?

Apache Kafka is a **distributed event streaming platform** used to publish, store, and consume events between multiple applications or microservices.

It helps services communicate asynchronously without directly depending on each other.

**Example: E-commerce application**

When a customer places an order:

1. Order Service creates an order.
2. Order Service publishes an `OrderCreated` event to Kafka.
3. Inventory Service consumes the event and updates stock.
4. Payment Service consumes the event and processes payment.
5. Notification Service consumes the event and sends an email.

The Order Service does not need to know which services are consuming its event.

**Main benefits:**
- Loose coupling between microservices.
- Asynchronous communication.
- High throughput.
- Scalability.
- Fault tolerance.
- Event replay.
- Multiple consumers can independently process the same event.

---

## 2. Event Streaming vs Message Queue

### Message Queue

A message queue generally follows a point-to-point communication model.

- Producer sends a message to a queue.
- Consumer receives the message.
- Once successfully acknowledged, the message is generally removed from the queue.
- Another competing consumer does not normally receive that same message.

Example: RabbitMQ work queue.

### Event Streaming

Event streaming stores events in an ordered, persistent log.

- Producer publishes an event.
- Kafka stores the event in a topic partition.
- Multiple consumer groups can independently consume the same event.
- Consuming an event does not delete it.
- Events remain available according to the retention configuration.

Example: Kafka.

### Important difference

| Feature | Message Queue | Event Streaming |
|---|---|---|
| Communication | Point-to-point / Pub-Sub | Event streaming |
| Message storage | Usually until acknowledged | Based on retention policy |
| Multiple consumers | Competing consumers share messages | Different groups independently read events |
| Message replay | Depends on broker and configuration | Supported through offsets |
| Ordering | Depends on queue and configuration | Guaranteed within a partition |
| Consumer model | Commonly push-based | Poll-based |
| Main purpose | Distribute tasks/messages | Store and distribute event streams |

---

## 3. RabbitMQ vs Kafka

| Feature | RabbitMQ | Kafka |
|---|---|---|
| Type | Message broker | Distributed event streaming platform |
| Architecture | Exchanges, queues, bindings | Topics, partitions, brokers |
| Message consumption | Commonly push-based | Pull/poll-based |
| Message deletion | Usually after acknowledgement | Based on retention policy |
| Replay | Limited / plugin-dependent | Native offset-based replay |
| Ordering | Queue-level ordering with configuration considerations | Partition-level ordering |
| Throughput | Good for messaging workloads | Designed for high-throughput event streaming |
| Routing | Powerful exchange-based routing | Topic and partition-based routing |
| Consumer groups | Competing consumers and subscriptions | Native consumer groups |
| Typical use | Task queues, RPC, background jobs | Event streaming, analytics, event-driven microservices |

### When to use RabbitMQ?

- Background job processing.
- Email processing.
- Task distribution.
- Request/reply messaging.
- Complex message routing.

### When to use Kafka?

- Communication between multiple microservices.
- Event-driven architecture.
- Real-time analytics.
- High-volume event processing.
- Event replay and audit history.
- Multiple independent consumers.

**Important:** Kafka and RabbitMQ are not direct replacements in every scenario. The choice depends on the application requirements.

---

## 4. What is a Kafka Broker?

A broker is an individual Kafka server responsible for storing and managing topic partitions.

A Kafka cluster contains multiple brokers.

Example:

```text
Kafka Cluster
     |
     |---- Broker 1
     |       |---- Orders Partition 0 (Leader)
     |       |---- Payments Partition 1 (Follower)
     |
     |---- Broker 2
     |       |---- Orders Partition 1 (Leader)
     |       |---- Payments Partition 0 (Leader)
     |
     |---- Broker 3
             |---- Orders Partition 2 (Leader)
             |---- Payments Partition 1 (Leader)
```

Responsibilities of a broker:

- Store partition data.
- Handle producer requests.
- Handle consumer fetch requests.
- Manage partition replicas.
- Participate in leader/follower replication.
- Maintain metadata about the cluster.

A broker is basically a Kafka server.

---

## 5. What is a Topic?

A topic is a logical category or name under which related events are stored.

Example:

```text
orders-topic
payments-topic
inventory-topic
notification-topic
```

When an Order Service creates an order, it publishes an event to `orders-topic`.

Example event:

```json
{
  "orderId": 101,
  "product": "Laptop",
  "quantity": 2,
  "status": "CREATED"
}
```

A topic is not the actual event. It is the logical container in which events are organized.

### Topic vs Event

| Topic | Event |
|---|---|
| Logical category | Actual data/message |
| Contains multiple events | Represents one occurrence |
| Example: orders-topic | Example: OrderCreated |
| Defined in Kafka | Published by producer |

---

## 6. What is a Partition?

A partition is a physical/logical subdivision of a Kafka topic where events are stored in an ordered sequence.

Kafka divides a topic into multiple partitions to achieve:

- Parallel processing.
- Scalability.
- Distribution across brokers.
- Higher throughput.

Example:

```text
Topic: orders-topic

Partition 0:
Offset 0 -> Order 101
Offset 1 -> Order 103
Offset 2 -> Order 105

Partition 1:
Offset 0 -> Order 102
Offset 1 -> Order 104

Partition 2:
Offset 0 -> Order 106
Offset 1 -> Order 107
```

### Topic vs Partition

| Topic | Partition |
|---|---|
| Logical category | Subdivision of a topic |
| Contains multiple partitions | Contains ordered events |
| Identified by topic name | Identified by topic and partition number |
| Example: orders-topic | Partition 0, Partition 1 |

A topic can contain one or multiple partitions.

### Why do we need partitions?

Suppose we have 100,000 events.

If a topic has only one partition, processing parallelism is limited to one consumer per consumer group.

If a topic has four partitions, up to four consumers in the same consumer group can process those partitions in parallel.

---

## 7. How to Add a Topic in Kafka

Topics can be created manually using Kafka CLI or automatically through application configuration, depending on the setup.

### Create a topic

```bash
kafka-topics.sh \
--bootstrap-server localhost:9092 \
--create \
--topic orders-topic \
--partitions 3 \
--replication-factor 3
```

This creates a topic with:

- Topic name: orders-topic
- Partitions: 3
- Replication factor: 3

The replication factor cannot exceed the number of available brokers.

### List topics

```bash
kafka-topics.sh \
--bootstrap-server localhost:9092 \
--list
```

### Describe a topic

```bash
kafka-topics.sh \
--bootstrap-server localhost:9092 \
--describe \
--topic orders-topic
```

### Increase partition count

```bash
kafka-topics.sh \
--bootstrap-server localhost:9092 \
--alter \
--topic orders-topic \
--partitions 5
```

**Important:** Kafka allows increasing partitions but does not support reducing the partition count of an existing topic through the normal topic alteration command.

Also, increasing partitions can change the key-to-partition mapping for future messages.

---

## 8. How Many Partitions Should a Topic Have?

There is no fixed number.

It depends on:

- Number of consumers.
- Expected throughput.
- Message volume.
- Processing time.
- Available brokers.
- Infrastructure resources.

Example:

If we have four consumers in one consumer group and want all four to process events in parallel, we need at least four partitions.

A common starting point is to define a small number of partitions and increase them based on throughput and parallelism requirements.

Do not create hundreds of partitions without considering resource and operational overhead.

---

## 9. How Does Kafka Decide Which Partition Receives an Event?

Kafka producers decide the destination partition.

Common approaches:

**1. Key-based partitioning**

```java
kafkaTemplate.send("orders-topic", order.getOrderId(), order);
```

Kafka uses the key to determine the partition.

Events with the same key normally go to the same partition, assuming the partition count and partitioning strategy remain unchanged.

Example:

```text
Order ID 101 -> Partition 1
Order ID 101 -> Partition 1
Order ID 102 -> Partition 2
Order ID 103 -> Partition 0
```

**2. No key**

If no key is supplied, the producer uses its partitioning strategy to distribute records among partitions.

**3. Explicit partition**

A producer can directly specify the partition.

---

## 10. Ordering in Kafka

Kafka guarantees ordering **within a single partition**, not across multiple partitions.

Example:

```text
Partition 0:
Offset 0 -> Order Created
Offset 1 -> Order Paid
Offset 2 -> Order Shipped
```

The events are read in partition order.

However:

```text
Partition 0:
Order Created

Partition 1:
Order Paid

Partition 2:
Order Shipped
```

There is no guaranteed ordering across these partitions.

### Why?

Each partition maintains its own independent ordered log.

Kafka does not provide a global ordering guarantee across all partitions of a topic.

### How to maintain order for an entity?

Use a common key.

Example:

```java
kafkaTemplate.send("orders-topic", orderId, orderEvent);
```

All events for the same order will normally go to the same partition.

This helps preserve the order of events for that order.

---

## 11. What is a Consumer?

A consumer is an application that reads events from Kafka topics.

Example:

Inventory Service consumes events from `orders-topic`.

```java
@KafkaListener(
    topics = "orders-topic",
    groupId = "inventory-service-group"
)
public void consume(OrderEvent event) {

    System.out.println(event);
}
```

The consumer receives the event and performs the required business operation.

---

## 12. What is a Consumer Group?

A consumer group is a collection of consumers working together to consume events from one or more topics.

Example:

```text
Topic: orders-topic
Partitions: 3

Consumer Group: inventory-service-group

Consumer 1 -> Partition 0
Consumer 2 -> Partition 1
Consumer 3 -> Partition 2
```

Each partition is assigned to only one consumer within a particular consumer group at a time.

### Important rules

1. Multiple consumers can belong to one consumer group.
2. Multiple consumers in the same group can consume different partitions.
3. One partition cannot be actively assigned to multiple consumers in the same group at the same time.
4. Multiple consumer groups can independently consume the same partition.
5. If consumers exceed partitions, some consumers will remain idle.

### Can multiple consumers consume the same partition?

**Within the same consumer group:** No. A partition is assigned to only one consumer at a time.

**Across different consumer groups:** Yes.

Example:

```text
Topic: orders-topic
Partition 0

       |-------------------------|
       |                         |
Inventory Group             Payment Group
       |                         |
Consumer 1                 Consumer 1
       |                         |
Reads events               Reads same events
```

Both services can independently process the same event.

### Important clarification

A consumer should generally be configured with a consumer group ID when using Kafka's group management.

A consumer can also read without a group using manual partition assignment, but this is a different consumption model.

---

## 13. Kafka Message Queue vs Consumer Group

Consider RabbitMQ:

```text
Producer
   |
   v
Queue
   |
   |---- Consumer 1
   |---- Consumer 2
   |---- Consumer 3
```

Consumers compete for messages. Normally, one successfully acknowledged message is not delivered to another competing consumer.

Kafka:

```text
                 orders-topic
                      |
              Partition 0
                      |
          |-----------|-----------|
          |           |           |
       Group A     Group B     Group C
          |           |           |
       Consumer    Consumer    Consumer
          1           1           1
```

Each group independently reads the events.

Therefore, the same event can be processed by Inventory Service, Payment Service, and Notification Service.

---

## 14. What is an Offset?

An offset is a sequential position number assigned to each record within a partition.

Example:

```text
Partition 0:

Offset 0 -> Order 101
Offset 1 -> Order 102
Offset 2 -> Order 103
Offset 3 -> Order 104
```

The offset helps consumers track their reading position.

Important:

- Offset is unique within a partition, not across the entire topic.
- Different partitions can have the same offset numbers.
- Kafka assigns offsets when records are appended to a partition.

### Who allocates the offset?

The partition leader broker assigns offsets as records are appended to the partition log.

### Where is the offset stored?

There are two different concepts:

**1. Record offset**

The record's offset is stored as part of the partition log/index information.

**2. Consumer committed offset**

Kafka stores committed consumer offsets in an internal topic:

```text
__consumer_offsets
```

This topic stores consumer group progress.

Example:

```text
Consumer Group: inventory-service-group

Topic: orders-topic

Partition 0 -> Committed Offset 10
Partition 1 -> Committed Offset 15
Partition 2 -> Committed Offset 8
```

This allows consumers to continue from their previously committed position after restarting.

---

## 15. How Long Does Kafka Store Events?

Kafka stores events according to the topic's retention configuration.

It does not delete an event immediately after a consumer reads it.

Example:

```text
Retention Period: 7 days

Day 1 -> Event published
Day 2 -> Consumer reads event
Day 3 -> Another consumer reads same event
Day 7 -> Event becomes eligible for deletion
```

Kafka can delete older log segments based on retention policies.

### Retention configurations

**Time-based retention**

```properties
retention.ms=604800000
```

This represents seven days.

**Size-based retention**

```properties
retention.bytes=1073741824
```

This represents approximately 1 GB per partition.

**Log compaction**

Kafka also supports log compaction, which retains the latest value for each key (subject to compaction and deletion rules).

### Important

Consumer consumption does not determine message deletion.

Retention policy determines how long records remain available, subject to Kafka's cleanup process.

---

## 16. How Do Consumers Consume Events from Kafka?

Kafka follows a pull-based model.

It does not normally push each event to the consumer when a new event arrives.

The consumer periodically requests records from Kafka using the poll mechanism.

Flow:

```text
Producer
   |
   v
Kafka Topic
   |
   v
Consumer calls poll()
   |
   v
Kafka returns available records
   |
   v
Consumer processes records
   |
   v
Consumer commits offset
   |
   v
Consumer calls poll() again
```

### Example

```java
@KafkaListener(
    topics = "orders-topic",
    groupId = "inventory-service-group"
)
public void consume(OrderEvent event) {

    System.out.println("Received: " + event);
}
```

Spring Kafka manages the underlying polling loop and invokes the listener method when records are received.

You do not need to manually call `poll()` when using `@KafkaListener`.

---

## 17. How Does Kafka Detect New Events?

Kafka consumers continuously poll Kafka brokers.

Example:

```text
10:00:00 -> Consumer polls Kafka
10:00:01 -> No new events
10:00:02 -> Producer publishes event
10:00:03 -> Consumer polls Kafka
10:00:03 -> Consumer receives event
```

Kafka can also use long polling, where the broker can wait briefly for data before responding.

The consumer is responsible for requesting new records.

---

## 18. Offset Commit Strategies

Committing an offset means recording the consumer's progress.

Kafka supports different strategies.

### 1. Auto Commit

Kafka automatically commits offsets periodically.

```properties
enable.auto.commit=true
auto.commit.interval.ms=5000
```

Advantages:
- Simple configuration.
- Less manual code.

Disadvantages:
- The offset might be committed before processing finishes.
- If the consumer crashes, an event could be skipped.

### 2. Manual Commit

The application commits offsets after processing.

```properties
enable.auto.commit=false
```

With Spring Kafka, acknowledgment can be used:

```java
@KafkaListener(
    topics = "orders-topic",
    groupId = "inventory-service-group"
)
public void consume(
    OrderEvent event,
    Acknowledgment acknowledgment
) {

    processOrder(event);

    acknowledgment.acknowledge();
}
```

This example assumes an appropriate manual acknowledgment mode is configured.

### Delivery Guarantees

**At-most-once**

- Event is committed before processing.
- If processing fails, the event may be lost from the application's perspective.
- No duplicate processing guarantee.

**At-least-once**

- Event is processed before its offset is committed.
- If the application crashes before committing, the event may be processed again.
- Duplicates are possible.

**Exactly-once**

Kafka supports exactly-once processing in supported transactional Kafka workflows.

However, exactly-once processing across external databases, APIs, and other systems requires additional coordination or idempotency mechanisms.

For most microservice applications, idempotent consumers are important.

---

## 19. How Kafka Handles Consumer Crashes?

Suppose we have:

```text
Topic: orders-topic
Partitions: 3

Consumer 1 -> Partition 0
Consumer 2 -> Partition 1
Consumer 3 -> Partition 2
```

If Consumer 2 crashes:

1. Kafka detects that the consumer is no longer participating in the group.
2. A rebalance is triggered.
3. Partition 1 is reassigned to another active consumer.
4. The new consumer resumes from the committed offset.
5. Uncommitted records may be processed again.

Example:

```text
Before crash:

Consumer 1 -> Partition 0
Consumer 2 -> Partition 1
Consumer 3 -> Partition 2

After Consumer 2 crashes:

Consumer 1 -> Partition 0, Partition 1
Consumer 3 -> Partition 2
```

The actual reassignment depends on the group assignment strategy.

**Important:** If a consumer crashes after processing an event but before committing its offset, the event can be processed again.

---

## 20. How Does Kafka Handle Wrong Events or Invalid Data?

Kafka does not automatically understand whether an event is valid according to your application's business rules.

For example:

```json
{
  "orderId": null,
  "quantity": -10
}
```

The consumer application should validate the event.

Common approaches:

**1. Validation**

Validate incoming events before business processing.

**2. Retry**

Retry temporary failures such as database connectivity issues.

**3. Dead Letter Topic (DLT/DLQ)**

Send permanently failed events to a separate topic.

Example:

```text
orders-topic
     |
     v
Inventory Consumer
     |
     v
Processing Failed
     |
     v
orders-dlt
```

**4. Logging and monitoring**

Log the exception and track failed events.

**Important:** Invalid JSON or deserialization failures can happen before your listener method is invoked. These require appropriate deserialization error handling.

Do not retry permanent business validation failures indefinitely.

---

## 21. Kafka Is Not a Traditional Database; It Is a Distributed Log

Kafka is not designed for traditional CRUD operations.

In a relational database:

- Create records.
- Read records.
- Update records.
- Delete records.

Kafka follows an append-oriented log model.

- Producers append events.
- Consumers read events.
- Consumers track their offsets.
- Records are cleaned up based on retention or compaction policies.

Example:

```text
orders-topic

Offset 0 -> Order Created
Offset 1 -> Order Paid
Offset 2 -> Order Shipped
```

If an order status changes, the application generally publishes another event instead of updating the earlier event in place.

Example:

```text
OrderCreated
OrderPaid
OrderShipped
```

This makes Kafka useful for event-driven architectures and event history.

---

## 22. Partition Replication – Leader and Follower

Kafka supports replication to provide fault tolerance.

A partition can have:

- One leader replica.
- One or more follower replicas.

Example:

```text
Topic: orders-topic
Partition: 0
Replication Factor: 3

Broker 1 -> Leader
Broker 2 -> Follower
Broker 3 -> Follower
```

### Leader

- Handles normal producer writes.
- Handles consumer reads in the standard leader-based read path.
- Coordinates replication for the partition.

### Follower

- Fetches records from the leader.
- Maintains a replica of the partition.
- Can become the leader if the existing leader fails and it is eligible.

### What happens if the leader crashes?

```text
Before:

Broker 1 -> Leader
Broker 2 -> Follower
Broker 3 -> Follower

Broker 1 crashes.

After:

Broker 2 -> New Leader
Broker 3 -> Follower
```

The Kafka controller manages leader election.

### How Many Replicas Should We Keep?

There is no fixed rule.

A replication factor of 3 is a common production starting point.

For example:

```text
Replication Factor = 3

1 Leader + 2 Followers
```

This provides better fault tolerance than a single replica.

The actual configuration depends on availability, infrastructure, and durability requirements.

Replication factor cannot exceed the number of brokers.

---

## 23. How Does Kafka Know Which Broker Is the Leader?

Kafka maintains cluster metadata.

Metadata contains information such as:

- Available brokers.
- Topic names.
- Partition information.
- Partition leaders.
- Replica assignments.
- Controller information.

The producer and consumer use metadata to identify the correct broker for a partition.

Example:

```text
Topic: orders-topic

Partition 0 -> Leader Broker 1
Partition 1 -> Leader Broker 2
Partition 2 -> Leader Broker 3
```

If a producer wants to publish an event to Partition 1, it uses metadata to identify Broker 2 as the leader.

### What is the Kafka Controller?

The controller manages important cluster-level operations, including:

- Broker membership.
- Partition leader election.
- Replica state management.
- Metadata changes.

---

## 24. KRaft vs ZooKeeper

Kafka historically used ZooKeeper for cluster coordination and metadata management.

Newer Kafka versions support KRaft (Kafka Raft).

### ZooKeeper Architecture

```text
Kafka Cluster
     |
     v
ZooKeeper
     |
     |---- Cluster Metadata
     |---- Broker Registration
     |---- Controller Coordination
     |---- Leader Election Coordination
```

ZooKeeper was an external coordination system.

### KRaft Architecture

```text
Kafka Cluster
     |
     v
KRaft Controller Quorum
     |
     |---- Metadata Management
     |---- Controller Election
     |---- Cluster Coordination
```

KRaft uses Kafka's own metadata quorum based on the Raft consensus protocol.

### KRaft vs ZooKeeper

| Feature | ZooKeeper | KRaft |
|---|---|---|
| Coordination | External ZooKeeper | Built into Kafka |
| Metadata | Managed through ZooKeeper | Managed by Kafka metadata quorum |
| Consensus | ZooKeeper protocol | Raft-based consensus |
| Infrastructure | Separate ZooKeeper cluster | No separate ZooKeeper cluster |
| Architecture | More components | Simpler architecture |
| Scalability | Older metadata architecture | Improved metadata scalability |

**Important:** KRaft does not mean that every Kafka broker becomes a controller.

Kafka can have dedicated controller nodes, broker nodes, or combined broker/controller nodes depending on deployment configuration.

Modern Kafka deployments use KRaft. ZooKeeper mode has been deprecated and removed from modern Kafka releases.

---

## 25. What is Kafka Metadata?

Kafka metadata is information describing the Kafka cluster and its structure.

Example:

```text
Cluster
  |
  |---- Broker 1
  |---- Broker 2
  |---- Broker 3
  |
  |---- orders-topic
  |       |---- Partition 0 -> Leader Broker 1
  |       |---- Partition 1 -> Leader Broker 2
  |
  |---- payments-topic
          |---- Partition 0 -> Leader Broker 3
```

Metadata works like a routing/reference map that helps clients locate partition leaders.

It is not the actual business event data.

---

## 26. Complete End-to-End Kafka Flow

Let's understand the complete flow using an e-commerce application.

### Step 1: Producer creates an event

Customer places an order.

Order Service creates:

```json
{
  "orderId": 101,
  "product": "Laptop",
  "quantity": 2,
  "status": "CREATED"
}
```

### Step 2: Producer publishes the event

```java
kafkaTemplate.send("orders-topic", orderId, orderEvent);
```

Producer responsibilities:

- Create the event.
- Serialize the event.
- Select a partition.
- Send the event to the partition leader.
- Handle acknowledgments and retries according to configuration.

### Step 3: Broker receives the event

The partition leader:

- Receives the event.
- Appends it to the partition log.
- Assigns an offset.
- Replicates the record to followers according to replication settings.

### Step 4: Consumer polls Kafka

Inventory Service polls Kafka through its consumer.

```java
@KafkaListener(
    topics = "orders-topic",
    groupId = "inventory-service-group"
)
public void consume(OrderEvent event) {

    System.out.println("Order received: " + event);
}
```

### Step 5: Consumer processes the event

Inventory Service updates the inventory database.

### Step 6: Consumer commits the offset

Kafka stores the consumer's committed progress in `__consumer_offsets`.

### Step 7: Other consumer groups process the same event

Payment Service and Notification Service independently consume the same event using different consumer groups.

### Complete Architecture

```text
                  Customer
                      |
                      v
                 Order Service
                      |
                      v
                   Producer
                      |
                      v
                Kafka Cluster
                      |
                 orders-topic
                      |
          +-----------+-----------+
          |           |           |
      Partition 0  Partition 1  Partition 2
          |           |           |
          +-----------+-----------+
                      |
          +-----------+-----------+
          |           |           |
     Inventory     Payment    Notification
      Service      Service      Service
          |           |           |
       Group A      Group B      Group C
          |           |           |
       Database    Payment      Email
```

---

## 27. How Many Consumers Can Consume a Topic?

The number of active consumers in a single consumer group is limited by the number of partitions assigned to that group.

Example:

```text
Topic: orders-topic
Partitions: 3

Consumer Group: inventory-group

Consumer 1 -> Partition 0
Consumer 2 -> Partition 1
Consumer 3 -> Partition 2
Consumer 4 -> Idle
Consumer 5 -> Idle
```

Even though five consumers exist, only three can actively consume these three partitions in this group at a time.

If we have another consumer group:

```text
Consumer Group: payment-group

Consumer 1 -> Partition 0
Consumer 2 -> Partition 1
Consumer 3 -> Partition 2
```

This group independently consumes the same topic.

---

## 28. Adding a Consumer Group to an Existing Topic

A consumer group does not need to be created manually before consuming.

When a consumer connects with a new group ID, Kafka automatically establishes the group through group coordination.

Example:

```java
@KafkaListener(
    topics = "orders-topic",
    groupId = "notification-service-group"
)
public void consume(OrderEvent event) {

    sendNotification(event);
}
```

Even if `orders-topic` already exists and is consumed by Inventory Service, Notification Service can independently consume it.

Example:

```text
orders-topic
     |
     |---- inventory-service-group
     |
     |---- payment-service-group
     |
     |---- notification-service-group
```

Each group maintains its own committed offsets.

---

## 29. How Kafka Handles Huge Write Operations

Kafka is designed for high-throughput event ingestion.

Some important techniques:

**1. Batching**

The producer groups multiple records into batches instead of sending each record separately.

```properties
batch.size=16384
linger.ms=5
```

**2. Compression**

Kafka supports compression algorithms such as:

- GZIP
- Snappy
- LZ4
- Zstandard

**3. Sequential disk writes**

Kafka appends records to partition logs, which is efficient for sequential writes.

**4. Partitioning**

Multiple partitions allow writes to be distributed across brokers.

**5. Replication**

Follower replicas maintain copies of partition data for fault tolerance.

**6. Zero-copy transfer**

Kafka can use efficient transfer mechanisms to reduce unnecessary data copying when serving consumers.

---

## 30. What is Backpressure in Kafka?

Backpressure occurs when consumers cannot process incoming events as quickly as producers publish them.

Example:

```text
Producer:
1000 events/second

Consumer:
500 events/second
```

The consumer cannot keep up.

As a result, the consumer lag increases.

### How to handle backpressure?

- Increase the number of partitions where appropriate.
- Add more consumers to the same consumer group.
- Optimize consumer processing.
- Batch database operations.
- Avoid unnecessarily slow external API calls.
- Apply controlled rate limiting.
- Monitor consumer lag.

Remember: Adding consumers alone does not increase parallelism beyond the available partition assignments.

---

## 31. What is Consumer Lag?

Consumer lag indicates how far behind a consumer group is compared with the latest available records.

Example:

```text
Current Consumer Offset = 100
Log End Offset = 150

Consumer Lag = 50
```

This means the consumer group is behind by approximately 50 records for that partition.

Command:

```bash
kafka-consumer-groups.sh \
--bootstrap-server localhost:9092 \
--describe \
--group inventory-service-group
```

Example output:

```text
GROUP                  TOPIC         PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG

inventory-service     orders-topic  0          100             150             50
```

High lag can indicate:

- Slow processing.
- Insufficient consumers.
- Insufficient partitions.
- Database performance problems.
- Consumer failures.

---

## 32. Pub-Sub vs Kafka Event-Driven Architecture

### Traditional Pub-Sub

In a traditional publish-subscribe system:

```text
Publisher
    |
    v
Message Broker
    |
    +---- Subscriber 1
    |
    +---- Subscriber 2
    |
    +---- Subscriber 3
```

The publisher publishes a message, and subscribers receive it based on the messaging system's delivery and retention behavior.

### Kafka Event-Driven Architecture

```text
Producer
   |
   v
Kafka Topic
   |
   +---- Consumer Group A
   |
   +---- Consumer Group B
   |
   +---- Consumer Group C
```

Kafka provides durable event storage, offsets, independent consumer progress, partitioning, and replay.

**Important:** Kafka itself also supports publish-subscribe patterns. Kafka is not an alternative to Pub-Sub; it is one platform used to implement event-driven Pub-Sub architectures.

---

## 33. Partitioning Across Multiple Topics

Each topic maintains its own partitions.

Example:

```text
orders-topic
   |---- Partition 0
   |---- Partition 1
   |---- Partition 2

payments-topic
   |---- Partition 0
   |---- Partition 1

inventory-topic
   |---- Partition 0
   |---- Partition 1
   |---- Partition 2
```

Partitions are distributed across available brokers.

A broker can host partitions belonging to multiple topics.

Partition IDs are local to their respective topics.

For example:

```text
orders-topic -> Partition 0
payments-topic -> Partition 0
```

These are two different partitions.

---

## 34. How Are New Topic Partitions Created?

When creating a new topic, specify the partition count.

Example:

```bash
kafka-topics.sh \
--bootstrap-server localhost:9092 \
--create \
--topic payments-topic \
--partitions 3 \
--replication-factor 3
```

Kafka creates three partitions and distributes their replicas across eligible brokers.

If a topic is created with only one partition, its parallelism is limited to one active consumer per group.

You can increase the partition count later, but decreasing it is not supported through normal topic alteration.

---

## 35. Can a Consumer Consume Multiple Topics?

Yes.

A single consumer can subscribe to multiple topics.

Example using Spring Kafka:

```java
@KafkaListener(
    topics = {"orders-topic", "payments-topic"},
    groupId = "application-service-group"
)
public void consume(Object event) {

    System.out.println(event);
}
```

However, if different topics contain different event structures, it is better to handle their types explicitly.

You can also create separate listeners:

```java
@KafkaListener(
    topics = "orders-topic",
    groupId = "application-service-group"
)
public void consumeOrder(OrderEvent event) {

    System.out.println(event);
}

@KafkaListener(
    topics = "payments-topic",
    groupId = "application-service-group"
)
public void consumePayment(PaymentEvent event) {

    System.out.println(event);
}
```

A consumer group can subscribe to multiple topics, and its consumers can be assigned partitions from all subscribed topics.

---

## 36. Adding a New Consumer to an Existing Consumer Group

Suppose:

```text
Topic: orders-topic
Partitions: 3

Consumer Group: inventory-group

Consumer 1 -> Partition 0
Consumer 2 -> Partition 1
Consumer 3 -> Partition 2
```

Now add Consumer 4 with the same group ID.

Kafka detects the membership change and triggers a rebalance.

However, since there are only three partitions, Consumer 4 will remain idle after assignment.

If the topic has six partitions, the four consumers can share the six partitions.

Example:

```text
Consumer 1 -> Partition 0, Partition 1
Consumer 2 -> Partition 2, Partition 3
Consumer 3 -> Partition 4
Consumer 4 -> Partition 5
```

The exact assignment depends on the configured partition assignment strategy.

---

## 37. Important Kafka Responsibilities

| Component | Responsibility |
|---|---|
| Producer | Creates and publishes events |
| Producer partitioner | Determines destination partition |
| Broker / Partition Leader | Appends records and assigns offsets |
| Follower | Replicates partition data |
| Controller | Manages cluster metadata and leader elections |
| Topic | Logical category of events |
| Partition | Ordered log of events |
| Consumer | Reads and processes events |
| Consumer Group | Coordinates consumers and tracks progress |
| Offset | Identifies a record's position within a partition |
| Retention Policy | Determines how long records are retained |

---

## 38. Quick Interview Revision

**1. Is a topic and an event the same?**

No. A topic is a logical category; an event is the actual record stored in it.

**2. Can multiple consumers consume the same partition?**

Within the same consumer group, only one active consumer is assigned that partition at a time. Different groups can independently consume it.

**3. Can Kafka delete an event after consumption?**

Consumption does not delete events. Retention and cleanup policies determine deletion.

**4. Where are consumer offsets stored?**

In Kafka's internal `__consumer_offsets` topic.

**5. Who assigns offsets?**

The partition leader broker assigns offsets when records are appended.

**6. Why is ordering guaranteed only within a partition?**

Each partition is an independent ordered log. Kafka does not guarantee ordering across partitions.

**7. How can we maintain ordering for an order?**

Use the same order ID as the Kafka message key.

**8. What happens when a consumer crashes?**

Kafka triggers a rebalance and assigns its partitions to other consumers. Processing resumes from committed offsets.

**9. What happens if consumers are more than partitions?**

Some consumers remain idle within that consumer group.

**10. Can multiple microservices consume the same topic?**

Yes. Use different consumer groups.

**11. Is Kafka push-based or pull-based?**

Kafka uses a pull-based consumer model through polling.

**12. Is Kafka a database?**

No. It is a distributed event streaming platform that stores events in partition logs.

**13. What is KRaft?**

Kafka's built-in metadata management and coordination architecture based on the Raft consensus protocol.

**14. What is the difference between leader and follower?**

The leader handles normal partition reads and writes; followers replicate the leader's data.

**15. What is consumer lag?**

The difference between the latest available offset and the consumer group's committed progress, measured per partition.

**16. What is a Dead Letter Topic?**

A separate topic where events that cannot be processed successfully can be sent for investigation or later handling.

**17. Can we consume an old event again?**

Yes. We can reset a consumer group's offsets to an earlier position, provided the required records are still retained.

**18. What happens when a new consumer group is added?**

Kafka establishes the group and assigns partitions. Its starting position depends on its committed offsets and `auto.offset.reset` configuration.

**19. What happens if the leader broker fails?**

The controller can elect an eligible in-sync replica as the new leader.

**20. What is replication factor 3?**

Three replicas of a partition: one leader and two followers.

---

## 39. Most Important Concepts to Remember

```text
Kafka
 |
 |-- Cluster
 |     |
 |     |-- Brokers
 |           |
 |           |-- Topics
 |                 |
 |                 |-- Partitions
 |                       |
 |                       |-- Events
 |                       |-- Offsets
 |                       |-- Replicas
 |
 |-- Producer
 |     |
 |     |-- Publishes events
 |     |-- Selects partition
 |
 |-- Consumer
       |
       |-- Polls events
       |-- Processes events
       |-- Commits offsets
       |
       |-- Consumer Group
             |
             |-- Multiple consumers
             |-- Partition assignment
             |-- Independent offsets
```

**Final understanding:**

- Producer is responsible for publishing events.
- Kafka broker stores events in partition logs.
- Partition leader assigns offsets.
- Replicas provide fault tolerance.
- Consumer polls and processes events.
- Consumer group manages partition assignments and consumption progress.
- Different consumer groups can independently consume the same events.
- Retention policy determines how long events remain in Kafka.
- KRaft manages Kafka cluster metadata and coordination without requiring ZooKeeper.