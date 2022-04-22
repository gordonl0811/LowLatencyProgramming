#ifndef LOW_LATENCY_MICROBENCHMARKS_BRANCH_PREDICTION_H
#define LOW_LATENCY_MICROBENCHMARKS_BRANCH_PREDICTION_H

#include <vector>
#include <random>

class BranchPrediction {

public:

    static std::vector<int> GenerateSomeEvens(float evenPercentage, int size, int range) {

        std::random_device rd;
        std::mt19937 mt(rd());
        std::uniform_real_distribution<double> distIntegers(1, range / 2);
        std::uniform_real_distribution<float> distProbability(0, 1);

        std::vector<int> someEvens = std::vector<int>(size);

        for (int i = 0; i < size; i++) {

            // Generate random n
            int n = (int) distIntegers(mt);

            // Return an even or odd number depending on the generated value
            if (distProbability(mt) * 100 < evenPercentage) {
                someEvens[i] = 2 * n;
            } else {
                someEvens[i] = 2 * n - 1;
            }

        }

        return someEvens;

    }

    static int CountEvens(std::vector<int> &nums) {

        int count = 0;

        for (int i = 0; i < nums.size(); i++) {
            if (nums[i] % 2 == 0) {
                count++;
            }
        }

        return count;
    }

    static int CountEvensLikely(std::vector<int> &nums) {

        int count = 0;

        for (int i = 0; i < nums.size(); i++) {
            if (__builtin_expect((nums[i] % 2 == 0), 0)) {
                count++;
            }
        }

        return count;
    }

};
#endif //LOW_LATENCY_MICROBENCHMARKS_BRANCH_PREDICTION_H
