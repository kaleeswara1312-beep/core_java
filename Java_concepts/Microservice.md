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
