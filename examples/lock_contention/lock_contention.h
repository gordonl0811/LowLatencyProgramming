#ifndef LOCK_CONTENTION_H
#define LOCK_CONTENTION_H

class LockContention {

public:

    static void Increment() {}

    static volatile void IncrementVolatile() {}

    static void IncrementAtomic() {}

    static void IncrementLock() {}
};

#endif //LOCK_CONTENTION_H
