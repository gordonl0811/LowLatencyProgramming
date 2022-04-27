#include <benchmark/benchmark.h>
#include "resource_contention.h"

#include <atomic>
#include <mutex>
#include <vector>
#include <thread>
#include <functional>

#define INCREMENT 4032000
#define MAX_THREADS 8

static void BenchmarkIncrement(benchmark::State& state) {
    int counter;
    int increment = (int) state.range(0);
    for (auto _ : state) {
        counter = 0;
        ResourceContention::Increment(counter, increment);
        benchmark::DoNotOptimize(counter);
    }
}

static void BenchmarkIncrementAtomic(benchmark::State& state) {

    std::atomic<int> counter;
    int threadCount = (int) state.range(0);

    std::vector<std::thread> threads(threadCount);
    int threadIncrement = INCREMENT / threadCount;

    for (auto _ : state) {

        counter.store(0);

        for (int i = 0; i < threadCount; i++) {
            threads[i] = (std::thread(ResourceContention::IncrementAtomic, std::ref(counter), threadIncrement));
        }

        for (auto& thread : threads) {
            thread.join();
        }

        benchmark::DoNotOptimize(counter);

    }
}

static void BenchmarkIncrementMutex(benchmark::State& state) {

    int counter;
    int threadCount = (int) state.range(0);

    std::vector<std::thread> threads(threadCount);
    int threadIncrement = INCREMENT / threadCount;

    std::mutex mtx;

    for (auto _ : state) {

        counter = 0;

        for (int i = 0; i < threadCount; i++) {
            threads[i] = (std::thread(ResourceContention::IncrementMutex, std::ref(counter), threadIncrement, std::ref(mtx)));
        }

        for (auto& thread : threads) {
            thread.join();
        }

        benchmark::DoNotOptimize(counter);
    }
}

BENCHMARK(BenchmarkIncrement)->Arg(INCREMENT);
BENCHMARK(BenchmarkIncrementAtomic)->DenseRange(1, MAX_THREADS);
BENCHMARK(BenchmarkIncrementMutex)->DenseRange(1, MAX_THREADS);

BENCHMARK_MAIN();
