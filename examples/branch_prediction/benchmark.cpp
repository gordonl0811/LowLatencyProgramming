#include "branch_prediction.h"
#include <benchmark/benchmark.h>

static void BenchmarkCountEvens(benchmark::State& state) {

    std::vector<int> someEvens = BranchPrediction::GenerateSomeEvens((float) state.range(0), 1000000, 100);

    for (auto _ : state) {
        benchmark::DoNotOptimize(BranchPrediction::CountEvens(someEvens));
    }
}

BENCHMARK(BenchmarkCountEvens)->DenseRange(0, 100, 5);

BENCHMARK_MAIN();
