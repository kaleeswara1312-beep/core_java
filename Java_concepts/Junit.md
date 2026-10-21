# Spring Boot Unit Testing — JUnit 5 and Mockito

## 1. What Is Testing?

Testing is the process of checking whether our application behaves as expected.

For example, in an e-commerce application, we may need to test:

- Whether an order is placed successfully.
- Whether the correct product ID is passed to another service.
- Whether the service returns the expected response.
- Whether an error is handled correctly.

Testing helps us find bugs early and make changes without breaking existing functionality.

---

## 2. What Is Unit Testing?

**Unit testing is the process of testing a small, isolated unit of code, such as a method or class, to verify that it behaves as expected.**

For example, consider this `OrderService`:

```java
@Service
public class OrderService {

    private final RestClient restClient;
    private final FeignClientConfig feignClientConfig;

    public OrderService(
            RestClient restClient,
            FeignClientConfig feignClientConfig) {
        this.restClient = restClient;
        this.feignClientConfig = feignClientConfig;
    }

    public String placeOrder(String productId) {
        return feignClientConfig.placeOrder(productId);
    }
}
```

Here, `OrderService` depends on `FeignClientConfig`.

When unit testing `OrderService`, we generally want to test the real `placeOrder()` method without making an actual HTTP request to another microservice.

Therefore:

- `OrderService` is the real object under test.
- `FeignClientConfig` is mocked.
- `RestClient` can also be mocked if needed for construction.
- We control the dependency's response and verify the service behavior.

### Why do we use unit testing?

- Find bugs early.
- Test methods without depending on external services.
- Avoid unnecessary database and network calls.
- Verify method results and interactions.
- Make code changes with confidence.

---

## 3. Types of Testing

| Testing type | Purpose | Example |
|---|---|---|
| Unit testing | Test one class in isolation | Test `OrderService` with mocked dependencies |
| Integration testing | Test multiple components working together | Test service, database, and repository integration |
| API testing | Test HTTP endpoints and responses | Test `POST /orders` |
| End-to-end testing | Test a complete business flow | Place an order and verify its final status |

**Important:** Unit testing does not mean avoiding all real functionality. We usually execute the real code of the class under test and mock its external dependencies.

---

## 4. What Is JUnit?

JUnit is a Java testing framework used to write and execute automated tests.

It provides annotations and assertions to define test methods and check their results.

Example:

```java
@Test
void testAddition() {
    int result = 2 + 3;

    Assertions.assertEquals(5, result);
}
```

Here:

- `@Test` identifies a test method.
- `assertEquals()` checks whether the actual result equals the expected result.
- If the assertion fails, the test fails.

---

## 5. What Is JUnit 5?

JUnit 5 is the modern generation of the JUnit testing framework.

It consists of three major parts:

1. **JUnit Platform** — launches and runs tests.
2. **JUnit Jupiter** — provides the programming model and annotations for writing JUnit 5 tests.
3. **JUnit Vintage** — allows older JUnit 3 and JUnit 4 tests to run on the JUnit Platform.

In everyday Spring Boot testing, we commonly use JUnit Jupiter.

### JUnit 4 vs. JUnit 5

| Feature | JUnit 4 | JUnit 5 (Jupiter) |
|---|---|---|
| Test annotation | `org.junit.Test` | `org.junit.jupiter.api.Test` |
| Before each test | `@Before` | `@BeforeEach` |
| After each test | `@After` | `@AfterEach` |
| Extension mechanism | `@RunWith` | `@ExtendWith` |
| Assertions | `org.junit.Assert` | `org.junit.jupiter.api.Assertions` |
| Java 8 support | Supported | Designed for modern Java features |

**Interview point:** JUnit 5 introduced a more flexible extension model through `@ExtendWith`, replacing many use cases of JUnit 4's `@RunWith`.

---

## 6. What Is Mockito?

Mockito is a Java mocking framework used to create mock objects, configure their behavior, and verify interactions.

Suppose `OrderService` calls `FeignClientConfig`, which normally communicates with another service.

We do not want every unit test to depend on that external service being available.

Mockito allows us to simulate the dependency's response.

### Real object vs. mock object

| Real object | Mock object |
|---|---|
| Executes its actual implementation | Uses configured mock behavior |
| Can perform real network or database operations | Does not execute real methods by default |
| Depends on its real configuration and dependencies | Allows dependency behavior to be controlled |
| Useful for real logic and integration tests | Useful for isolated unit tests |

For a unit test of `OrderService`, we want the real service and a mocked `FeignClientConfig`.

---

## 7. Important Mockito Annotations

### 7.1 `@ExtendWith(MockitoExtension.class)`

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
}
```

This integrates Mockito with JUnit 5.

It initializes Mockito annotations such as `@Mock` and `@InjectMocks` for each test instance and manages their lifecycle.

Without this extension, the annotations will not initialize the fields automatically unless another initialization mechanism is used.

### 7.2 `@Mock`

```java
@Mock
FeignClientConfig feignClientConfig;
```

`@Mock` creates a Mockito mock object.

It does not execute the real method implementations by default.

For a method returning `String`, an unstubbed call normally returns `null`.

For example:

```java
String result = feignClientConfig.placeOrder("1");
```

If the method has not been stubbed, `result` is normally `null`.

**Important:** All methods on the mocked object are available, but their behavior is simulated rather than real by default.

### 7.3 `@InjectMocks`

```java
@InjectMocks
OrderService orderService;
```

`@InjectMocks` asks Mockito to create an instance of `OrderService` and inject the mocks it can resolve into its dependencies.

In our example, Mockito injects the mocked `FeignClientConfig` and `RestClient` into the real `OrderService` constructor.

Therefore:

- `orderService` is a real service object.
- `feignClientConfig` is a mock object.
- Calling `orderService.placeOrder("1")` executes the real service method.
- The call made to `feignClientConfig.placeOrder("1")` uses the mock behavior.

**Remember:** `@InjectMocks` does not make the service a mock.

### 7.4 `@SpringBootTest`

```java
@SpringBootTest
class OrderServiceTest {
}
```

`@SpringBootTest` loads the Spring application context for testing.

It is commonly used for integration tests involving actual Spring-managed beans and configuration.

It is not necessary for a simple unit test using `@ExtendWith(MockitoExtension.class)`.

### 7.5 `@Mock` vs. `@MockitoBean`

- `@Mock` creates a Mockito mock for a regular unit test.
- `@MockitoBean` creates or overrides a bean with a Mockito mock in the Spring application context.

Use `@Mock` with `MockitoExtension` for isolated unit tests. Use `@MockitoBean` when you need to replace a Spring bean in a Spring-context test.

---

## 8. Stubbing a Method with `when().thenReturn()`

Stubbing means defining the behavior of a mock method.

Syntax:

```java
when(mockObject.method(arguments))
        .thenReturn(expectedValue);
```

Example:

```java
Mockito.when(feignClientConfig.placeOrder("1"))
       .thenReturn("Order placed successfully");
```

This tells Mockito:

"When the mock receives `placeOrder("1")`, return `"Order placed successfully"`."

It does not call the real implementation of `FeignClientConfig.placeOrder()`.

### What if we do not stub the method?

```java
String result = feignClientConfig.placeOrder("1");
```

For an unstubbed mock method returning `String`, Mockito normally returns `null`.

Therefore, configure the behavior when your test needs a specific response.

---

## 9. Verifying Method Calls with `verify()`

`verify()` checks whether a mock received a particular invocation during the test.

Example:

```java
Mockito.verify(feignClientConfig)
       .placeOrder("1");
```

This checks that `placeOrder("1")` was called on the mock.

By default, Mockito verifies that the invocation occurred exactly once.

You can also specify a call count:

```java
Mockito.verify(feignClientConfig, Mockito.times(1))
       .placeOrder("1");
```

Other useful verification methods:

```java
Mockito.verify(feignClientConfig, Mockito.never())
       .cancelOrder("1");

Mockito.verify(feignClientConfig, Mockito.times(2))
       .placeOrder("1");
```

- `times(1)` — called once.
- `times(2)` — called twice.
- `never()` — never called.

**Important:** `verify()` checks calls recorded on the mock. It does not check whether the test source code called the dependency directly.

If the real `OrderService` calls the mock internally, Mockito records that invocation too.

---

## 10. Assertions

Assertions compare actual results with expected results.

Example:

```java
String expected = "Order placed successfully";
String actual = orderService.placeOrder("1");

Assertions.assertEquals(expected, actual);
```

Common JUnit 5 assertions:

```java
Assertions.assertEquals(expected, actual);
Assertions.assertNotNull(actual);
Assertions.assertNull(actual);
Assertions.assertTrue(condition);
Assertions.assertFalse(condition);
```

If an assertion fails, the test fails.

---

## 11. The AAA Testing Pattern

Most unit tests follow three steps.

### Arrange

Prepare test data and configure mock behavior.

```java
String productId = "1";

Mockito.when(feignClientConfig.placeOrder(productId))
       .thenReturn("1");
```

### Act

Call the real method under test.

```java
String actualProductId = orderService.placeOrder(productId);
```

### Assert

Check the result and verify interactions.

```java
Assertions.assertEquals("1", actualProductId);

Mockito.verify(feignClientConfig)
       .placeOrder(productId);
```

AAA stands for **Arrange, Act, Assert**.

---

## 12. Correct Unit Test for Our `OrderService`

```java
package org.example.ecomorderservice;

import org.example.ecomorderservice.config.FeignClientConfig;
import org.example.ecomorderservice.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class EcomOrderServiceApplicationTests {

    @Mock
    FeignClientConfig feignClientConfig;

    @Mock
    RestClient restClient;

    @InjectMocks
    OrderService orderService;

    @Test
    void placeOrderTest() {
        // Arrange
        String productId = "1";

        Mockito.when(feignClientConfig.placeOrder(productId))
               .thenReturn("1");

        // Act
        String actualProductId = orderService.placeOrder(productId);

        // Assert
        Assertions.assertEquals("1", actualProductId);

        Mockito.verify(feignClientConfig)
               .placeOrder(productId);
    }
}
```

### How the test executes

1. JUnit starts the test.
2. `MockitoExtension` initializes the mocks.
3. `@InjectMocks` creates the real `OrderService` and injects the mocks.
4. `when(...).thenReturn(...)` configures the mock dependency.
5. `orderService.placeOrder("1")` executes the real service method.
6. The real service calls the mocked `feignClientConfig.placeOrder("1")`.
7. The mock returns `"1"`.
8. `assertEquals()` checks the result.
9. `verify()` confirms the dependency call occurred.

### Why does `verify()` pass?

Because this line:

```java
String actualProductId = orderService.placeOrder(productId);
```

executes the real service method, which internally calls:

```java
return feignClientConfig.placeOrder(productId);
```

Mockito records the call, so `verify()` passes.

If you remove the service invocation and no other code calls the dependency, the verification fails.

---

## 13. An Important Mistake to Avoid

Consider this code:

```java
Mockito.when(orderService.placeOrder(productId))
       .thenReturn("1");
```

This is not the correct setup for the unit test above.

Why?

- `orderService` is a real object created using `@InjectMocks`.
- `Mockito.when()` evaluates its argument while setting up the stub.
- Therefore, the real service method can execute during this setup.
- The dependency mock may be called before your apparent Act step.
- Your later assertion may pass without meaningfully testing the intended service execution.

For ordinary unit testing, **stub the dependencies, not the real method you are trying to test**.

Correct:

```java
Mockito.when(feignClientConfig.placeOrder(productId))
       .thenReturn("1");
```

Then call the real service:

```java
String actualProductId = orderService.placeOrder(productId);
```

---

## 14. Does This Test the Actual Feign HTTP Request?

No.

Our test verifies the behavior of `OrderService`, but `FeignClientConfig` is mocked.

That means:

- The real `OrderService.placeOrder()` method executes.
- The mocked `FeignClientConfig.placeOrder()` returns the configured result.
- No real Feign HTTP request is made.
- The remote microservice is not contacted.

To test actual HTTP communication, use an integration test or a mock HTTP server such as WireMock, depending on the client configuration.

---

## 15. Required Maven Dependency

For a typical Spring Boot project, JUnit Jupiter and Mockito are provided by:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Check your `pom.xml` before adding it, because Spring Boot projects commonly already include it.

---

## 16. Common Interview Questions

**Q1. What is unit testing?**

Testing a small unit of code in isolation to check that it behaves as expected.

**Q2. What is JUnit?**

A Java testing framework used to write and execute automated tests.

**Q3. What is JUnit 5?**

The modern JUnit generation, with JUnit Jupiter providing the annotations and programming model commonly used to write tests.

**Q4. What is Mockito?**

A mocking framework used to simulate dependencies, configure their responses, and verify interactions.

**Q5. What is the difference between `@Mock` and `@InjectMocks`?**

`@Mock` creates a mock dependency. `@InjectMocks` creates the object under test and injects available mocks into it.

**Q6. Why do we use `@ExtendWith(MockitoExtension.class)`?**

To integrate Mockito with JUnit 5 and initialize Mockito annotations for the test.

**Q7. What does `when().thenReturn()` do?**

It configures the response of a mock method for a matching invocation.

**Q8. What does `verify()` do?**

It verifies that a specified invocation occurred on a mock, with the expected arguments and call count.

**Q9. Does `@InjectMocks` create a mock object?**

No. It creates an instance of the class under test and injects available mocks into it.

**Q10. Why mock dependencies in a unit test?**

To isolate the class under test from external systems and make the test predictable and fast.

**Q11. Does `@SpringBootTest` need to be used for every unit test?**

No. It is intended for tests that need the Spring application context. Simple Mockito unit tests generally do not need it.

**Q12. What is the difference between a unit test and an integration test?**

A unit test isolates a class and usually mocks its dependencies. An integration test checks whether multiple components work correctly together.

---

## 17. Final Revision

Remember these points:

- **JUnit 5** provides the testing framework and test lifecycle.
- **Mockito** creates and manages mock behavior.
- `@ExtendWith(MockitoExtension.class)` initializes Mockito for JUnit 5.
- `@Mock` creates a mock dependency.
- `@InjectMocks` creates the real object under test and injects mocks.
- `when().thenReturn()` configures mock behavior.
- `assertEquals()` checks the returned result.
- `verify()` checks whether a mock method was invoked.
- `@SpringBootTest` loads the Spring application context.
- A good unit test executes the real method under test while isolating external dependencies.

**Golden rule:** Test the real logic of the class you want to validate; mock the dependencies that class uses.

# JUnit 5 Assertions — Interview Notes

Import:

```java
import org.junit.jupiter.api.Assertions;
```

## 1. `assertEquals()` — Check equality

Checks whether the expected value equals the actual value.

```java
String expected = "Order placed";
String actual = "Order placed";

Assertions.assertEquals(expected, actual);
```

**Use case:** Verify a service method returns the expected response.

## 2. `assertNotEquals()` — Check inequality

Passes if the expected and actual values are different.

```java
Assertions.assertNotEquals("Failed", "Success");
```

## 3. `assertTrue()` — Check a condition is true

```java
int orderId = 101;

Assertions.assertTrue(orderId > 0);
```

## 4. `assertFalse()` — Check a condition is false

```java
boolean orderCancelled = false;

Assertions.assertFalse(orderCancelled);
```

## 5. `assertNull()` — Check that a value is null

```java
String response = null;

Assertions.assertNull(response);
```

## 6. `assertNotNull()` — Check that a value is not null

```java
String response = "Order placed";

Assertions.assertNotNull(response);
```

## 7. `assertSame()` — Check object identity

Checks whether two references point to the exact same object (`==`).

```java
Object first = new Object();
Object second = first;

Assertions.assertSame(first, second);
```

## 8. `assertNotSame()` — Check different object identities

Checks that two references do not point to the same object.

```java
Object first = new Object();
Object second = new Object();

Assertions.assertNotSame(first, second);
```

## 9. `assertArrayEquals()` — Compare array contents

Checks that arrays contain equal elements in the same order.

```java
int[] expected = {1, 2, 3};
int[] actual = {1, 2, 3};

Assertions.assertArrayEquals(expected, actual);
```

## 10. `assertIterableEquals()` — Compare iterable contents

Useful for lists and other iterables. Checks corresponding elements in iteration order.

```java
List<String> expected = List.of("A", "B");
List<String> actual = List.of("A", "B");

Assertions.assertIterableEquals(expected, actual);
```

Required import:

```java
import java.util.List;
```

## 11. `assertThrows()` — Check an exception is thrown

Passes if the specified exception type is thrown by the executable.

```java
Assertions.assertThrows(
    IllegalArgumentException.class,
    () -> {
        throw new IllegalArgumentException("Invalid product ID");
    }
);
```

**Use case:** Verify invalid input produces the expected exception.

## 12. `assertDoesNotThrow()` — Check no exception is thrown

```java
Assertions.assertDoesNotThrow(() -> {
    String productId = "1";
    productId.length();
});
```

## 13. `assertAll()` — Group multiple assertions

Runs all the supplied assertions, even if an earlier assertion fails, and reports failures together.

```java
String productId = "1";
String status = "SUCCESS";

Assertions.assertAll(
    () -> Assertions.assertEquals("1", productId),
    () -> Assertions.assertEquals("SUCCESS", status),
    () -> Assertions.assertNotNull(productId)
);
```

## 14. `assertTimeout()` — Check execution duration

Checks that an executable completes within a specified duration. The executable may continue beyond the timeout because this is not a preemptive timeout.

```java
Assertions.assertTimeout(
    java.time.Duration.ofSeconds(2),
    () -> {
        Thread.sleep(100);
    }
);
```

## 15. `assertTimeoutPreemptively()` — Enforce a timeout

Runs the executable in a separate thread and aborts the test execution if the timeout is exceeded. Be careful with code that relies on `ThreadLocal`, transactions, or Spring test-managed thread state.

```java
Assertions.assertTimeoutPreemptively(
    java.time.Duration.ofSeconds(2),
    () -> {
        Thread.sleep(100);
    }
);
```

## 16. `fail()` — Fail a test explicitly

Useful when a code path should never be reached.

```java
Assertions.fail("This code path should not execute");
```

## 17. Floating-point assertions with a delta

Floating-point calculations can produce tiny precision differences. Use a delta to allow a tolerance.

```java
Assertions.assertEquals(0.3, 0.1 + 0.2, 0.000001);
```

The third argument is the permitted difference.

## 18. Assertions with a failure message

You can provide a message to explain why an assertion matters.

```java
Assertions.assertEquals(
    "SUCCESS",
    "FAILED",
    "Order status should be SUCCESS"
);
```

The test fails and displays the message because the values differ.

---

# Example: Assertions in Your OrderService Test

```java
@Test
void placeOrderTest() {
    String productId = "1";

    Mockito.when(feignClientConfig.placeOrder(productId))
           .thenReturn("Order placed successfully");

    String actualResponse = orderService.placeOrder(productId);

    Assertions.assertEquals(
        "Order placed successfully",
        actualResponse
    );

    Assertions.assertNotNull(actualResponse);
    Assertions.assertTrue(actualResponse.contains("Order"));

    Mockito.verify(feignClientConfig)
           .placeOrder(productId);
}
```

Here:
- `assertEquals()` checks the exact response.
- `assertNotNull()` checks that a response was returned.
- `assertTrue()` checks a condition on the response.
- `verify()` is a Mockito method, not a JUnit assertion.

---

# Quick Revision Table

| Method | What it checks |
|---|---|
| `assertEquals()` | Values are equal |
| `assertNotEquals()` | Values differ |
| `assertTrue()` | Condition is true |
| `assertFalse()` | Condition is false |
| `assertNull()` | Value is null |
| `assertNotNull()` | Value is not null |
| `assertSame()` | Same object reference |
| `assertNotSame()` | Different object references |
| `assertArrayEquals()` | Array contents match |
| `assertIterableEquals()` | Iterable contents match in order |
| `assertThrows()` | Expected exception is thrown |
| `assertDoesNotThrow()` | No exception is thrown |
| `assertAll()` | Run multiple assertions together |
| `assertTimeout()` | Completes within a duration |
| `assertTimeoutPreemptively()` | Enforces a timeout on a separate thread |
| `fail()` | Fails the test explicitly |

**Interview tip:** The assertions you'll use most frequently in Spring Boot unit tests are `assertEquals()`, `assertNotNull()`, `assertTrue()`, `assertThrows()`, and `assertAll()`.

# Mockito and JUnit 5 — Interview Preparation Notes

## 1. `@Mock` vs `@Spy`

### `@Mock`

`@Mock` creates a fake object whose methods do not execute the real implementation by default.

- Use it to isolate the class being tested from its dependencies.
- Unstubbed methods return default values, such as `null`, `0`, or `false`.
- Use `when().thenReturn()` to configure return values.

```java
@Mock
FeignClientConfig feignClientConfig;

@Test
void mockTest() {
    Mockito.when(feignClientConfig.placeOrder("123"))
           .thenReturn("Order placed");

    String result = feignClientConfig.placeOrder("123");

    Assertions.assertEquals("Order placed", result);
}
```

### `@Spy`

`@Spy` wraps a real object. Real methods execute by default unless you override their behavior.

- Use it when you want real behavior for most methods but want to stub selected methods.
- A spy can be used with `Mockito.doNothing()`, `Mockito.doReturn()` and `Mockito.doThrow()`.

```java
@Spy
OrderService orderService;

@Test
void spyTest() {
    Mockito.doNothing()
           .when(orderService)
           .cancelOrder("123");

    orderService.cancelOrder("123");

    Mockito.verify(orderService).cancelOrder("123");
}
```

For a class with constructor dependencies, use `@Spy` with `@InjectMocks` and mocked dependencies, or construct the spy explicitly.

### Mock vs Spy

| Feature | Mock | Spy |
|---|---|---|
| Object behavior | Mock behavior by default | Real behavior by default |
| Real methods execute? | No, unless configured with custom answers | Yes, unless stubbed |
| `when().thenReturn()` | Usually suitable | May execute the real method during stubbing |
| `doReturn()` / `doNothing()` | Supported | Supported; especially useful for stubbing spies |
| Typical purpose | Isolate dependencies | Partially mock an object |
| Example | Mock Feign client | Spy on `OrderService` |

**Interview tip:** Prefer mocks for dependencies and a real object for the class under test. Use spies only when there is a specific reason.

---

## 2. JUnit 5 Test Lifecycle

Test lifecycle annotations control what runs before and after test methods.

| Annotation | Execution | Purpose |
|---|---|---|
| `@BeforeAll` | Once before all tests | Initialize shared resources |
| `@BeforeEach` | Before each test | Prepare test data |
| `@Test` | Executes a test | Verify behavior |
| `@AfterEach` | After each test | Clean up test data |
| `@AfterAll` | Once after all tests | Release shared resources |

### Example

```java
import org.junit.jupiter.api.*;

class OrderServiceTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before all tests");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before each test");
    }

    @Test
    void testOne() {
        System.out.println("Test one");
    }

    @Test
    void testTwo() {
        System.out.println("Test two");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After each test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After all tests");
    }
}
```

### Execution flow

```text
@BeforeAll
    |
@BeforeEach
    |
@Test (testOne or testTwo)
    |
@AfterEach
    |
@BeforeEach
    |
@Test (the other test)
    |
@AfterEach
    |
@AfterAll
```

The order of test methods is not guaranteed unless you configure test ordering. `@BeforeAll` and `@AfterAll` are static by default; using `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` allows non-static lifecycle methods.

**Interview tip:** `@BeforeEach` is commonly used to set up test data, while `@AfterEach` is used to clean up resources.

---

## 3. Mockito `doNothing()`

`Mockito.doNothing()` configures a void method on a mock or spy to do nothing when called.

### Example from `OrderService`

Suppose the service has this method:

```java
public void cancelOrder(String productId) {
    System.out.println("Cancel order: " + productId);
}
```

### Test using a spy

```java
@Spy
@InjectMocks
OrderService orderService;

@Test
void testDoNothing() {

    Mockito.doNothing()
           .when(orderService)
           .cancelOrder("123");

    orderService.cancelOrder("123");

    Mockito.verify(orderService)
           .cancelOrder("123");
}
```

Here, the real `cancelOrder()` implementation is suppressed, so the `println` statement is not executed.

### Important points

- `doNothing()` is intended for void methods.
- Mockito mocks already do nothing by default for void methods, so explicitly configuring it is often unnecessary on a mock.
- `doNothing()` is particularly useful when stubbing a void method on a spy.
- You cannot use `doNothing()` on a regular real object; it must be a mock or spy.

Related methods:

```java
Mockito.doReturn("Order placed")
       .when(orderServiceSpy)
       .someStringMethod();

Mockito.doThrow(new RuntimeException("Failed"))
       .when(orderServiceSpy)
       .cancelOrder("123");
```

Use `doReturn()` for a method with a return value and `doThrow()` to make a void method throw an exception.

---

## 4. Testing a Private Method Using Reflection

Normally, private methods should be tested indirectly through public methods. Java Reflection can call a private method directly when you specifically need to do so.

### Private method in `OrderService`

```java
private String validateProductId(String productId) {

    if (productId == null || productId.isBlank()) {
        throw new IllegalArgumentException(
                "Product ID cannot be null or blank");
    }

    return productId.trim();
}
```

### Test the private method

```java
import java.lang.reflect.Method;

@Test
void testPrivateMethod() throws Exception {

    Method method = OrderService.class
            .getDeclaredMethod("validateProductId", String.class);

    method.setAccessible(true);

    Object result = method.invoke(orderService, " 123 ");

    Assertions.assertEquals("123", result);
}
```

### How it works

1. `getDeclaredMethod()` finds a method declared in the class, including private methods.
2. `setAccessible(true)` attempts to allow reflective access.
3. `invoke()` executes the method on the supplied object.
4. The returned result can be checked using JUnit assertions.

`setAccessible(true)` can be restricted by Java module boundaries.

**Interview tip:** Reflection can test private methods, but it couples tests to implementation details. Prefer testing through public methods or extracting complex logic into a separate class.

---

## 5. Testing Exceptions with `assertThrows()`

JUnit 5's `Assertions.assertThrows()` verifies that an operation throws an expected exception type.

### Test the public method

```java
@Test
void testNullProductIdException() {

    IllegalArgumentException exception =
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> orderService.placeOrder(null)
            );

    Assertions.assertEquals(
            "Product ID cannot be null or blank",
            exception.getMessage()
    );

    Mockito.verifyNoInteractions(feignClientConfig);
}
```

This assumes `placeOrder()` calls `validateProductId()` before calling Feign.

### Test the private method through Reflection

Reflection wraps exceptions thrown by the invoked method in `InvocationTargetException`.

```java
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Test
void testPrivateMethodException() throws Exception {

    Method method = OrderService.class
            .getDeclaredMethod("validateProductId", String.class);

    method.setAccessible(true);

    InvocationTargetException exception =
            Assertions.assertThrows(
                    InvocationTargetException.class,
                    () -> method.invoke(orderService, (Object) null)
            );

    Throwable actualException = exception.getCause();

    Assertions.assertInstanceOf(
            IllegalArgumentException.class,
            actualException
    );

    Assertions.assertEquals(
            "Product ID cannot be null or blank",
            actualException.getMessage()
    );
}
```

### Why do we use `getCause()`?

The private method throws `IllegalArgumentException`, but reflection wraps it in `InvocationTargetException`.

```text
method.invoke()
      |
      v
InvocationTargetException
      |
      v
getCause()
      |
      v
IllegalArgumentException
```

Therefore, check the original exception's type and message using `exception.getCause()`.

### Other exception assertions

| Assertion | Purpose |
|---|---|
| `assertThrows()` | Verifies an expected exception type is thrown |
| `assertThrowsExactly()` | Verifies the exact exception class |
| `assertDoesNotThrow()` | Verifies that the operation completes without an exception |
| `assertInstanceOf()` | Verifies an object's type |

Example:

```java
Assertions.assertDoesNotThrow(
        () -> orderService.placeOrder("123")
);
```

For a successful test, configure the mocked Feign dependency to return a value if needed.

---

## 6. Quick Revision — Interview Questions

**Q1. What is the difference between `@Mock` and `@Spy`?**

`@Mock` provides mock behavior by default; `@Spy` calls real methods by default unless stubbed.

**Q2. Why does `@InjectMocks` not make an object a mock?**

It creates or uses a real instance of the class and injects the available Mockito mocks or spies into it.

**Q3. Why does `doNothing()` fail with `NotAMockException`?**

Because the object passed to `when()` is neither a mock nor a spy.

**Q4. Can we test private methods?**

Yes, reflection can invoke them, but testing through public methods is generally preferred.

**Q5. Why does reflection produce `InvocationTargetException`?**

It wraps exceptions thrown by the method being invoked. The original exception is available through `getCause()`.

**Q6. What is the difference between `assertThrows()` and `assertDoesNotThrow()`?**

`assertThrows()` verifies that an expected exception occurs; `assertDoesNotThrow()` verifies that the operation completes without an exception.

**Q7. What is the difference between `@BeforeEach` and `@BeforeAll`?**

`@BeforeEach` runs before every test method; `@BeforeAll` runs once before all test methods in the class.

---

## Final Best Practices

- Use `@Mock` for external dependencies, such as Feign clients.
- Use `@InjectMocks` to construct the real service under test.
- Use `@Spy` only when partial mocking is necessary.
- Use `assertThrows()` for expected exceptions.
- Use reflection for private-method testing only when there is a good reason.
- Prefer testing observable public behavior rather than implementation details.
- Use `@BeforeEach` to prepare test-specific state and `@AfterEach` to clean it up.
```