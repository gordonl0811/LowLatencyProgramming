# Overview

SIMD (Single Instruction, Multiple Data) describes computers with the capability of performing a single operation on multiple data points in parallel.

# Motivation




# Description

Vectors are instruction operands represented as a one-dimensional array of data elements, being either integer or floating-point values. Vector registers hold these data elements as a single vector and can vary in size. Intel's AVX-512 instruction set performs vector operations on 512-bit vectors, equivalent to 16 `ints`.

Compilers attempt to auto-vectorize code, for which they are successful for simple cases like the element-wise multiplication of two vectors.

However, there are cases where auto-vectorisation is unsuccessful. In this case, the programmer must vectorize their code manually using intrinsics. Consider the following function, which multiplies the elements of two arrays containing 8 floats together and adds the values of a third array, storing it in a fourth array (`d`):

```c++
static void MultiplyAddScalar(const float* a, const float* b, const float* c, float* d) {

    for (int i = 0; i < 8; i++) {
        d[i] = a[i] * b[i] + c[i];
    }

}
```

SIMD instructions can perform the same calculation by loading the values into vector registers and performing the same calculation on them. In the following example, the arrays of `floats` have been casted to the `__m256` data type, allowing it to be loaded into an AVX register. The `_mm256_fmadd_ps` then performs the same calculation as before (`a * b + c`) and returns the result:

```c++
static __m256 MultiplyAddVectorized(__m256 a, __m256 b, __m256 c) {
    return _mm256_fmadd_ps(a, b, c);
}
```

# Benchmark Results

![SumVectors Benchmark Results](./images/SumVectors.png)

# Use cases