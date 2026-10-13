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

It commonly uses REST APIs with clients such as `RestClient` or `WebClient` (when used in a blocking manner).

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