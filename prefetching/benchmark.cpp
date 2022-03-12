#include "prefetching.h"
#include <benchmark/benchmark.h>

using namespace std;

static int* GenerateSortedArray(int size) {

    int* nums = new int[size];

    for (int i = 0; i < size; i++) {
        nums[i] = i;
    }

    return nums;
}

static void BenchmarkBinarySearchNoPrefetching(benchmark::State& state) {

    srand( (unsigned)time(NULL) );
    int size = state.range(0);
    int* nums = GenerateSortedArray(size);

    for (auto _ : state) {
        int target = rand() % size;
        int idx = Prefetching::BinarySearchNoPrefetch(target, nums, size);
        benchmark::DoNotOptimize(idx);
    }

    delete nums;
}

static void BenchmarkBinarySearchPrefetching(benchmark::State& state) {

    srand( (unsigned)time(NULL) );
    int size = state.range(0);
    int* nums = GenerateSortedArray(size);

    for (auto _ : state) {
        int target = rand() % size;
        int idx = Prefetching::BinarySearchPrefetch(target, nums, size);
        benchmark::DoNotOptimize(idx);
    }

    delete nums;

}

BENCHMARK(BenchmarkBinarySearchNoPrefetching)->RangeMultiplier(8)->Range(1, 4096*4096*8);
BENCHMARK(BenchmarkBinarySearchPrefetching)->RangeMultiplier(8)->Range(1, 4096*4096*8);

BENCHMARK_MAIN();
