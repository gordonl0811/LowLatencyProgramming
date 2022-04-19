#include <benchmark/benchmark.h>
#include "loop_unrolling.h"

#include <vector>
#include "../utils/utils.h"

static void BenchmarkSumVectors(benchmark::State& state) {

    int size = (int) state.range(0);
    std::vector<int> x = GenerateRandomInts(size);
    std::vector<int> y = GenerateRandomInts(size);

    for (auto _ : state) {
        benchmark::DoNotOptimize(LoopUnrolling::SumVectors(x, y, size));
    }

}

static void BenchmarkSumVectorsUnrolledTwo(benchmark::State& state) {

    int size = (int) state.range(0);
    std::vector<int> x = GenerateRandomInts(size);
    std::vector<int> y = GenerateRandomInts(size);

    for (auto _ : state) {
        benchmark::DoNotOptimize(LoopUnrolling::SumVectorsUnrolledTwo(x, y, size));
    }

}

static void BenchmarkSumVectorsUnrolledFour(benchmark::State& state) {

    int size = (int) state.range(0);
    std::vector<int> x = GenerateRandomInts(size);
    std::vector<int> y = GenerateRandomInts(size);

    for (auto _ : state) {
        benchmark::DoNotOptimize(LoopUnrolling::SumVectorsUnrolledFour(x, y, size));
    }

}

BENCHMARK(BenchmarkSumVectors)->RangeMultiplier(8)->Range(8, 4096*4096);
BENCHMARK(BenchmarkSumVectorsUnrolledTwo)->RangeMultiplier(8)->Range(8, 4096*4096);
BENCHMARK(BenchmarkSumVectorsUnrolledFour)->RangeMultiplier(8)->Range(8, 4096*4096);

BENCHMARK_MAIN();
