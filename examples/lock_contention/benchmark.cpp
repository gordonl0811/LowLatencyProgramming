#include <benchmark/benchmark.h>
#include "lock_contention.h"

#include <atomic>
#include <mutex>
#include <vector>
#include <thread>
#include <functional>

#include <iostream>

#define MAX_ITERATIONS 16777216
#define MAX_THREADS 8

static void BenchmarkIncrement(benchmark::State& state) {
    int counter;
    int increment = (int) state.range(0);
    for (auto _ : state) {
        counter = 0;
        LockContention::Increment(counter, increment);
        benchmark::DoNotOptimize(counter);
    }
}

static void BenchmarkIncrementAtomic(benchmark::State& state) {

    std::atomic<int> counter;

    int increment = (int) state.range(0);
    int threadCount = (int) state.range(1);

    std::vector<std::thread> threads(threadCount);
    int threadIncrement = increment / threadCount;

    for (auto _ : state) {

        counter.store(0);

        for (int i = 0; i < threadCount; i++) {
            threads[i] = (std::thread(LockContention::IncrementAtomic, std::ref(counter), threadIncrement));
        }

        for (auto& thread : threads) {
            thread.join();
        }

        benchmark::DoNotOptimize(counter);

    }
}

static void BenchmarkIncrementMutex(benchmark::State& state) {

    int counter;

    int increment = (int) state.range(0);
    int threadCount = (int) state.range(1);

    std::vector<std::thread> threads(threadCount);
    int threadIncrement = increment / threadCount;

    std::mutex mtx;

    for (auto _ : state) {

        counter = 0;

        for (int i = 0; i < threadCount; i++) {
            threads[i] = (std::thread(LockContention::IncrementMutex, std::ref(counter), threadIncrement, std::ref(mtx)));
        }

        for (auto& thread : threads) {
            thread.join();
        }

        benchmark::DoNotOptimize(counter);

        std::cout << counter;
    }
}


BENCHMARK(BenchmarkIncrement)->RangeMultiplier(8)->Range(8, MAX_ITERATIONS);
BENCHMARK(BenchmarkIncrementAtomic)->ArgsProduct({
    benchmark::CreateRange(8, MAX_ITERATIONS, 8),
    benchmark::CreateRange(1, MAX_THREADS, 2)
});
BENCHMARK(BenchmarkIncrementMutex)->ArgsProduct({
    benchmark::CreateRange(8, MAX_ITERATIONS, 8),
    benchmark::CreateRange(1, MAX_THREADS, 2)
});

BENCHMARK_MAIN();
