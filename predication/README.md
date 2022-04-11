# Overview



# Motivation

Branches and jumps are sources of pipeline hazards that introduce latencies during runtime. The cost of a branch misprediction can be high on architectures that are deeply pipelined.

# Description

Function inlining is a good example of "partial evaluation", which turns the jumps from functions into branches, mitigating the overhead of calling the function. We can take this a step further and strive for "branch-free" code, removing the penalty of control hazards altogether. 

Consider the following function, which returns a copy of a `vector<int>` excluding values above a defined threshold:

```c++
static std::vector<int> Trim(int max, const std::vector<int>& input, int size) {

    std::vector<int> output(size);
    int outputI = 0;

    for (auto i = 0; i < size; i++) {
        if (input[i] < max) {
            output[outputI++] = input[i];
        }
    }

    return output;
}
```

A branch is performed on each iteration of the loop, checking if the current value exceeds the threshold. This means that each iteration has the risk of a branch misprediction, which could result in a total latency penalty of a considerable size.

Consider the improved implementation of the `Trim` function below:

```c++
static std::vector<int> TrimPredicated(int max, const std::vector<int>& input, int size) {

    std::vector<int> output(size);
    int outputI = 0;

    for (auto i = 0; i < size; i++) {
        output[outputI] = input[i];
        outputI += (input[i] < max);
    }

    return output;
}
```

This implementation exploits the evaluation of `input[i] < max` into a boolean, where `true` and `false` is equivalent to `1` and `0` respectively. This value is then added to the `outputI` variable, (used to indicate the position in the array) which eliminates the branch altogether - this technique is known as "predication".

Defined more formally, predication has non-branch instructions associated with a predicate, and a boolean value controls whether the state of the program is changed (with the state being modified if the `true` is evaluated).

# Benchmark Results

# Use cases

Writing branch-free code is usually non-trivial, but this technique is very self-explanatory - the opportunities for improving performance comes from manipulating `if-else` and similar statements that are compiled into branches.

Vector processors and GPUs heavily use predication because of the absence of branches.