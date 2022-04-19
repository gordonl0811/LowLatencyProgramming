# Programming Optimisations and Techniques for Low-Latency Applications

A repository demonstrating a subset of techniques designed to increasing the performance of applications, particularly those that should be optimised for low-latency.

## Index

The following table is a directory of the optimisations included and their corresponding benchmarks. More detail about the benchmarking results can be found in the Documentation, also provided in the table.

| Optimisation      | Documentation                     | Benchmarks                                       |
|-------------------|-----------------------------------|--------------------------------------------------|
| Inlining          | [Link](docs/inlining.md)          | [Link](examples/inlining/benchmark.cpp)          |
| Loop Unrolling    | [Link](docs/loop_unrolling.md)    | [Link](examples/loop_unrolling/benchmark.cpp)    |
| Predication       | [Link](docs/predication.md)       | [Link](examples/predication/benchmark.cpp)       |
| Prefetching       | [Link](docs/prefetching.md)       | [Link](examples/prefetching/benchmark.cpp)       |
| SIMD Instructions | [Link](docs/simd_instructions.md) | [Link](examples/simd_instructions/benchmark.cpp) |

## Benchmarks

### Overview

Benchmarks were collected using Imperial College London Department of Computing's undergraduate lab machines, with an Intel Core i7-8700 (3.20GHz) and 16GB of RAM, using g++ `9.4.0`.

### Requirements

- CMake, Version 3.16+
- GNU's g++ compiler

### Running Benchmarks Locally

Follow these instructions to rerun the benchmarks in the `examples` directory:

```
// Navigate to the directory of the optimisation
cd examples/{OPTIMISATION}

// Create a build directory
mkdir build
cd build

// Create the benchmark executable
cmake -DCMAKE_BUILD_TYPE=Release
make

// Run the benchmark
./{OPTIMISATION}Benchmarks
```