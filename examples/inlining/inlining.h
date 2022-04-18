#ifndef LOW_LATENCY_MICROBENCHMARKS_INLINING_H
#define LOW_LATENCY_MICROBENCHMARKS_INLINING_H


class Inlining {

public:

    static int __attribute__ ((always_inline)) CubeInlined(int x) {
        return x * x * x;
    };

    static int __attribute__ ((noinline)) Cube(int x) {
        return x * x * x;
    }

};


#endif //LOW_LATENCY_MICROBENCHMARKS_INLINING_H
