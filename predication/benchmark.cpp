#include "predication.h"
#include "../utils/utils.h"
#include <benchmark/benchmark.h>

#include <vector>

static void BenchmarkHeapify(benchmark::State& state) {

    for (auto _ : state) {
        int n = (int) state.range(0);
        std::vector<int> ints = GenerateRandomInts(n, 1000);

        // HeapSort: Building heap stage
        for (int i = n / 2 - 1; i >= 0; i--) {
            Predication::Heapify(ints, i);
        }
    }

}

static void BenchmarkHeapifyWithPredication(benchmark::State& state) {

    for (auto _ : state) {
        int n = (int) state.range(0);
        std::vector<int> ints = GenerateRandomInts(n, 1000);

        // HeapSort: Building heap stage
        for (int i = n / 2 - 1; i >= 0; i--) {
            Predication::HeapifyWithPredication(ints, i);
        }
    }

}

BENCHMARK(BenchmarkHeapify)->RangeMultiplier(10)->Range(1, 10000);
BENCHMARK(BenchmarkHeapifyWithPredication)->RangeMultiplier(10)->Range(1, 10000);

BENCHMARK_MAIN();
