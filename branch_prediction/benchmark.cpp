#include "branch_prediction.h"
#include <benchmark/benchmark.h>
#include <iostream>

static void BenchmarkNoBranchPrediction(benchmark::State& state) {
    for (auto _ : state) {
        int res = BranchPrediction::IsSeven(7);
        benchmark::DoNotOptimize(res);
    }
}

static void BenchmarkBranchPrediction(benchmark::State& state) {
    for (auto _ : state) {
        int res = BranchPrediction::IsSevenPredicted(7);
        benchmark::DoNotOptimize(res);
    }
}


BENCHMARK(BenchmarkNoBranchPrediction)->RangeMultiplier(10)->Range(1, 10000000);
BENCHMARK(BenchmarkBranchPrediction)->RangeMultiplier(10)->Range(1, 10000000);

BENCHMARK_MAIN();
