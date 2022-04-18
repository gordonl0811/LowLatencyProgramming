# Overview

Inlining is a simple code optimisation that replaces a function call with the body of the function in the compiled assembly code. This is a simple space-time tradeoff which potentially increases the speed of program execution at the cost of larger binaries. Compilers can perform this optimisation by analysing the costs and benefits that occur from inlining the function, given that it meets the requirements for being inlined.

# Motivation

Functions are used to organise code and break down programs into individual components. This makes code reusable via function calls, and function calls are usually made multiple times within a program.

However, function calls within a program are expensive. This is a brief overview of the operations that are executed:

- The stack frame is pushed onto the stack
- The subroutine/functions instructions are executed
- The stack frame is popped from the stack
- The function returns to its return address

In lower-level/assembly terms, the call for the destination function can also cause the instruction pipeline to be flushed, causing a frontend stall. Repeatedly calling small functions means that a lot of overhead is generated for little work done.

This could be avoided by the programmer by copying the code into the function call site, but this contradicts the software engineering philosophy of creating modular, well-organised code.

# Description

Function inlining aims to eliminate the overhead of subroutine/function calls by embedding the body of the functions in the place of the call at compile time, i.e. when translating source code into assembly.

Consider the following functions:

```c++
static int Cube(int x) {
    return x * x * x; 
};

static int Main() {
    int num = 8;
    int result = Cube(num);
    return result;
}
```

This is what `Cube()` and `Main()` look like in assembly when compiling with `x86-64 gcc 11.2` without optimisations (and without inlining):

```asm
Cube(int):
        push    rbp
        mov     rbp, rsp
        mov     DWORD PTR [rbp-4], edi
        mov     eax, DWORD PTR [rbp-4]
        imul    eax, eax
        imul    eax, DWORD PTR [rbp-4]
        pop     rbp
        ret
Main():
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     DWORD PTR [rbp-4], 8
        mov     eax, DWORD PTR [rbp-4]
        mov     edi, eax
        call    Cube(int)
        mov     DWORD PTR [rbp-8], eax
        mov     eax, DWORD PTR [rbp-8]
        leave
        ret
```

In this example, no level of optimisation was used because GCC (and other modern optimising compilers) are smart enough to inline such a function automatically. In C++, the programmer can add the `inline` keyword to the function to _hint_ to the compiler that the function should be inlined.

However, sometimes it isn't enough to trust that the compiler will inline a function for you. For GCC, the `always_inline` attribute can be used to force a function to be inlined (as long as it does not breach any requirements). Other compilers will have their own set of compiler intrinsics.

Here is the `Cube` function as before, but with the inlining attribute included:

```c++
static int __attribute__ ((always_inline)) Cube(int x) {
    return x * x * x; 
};
```

When compiled with the `Main()` function, the assembly looks rather different:

```asm
Main():
        push    rbp
        mov     rbp, rsp
        mov     DWORD PTR [rbp-4], 8
        mov     eax, DWORD PTR [rbp-4]
        mov     DWORD PTR [rbp-12], eax
        mov     eax, DWORD PTR [rbp-12]
        imul    eax, eax
        imul    eax, DWORD PTR [rbp-12]
        mov     DWORD PTR [rbp-8], eax
        mov     eax, DWORD PTR [rbp-8]
        pop     rbp
        ret
```

The key thing to notice here is that the functionality of `Cube()` is now directly embedded in the `Main()` function, replacing the `call` instruction seen in the original compiled assembly.


There are tradeoffs that need to be considered when inlining functions. The absence of the call stack also makes debugging more challenging. This optimisation also results in "code bloat", where the program becomes longer than it would be without the inlined function. The more locations where a function is inlined will cause the binaries of the compiled program to grow. In the worst case scenario, this causes thrashing when the sections of code are fetched from higher-level memory.

These tradeoffs aren't much of a concern when programming with low-latency in mind. However, there are certain cases where a function cannot be inlined. For example, in C++ a compiler can't inline a function if:
1. The function is recursive
2. It has a variable length argument list
3. It is a virtual function that is called virtually (direct calls can be inlined)

These are a few of the main cases - several more subtle restrictions can be found online.

# Benchmark Results

| Iterations Performed | Cube Execution Time (ns) | CubeInlined Execution Time (ns) |
|----------------------|--------------------------|---------------------------------|
|                    1 |                     6.06 |                            5.22 |
|                   10 |                       35 |                            29.9 |
|                  100 |                      339 |                             287 |
|                 1000 |                     3237 |                            2756 |
|                10000 |                    32711 |                           27364 |
|               100000 |                   322723 |                          274317 |
|              1000000 |                  3266517 |                         2755897 |
|             10000000 |                 32828489 |                        27445521 |

![Cube Benchmark Results](./images/Cube.png)

# Use Cases

Function inlining is a commonly-used optimisation that can be performed by most compilers automatically, which means it is used almost universally whether intentional or not. The compiler performs a cost-benefit analysis to gauge whether the optimisation is worthwhile, but inlining can be forced with compiler intrinsics - as shown in the previous examples, which uses GCC's `always_inline` attribute to achieve this. This overrides the judgement of the compiler, which will inline the function if it is able to do so.

It is always good practice to explicitly declare when a function should be inlined instead of trusting the compiler to do so for you, especially when working in a low-latency domain where achieving nanoseconds of speedup is crucial. It is the programmer's responsibility to benchmark their code and ensure that thrashing or other performance-related issues do not occur.

