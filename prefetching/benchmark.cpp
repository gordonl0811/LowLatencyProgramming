#include <benchmark/benchmark.h>
#include "prefetching.h"

#include "../utils/utils.h"

static void BenchmarkBinarySearch(benchmark::State& state) {

    int size = state.range(0);
    int* nums = GenerateSequencedArray(size);

    srand( (unsigned)time(NULL) );

    for (auto _ : state) {
        int target = rand() % size;
        benchmark::DoNotOptimize(Prefetching::BinarySearch(target, nums, size));
    }

    delete nums;
}

static void BenchmarkBinarySearchPrefetched(benchmark::State& state) {

    int size = state.range(0);
    int* nums = GenerateSequencedArray(size);

    srand( (unsigned)time(NULL) );

    for (auto _ : state) {
        int target = rand() % size;
        benchmark::DoNotOptimize(Prefetching::BinarySearchPrefetched(target, nums, size));
    }

    delete nums;
}

BENCHMARK(BenchmarkBinarySearch)->RangeMultiplier(10)->Range(1, 1000000000);
BENCHMARK(BenchmarkBinarySearchPrefetched)->RangeMultiplier(10)->Range(1, 1000000000);

BENCHMARK_MAIN();
