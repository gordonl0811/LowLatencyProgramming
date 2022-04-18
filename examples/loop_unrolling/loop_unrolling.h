#ifndef LOW_LATENCY_MICROBENCHMARKS_LOOP_UNROLLING_H
#define LOW_LATENCY_MICROBENCHMARKS_LOOP_UNROLLING_H

#define UNROLL_FACTOR 2

#include <vector>

using namespace std;

class LoopUnrolling {

public:

    static vector<int> SumVectors(const vector<int>& x, const vector<int>& y, int size) {

        vector<int> z(size);

        for (auto i = 0; i < size; i++) {
            z[i] = x[i] + y[i];
        }

        return z;

    }

    static vector<int> SumVectorsUnrolled(const vector<int>& x, const vector<int>& y, int size) {

        vector<int> z(size);

        for (auto i = 0; i < size / UNROLL_FACTOR; i += UNROLL_FACTOR) {
            // UNROLL_FACTOR lines of loop unrolling
            z[i] = x[i] + y[i];
            z[i+1] = x[i+1] + y[i+1];
        }

        return z;

    };

};


#endif //LOW_LATENCY_MICROBENCHMARKS_LOOP_UNROLLING_H
