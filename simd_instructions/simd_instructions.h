#ifndef LOW_LATENCY_MICROBENCHMARKS_SIMD_INSTRUCTIONS_H
#define LOW_LATENCY_MICROBENCHMARKS_SIMD_INSTRUCTIONS_H

#include <vector>
#include <immintrin.h>

class SimdInstructions {

public:

    union v8f {
        float floats[8];
        __m256 simdVector;
    };

    static float* MultiplyAddScalar(const float* a, const float* b, const float* c) {

        auto* d = new float[8];

        for (int i = 0; i < 8; i++) {
            d[i] = a[i] * b[i];
            d[i] = d[i] + c[i];
        }

        return d;

    }

    static float* MultiplyAddVectorized(const float* a, const float* b, const float* c) {

        auto* d = new v8f;

        __m256 a_simd = _mm256_load_ps(a);
        __m256 b_simd = _mm256_load_ps(b);
        __m256 c_simd = _mm256_load_ps(c);

        d->simdVector = _mm256_fmadd_ps(a_simd, b_simd, c_simd);

        return d->floats;
    }
};


#endif //LOW_LATENCY_MICROBENCHMARKS_SIMD_INSTRUCTIONS_H
