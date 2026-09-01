# Spring Framework

## 1. What is Spring?

Spring is a Java framework that makes application development easier by providing infrastructure for things such as:

- Inversion of Control (IoC)
- Dependency Injection (DI)
- Aspect-Oriented Programming (AOP)
- Transaction management
- Web application development
- Data access
- Security integration
- Testing support

One of the most important concepts in Spring is **IoC (Inversion of Control)**, which is implemented primarily through **Dependency Injection (DI)**.

---

# 2. Spring vs Spring Boot — High-Level Idea

Spring Framework generally requires more explicit configuration, while Spring Boot provides conventions and auto-configuration on top of Spring.

| Spring Framework | Spring Boot |
|---|---|
| More configuration is commonly required | Much of the configuration is automated |
| Traditionally deployed to an external servlet container such as Tomcat | Commonly uses an embedded servlet container |
| XML configuration was widely used traditionally | Annotation/configuration-based approach is commonly used |
| Dependencies are configured explicitly | Starters simplify dependency management |
| Application setup can require several configuration steps | Auto-configuration reduces setup |

> **Important:** Spring Framework itself is not dependent on XML. Modern Spring applications can also be configured using Java configuration and annotations.

---

# 3. What is IoC?

**IoC (Inversion of Control)** means that the responsibility for creating and managing application objects is transferred from the application code to the Spring container.

Without Spring:

```java
EmployeeService service = new EmployeeService();
```

The application code explicitly creates the object.

With Spring:

```java
EmployeeService service = context.getBean(EmployeeService.class);
```

The Spring container creates and manages the object, and the application requests it from the container.

So, instead of the application controlling object creation:

```text
Application → creates object
```

Spring changes the responsibility to:

```text
Spring Container → creates and manages object
                         ↓
                    Application
```

---

# 4. IoC Container

The **IoC container** is responsible for:

- Creating Spring-managed objects
- Managing their lifecycle
- Resolving dependencies
- Injecting dependencies
- Configuring objects
- Managing scopes
- Destroying objects when appropriate

These Spring-managed objects are commonly called **beans**.

## Common IoC Container Interfaces

The two important container interfaces are:

### BeanFactory

A basic IoC container.

```java
BeanFactory
```

### ApplicationContext

A more feature-rich container built on top of BeanFactory.

```java
ApplicationContext
```

In most Spring applications, `ApplicationContext` is commonly used.

---

# 5. ApplicationContext

`ApplicationContext` is an interface representing the Spring IoC container.

For example:

```java
ApplicationContext context =
        new ClassPathXmlApplicationContext("spring.xml");
```

Here:

```text
ApplicationContext
        ↓
ClassPathXmlApplicationContext
        ↓
spring.xml
        ↓
Spring Bean Definitions
        ↓
Spring-managed Objects
```

`ClassPathXmlApplicationContext` loads the Spring configuration file from the application's classpath.

---

# 6. What is a Spring Bean?

A **Spring Bean** is an object that is created, configured, and managed by the Spring IoC container.

For example:

```java
public class EmployeeService {

    public void display() {
        System.out.println("Employee Service");
    }
}
```

If Spring is configured to manage this class, the object becomes a Spring bean.

---

# 7. Defining Beans Using XML

In traditional Spring applications, beans can be defined in an XML configuration file.

Example:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="employeeService"
          class="com.example.EmployeeService"/>

</beans>
```

The `<bean>` element tells Spring to create and manage an object of the specified class.

---

# 8. Getting a Bean From the Container

Once the bean is registered, we can retrieve it using `getBean()`.

```java
ApplicationContext context =
        new ClassPathXmlApplicationContext("spring.xml");

EmployeeService service =
        context.getBean(EmployeeService.class);

service.display();
```

Another form is:

```java
EmployeeService service =
        (EmployeeService) context.getBean("employeeService");
```

The container creates and manages the object instead of the application directly using `new`.

---

# 9. Dependency Injection

**Dependency Injection (DI)** is a design technique where an object's dependencies are provided to it instead of the object creating those dependencies itself.

Suppose:

```java
public class EmployeeService {

    private EmployeeRepository repository;

}
```

`EmployeeService` depends on `EmployeeRepository`.

Instead of doing this:

```java
repository = new EmployeeRepository();
```

Spring can provide the dependency.

```text
EmployeeService
       ↑
       |
EmployeeRepository
```

This makes the classes more loosely coupled.

---

# 10. Types of Dependency Injection

Spring supports three common styles:

1. Constructor Injection
2. Setter Injection
3. Field Injection

---

# 11. Constructor Injection

The dependency is provided through the constructor.

```java
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }
}
```

In modern Spring applications, **constructor injection is generally preferred** because:

- Dependencies can be `final`
- Required dependencies are explicit
- Objects can be created in a valid state
- It is easier to test
- It avoids field injection

Example XML configuration:

```xml
<bean id="employeeRepository"
      class="com.example.EmployeeRepository"/>

<bean id="employeeService"
      class="com.example.EmployeeService">

    <constructor-arg ref="employeeRepository"/>

</bean>
```

The `ref` attribute tells Spring to inject another Spring bean.

---

# 12. Setter Injection

The dependency is provided through a setter method.

```java
public class EmployeeService {

    private EmployeeRepository repository;

    public void setRepository(EmployeeRepository repository) {
        this.repository = repository;
    }
}
```

XML configuration:

```xml
<bean id="employeeRepository"
      class="com.example.EmployeeRepository"/>

<bean id="employeeService"
      class="com.example.EmployeeService">

    <property name="repository"
              ref="employeeRepository"/>

</bean>
```

Here:

```xml
<property name="repository"
          ref="employeeRepository"/>
```

means:

```text
name → property/setter name
ref  → bean that should be injected
```

Spring effectively performs the equivalent of:

```java
employeeService.setRepository(employeeRepository);
```

---

# 13. Field Injection

The dependency is injected directly into a field.

```java
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

}
```

Spring performs the dependency injection automatically.

However, **field injection is generally not preferred for new code** because the dependency is hidden inside the class and makes testing and immutability harder.

---

# 14. @Autowired

`@Autowired` tells Spring to resolve and inject a dependency.

Example:

```java
@Autowired
private EmployeeRepository repository;
```

It can also be used with constructors and setter methods.

## Constructor

```java
@Autowired
public EmployeeService(EmployeeRepository repository) {
    this.repository = repository;
}
```

In modern Spring, if a class has a single constructor, `@Autowired` is usually not required:

```java
public EmployeeService(EmployeeRepository repository) {
    this.repository = repository;
}
```

Spring can automatically use the single constructor for dependency injection.

## Setter

```java
@Autowired
public void setRepository(EmployeeRepository repository) {
    this.repository = repository;
}
```

So, `@Autowired` is **not limited to field and setter injection**.

---

# 15. @Component

`@Component` tells Spring that a class should be detected and registered as a Spring bean when component scanning is enabled.

Example:

```java
@Component
public class EmployeeService {

}
```

Spring detects the class and creates a bean for it.

This removes the need to explicitly define that bean in XML.

---

# 16. Component Scanning

`@Component` works together with component scanning.

For example:

```java
@Configuration
@ComponentScan("com.example")
public class AppConfig {

}
```

Spring scans the specified package and detects classes annotated with:

```text
@Component
@Service
@Repository
@Controller
```

and other supported stereotype annotations.

---

# 17. Common Spring Stereotype Annotations

### @Component

Generic Spring-managed component.

```java
@Component
public class EmailService {
}
```

### @Service

Usually used for service/business logic classes.

```java
@Service
public class EmployeeService {
}
```

### @Repository

Usually used for data-access/repository classes.

```java
@Repository
public class EmployeeRepository {
}
```

### @Controller

Used for Spring MVC controller classes.

```java
@Controller
public class EmployeeController {
}
```

These annotations are specialized forms of `@Component` and help communicate the role of a class.

---

# 18. Dependency Ambiguity

Suppose two classes implement the same interface:

```java
public interface PaymentService {
}
```

Two implementations:

```java
@Component
public class CardPaymentService implements PaymentService {
}
```

```java
@Component
public class UpiPaymentService implements PaymentService {
}
```

Now suppose:

```java
@Autowired
private PaymentService paymentService;
```

Spring finds two candidates:

```text
PaymentService
      |
      +---- CardPaymentService
      |
      +---- UpiPaymentService
```

Spring does not know which implementation should be injected.

This results in a dependency ambiguity error.

---

# 19. Solving Ambiguity With @Primary

We can mark one implementation as the default candidate.

```java
@Component
@Primary
public class CardPaymentService implements PaymentService {
}
```

Now:

```java
@Autowired
private PaymentService paymentService;
```

will use `CardPaymentService` by default.

`@Primary` is useful when one implementation should normally be preferred.

---

# 20. Solving Ambiguity With @Qualifier

If we specifically want a particular implementation, we can use `@Qualifier`.

```java
@Component("cardPayment")
public class CardPaymentService implements PaymentService {
}
```

```java
@Component("upiPayment")
public class UpiPaymentService implements PaymentService {
}
```

Then:

```java
@Autowired
@Qualifier("upiPayment")
private PaymentService paymentService;
```

Spring specifically selects the `upiPayment` bean.

Constructor example:

```java
public PaymentController(
        @Qualifier("upiPayment")
        PaymentService paymentService) {

    this.paymentService = paymentService;
}
```

### @Primary vs @Qualifier

```text
@Primary
    ↓
Default choice when multiple candidates exist

@Qualifier
    ↓
Explicitly select a particular candidate
```

---

# 21. Autowiring in XML

Spring also supports dependency injection through XML using the `autowire` attribute.

Example:

```xml
<bean id="employeeRepository"
      class="com.example.EmployeeRepository"/>

<bean id="employeeService"
      class="com.example.EmployeeService"
      autowire="byType"/>
```

There are two commonly discussed autowiring modes:

- `byName`
- `byType`

---

# 22. Autowire byName

With:

```xml
autowire="byName"
```

Spring tries to find a bean whose **bean name matches the property name**.

Example:

```java
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public void setEmployeeRepository(
            EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
    }
}
```

XML:

```xml
<bean id="employeeRepository"
      class="com.example.EmployeeRepository"/>

<bean id="employeeService"
      class="com.example.EmployeeService"
      autowire="byName"/>
```

The property is:

```text
employeeRepository
```

The bean name is:

```text
employeeRepository
```

They match, so Spring injects the dependency.

Conceptually:

```text
property name
      ↓
employeeRepository

bean name
      ↓
employeeRepository

      ↓
MATCH

Dependency injected
```

---

# 23. Autowire byType

With:

```xml
autowire="byType"
```

Spring looks for a bean whose type matches the dependency property type.

Example:

```java
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public void setEmployeeRepository(
            EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
    }
}
```

Spring looks at:

```text
EmployeeRepository
```

and searches for a compatible bean.

If exactly one matching bean exists, Spring injects it.

---

# 24. Problem With byType

Suppose there are multiple beans of the same type:

```xml
<bean id="mysqlRepository"
      class="com.example.EmployeeRepository"/>

<bean id="oracleRepository"
      class="com.example.EmployeeRepository"/>
```

If Spring uses:

```xml
autowire="byType"
```

there are multiple candidates.

Spring cannot determine which one should be injected.

Therefore, dependency ambiguity can occur.

In modern annotation-based configuration, `@Primary` and `@Qualifier` are commonly used to resolve such situations.

---

# 25. Explicit Wiring vs Autowiring

## Explicit Setter Wiring

```xml
<bean id="employeeService"
      class="com.example.EmployeeService">

    <property name="repository"
              ref="employeeRepository"/>

</bean>
```

You explicitly tell Spring which bean should be injected.

## Explicit Constructor Wiring

```xml
<bean id="employeeService"
      class="com.example.EmployeeService">

    <constructor-arg ref="employeeRepository"/>

</bean>
```

You explicitly provide the constructor dependency.

## Autowiring

```xml
<bean id="employeeService"
      class="com.example.EmployeeService"
      autowire="byType"/>
```

Spring tries to determine the dependency automatically.

---

# 26. Java-Based Configuration

Spring does not require XML configuration.

We can configure Spring using Java configuration.

Example:

```java
@Configuration
public class AppConfig {

    @Bean
    public EmployeeRepository employeeRepository() {
        return new EmployeeRepository();
    }

    @Bean
    public EmployeeService employeeService(
            EmployeeRepository employeeRepository) {

        return new EmployeeService(employeeRepository);
    }
}
```

Then create the container:

```java
ApplicationContext context =
        new AnnotationConfigApplicationContext(AppConfig.class);
```

Retrieve the bean:

```java
EmployeeService service =
        context.getBean(EmployeeService.class);
```

This is an important point:

> **Spring Framework can be configured using XML, Java configuration, annotations, or a combination of these approaches.**

---

# 27. @Bean

`@Bean` tells Spring that the object returned by the method should be managed by the Spring container.

Example:

```java
@Configuration
public class AppConfig {

    @Bean
    public EmployeeService employeeService() {
        return new EmployeeService();
    }
}
```

This is especially useful when:

- You need to configure a third-party class
- You cannot add `@Component` to the class
- You need custom object creation logic

---

# 28. @Configuration

`@Configuration` indicates that a class contains Spring bean definitions.

Example:

```java
@Configuration
public class AppConfig {

    @Bean
    public EmployeeRepository employeeRepository() {
        return new EmployeeRepository();
    }
}
```

The configuration class itself becomes part of the Spring configuration mechanism.

---

# 29. Bean Lifecycle

Spring also manages the lifecycle of its beans.

A simplified lifecycle is:

```text
Create Bean
    ↓
Inject Dependencies
    ↓
Initialization
    ↓
Bean Ready
    ↓
Bean Used
    ↓
Destroy Bean
```

Spring provides lifecycle callbacks such as:

```java
@PostConstruct
```

and:

```java
@PreDestroy
```

Example:

```java
@Component
public class EmployeeService {

    @PostConstruct
    public void init() {
        System.out.println("Bean initialized");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean destroyed");
    }
}
```

---

# 30. Bean Scope

Spring supports different bean scopes.

Common scopes include:

### Singleton

Default scope.

One bean instance is created per Spring container.

```java
@Scope("singleton")
```

### Prototype

A new instance is created whenever the bean is requested.

```java
@Scope("prototype")
```

In web applications, additional scopes include:

- Request
- Session
- Application
- WebSocket

---

# 31. Spring Container Does Not Create Every Class Automatically

A common misunderstanding is:

> "When Spring starts, it creates objects for every class in the project."

That is not correct.

Spring only manages objects that are registered as beans.

Beans can be registered through mechanisms such as:

```text
@Component
@Service
@Repository
@Controller
@Bean
XML <bean>
```

depending on the configuration.

For example:

```java
public class Employee {

}
```

A plain class is not automatically a Spring bean just because Spring is running.

But:

```java
@Component
public class Employee {

}
```

can be detected by component scanning and registered as a bean.

---

# 32. Spring Container and JVM

It is better to think of the IoC container as an **object-management mechanism inside the running application**, rather than literally saying that Spring creates a special physical "container inside the JVM."

Conceptually:

```text
JVM
 |
 +-- Spring Application
       |
       +-- ApplicationContext
              |
              +-- Bean: EmployeeService
              |
              +-- Bean: EmployeeRepository
              |
              +-- Bean: PaymentService
```

The `ApplicationContext` maintains and manages the Spring beans.

---

# 33. Loose Coupling

One of the major benefits of Dependency Injection is **loose coupling**.

Without DI:

```java
public class EmployeeService {

    private EmployeeRepository repository =
            new EmployeeRepository();

}
```

`EmployeeService` directly controls the creation of `EmployeeRepository`.

With DI:

```java
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }
}
```

Now the dependency is provided from outside.

This makes the class easier to:

- Test
- Maintain
- Replace dependencies
- Extend

---

# 34. Why Spring Uses IoC and DI

The main idea is to separate:

```text
Object creation
```

from:

```text
Business logic
```

Instead of:

```text
EmployeeService
      |
      +---- new EmployeeRepository()
```

we have:

```text
Spring Container
      |
      +---- EmployeeRepository
      |
      +---- EmployeeService
                |
                +---- injected EmployeeRepository
```

The class focuses on its actual responsibility rather than managing the creation of its dependencies.

---

# 35. Spring MVC Is Part of the Spring Ecosystem

Spring Framework contains multiple modules/projects.

One important part is **Spring MVC**, which is used to build web applications and REST APIs.

A simplified Spring MVC request flow is:

```text
Client
  ↓
HTTP Request
  ↓
DispatcherServlet
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
Database
  ↓
Response
```

Spring MVC uses the Spring IoC/DI infrastructure underneath.

So:

```text
Spring Framework
       |
       +-- IoC / DI
       +-- Spring MVC
       +-- AOP
       +-- Transaction Management
       +-- Data Access
       +-- Testing
       +-- ...
```

---

# 36. Spring and External Tomcat

Traditionally, a Spring web application can be packaged as a **WAR** and deployed to an external servlet container such as Tomcat.

Typical flow:

```text
Spring Application
       ↓
WAR file
       ↓
External Tomcat
       ↓
Application runs
```

This is different from the common Spring Boot approach where an embedded servlet container can be packaged with the application.

However, Spring itself is **not inherently tied to external Tomcat**. The deployment model depends on how the application is configured and packaged.

---

# 37. Important Corrections to Remember

### IoC vs DI

They are related but not exactly the same.

```text
IoC
 ↓
General principle: control is transferred to a framework/container

DI
 ↓
A common technique used to implement IoC
```

### @Autowired

It is not only for field and setter injection.

It can also be used with constructors, although a single constructor usually does not need `@Autowired`.

### @Component

`@Component` does not magically create a bean without configuration. Component scanning must be enabled for Spring to discover it.

### Spring Configuration

Spring does not require XML everywhere.

Modern Spring supports:

```text
XML Configuration
Java Configuration
Annotation-based Configuration
```

### @Primary

`@Primary` is commonly used with annotation-based configuration to make one bean the preferred candidate when multiple candidates match.

### ApplicationContext

`ApplicationContext` is an interface. Classes such as:

```java
ClassPathXmlApplicationContext
AnnotationConfigApplicationContext
```

are implementations that can create/configure an application context.

---

# 38. Complete Traditional XML Example

## EmployeeRepository

```java
public class EmployeeRepository {

    public void save() {
        System.out.println("Employee saved");
    }
}
```

## EmployeeService

```java
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public void saveEmployee() {
        repository.save();
    }
}
```

## spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="employeeRepository"
          class="com.example.EmployeeRepository"/>

    <bean id="employeeService"
          class="com.example.EmployeeService">

        <constructor-arg ref="employeeRepository"/>

    </bean>

</beans>
```

## Main Class

```java
public class App {

    public static void main(String[] args) {

        ApplicationContext context =
                new ClassPathXmlApplicationContext("spring.xml");

        EmployeeService service =
                context.getBean(EmployeeService.class);

        service.saveEmployee();
    }
}
```

The flow is:

```text
main()
  ↓
ClassPathXmlApplicationContext
  ↓
spring.xml
  ↓
Create EmployeeRepository bean
  ↓
Create EmployeeService bean
  ↓
Inject EmployeeRepository
  ↓
context.getBean()
  ↓
EmployeeService
```

---

# 39. The Core Spring Mental Model

Remember this flow:

```text
Application Starts
       ↓
Spring Container Starts
       ↓
Configuration is Loaded
       ↓
Bean Definitions are Identified
       ↓
Beans are Created
       ↓
Dependencies are Resolved
       ↓
Dependencies are Injected
       ↓
Beans are Ready
       ↓
Application Uses the Beans
```

The most important relationship to remember is:

```text
IoC
 ↓
Spring controls object creation and lifecycle

DI
 ↓
Spring provides required dependencies

IoC Container
 ↓
ApplicationContext
 ↓
Manages Spring Beans
```

---

# 40. Quick Revision

```text
Spring
  ↓
Framework for Java application development

IoC
  ↓
Control of object creation is given to the Spring container

DI
  ↓
Dependencies are provided by the container

IoC Container
  ↓
Manages Spring beans

ApplicationContext
  ↓
Common Spring IoC container interface

Bean
  ↓
Object managed by Spring

@Component
  ↓
Marks a class for component scanning

@Bean
  ↓
Registers an object returned from a configuration method

@Autowired
  ↓
Resolves and injects a dependency

@Primary
  ↓
Makes one candidate the preferred choice

@Qualifier
  ↓
Selects a specific candidate

Constructor Injection
  ↓
Preferred approach for required dependencies

Setter Injection
  ↓
Useful when dependency can be changed/optional

Field Injection
  ↓
Possible, but generally discouraged for new code

XML
  ↓
Traditional Spring configuration approach

Java Configuration
  ↓
@Configuration + @Bean

Spring MVC
  ↓
Spring's web MVC framework
```

# 41. One-Line Interview Definitions

**What is Spring?**

> Spring is a Java framework that simplifies application development by providing features such as IoC, Dependency Injection, AOP, transaction management, web support, and data access.

**What is IoC?**

> IoC is a principle where the responsibility for creating and managing application objects is transferred from the application code to a container or framework.

**What is Dependency Injection?**

> Dependency Injection is a technique where an object's dependencies are provided from outside rather than being created by the object itself.

**What is an IoC container?**

> An IoC container is responsible for creating, configuring, managing, and injecting dependencies into Spring beans.

**What is ApplicationContext?**

> ApplicationContext is a feature-rich Spring IoC container interface used to manage Spring beans and provide additional application infrastructure.

**What is a Spring Bean?**

> A Spring Bean is an object that is created and managed by the Spring IoC container.

**What is @Autowired?**

> @Autowired tells Spring to resolve and inject a suitable dependency.

**What is @Primary?**

> @Primary tells Spring which bean should be preferred when multiple beans match the same dependency.

**What is @Qualifier?**

> @Qualifier explicitly identifies which bean should be injected when multiple candidates exist.

**What is the difference between Spring and Spring Boot?**

> Spring is the underlying application framework, while Spring Boot builds on Spring and reduces configuration and setup through conventions, auto-configuration, starters, and commonly embedded servers.
