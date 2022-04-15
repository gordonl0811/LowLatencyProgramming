#include <benchmark/benchmark.h>
#include "simd_instructions.h"

#include "../utils/utils.h"

static void BenchmarkMultiplyAddScalar(benchmark::State& state) {

    std::vector<float> floats_a = GenerateRandomFloats(8, 100);
    std::vector<float> floats_b = GenerateRandomFloats(8, 100);
    std::vector<float> floats_c = GenerateRandomFloats(8, 100);

    float a[8];
    float b[8];
    float c[8];
    float d[8];

    std::copy(floats_a.begin(), floats_a.end(), a);
    std::copy(floats_b.begin(), floats_b.end(), b);
    std::copy(floats_c.begin(), floats_c.end(), c);

    for (auto _ : state) {
        for (int i = 0; i < state.range(0); i++) {
            SimdInstructions::MultiplyAddScalar(a, b, c, d);
            benchmark::DoNotOptimize(d);
        }
    }

}

static void BenchmarkMultiplyAddVectorized(benchmark::State& state) {

    std::vector<float> floats_a = GenerateRandomFloats(8, 100);
    std::vector<float> floats_b = GenerateRandomFloats(8, 100);
    std::vector<float> floats_c = GenerateRandomFloats(8, 100);

    float a[8];
    float b[8];
    float c[8];

    std::copy(floats_a.begin(), floats_a.end(), a);
    std::copy(floats_b.begin(), floats_b.end(), b);
    std::copy(floats_c.begin(), floats_c.end(), c);

    __m256 a_simd = _mm256_load_ps(a);
    __m256 b_simd = _mm256_load_ps(b);
    __m256 c_simd = _mm256_load_ps(c);

    for (auto _ : state) {
        for (int i = 0; i < state.range(0); i++) {
            benchmark::DoNotOptimize(SimdInstructions::MultiplyAddVectorized(a_simd, b_simd, c_simd));
        }
    }

}

BENCHMARK(BenchmarkMultiplyAddScalar)->RangeMultiplier(10)->Range(1, 10000000);;
BENCHMARK(BenchmarkMultiplyAddVectorized)->RangeMultiplier(10)->Range(1, 10000000);;

BENCHMARK_MAIN();
