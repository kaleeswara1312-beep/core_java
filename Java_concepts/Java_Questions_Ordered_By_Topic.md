# Java Interview Questions Ordered by Topic

This is a compact, topic-wise question bank from the big file. No long answers — only the questions, grouped so they are easy to revise.

---

## 1. Core Java

1. Why is String immutable in Java?
2. What is the difference between == and equals()?
3. What is the difference between String, StringBuilder, and StringBuffer?
4. What are checked and unchecked exceptions?
5. What is the difference between final, finally, and finalize?
6. What is the static keyword in Java?
7. What are access modifiers in Java?
8. What is the difference between abstract class and interface?
9. What is method overloading and method overriding?
10. What is the Java classloader?
11. What are Java annotations?
12. What are anonymous inner classes?
13. What is enum in Java?
14. What are varargs in Java?
15. What is serialization and deserialization?
16. What is a singleton design pattern?
17. What is an immutable class?
18. Why are default and static methods in interfaces added in Java 8?
19. What is a functional interface?
20. What is a lambda expression?
21. What are method references?
22. What is the difference between Comparable and Comparator?
23. What are Java 17 features you have used in production?
24. What are generics in Java?
25. Can you override a static method?
26. What is try-with-resources?
27. What is the Java Memory Model (JMM)?
28. What is reflection in Java?
29. What is transient keyword?
30. What is final keyword used for?
31. What is the difference between this and super?
32. What is package and import in Java?
33. What is method hiding?
34. What is the difference between primitive and non-primitive types?
35. What is type casting in Java?

---

## 2. OOP and Design Concepts

1. What are the 4 pillars of OOP?
2. Explain encapsulation with an example.
3. Explain inheritance and its types.
4. Explain polymorphism with examples.
5. What is abstraction?
6. What is composition?
7. What is dependency injection?
8. What are the types of dependency injection?
9. What is bean lifecycle in Spring?
10. What is the difference between controller and rest controller?
11. What is the difference between component, service, and repository?
12. What are design patterns?
13. What is factory pattern?
14. What is builder pattern?
15. What is strategy pattern?
16. What is adapter pattern?
17. What is decorator pattern?
18. What is observer pattern?
19. What is chain of responsibility?
20. What is composite pattern?
21. What is memento pattern?
22. What is facade pattern?
23. What is singleton pattern and how do you make it thread-safe?
24. What is prototype pattern?
25. Why is dependency injection important?

---

## 3. Collections and Data Structures

1. What is the difference between ArrayList and LinkedList?
2. What is the difference between ArrayList and Vector?
3. How does HashMap work internally?
4. What happens when two keys have the same hashcode?
5. Why must equals() and hashCode() follow a contract?
6. How does HashSet work?
7. Why can HashSet detect duplicates incorrectly if equals/hashCode are wrong?
8. What is the difference between HashMap and ConcurrentHashMap?
9. What is the difference between HashMap and Hashtable?
10. What is the difference between TreeMap and HashMap?
11. What is the difference between HashMap, LinkedHashMap, and TreeMap?
12. What is the difference between ArrayList and CopyOnWriteArrayList?
13. What is the difference between fail-fast and fail-safe iterators?
14. Why does ConcurrentHashMap not allow null keys or values?
15. What happens if you modify a collection while iterating over it?
16. What is a priority queue?
17. What is a queue and a deque?
18. How do you remove duplicates from a list?
19. How do you count frequency of characters using HashMap?
20. How do you convert List to Map using Streams?
21. What is Collections.synchronizedList()?
22. What is a TreeSet?
23. What is a LinkedHashSet?
24. What is Collections framework?
25. How does TreeMap maintain ordering?
26. Can we provide custom sorting logic in TreeMap?
27. What is ConcurrentModificationException?
28. When would you use CopyOnWriteArrayList?
29. What is a Set, List, and Map?
30. What is the difference between HashSet and TreeSet?
31. How would you find duplicates in a list using Java 8?

---

## 4. Java 8 + Streams

1. What are Java 8 features used in production?
2. What is a lambda expression?
3. What is a functional interface?
4. What is stream API?
5. What is the difference between Stream and ParallelStream?
6. What is the difference between intermediate and terminal operations?
7. What is map() vs flatMap()?
8. What is filter(), reduce(), and collect()?
9. What is Optional and where does it help?
10. When should you avoid Optional?
11. How do you find duplicate strings using Streams?
12. How do you sort an employee list using Comparator and Streams?
13. How do you find top N highest salaries using Streams?
14. How do you group employees by department using Streams?
15. How do you remove duplicates using Streams?
16. How do you convert List to Map using Streams?
17. What is default method in interface?
18. What is static method in interface?
19. What is the difference between streams and loops?
20. When would you prefer ParallelStream over Stream?
21. How do you find the longest substring without repeating characters using Java?
22. What is the difference between reduce and collect?
23. How do you sort a Map by value?
24. How do you find the first non-repeated character in a string using Streams?
25. How do you find duplicates in a list using streams?
26. What is peak memory cost of streams?

---

## 5. Multithreading and Concurrency

1. What is the difference between process and thread?
2. What are the ways to create threads in Java?
3. What is Runnable vs Callable?
4. What is ExecutorService?
5. Why use ExecutorService instead of manually creating threads?
6. What is a thread pool?
7. What is FixedThreadPool vs CachedThreadPool?
8. What is ThreadPoolExecutor?
9. What are corePoolSize, maxPoolSize, and queue?
10. What happens when the queue is full in ThreadPoolExecutor?
11. What is ForkJoinPool?
12. What is work stealing?
13. What is ExecutorService vs ForkJoinPool?
14. What is Future vs CompletableFuture?
15. What is thenApply vs thenCompose?
16. What is thenCombine vs allOf?
17. How does CompletableFuture handle exceptions?
18. How do you implement timeout handling with CompletableFuture?
19. How do you control the executor used by CompletableFuture?
20. What happens if CompletableFuture tasks block?
21. What is the Java Memory Model (JMM)?
22. What is happens-before relationship?
23. What is a race condition?
24. What is thread safety?
25. What is immutability and how does it help concurrency?
26. What is CAS (Compare-And-Swap)?
27. What is synchronized?
28. What is volatile?
29. What is synchronized method vs synchronized block?
30. What is ReentrantLock vs synchronized?
31. What is ReentrantReadWriteLock?
32. What is StampedLock?
33. What is a deadlock?
34. How do you detect and prevent deadlock?
35. What is livelock?
36. What is starvation?
37. What is lock contention?
38. What is lock-free programming?
39. When would you use AtomicInteger instead of synchronized?
40. What is the difference between AtomicInteger and LongAdder?
41. Why is HashMap unsafe for concurrent modifications?
42. How does ConcurrentHashMap achieve concurrency without locking the entire map?
43. Why does ConcurrentHashMap not allow null keys and values?
44. Can a thread acquire the same lock multiple times?
45. What happens if a thread throws an exception while holding a synchronized lock?
46. Does calling Thread.start() establish a happens-before relationship?
47. What is the difference between wait(), notify(), and notifyAll()?
48. What is a producer-consumer problem?
49. How do you implement a thread-safe singleton?
50. What is a deadlock example in Java?
51. How do you size a thread pool for CPU-bound tasks?
52. How do you size a thread pool for I/O-bound tasks?
53. What is a virtual thread?
54. Virtual threads vs platform threads — when to use which?
55. When should you not use virtual threads?
56. What are the limitations of virtual threads?
57. How do virtual threads handle blocking I/O?
58. What is a thread dump?
59. What is a stack trace?
60. How do you debug thread contention?
61. What is Thread starvation?

---

## 6. JVM, Garbage Collection, and Memory

1. What is JVM architecture?
2. What is the difference between stack and heap?
3. What is stored in stack vs heap?
4. What is garbage collection?
5. How does GC decide which objects to clean?
6. What is Minor GC, Major GC, and Full GC?
7. What happens during a Full GC and why can it affect API latency?
8. What is OutOfMemoryError?
9. How do you identify memory leaks in Java?
10. How do you analyze a heap dump?
11. How do you analyze a thread dump?
12. What are the common causes of memory leak?
13. How can Java have a memory leak even with GC?
14. What are static references and why are they risky?
15. What is JIT compiler?
16. What is ClassLoader in Java?
17. What are bootstrap, extension, and application class loaders?
18. What is the transient keyword?
19. What is reflection API?
20. What is Java Memory Model (JMM)?
21. What is a memory leak in a long-running service?
22. Why can adding more instances not improve performance?
23. Why can CPU be low but requests still time out?
24. What JVM metrics would you check first when API is slow?
25. Why do GC pauses affect latency?
26. What is the difference between Young Generation and Old Generation?
27. What is object retention?
28. What is heap fragmentation?
29. How do you debug high GC pauses?
30. How do you tune JVM memory for Kubernetes?
31. What is G1 GC vs ZGC?

---

## 7. Spring Core and Spring Boot

1. What is the difference between Spring and Spring Boot?
2. What is dependency injection?
3. How does Spring resolve dependencies at startup?
4. What is a Spring bean?
5. What is bean lifecycle?
6. What is the difference between @Component, @Service, @Repository, and @Controller?
7. What is @Autowired and constructor injection?
8. What is @Value?
9. What is @ConfigurationProperties?
10. What is Spring Boot auto-configuration?
11. How does Spring Boot decide which auto-config to apply?
12. What happens internally when you add spring-boot-starter-web?
13. How does Spring Boot detect embedded Tomcat?
14. Why does Spring Boot prefer convention over configuration?
15. What is the startup flow of a Spring Boot application?
16. What is the difference between @Controller and @RestController?
17. What is @RequestMapping?
18. What is the difference between @PathVariable, @RequestParam, and @RequestBody?
19. How do you handle global exception handling?
20. What is @ControllerAdvice?
21. What is @Transactional and how does it work?
22. Why does @Transactional not work sometimes?
23. What happens if a method is private, final, or invoked via this?
24. How does rollback work for checked exceptions?
25. What is @TransactionalEventListener?
26. What are filters and interceptors?
27. What is Spring Security?
28. How is JWT implemented in Spring Boot?
29. How do you implement role-based authorization?
30. What is Spring Data JPA?
31. What is Actuator and why is it useful?
32. What is CORS and how do you configure it?
33. What is Swagger/OpenAPI?
34. How do you schedule tasks in Spring Boot?
35. How do you configure caching in Spring Boot?
36. What is lazy vs eager initialization?
37. Why can @Lazy fix startup issues but cause runtime failure later?
38. What happens if two beans have the same name?
39. What happens if ApplicationEvent listener throws an exception?
40. When would you use @EventListener vs @TransactionalEventListener?
41. What happens if a bean throws an exception in @PostConstruct?
42. How does Spring Boot reduce XML configuration completely?
43. What is SpringFactoriesLoader?
44. How does Spring Boot handle externalized configuration?
45. How does Spring Boot manage dependency versions internally?
46. What happens if a request-scoped bean is injected into a singleton?
47. Why are scoped proxies needed?
48. What happens when a configuration property is missing with @Value? 
49. Why can @ConfigurationProperties be safer than many @Value fields?
50. What is the difference between @MockBean and @SpyBean?

---

## 8. JPA, Hibernate, and SQL

1. What is the difference between CrudRepository and JpaRepository?
2. What is the difference between SessionFactory and EntityManager?
3. In Spring JPA, what does it internally use?
4. What is the difference between save(), persist(), and merge()?
5. What are entity states in Hibernate?
6. What is lazy loading vs eager loading?
7. How do you avoid LazyInitializationException?
8. What is the N+1 problem and how do you fix it?
9. What is @OneToOne, @OneToMany, and @ManyToMany?
10. What are common JPA methods?
11. How do you implement batch inserts/updates in Hibernate?
12. How does Spring talk to the database?
13. What is query optimization?
14. What are SQL joins?
15. What is the difference between WHERE and HAVING?
16. What is the difference between INNER JOIN and LEFT JOIN?
17. How do you find the second highest salary?
18. How do you find the nth highest salary?
19. How do you remove duplicate records from a table?
20. How do you find duplicate rows in a table?
21. How do you count employees department-wise?
22. How do you implement pagination in SQL?
23. What are ACID properties?
24. What are isolation levels?
25. What is optimistic locking?
26. What is pessimistic locking?
27. When can an index hurt performance?
28. What is database connection pooling?
29. What is the role of indexes in performance?
30. What is schema design?
31. What is the difference between database and application-level cache?
32. What is the difference between @Entity and @Table?
33. How do you configure multiple datasources in Spring Boot?
34. How do you monitor SQL performance in production?
35. What is a deadlock in a database?
36. What is a query plan?
37. What is execution plan analysis?
38. Why can a query be fast in dev but slow in production?

---

## 9. Microservices and System Design

1. What are the core components of microservices architecture?
2. What is an API Gateway?
3. How do microservices discover each other?
4. What is synchronous communication?
5. What is asynchronous communication?
6. What is the difference between REST and Kafka messaging?
7. What is circuit breaker?
8. When should you use retry, timeout, circuit breaker, and bulkhead?
9. How do you prevent cascading failure?
10. How do you design a fault-tolerant microservice architecture?
11. How do you design an API that handles 10K+ requests/sec?
12. How do you trace a request across multiple services?
13. What is distributed logging?
14. What is an idempotency key?
15. How do you prevent duplicate orders?
16. How do you prevent duplicate payments?
17. What happens if a downstream service is unavailable?
18. How do you maintain consistency across multiple services?
19. What is the Saga pattern?
20. What is choreography vs orchestration?
21. What is a retry storm and how do you prevent it?
22. What is a rate limiter?
23. How do you design rate limiting for millions of users?
24. What is API versioning?
25. What is a fallback mechanism?
26. What is the difference between synchronous vs asynchronous communication in microservices?
27. How do you implement service discovery and configuration management?
28. How do you design a fault-tolerant API Gateway?
29. How do you implement distributed transactions?
30. How do you keep one failing service from cascading failures?
31. What is eventual consistency?
32. What is outbox pattern?
33. How do you handle service-to-service communication failures?
34. How do you design for zero-downtime deployment?
35. What is blue-green deployment?
36. What is canary release?
37. What is graceful shutdown?

---

## 10. Kafka and Messaging

1. What is Kafka and why use it?
2. What is the difference between Kafka and RabbitMQ?
3. What is a topic, partition, and offset?
4. What is consumer lag?
5. How do you prevent duplicate processing of the same Kafka event?
6. What happens if a consumer crashes after processing a message but before committing the offset?
7. How do you handle a message that repeatedly fails?
8. How do you handle Kafka consumer lag?
9. How do you scale Kafka consumers?
10. What is a dead-letter topic?
11. What is exactly-once semantics?
12. When should you choose synchronous calls over asynchronous messaging?
13. How do you implement idempotent Kafka consumers?
14. What is event-driven architecture?
15. How do you design Kafka producers and consumers for resilience?
16. Can Kafka guarantee message delivery?
17. If Kafka is receiving faster than consumers can process, what do you do?
18. What is the difference between a queue and a topic?
19. What is the role of message brokers in microservices?
20. What is a retry queue?
21. How do you recover messages that failed processing?

---

## 11. Production Debugging and System Reliability

1. API is slow in production — where do you start debugging?
2. High CPU usage after deployment — what will you check?
3. Memory usage keeps increasing — what are the causes?
4. How do you identify memory leaks in Java?
5. How do you analyze a heap dump?
6. How do you analyze a thread dump?
7. What if DB queries suddenly become slow?
8. How do you identify missing indexes?
9. How do you debug connection pool exhaustion?
10. What happens if a thread pool is exhausted?
11. How do you handle high latency in APIs?
12. What if one microservice is slowing the entire system?
13. How do you debug timeout issues?
14. How do you handle cascading failures?
15. What is a circuit breaker real use case?
16. How do you debug Kafka consumer lag?
17. What if messages are duplicated?
18. How do you handle idempotency in retries?
19. What if logs are not sufficient?
20. How do you improve observability?
21. How do you implement distributed tracing?
22. What metrics do you monitor in production?
23. How do you detect slow endpoints?
24. What is p95 and p99 latency?
25. How do you debug GC pauses?
26. What causes OutOfMemoryError?
27. How do you fix memory fragmentation?
28. How do you handle traffic spikes?
29. How do you scale an application quickly?
30. What is autoscaling strategy?
31. What if deployment breaks production?
32. How do you rollback safely?
33. What is blue-green deployment?
34. How do you validate a canary release?
35. How do you handle config issues in production?
36. What if cache is not working?
37. What is cache miss vs cache stampede?
38. How do you debug Redis issues?
39. How do you prevent stale cache?
40. How do you debug authentication failures?
41. What if JWT expires frequently?
42. How do you debug CORS issues?
43. How do you debug network latency?
44. What if a third-party API fails?
45. How do you design a fallback mechanism?
46. How do you implement retry with backoff?
47. What if disk space is full?
48. How do you monitor logs efficiently?
49. What tools do you use for debugging?
50. What is the biggest production issue you handled?
51. What is meant by observability?
52. How do you detect if issue is in Java, DB, K8s, or AWS?
53. Why can a request be slow even with low CPU usage?
54. Why is DB healthy but API still slow?
55. Why do users see stale data even after DB update?
56. What if an external API takes 20 seconds to respond?
57. Why can retries make an outage worse?
58. How do you prevent one slow downstream service from exhausting your threads?
59. How do you design fallback for Redis outage?
60. What if the database is temporarily unavailable?
61. How do you handle message recovery after failure?

---

## 12. DSA and Coding Questions

1. Find duplicate elements in an array.
2. Find first non-repeated character in a String.
3. Reverse a String without using built-in methods.
4. Sort Employee list using Comparator and Streams.
5. Find top N highest salaries using Streams.
6. Group employees by department using Streams.
7. Convert List to Map using Streams.
8. Remove duplicates using Streams.
9. Find longest substring without repeating characters.
10. Balanced parentheses problem using Stack.
11. Merge two sorted arrays.
12. Implement LRU Cache.
13. Producer-consumer problem.
14. Deadlock example in Java.
15. Thread-safe singleton implementation.
16. CompletableFuture real-time scenario.
17. ConcurrentHashMap vs HashMap scenario.
18. Retry failed REST API calls.
19. Kafka duplicate message handling.
20. Pagination implementation in Spring Boot.
21. Rate limiter implementation.
22. Write immutable class in Java.
23. Count frequency of characters using HashMap.
24. Difference between map() and flatMap().
25. Comparable vs Comparator with examples.
26. Find 2nd highest salary.
27. Find nth highest salary.
28. Remove duplicate records from a table.
29. Employees having same salary.
30. Difference between WHERE and HAVING.
31. INNER JOIN vs LEFT JOIN.
32. Department-wise maximum salary query.
33. Pagination using LIMIT and OFFSET.
34. Find duplicate rows in table.
35. Count employees department-wise.
36. Implement a thread-safe cache with expiration policy.
37. Design a system to send alerts to millions of users.
38. Write a Java program to group patients by insurance plan and count them.
39. Write a Java program to find duplicate records in a large data stream.
40. Implement REST endpoint to fetch last 10 activities of a user.
41. Move all zeroes to the end.
42. Check if two strings are anagrams.
43. Find longest common prefix.
44. Find missing number in an array.
45. Implement a queue using two stacks.
46. Find intersection of two arrays.
47. Top K frequent elements.
48. Valid palindrome.
49. Merge intervals.
50. Matrix spiral traversal.

---

## 13. Behavioral and Managerial Questions

1. Tell me about yourself.
2. Explain your project architecture end-to-end.
3. Explain a production bug you owned.
4. How do you troubleshoot when a project suddenly stops working?
5. What is your approach to identifying the root cause of a production issue?
6. How do you handle production incidents with SLA pressure?
7. What is your notice period?
8. What is your relocation preference?
9. Why do you want to switch jobs?
10. What are your strengths and weaknesses?
11. How do you mentor juniors?
12. How do you handle conflicts in a team?
13. How do you prioritize work during an outage?
14. What are your career goals?
15. How do you communicate technical issues to non-technical stakeholders?
16. What is your approach to code review?
17. How do you work with cross-functional teams?
18. Why should we hire you?
19. What is your greatest achievement?
20. What is the biggest challenge you faced in your last project?

---

## 14. Design Pattern and Architecture Scenarios

1. Your payment service supports UPI, Credit Card, Debit Card, and Net Banking. New payment methods are added frequently. Which pattern would you use?
2. Your application creates Email, SMS, and Push notifications based on business rules. Which pattern fits?
3. Customers can have Regular, Premium, and Enterprise pricing strategies. Which pattern fits?
4. Your request passes through 10+ validation steps, and different clients need different validation chains. Which pattern would you choose?
5. Your service integrates with multiple third-party payment providers with different APIs. How would you hide those differences?
6. You need to generate reports in PDF, Excel, CSV, and JSON with different construction steps. Which pattern would you consider?
7. An Order has 20 optional fields. How would you create it without a constructor with 20 parameters?
8. When an order is created, multiple components must react: inventory, email, analytics, and notifications. Which pattern fits?
9. A legacy API does not match your application interface. How would you integrate it without changing the legacy code?
10. Multiple database implementations exist and the implementation must be selected at runtime. Which pattern would you use?
11. You need exactly one instance of a component but it must remain safe against multithreading, serialization, and reflection issues. How would you design it?
12. You need to add logging, metrics, caching, and authorization around an existing service without modifying its core logic. Which pattern fits?
13. Shape and RenderingEngine can vary independently. How would you avoid a large number of subclasses?
14. Your application has File and Directory objects that should be treated uniformly. Which pattern would you use?
15. You need to traverse a complex object structure without exposing its internal representation. Which pattern would you consider?
16. Different business rules execute based on request type, and new rules are added frequently. How would you avoid a huge if-else block?
17. A request moves through multiple processing stages and each stage can either handle it or pass it forward. Which pattern fits?
18. Users need to undo changes and restore previous object states. Which pattern would you use?
19. A legacy system exposes a complicated API, but your application needs a simple interface. Which pattern would simplify it?
20. Your OrderService has grown to handle validation, payment, inventory, notifications, and persistence. Which patterns would you combine to refactor it?

---

## 15. Real-World Scenario Questions

1. How would you design an API that can handle 10K+ requests per second?
2. Your API is slow but CPU and memory are normal. How would you find the bottleneck?
3. How would you prevent duplicate orders if the same request is received twice?
4. What happens when your database becomes unavailable for 30 seconds?
5. How would you design a retry mechanism without creating a retry storm?
6. One microservice is down. How should dependent services behave?
7. How would you maintain consistency when a business operation spans multiple microservices?
8. Kafka is receiving messages faster than consumers can process them. How would you solve it?
9. How would you handle a Kafka message that repeatedly fails?
10. Your database has 100 million records and a query suddenly becomes slow. How would you investigate?
11. How would you design pagination for millions of records?
12. Your Java application has high GC pauses in production. What would you investigate?
13. How would you prevent one slow downstream service from exhausting your application's threads?
14. How would you design an API so that it remains backward compatible when the response structure changes?
15. Your system works perfectly at normal traffic but fails during sudden traffic spikes. Walk me through your debugging approach.
16. A payment succeeds, but Order Service doesn't receive the confirmation. How would you handle this?
17. Users are seeing stale data even though the database has been updated. How would you debug it?
18. One service is receiving much more traffic than others. How would you handle it?
19. A request travels through 6 microservices. How would you trace it end-to-end?
20. An external API takes 20 seconds to respond. How would you protect your service?
21. Your service works in staging but randomly fails in production. Where do you start?
22. A database connection pool is exhausted while the database itself is healthy. Why?
23. CPU is only 20%, but requests are timing out. What would you investigate?
24. What happens if a message is consumed twice in Kafka?
25. What happens when a pod keeps restarting in Kubernetes?
26. How do you do zero-downtime deployment?
27. How do you handle graceful shutdown in Kubernetes or Spring Boot?
28. How do you debug a slow SQL query in production?
29. How do you debug auth failure in a JWT-based app?
30. What happens if a cache is not working?
31. What if Redis goes down?
32. How do you handle a downstream service timeout?
33. How do you handle message recovery after failure?
34. How do you do distributed tracing?
35. What metrics would you check during a production incident?

---

## 16. Most Repeated Topics to Revise

- Java basics and OOP
- Collections and HashMap internals
- Streams and lambda expressions
- Multithreading and concurrency
- JVM and GC
- Spring Boot and REST
- JPA and SQL
- Microservices and Kafka
- Production debugging and performance
- Behavioral and system design

---

## 17. Best Order to Revise Before Interview

1. Java basics and OOP
2. Collections and HashMap internals
3. Streams and Java 8 features
4. Multithreading and concurrency
5. JVM, GC, and memory management
6. Spring Boot and REST
7. SQL and JPA/Hibernate
8. Microservices and messaging
9. Kafka and distributed systems
10. Production debugging and performance
11. DSA coding problems
12. Behavioral and design discussion

This is the cleanest way to revise without reading the entire big file every time.
