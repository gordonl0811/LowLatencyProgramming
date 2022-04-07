# Overview

Inlining is a simple code optimisation that replaces a function call with the body of the function in the compiled assembly code. Compilers can perform this optimisation by analysing the costs and benefits that occur from inlining the function, given that it meets the requirements for being inlined.

# Motivation

Function calls within a program are expensive. This is a brief overview of operations that are executed:

- The stack frame is pushed onto the stack
- The subroutine/functions instructions are executed
- The stack frame is popped from the stack
- The function returns to its return address

The branch to the destination function can also cause the instruction pipeline to be flushed, causing a frontend stall.

Using many instances of small functions

# Description

Function inlining aims to eliminate the overhead of subroutine/function calls by embedding the body of the functions in the place of the call.

There are tradeoffs that need to be considered when inlining functions. The absence of the call stack also makes debugging more challenging. This optimisation also results in "code bloat", where the program becomes longer than it would be without the inlined function. The more locations where a function is inlined will cause the binaries of the compiled program to grow. In the worst case scenario, this causes thrashing when the sections of code are fetched from higher-level memory.

These tradeoffs aren't much of a concern when programming with low-latency in mind.

However, there are certain cases where a function cannot be inlined. For example, in C++ a compiler can't inline a function if:
1. The function is recursive
2. It has a variable length argument list
3. It is a virtual function that is called virtually (direct calls can be inlined)

These are a few of the main cases - several more subtle restrictions can be found online.

# Benchmark Results

TODO

# Use Cases

Function inlining is a commonly-used optimisation that can be performed by most compilers automatically, which means it is used almost universally whether intentional or not. The compiler performs a cost-benefit analysis to gauge whether the optimisation is worthwhile, but inlining can be forced with compiler intrinsics - as shown in the benchmarking example, which uses GCC's `always_inline` attribute to achieve this. This overrides the judgement of the compiler, which will inline the function if it is able to do so.

It is always good practice to explicitly declare when a function should be inlined instead of trusting the compiler to do so for you, especially when working in a low-latency domain where achieving nanoseconds of speedup is crucial. It is the programmer's responsibility to benchmark their code and ensure that thrashing or other performance-related issues do not occur.

