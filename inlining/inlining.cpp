#include "inlining.h"

static int __attribute__ ((always_inline)) Inlining::CubeInline(int x) {
    return x * x * x
}

static int __attribute__ ((noinline))  Inlining::CubeNoInline(int x) {
    return x * x * x
}