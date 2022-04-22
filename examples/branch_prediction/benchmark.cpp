#include "branch_prediction.h"
#include <benchmark/benchmark.h>
#include <iostream>

static void BenchmarkCountEvens(benchmark::State& state) {

    std::vector<int> someEvens = BranchPrediction::GenerateSomeEvens((float) state.range(0), 1000000, 100);

    for (auto _ : state) {
        benchmark::DoNotOptimize(BranchPrediction::CountEvens(someEvens));
    }
}

static void BenchmarkCountEvensLikely(benchmark::State& state) {

    std::vector<int> someEvens = BranchPrediction::GenerateSomeEvens((float) state.range(0), 1000000, 100);

    for (auto _ : state) {
        benchmark::DoNotOptimize(BranchPrediction::CountEvensLikely(someEvens));
    }
}


//BENCHMARK(BenchmarkNoBranchPrediction)->RangeMultiplier(10)->Range(1, 10000000);
//BENCHMARK(BenchmarkBranchPrediction)->RangeMultiplier(10)->Range(1, 10000000);

BENCHMARK(BenchmarkCountEvens)->DenseRange(0, 100, 10);
BENCHMARK(BenchmarkCountEvensLikely)->DenseRange(0, 100, 10);

BENCHMARK_MAIN();
