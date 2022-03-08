#include "loop_unrolling.h"
#include <benchmark/benchmark.h>
#include <iostream>

using namespace std;

static void benchmarkSimpleLoop(benchmark::State& state) {
    for (auto _ : state) {
        for (auto i = 0; i < state.range(0); i++) {
            auto zippedVector = LoopUnrolling::SimpleLoop();
            benchmark::DoNotOptimize(zippedVector);
        }
    }
}

static void benchmarkSimpleLoopUnrolled(benchmark::State& state) {
    for (auto _ : state) {
        for (auto i = 0; i < state.range(0); i++) {
            auto zippedVector = LoopUnrolling::SimpleLoopUnrolled();
            benchmark::DoNotOptimize(zippedVector);
        }
    }
}

BENCHMARK(benchmarkInlining)->RangeMultiplier(10)->Range(1, 10000000);
BENCHMARK(benchmarkNoInlining)->RangeMultiplier(10)->Range(1, 10000000);

BENCHMARK_MAIN();
