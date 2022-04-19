#ifndef LOW_LATENCY_MICROBENCHMARKS_LOOP_UNROLLING_H
#define LOW_LATENCY_MICROBENCHMARKS_LOOP_UNROLLING_H

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

    static vector<int> SumVectorsUnrolledTwo(const vector<int>& x, const vector<int>& y, int size) {

        vector<int> z(size);

        for (auto i = 0; i < size / 2; i += 2) {
            z[i] = x[i] + y[i];
            z[i+1] = x[i+1] + y[i+1];
        }

        return z;

    };

    static vector<int> SumVectorsUnrolledFour(const vector<int>& x, const vector<int>& y, int size) {

        vector<int> z(size);

        for (auto i = 0; i < size / 4; i += 4) {
            z[i] = x[i] + y[i];
            z[i+1] = x[i+1] + y[i+1];
            z[i+2] = x[i+2] + y[i+2];
            z[i+3] = x[i+3] + y[i+3];
        }

        return z;

    };

};


#endif //LOW_LATENCY_MICROBENCHMARKS_LOOP_UNROLLING_H
