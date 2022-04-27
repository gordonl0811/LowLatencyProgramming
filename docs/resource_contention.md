# Overview

A lot of modern applications are multi-threaded to improve performance and utilise the computational power available. However, a lot of programs work on shared resources (e.g. files, data structures) that must have safeguards to prevent concurrency bugs from occurring. The contention between threads to access these resources can hamper the benefits of parallel programming, even degrading the performance of an application in certain scenarios. 

# Motivation

Intuition will often suggest that having more threads working on a task will decrease the execution time of it, as tasks can be distributed to several parallel workers. Examine the following three implementations of a function that increments a `counter` a given number of times.

The first implementation is for a single thread and does not use any concurrency primitives:

```c++
static void Increment(int &counter, int iterations) {
    for (int i = 0; i < iterations; i++) {
        counter++;
    }
}
```

This multi-threaded implementation of the `Increment` function uses `atomic` variables to prevent multiple threads reading/writing to the variable simultaneously, using "memory barriers" to enforce a certain ordering when accessing the `counter` which prevents data races:

```c++
static void IncrementAtomic(std::atomic<int> &counter, int iterations) {
    for (int i = 0; i < iterations; i++) {
        counter.fetch_add(1);
    }
}
```

This alternative multi-threaded implementation uses a `mutex` (otherwise known as a "lock"), which only allows the thread that "holds" the mutex to operate on the counter:

```c++
static void IncrementMutex(int &counter, int iterations, std::mutex &mtx) {
    for (int i = 0; i < iterations; i++) {
        mtx.lock();
        counter++;
        mtx.unlock();
    }
}
```

Benchmarking these functions shows considerable performance degradation when having more than one thread working on the task. These were the results when using these functions to increment a counter 10 million times:

| Function                      | Execution Time (ns) |
|-------------------------------|---------------------|
| Increment (one thread)        | 12713615            |
| IncrementAtomic (one thread)  | 54969640            |
| IncrementAtomic (two threads) | 151883027           |
| IncrementMutex (one thread)   | 162426559           |
| IncrementMutex (two threads)  | 472225654           |

Contrary to the idea that more threads corresponds to increased performance, the results show that having two threads compete for a single resource caused the execution time to triple for both `IncrementAtomic` and `IncrementMutex`. Not only that, significant overhead is induced from having additional concurrency primitives protecting the `counter` variable from creating a data race.

# Description

# Benchmark Results

| Number of Threads | Increment Execution Time (ns) | IncrementAtomic Execution Time (ns) | IncrementMutex Execution Time (ns) |
|-------------------|-------------------------------|-------------------------------------|------------------------------------|
|                 1 |                       5083685 |                            22228341 |                           65573654 |
|                 2 |                           N/A |                            64777800 |                          184217894 |
|                 3 |                           N/A |                            68674781 |                          233319335 |
|                 4 |                           N/A |                            68748340 |                          253981544 |
|                 5 |                           N/A |                            70246825 |                          292990498 |
|                 6 |                           N/A |                            71283986 |                          327506162 |
|                 7 |                           N/A |                            66261171 |                          377840225 |
|                 8 |                           N/A |                            68845000 |                          417544009 |

Incrementing 4032000 times (divisible by integers 1-8 so that the workload is evenly distributed between threads).