#ifndef LOW_LATENCY_MICROBENCHMARKS_UTILS_H
#define LOW_LATENCY_MICROBENCHMARKS_UTILS_H

#include <vector>
#include <random>

static float* GenerateRandomFloats(int size, int range) {

    std::random_device rd;
    std::mt19937 mt(rd());
    std::uniform_real_distribution<float> dist(1, range);

    float* floats = new float[size];

    for (int i = 0; i < size; i++) {
        floats[i] = dist(mt);
    }

    return floats;
}


static std::vector<int> GenerateRandomInts(int size, int range) {

    std::random_device rd;
    std::mt19937 mt(rd());
    std::uniform_real_distribution<double> dist(1, range);

    auto generator = [&dist, &mt](){ return dist(mt); };

    std::vector<int> ints(size);
    std::generate(std::begin(ints), std::end(ints), generator);

    return ints;
}

#endif //LOW_LATENCY_MICROBENCHMARKS_UTILS_H
