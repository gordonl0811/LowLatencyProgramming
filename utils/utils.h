#ifndef LOW_LATENCY_MICROBENCHMARKS_UTILS_H
#define LOW_LATENCY_MICROBENCHMARKS_UTILS_H

#include <vector>
#include <random>
#include <algorithm>

static std::vector<int> GenerateRandomInts(int size, int range = 100) {

    std::random_device rd;
    std::mt19937 mt(rd());
    std::uniform_real_distribution<double> dist(1, range);

    auto generator = [&dist, &mt](){ return dist(mt); };

    std::vector<int> ints(size);
    std::generate(std::begin(ints), std::end(ints), generator);

    return ints;
}


static std::vector<float> GenerateRandomFloats(int size, int range) {

    std::random_device rd;
    std::mt19937 mt(rd());
    std::uniform_real_distribution<float> dist(1, range);

    auto generator = [&dist, &mt](){ return dist(mt); };

    std::vector<float> floats(size);
    std::generate(std::begin(floats), std::end(floats), generator);

    return floats;
}

static int* GenerateSequencedArray(int size) {

    int* nums = new int[size];

    for (int i = 0; i < size; i++) {
        nums[i] = i;
    }

    return nums;
}

#endif //LOW_LATENCY_MICROBENCHMARKS_UTILS_H
