#include "loop_unrolling.h"
#include <benchmark/benchmark.h>
#include <iostream>

#include <vector>
#include <random>

static std::vector<int> GenerateRandomInts(int size) {

    std::random_device rd;
    std::mt19937 mt(rd());
    std::uniform_real_distribution<double> dist(1, 100);

    std::vector<int> ints;
    ints.reserve(size);

    for (int i = 0; i < size; i++) {
        ints.push_back(dist(mt));
    }

    return ints;
}

static void BenchmarkSimpleLoop(benchmark::State& state) {
    int size = state.range(0);
    std::vector<int> x = GenerateRandomInts(size);
    std::vector<int> y = GenerateRandomInts(size);

    for (auto _ : state) {
        auto zippedVector = LoopUnrolling::SimpleLoop(x, y, size);
        benchmark::DoNotOptimize(zippedVector);
    }
}

static void BenchmarkSimpleLoopUnrolled(benchmark::State& state) {
    int size = state.range(0);
    std::vector<int> x = GenerateRandomInts(size);
    std::vector<int> y = GenerateRandomInts(size);

    for (auto _ : state) {
        auto zippedVector = LoopUnrolling::SimpleLoopUnrolled(x, y, size);
        benchmark::DoNotOptimize(zippedVector);
    }
}

BENCHMARK(BenchmarkSimpleLoop)->RangeMultiplier(10)->Range(1, 1000000);
BENCHMARK(BenchmarkSimpleLoopUnrolled)->RangeMultiplier(10)->Range(1, 1000000);

BENCHMARK_MAIN();
