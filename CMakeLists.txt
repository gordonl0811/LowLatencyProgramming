cmake_minimum_required(VERSION 3.21)
project(low_latency_microbenchmarks)

set(CMAKE_CXX_STANDARD 14)

include_directories(.)
include_directories(inlining)

add_executable(low_latency_microbenchmarks
        inlining/benchmark.cpp
        inlining/inlining.cpp
        inlining/inlining.h)
