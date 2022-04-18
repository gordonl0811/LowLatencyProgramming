#ifndef LOW_LATENCY_MICROBENCHMARKS_BRANCH_PREDICTION_H
#define LOW_LATENCY_MICROBENCHMARKS_BRANCH_PREDICTION_H

class BranchPrediction {

public:

    static int IsSeven(int num) {
        if (num == 7) {
            return num;
        } else {
            return -1;
        }

    }

    static int IsSevenPredicted(int num) {

        if (__builtin_expect(num == 7, 0)) {
            return num;
        } else {
            return -1;
        }
    }

};
#endif //LOW_LATENCY_MICROBENCHMARKS_BRANCH_PREDICTION_H
