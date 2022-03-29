#ifndef PREDICATION_PREDICATION_H
#define PREDICATION_PREDICATION_H

#include <vector>

class Predication {

public:
    static void Heapify(std::vector<int> input, int i) {
        int max = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if(left < input.size() && input[left] > input[max])
            max = left;
        if(right < input.size() && input[right] > input[max])
            max = right;
        if(max != i) {
            std::swap(input[i], input[max]);
            Heapify(input, max);
        }
    }

    static void HeapifyWithPredication(std::vector<int> input, int i) {
        int max = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        bool resultLeft = left < input.size() && input[left] > input[max];
        max = (left * resultLeft) + (max * (1 - resultLeft));

        bool resultRight = right < input.size() && input[right] > input[max];
        max = (right * resultRight) + (max * (1 - resultRight));

        if(max != i) {
            std::swap(input[i], input[max]);
            HeapifyWithPredication(input, max);
        }
    }
};

#endif //PREDICATION_PREDICATION_H
