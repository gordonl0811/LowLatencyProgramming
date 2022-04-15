#include <benchmark/benchmark.h>
#include "inlining.h"

static void BenchmarkCube(benchmark::State& state) {
    for (auto _ : state) {
        for (auto i = 0; i < state.range(0); i++) {
            benchmark::DoNotOptimize(Inlining::Cube(5));
        }
    }
}

static void BenchmarkCubeInlined(benchmark::State& state) {
    for (auto _ : state) {
        for (auto i = 0; i < state.range(0); i++) {
            benchmark::DoNotOptimize(Inlining::CubeInlined(5));
        }
    }
}


BENCHMARK(BenchmarkCube)->RangeMultiplier(10)->Range(1, 10000000);
BENCHMARK(BenchmarkCubeInlined)->RangeMultiplier(10)->Range(1, 10000000);

BENCHMARK_MAIN();
