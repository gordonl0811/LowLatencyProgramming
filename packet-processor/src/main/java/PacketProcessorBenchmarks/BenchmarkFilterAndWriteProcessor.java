package PacketProcessorBenchmarks;

import PacketProcessor.DisruptorPacketProcessor.FilterAndWriteDisruptorProcessor;
import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.FilterAndWriteQueueProcessor;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 1, time = 3)
@Fork(value = 1)
public class BenchmarkFilterAndWriteProcessor {

    @State(Scope.Benchmark)
    public static class DisruptorImplementationState {

        public PacketProcessor processor;

        @Param({"1", "8", "64", "512"})
        public int bufferSize;

        @Setup(Level.Invocation)
        public void setup() throws IOException {
            processor = new FilterAndWriteDisruptorProcessor(
                    bufferSize,
                    "src/main/resources/input_thousand.pcap",
                    "src/main/resources/output/tcp_output.pcap",
                    "src/main/resources/output/udp_output.pcap",
                    505, 495);
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
            processor = new FilterAndWriteQueueProcessor(
                    queueSize,
                    "src/main/resources/input_thousand.pcap",
                    "src/main/resources/output/tcp_output.pcap",
                    "src/main/resources/output/udp_output.pcap",
                    505, 495);
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
