# Overview

Prefetching is a technique where processors are instructed to speculatively fetch instructions or data from their storage into faster memory. There are two main classifications, the first being hardware prefetching, usually involving a dedicated mechanism in the processor that recognises patterns of instructions or data being requested by the program. The alternative approach is software prefetching, which is where prefetch instructions are inserted into code. This is either performed by the compiler, or manually by the programmer.

# Motivation

Computers often have a two-level cache hierarchy consisting of primary on-chip cache and secondary off-chip cache. A cache miss occurs when a CPU attempts to retrieve data in a cache, but the data isn't there (contrary to a cache hit). The data must then be retrieved from a lower level of cache/memory (i.e. the secondary cache) which has a longer access time. This time penalty, added to the time penalty from attempting to retrieve data from the first level of cache, is detrimental to low latency applications and must be mitigated where possible.

# Description

The CPU can be instructed to fetch instructions or data into faster (and more local) memory, reducing latency when it comes to actually using it. This technique is known as "prefetching". Modern hardware can automatically speculate the instructions/data that will be used by recognising patterns in the execution of a program using a "hardware prefetcher". There are two main approaches: "sequential" prefetching and "stride" prefetching. The prefetcher works well for regular memory accesses, but is not as effective for irregular accesses (such as those that are data-dependent).

Software prefetching is another approach to instructing the CPU when to prefetch instructions. The compiler is able to do this automatically by embedding prefetch instructions into the compiled code. For example, one method involves first prefetching data into L2 cache with a `vprefetch1` instruction, followed by a `vprefetch0` instruction to move the data into L1 cache.

As with most performance optimisations, it is risky to rely on the compiler to prefetch data optimally. Sometimes the programmer must indicate which addresses of data in memory are likely to be accessed soon, and software prefetch intrinsics are used to direct this. GCC uses `__builtin_prefetch` on memory addresses to explicitly move data into a cache, as long as the target supports prefetching.

The timing of the prefetch is also important. If a prefetch is performed too late, the data will not yet be in cache and a cache miss will occur as before. If a prefetch is too early, the data could be evicted from the cache before it is accessed, essentially causing another cache miss. The table below describes the classifications of [prefetches](https://faculty.cc.gatech.edu/~hyesoon/lee_taco12.pdf), with the first three rows being particularly of interest:

| Classification | Accuracy   | Timeliness                                                       |
|----------------|------------|------------------------------------------------------------------|
| Timely         | accurate   | best prefetching                                                 |
| Late           | accurate   | demand request arrives before prefetched block is serviced       |
| Early          | accurate   | demand request arrives after the block is evicted from a cache   |
| Redundant_dc   | accurate   | the same cache block is in data cache                            |
| Redundant_mshr | accurate   | the same cache block is in MSHR (Miss Status Holding Register)   |
| Incorrect      | inaccurate | block is never used even after the block is evicted from a cache |

Without carefully timing the prefetch requests, prefetching will not be useful for the application.

Consider the following implementation of a binary search:

```c++
static int BinarySearchPrefetched(int target, int nums[], int size) {
    
    int left = 0;
    int mid;
    int right = size - 1;

    while (left <= right) {

        mid = (left + right) / 2;

        __builtin_prefetch (&nums[(mid + 1 + right) / 2], 0, 1);
        __builtin_prefetch (&nums[(left + mid - 1) / 2], 0, 1);

        if (nums[mid] < target) {
            left = mid + 1;
        } else if (nums[mid] > target) {
            right = mid - 1;
        } else {
            return mid;
        }
    }

    return -1;

}
```

The two "mid" locations are prefetched for the next iteration of the `while` loop using the `__builtin_prefetch` intrinsic, with the second and third arguments specifying read-write access and temporal locality respectively. The 0 indicates that the code is preparing for a read-only acccess, and the 1 suggest a low degree of temporal locality (it will not be reused often).

# Benchmark Results

![BinarySearch Benchmark Results](./images/BinarySearch.png)

# Use cases

There are plenty of cases where software prefetching is advantageous to relying on automatic hardware prefetching. This section contains examples of when explicitly requesting data is beneficial to an application.

## Irregular Memory Accesses

The benchmarked binary search is a good example of prefetching memory irregularly. The hardware prefetcher is unlikely to predict the data that should be fetched as the access pattern of the array seems random, so by using GCC's `__builtin_prefetch` intrinsic we can see an improvement in the benchmarks.

## Handling a Large Number of Data Streams

Prefetching from a stream of data (e.g. from loops) can be limited by the hardware available. For example, a Loop Stream Detector detects when the CPU is executing a loop in an application. When the number of streams exceeds the capacity of the hardware, hardware prefetchers struggle to keep track of the number of streams - this can be mitigated by utilising software prefetching and inserting prefetch requests for each stream independently. 

## Handling Short Data Streams

Hardware prefetchers do not automatically know the direction and distance of a stream. Training time is needed, and even aggressive prefetchers need at least two cache misses to detect the direction of a stream. This means that short streams of data will result in latency penalties from training, whilst the hardware prefetchers may not even be trained at all. In the context of low-latency applications, the programmer may want the training time (and cache misses) to be eliminated altogether. For this reason, explicit software prefetching will be required to prevent the extra time penalty that occurs. 

It is worth noting that modern hardware has been developing the ability to handle short streams with hardware-based prefetching, but explicitly declaring prefetch instructions within software eliminates the risk of hardware failing to handle the stream without a cost.

## Fine-grained Control over Cache

With software prefetching, the programmer has more control of where the prefetched data is placed in the cache hierarchy. In most cases, the data should be placed in L1 cache (the highest and fastest level of memory), but hardware prefetchers either have their own cache insertion policy which may end up with data being placed unintentionally in L2 or L3 cache. For applications where minimising latency is critical, the programmer will want as much control of the cache as possible.