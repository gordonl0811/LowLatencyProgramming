#ifndef LOCK_CONTENTION_H
#define LOCK_CONTENTION_H

#include <atomic>
#include <mutex>

class LockContention {

public:

    static void Increment(int &counter, int iterations) {
        for (int i = 0; i < iterations; i++) {
            counter++;
        }
    }

    static void IncrementAtomic(std::atomic<int> &counter, int iterations) {
        for (int i = 0; i < iterations; i++) {
            counter.fetch_add(1);
        }
    }

    static void IncrementMutex(int &counter, int iterations, std::mutex &mtx) {
        for (int i = 0; i < iterations; i++) {
            mtx.lock();
            counter++;
            mtx.unlock();
        }
    }
};

#endif //LOCK_CONTENTION_H
