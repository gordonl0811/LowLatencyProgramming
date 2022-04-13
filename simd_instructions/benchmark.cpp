#include "simd_instructions.h"
#include <benchmark/benchmark.h>

#include "../utils/utils.h"

static void BenchmarkMultiplyAddScalar(benchmark::State& state) {
    for (auto _ : state) {
        for (int i = 0; i < state.range(0); i++) {
            float* a = GenerateRandomFloats(4, 100);
            float* b = GenerateRandomFloats(4, 100);
            float* c = GenerateRandomFloats(4, 100);
            float* result = SimdInstructions::MultiplyAddScalar(a, b, c);
            benchmark::DoNotOptimize(result);
        }
    }
}

static void BenchmarkMultiplyAddVectorized(benchmark::State& state) {
    for (auto _ : state) {
        for (int i = 0; i < state.range(0); i++) {
            float* a = GenerateRandomFloats(4, 100);
            float* b = GenerateRandomFloats(4, 100);
            float* c = GenerateRandomFloats(4, 100);
            float* result = SimdInstructions::MultiplyAddVectorized(a, b, c);
            benchmark::DoNotOptimize(result);
        }
    }
}


BENCHMARK(BenchmarkMultiplyAddScalar)->RangeMultiplier(10)->Range(1, 10000);
BENCHMARK(BenchmarkMultiplyAddVectorized)->RangeMultiplier(10)->Range(1, 10000);

BENCHMARK_MAIN();
