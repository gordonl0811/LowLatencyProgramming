# Programming Optimisations and Techniques for Low-Latency Applications

This repository includes content tuned for writing low-latency applications and the considerations that should be made in their development:

- Documentation of important concepts that are critical for high-performance applications, e.g. computer architecture, systems design.
- Demonstrations of techniques designed to increase the performance of applications. Reproducible benchmarks are provided (see the index section below), with instructions on how to run them.

Many of the code transformations are performed automatically by modern optimising compilers, but even the best compilers can miss out on optimising opportunities for complex functions and codebases. A difference of nanoseconds can determine the success of an application, whether that be in high-frequency trading, networking, or other services where low latencies and fast execution times are critical.

## Index

The following table is a directory of the optimisations included and their corresponding benchmarks. More detail about the benchmarking results can be found in the Documentation, also provided in the table.

| Optimisation        | Documentation                       | Benchmarks                                         |
|---------------------|-------------------------------------|----------------------------------------------------|
| Inlining            | [Link](docs/inlining.md)            | [Link](examples/inlining/benchmark.cpp)            |
| Loop Unrolling      | [Link](docs/loop_unrolling.md)      | [Link](examples/loop_unrolling/benchmark.cpp)      |
| Predication         | [Link](docs/predication.md)         | [Link](examples/predication/benchmark.cpp)         |
| Prefetching         | [Link](docs/prefetching.md)         | [Link](examples/prefetching/benchmark.cpp)         |
| SIMD Instructions   | [Link](docs/simd_instructions.md)   | [Link](examples/simd_instructions/benchmark.cpp)   |
| Branch Prediction   | [Link](docs/branch_prediction.md)   | [Link](examples/branch_prediction/benchmark.cpp)   |
| Resource Contention | [Link](docs/resource_contention.md) | [Link](examples/resource_contention/benchmark.cpp) |

The repository is gradually being expanded - if there are any mistakes within the documentation, or there are any interesting optimisations that haven't been included, please feel free to get in touch!

## Benchmarks

### Overview

Benchmarks were collected using Imperial College London Department of Computing's undergraduate laboratory machines, with an Intel Core i7-8700 (3.20GHz) and 16GB of RAM, using g++ `9.4.0`. Most of the benchmarks were run without optimisations enabled (`-O0`), particularly because some optimisations may already be performed at certain optimisation levels.

### Requirements

- CMake, Version 3.16+
- GNU's g++ compiler

### Running Benchmarks Locally

Follow these instructions to run the benchmarks in the `examples` directory:

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