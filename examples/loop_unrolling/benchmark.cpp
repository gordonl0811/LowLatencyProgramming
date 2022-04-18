#include <benchmark/benchmark.h>
#include "loop_unrolling.h"

#include <vector>
#include "../../utils/utils.h"

static void BenchmarkSumVectors(benchmark::State& state) {

    int size = (int) state.range(0);
    std::vector<int> x = GenerateRandomInts(size);
    std::vector<int> y = GenerateRandomInts(size);

    for (auto _ : state) {
        benchmark::DoNotOptimize(LoopUnrolling::SumVectors(x, y, size));
    }

}

static void BenchmarkSumVectorsUnrolled(benchmark::State& state) {

    int size = (int) state.range(0);
    std::vector<int> x = GenerateRandomInts(size);
    std::vector<int> y = GenerateRandomInts(size);

    for (auto _ : state) {
        benchmark::DoNotOptimize(LoopUnrolling::SumVectorsUnrolled(x, y, size));
    }

}

BENCHMARK(BenchmarkSumVectors)->RangeMultiplier(10)->Range(10, 10000000);
BENCHMARK(BenchmarkSumVectorsUnrolled)->RangeMultiplier(10)->Range(10, 10000000);

BENCHMARK_MAIN();
