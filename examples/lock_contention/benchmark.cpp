#include <benchmark/benchmark.h>
#include "lock_contention.h"

#include "../utils/utils.h"

static void BenchmarkTrimVector(benchmark::State& state) {

    int n = (int) state.range(0);
    std::vector<int> ints = GenerateRandomInts(n, 1000);

    for (auto _ : state) {
        benchmark::DoNotOptimize(Predication::TrimVector(500, ints, n));
    }

}

static void BenchmarkTrimVectorPredicated(benchmark::State& state) {

    int n = (int) state.range(0);
    std::vector<int> ints = GenerateRandomInts(n, 1000);

    for (auto _ : state) {
        benchmark::DoNotOptimize(Predication::TrimVectorPredicated(500, ints, n));
    }

}

BENCHMARK(BenchmarkTrimVector)->RangeMultiplier(10)->Range(1, 10000000);
BENCHMARK(BenchmarkTrimVectorPredicated)->RangeMultiplier(10)->Range(1, 10000000);

BENCHMARK_MAIN();
