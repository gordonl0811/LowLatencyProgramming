#include "prefetching.h"
#include <benchmark/benchmark.h>

using namespace std;

static void BenchmarkBinarySearchNoPrefetching(benchmark::State& state) {
    for (auto _ : state) {
        for (auto i = 0; i < state.range(0); i++) {
            int size = state.range(0);
            int idx = Prefetching::BinarySearch();
            benchmark::DoNotOptimize(idx);
        }
    }
}

static void BenchmarkBinarySearchPrefetching(benchmark::State& state) {
    for (auto _ : state) {
        for (auto i = 0; i < state.range(0); i++) {
            int size = state.range(0);
            int idx = Prefetching::BinarySearch();
            benchmark::DoNotOptimize(idx);
        }
    }
}

BENCHMARK(BenchmarkBinarySearchNoPrefetching)->RangeMultiplier(10)->Range(1, 10000);
BENCHMARK(BenchmarkBinarySearchPrefetching)->RangeMultiplier(10)->Range(1, 10000);

BENCHMARK_MAIN();
