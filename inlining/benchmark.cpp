#include "inlining.h"
#include <benchmark/benchmark.h>
#include <iostream>

using namespace std;

static void benchmarkInlining(benchmark::State& state) {
    for (auto _ : state) {
        for (auto i = 0; i < state.range(0); i++) {
            auto cubed = Inlining::CubeNoInline(5);
            benchmark::DoNotOptimize(cubed);
        }
    }
}

BENCHMARK(benchmarkInlining)->RangeMultiplier(10)->Range(1, 10000000);

BENCHMARK_MAIN();
