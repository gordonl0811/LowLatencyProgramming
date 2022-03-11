#ifndef LOW_LATENCY_MICROBENCHMARKS_PREFETCHING_H
#define LOW_LATENCY_MICROBENCHMARKS_PREFETCHING_H

using namespace std;

class Prefetching {

public:

    static int BinarySearch(int target, int nums[], int size, bool prefetching) {
        
        int left = 0;
        int mid;
        int right = size - 1;

        while (left <= right) {
            mid = (left + right) / 2;

            if (prefetching) {
                __builtin_prefetch (&nums[(mid + 1 + right) / 2], 0, 1);
                __builtin_prefetch (&nums[(left + mid - 1) / 2], 0, 1);
            }

            int pivot = nums[mid];

            if (pivot < target) {
                left = mid + 1;
            } else if (pivot > target) {
                right = mid - 1;
            } else {
                return mid;
            }
        }

        return -1;

    }

};


#endif //LOW_LATENCY_MICROBENCHMARKS_PREFETCHING_H
