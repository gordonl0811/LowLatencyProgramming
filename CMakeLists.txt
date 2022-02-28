cmake_minimum_required(VERSION 3.21)
project(low_latency_microbenchmarks)

set(CMAKE_CXX_STANDARD 14)

# Add -O0 to remove optimizations
IF(CMAKE_COMPILER_IS_GNUCXX)
    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -O0")
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_DEBUG} -O0")
ENDIF(CMAKE_COMPILER_IS_GNUCXX)

include_directories(.)
include_directories(inlining)

add_executable(low_latency_microbenchmarks
        inlining/benchmark.cpp
        inlining/inlining.h)

find_package(benchmark REQUIRED)
target_link_libraries(low_latency_microbenchmarks benchmark::benchmark)