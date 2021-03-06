cmake_minimum_required(VERSION 3.16)
project(low_latency_microbenchmarks)

set(CMAKE_CXX_STANDARD 14)

# Add -O0 to remove optimizations
IF(CMAKE_COMPILER_IS_GNUCXX)
    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -O0")
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O0")
ENDIF(CMAKE_COMPILER_IS_GNUCXX)

############################### External Projects ###############################

include(ExternalProject)

ExternalProject_Add(googlebenchmark
        URL "https://github.com/google/benchmark/archive/refs/tags/v1.6.1.tar.gz"
        DOWNLOAD_DIR $ENV{HOME}/.cmake-downloads
        CMAKE_ARGS -DCMAKE_INSTALL_PREFIX=${CMAKE_CURRENT_BINARY_DIR}/deps -DBENCHMARK_DOWNLOAD_DEPENDENCIES=ON -DBENCHMARK_ENABLE_GTEST_TESTS=OFF -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} -DCMAKE_CXX_COMPILER=${CMAKE_CXX_COMPILER}
        BUILD_BYPRODUCTS ${CMAKE_CURRENT_BINARY_DIR}/deps/lib/${CMAKE_SHARED_LIBRARY_PREFIX}benchmark.a ${CMAKE_CURRENT_BINARY_DIR}/deps/lib/${CMAKE_SHARED_LIBRARY_PREFIX}benchmark_main.a
        )

#################################### Targets ####################################

set(PrefetchingFiles benchmark.cpp prefetching.h)

add_executable(PrefetchingBenchmarks ${PrefetchingFiles})

set(THREADS_PREFER_PTHREAD_FLAG ON)
find_package(Threads REQUIRED)
target_link_libraries(PrefetchingBenchmarks Threads::Threads)

add_dependencies(PrefetchingBenchmarks googlebenchmark)
target_include_directories(PrefetchingBenchmarks SYSTEM PUBLIC ${CMAKE_CURRENT_BINARY_DIR}/deps/include)
target_link_libraries(PrefetchingBenchmarks ${CMAKE_CURRENT_BINARY_DIR}/deps/lib/${CMAKE_SHARED_LIBRARY_PREFIX}benchmark.a)
target_link_libraries(PrefetchingBenchmarks ${CMAKE_CURRENT_BINARY_DIR}/deps/lib/${CMAKE_SHARED_LIBRARY_PREFIX}benchmark_main.a)
