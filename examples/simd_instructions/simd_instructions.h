#ifndef LOW_LATENCY_MICROBENCHMARKS_SIMD_INSTRUCTIONS_H
#define LOW_LATENCY_MICROBENCHMARKS_SIMD_INSTRUCTIONS_H

#include <vector>
#include <immintrin.h>

class SimdInstructions {

public:

    static void MultiplyAddScalar(const float* a, const float* b, const float* c, float* d) {

        for (int i = 0; i < 8; i++) {
            d[i] = a[i] * b[i] + c[i];
        }

    }

    static __m256 MultiplyAddVectorized(__m256 a, __m256 b, __m256 c) {
        return _mm256_fmadd_ps(a, b, c);
    }
};


#endif //LOW_LATENCY_MICROBENCHMARKS_SIMD_INSTRUCTIONS_H
