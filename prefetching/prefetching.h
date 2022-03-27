#ifndef LOW_LATENCY_MICROBENCHMARKS_PREFETCHING_H
#define LOW_LATENCY_MICROBENCHMARKS_PREFETCHING_H

class Prefetching {

public:

    static int BinarySearchNoPrefetch(int target, const int nums[], int size) {

        int left = 0;
        int mid;
        int right = size - 1;

        while (left <= right) {

            mid = (left + right) / 2;

            if (nums[mid] < target) {
                left = mid + 1;
            } else if (nums[mid] > target) {
                right = mid - 1;
            } else {
                return mid;
            }
        }

        return -1;

    }

    static int BinarySearchPrefetch(int target, int nums[], int size) {
        
        int left = 0;
        int mid;
        int right = size - 1;

        while (left <= right) {

            mid = (left + right) / 2;

            __builtin_prefetch (&nums[(mid + 1 + right) / 2], 0, 1);
            __builtin_prefetch (&nums[(left + mid - 1) / 2], 0, 1);

            if (nums[mid] < target) {
                left = mid + 1;
            } else if (nums[mid] > target) {
                right = mid - 1;
            } else {
                return mid;
            }
        }

        return -1;

    }

};


#endif //LOW_LATENCY_MICROBENCHMARKS_PREFETCHING_H
