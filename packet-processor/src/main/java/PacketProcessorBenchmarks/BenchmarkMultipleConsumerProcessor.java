package PacketProcessorBenchmarks;

import PacketProcessor.DisruptorPacketProcessor.MultipleConsumerDisruptorProcessor;
import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.MultipleConsumerQueueProcessor;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 5, time = 3)
@Fork(value = 1)
public class BenchmarkMultipleConsumerProcessor {

    @State(Scope.Benchmark)
    public static class DisruptorImplementationState {

        public PacketProcessor processor;

        @Param({"8", "64", "512", "4096"})
        public int bufferSize;

        @Setup(Level.Invocation)
        public void setup() throws IOException {
            processor = new MultipleConsumerDisruptorProcessor(
                    1024,
                    "src/main/resources/example_one.pcap",
                    1000
            );
            processor.initialize();
        }

        @TearDown(Level.Invocation)
        public void teardown() {
            processor.shutdown();
        }
    }

    @Benchmark
    public void benchmarkDisruptorImplementation(DisruptorImplementationState state) throws InterruptedException {
        state.processor.start();
    }

    @State(Scope.Benchmark)
    public static class QueueImplementationState {

        public PacketProcessor processor;

        @Param({"8", "64", "512", "4096"})
        public int queueSize;

        @Setup(Level.Invocation)
        public void setup() throws IOException {
            processor = new MultipleConsumerQueueProcessor(
                    1024,
                    "src/main/resources/example_one.pcap",
                    1000
            );
            processor.initialize();
        }

        @TearDown(Level.Invocation)
        public void teardown() {
            processor.shutdown();
        }
    }

    @Benchmark
    public void benchmarkQueueImplementation(QueueImplementationState state) throws InterruptedException {
        state.processor.start();
    }

}
