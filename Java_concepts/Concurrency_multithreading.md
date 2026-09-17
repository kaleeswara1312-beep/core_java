# Java Concurrency – Quick Reference

## 1. Process

A **Process** is a running instance of an application.

Example:

```text
Chrome → Process
IntelliJ → Process
Java Application → Process
```

* A process has its own memory space.
* A process can contain multiple threads.
* Processes are more heavyweight than threads.

```text
Process
 ├── Thread 1
 ├── Thread 2
 └── Thread 3
```

---

## 2. Thread

A **Thread** is a lightweight unit of execution inside a process.

Example:

```java
Thread t = new Thread();
t.start();
```

Multiple threads can execute different tasks within the same process.

```text
Java Process
     |
     ├── Thread 1
     ├── Thread 2
     └── Thread 3
```

### Why use threads?

To perform multiple tasks concurrently without blocking the entire application.

---

## 3. Thread Lifecycle

Java provides 6 thread states:

```text
NEW
 ↓
RUNNABLE
 ↓
TIMED_WAITING / WAITING / BLOCKED
 ↓
RUNNABLE
 ↓
TERMINATED
```

### States

| State           | Meaning                                     |
| --------------- | ------------------------------------------- |
| `NEW`           | Thread created but `start()` not called     |
| `RUNNABLE`      | Ready to run or currently running           |
| `BLOCKED`       | Waiting to acquire a lock                   |
| `WAITING`       | Waiting indefinitely for another thread     |
| `TIMED_WAITING` | Waiting for a specific time, e.g. `sleep()` |
| `TERMINATED`    | `run()` completed                           |

Check state:

```java
thread.getState();
```

**Important:** Java does not have a separate `RUNNING` state. Running and ready-to-run are represented by `RUNNABLE`.

---

# 4. Thread Class

One way to create a thread is by extending `Thread`.

```java
public class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("Task running");
    }
}
```

Start it:

```java
MyThread t = new MyThread();
t.start();
```

### Important

```java
t.start(); // creates/starts a new thread
t.run();   // normal method call; does NOT create a new thread
```

---

# 5. Runnable

`Runnable` represents the **task/work** that needs to be executed.

```java
public class MyTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Task running");
    }
}
```

Then give the task to a thread:

```java
MyTask task = new MyTask();

Thread t = new Thread(task);

t.start();
```

### Thread vs Runnable

```text
Thread
  ↓
Worker

Runnable
  ↓
Task / Work
```

**Preferred approach:** Generally prefer `Runnable` when you only need to define a task, because Java allows a class to extend only one class, while it can implement multiple interfaces.

---

# 6. Thread Pool

Creating a new thread for every task can become expensive.

```text
1000 Tasks
   ↓
1000 Threads
```

Problems:

* More memory usage
* Thread creation overhead
* Thread destruction overhead
* Excessive context switching

### Thread Pool solution

```text
1000 Tasks
    ↓
Thread Pool
    ↓
10 Worker Threads
```

Example:

```java
ExecutorService pool =
        Executors.newFixedThreadPool(3);

for (int i = 1; i <= 10; i++) {
    pool.submit(new MyTask());
}

pool.shutdown();
```

The same worker threads are **reused** for multiple tasks.

```text
Thread 1 → Task 1 → Task 4 → Task 7
Thread 2 → Task 2 → Task 5 → Task 8
Thread 3 → Task 3 → Task 6 → Task 9
```

### Key idea

> **Task ≠ Thread**

You can have many tasks but a limited number of worker threads.

---

# Interview Points

### Process vs Thread

```text
Process
→ Independent memory space
→ Heavyweight

Thread
→ Runs inside a process
→ Shares process resources
→ Lightweight
```

### Thread vs Runnable

```text
Thread
→ Represents the thread/worker

Runnable
→ Represents the task/work
```

### Thread Pool

> A thread pool maintains a fixed/reusable set of worker threads that execute submitted tasks, avoiding the cost of creating a new thread for every task.

### Important Classes

```text
Thread
Runnable
Executor
ExecutorService
Executors
```

### Remember

```text
Process
   ↓
Contains Threads
   ↓
Threads execute Tasks
   ↓
Thread Pool manages reusable Threads
   ↓
Tasks are submitted to the pool
```

# Java Thread Pool – Worker Thread Lifecycle

## Fixed Thread Pool

```java
ExecutorService pool = Executors.newFixedThreadPool(3);
```

This creates a thread pool that can have **up to 3 worker threads**.

```text
Thread Pool
├── Worker Thread 1
├── Worker Thread 2
└── Worker Thread 3
```

> The 3 threads are created as needed when tasks are submitted.

---

## Multiple Tasks

Suppose we submit 10 tasks:

```java
for (int i = 1; i <= 10; i++) {
    pool.submit(new MyTask(i));
}
```

The pool uses the available worker threads:

```text
10 Tasks
   ↓
3 Worker Threads
   ↓
Task 1 → Thread 1
Task 2 → Thread 2
Task 3 → Thread 3

Task 4 → waits
Task 5 → waits
...
```

When a worker finishes:

```text
Thread 1
   ↓
Task 1 completed
   ↓
takes Task 4
```

The same worker thread is **reused**.

---

## Why Thread Pool?

Without a pool:

```text
10 Tasks
   ↓
10 Threads
```

With a pool:

```text
10 Tasks
   ↓
3 Reusable Threads
```

This avoids creating a new thread for every task.

---

## What happens after tasks finish?

If we don't call:

```java
pool.shutdown();
```

the worker threads generally remain alive and wait for more tasks.

```text
Tasks completed
      ↓
Worker Threads still alive
      ↓
Waiting for new tasks
```

Therefore, the application may not terminate normally.

---

## `shutdown()`

```java
pool.shutdown();
```

Means:

> Stop accepting new tasks and allow already submitted tasks to finish.

After the existing tasks finish:

```text
Task completed
      ↓
Worker Threads terminate
      ↓
Thread Pool shut down
```

---

## Normal Thread vs Thread Pool

### Normal Thread

```text
Task
 ↓
Thread
 ↓
run() finishes
 ↓
Thread terminates
```

### Thread Pool

```text
Task
 ↓
Worker Thread
 ↓
Task finishes
 ↓
Worker waits for another task
 ↓
Another task
 ↓
Worker reused
```

## Key Interview Point

> **A normal thread terminates after its `run()` method finishes. A thread-pool worker is reusable and remains available for more tasks until the executor is shut down.**

Also remember:

> `newFixedThreadPool(3)` means **maximum 3 worker threads**, not necessarily that all 3 are created immediately.


# Java Thread Pools — Production & Interview Notes

## 1. `newFixedThreadPool()`

```java
ExecutorService executor = Executors.newFixedThreadPool(5);
```

### What it does

Creates a thread pool with a **fixed number of worker threads**.

If all 5 threads are busy, additional tasks wait in the queue.

### Production Example

**Report generation service**

Generating large PDF/Excel reports can consume CPU and memory.

```text
100 report requests
       ↓
FixedThreadPool(5)
       ↓
5 reports → running
95 reports → waiting in queue
```

This prevents the application from creating too many threads and consuming all CPU/memory.

### Interview point

> "I would use a FixedThreadPool when I need to control the maximum number of concurrent tasks, especially for CPU-intensive or resource-heavy operations."

---

# 2. `newCachedThreadPool()`

```java
ExecutorService executor = Executors.newCachedThreadPool();
```

### What it does

It **reuses idle threads**.

If no idle thread is available, it **creates a new thread**.

Idle threads are removed after the keep-alive period.

```text
New Task
   ↓
Idle thread available?
   ├── YES → Reuse it
   └── NO  → Create new thread
```

### Production Example

Suppose an application performs many **short-lived, independent background tasks**, such as small external service calls.

```text
Task 1 → Thread 1
Task 2 → Thread 2
Task 3 → Thread 3

Thread 2 becomes idle

Task 4 → Reuse Thread 2
```

### Important

`CachedThreadPool` can create a very large number of threads.

Therefore, in a production system with unpredictable/high traffic, I would generally prefer a **bounded custom `ThreadPoolExecutor`**.

### Interview point

> "CachedThreadPool is useful for short-lived, bursty tasks where threads can be reused, but I would be careful in production because it doesn't provide a practical upper bound on the number of threads."

---

# 3. `ThreadPoolExecutor`

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    4,                      // corePoolSize
    40,                     // maximumPoolSize
    60,
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(200)
);
```

### What it does

Provides **fine-grained control** over the thread pool.

We can configure:

* Core thread count
* Maximum thread count
* Queue capacity
* Keep-alive time
* Rejection policy
* Thread factory

### Production Example

**Order processing / background job service**

Suppose:

```text
Core threads     = 4
Maximum threads  = 40
Queue capacity   = 200
```

Behavior:

```text
First 4 tasks
     ↓
4 core threads execute

Next tasks
     ↓
Queue (up to 200)

Queue full
     ↓
Create additional threads
     ↓
Up to 40 threads

40 threads busy + queue full
     ↓
Rejection policy
```

This gives the application **backpressure** instead of allowing unlimited work to consume system resources.

### Interview point

> "For a production application, I generally prefer a custom ThreadPoolExecutor when I need predictable resource usage because I can control the core pool, maximum pool, queue capacity and rejection policy."

---

# Quick Comparison

| API                     | Main Idea                              | Production Use                                  |
| ----------------------- | -------------------------------------- | ----------------------------------------------- |
| `newFixedThreadPool(5)` | Fixed workers + queue                  | Controlled CPU/resource-heavy tasks             |
| `newCachedThreadPool()` | Reuse idle threads, create when needed | Short-lived, bursty tasks                       |
| `ThreadPoolExecutor`    | Full customization                     | Production workloads requiring resource control |

## Interviewer-Impressing Summary

> **"I don't choose a thread pool only based on the number of requests. I look at the workload — CPU-bound vs I/O-bound, task duration, traffic burst, queue capacity and downstream resource limits. For predictable workloads I can use a fixed pool; for short-lived bursty tasks a cached pool may work, but for production systems I generally prefer a bounded `ThreadPoolExecutor` so I can control concurrency, queueing and rejection."**
