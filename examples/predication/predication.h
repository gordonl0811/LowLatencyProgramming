#ifndef PREDICATION_PREDICATION_H
#define PREDICATION_PREDICATION_H

#include <vector>

class Predication {

public:

    static std::vector<int> TrimVector(int max, const std::vector<int>& input, int size) {

        std::vector<int> output(size);
        int outputI = 0;

        for (auto i = 0; i < size; i++) {
            if (input[i] < max) {
                output[outputI++] = input[i];
            }
        }

        return output;
    }

    static std::vector<int> TrimVectorPredicated(int max, const std::vector<int>& input, int size) {

        std::vector<int> output(size);
        int outputI = 0;

        for (auto i = 0; i < size; i++) {
            output[outputI] = input[i];
            outputI += (input[i] < max);
        }

        if (output[outputI] < max) {
            output.pop_back();
        }

        return output;
    }
};

#endif //PREDICATION_PREDICATION_H
