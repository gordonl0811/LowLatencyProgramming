#include "predication.h"
#include "../utils/utils.h"
#include <benchmark/benchmark.h>

#include <vector>

static void BenchmarkTrim(benchmark::State& state) {

    for (auto _ : state) {
        int n = (int) state.range(0);
        std::vector<int> ints = GenerateRandomInts(n, 1000);
        std::vector<int> trimmedInts = Predication::Trim(500, ints, n);
        benchmark::DoNotOptimize(trimmedInts);
    }

}

static void BenchmarkTrimPredicated(benchmark::State& state) {

    for (auto _ : state) {
        int n = (int) state.range(0);
        std::vector<int> ints = GenerateRandomInts(n, 1000);
        std::vector<int> trimmedInts = Predication::TrimPredicated(500, ints, n);
        benchmark::DoNotOptimize(trimmedInts);
    }

}

BENCHMARK(BenchmarkTrim)->RangeMultiplier(10)->Range(1, 100000);
BENCHMARK(BenchmarkTrimPredicated)->RangeMultiplier(10)->Range(1, 100000);

BENCHMARK_MAIN();
