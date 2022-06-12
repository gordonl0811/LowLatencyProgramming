package PacketProcessorBenchmarks;

import PacketProcessor.DisruptorPacketProcessor.MultipleProducerDisruptorProcessor;
import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.MultipleProducerQueueProcessor;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 3, time = 5)
@Fork(value = 1)
public class BenchmarkMultipleProducerProcessor {

    private static final int BUFFER_SIZE = 1024;

    @State(Scope.Benchmark)
    public static class DisruptorImplementationState {

        public PacketProcessor processor;

        @Param({"1", "10", "100", "1000", "10000", "100000"})
        public int numPackets;

        @Setup(Level.Invocation)
        public void setup() throws IOException {
            processor = new MultipleProducerDisruptorProcessor(
                    BUFFER_SIZE,
                    "src/main/resources/inputs/copies/input_1_" + numPackets + ".pcap",
                    "src/main/resources/inputs/copies/input_2_" + numPackets + ".pcap",
                    "src/main/resources/inputs/copies/input_3_" + numPackets + ".pcap",
                    numPackets * 3
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

        @Param({"1", "10", "100", "1000", "10000", "100000"})
        public int numPackets;

        @Setup(Level.Invocation)
        public void setup() throws IOException {
            processor = new MultipleProducerQueueProcessor(
                    BUFFER_SIZE,
                    "src/main/resources/inputs/copies/input_1_" + numPackets + ".pcap",
                    "src/main/resources/inputs/copies/input_2_" + numPackets + ".pcap",
                    "src/main/resources/inputs/copies/input_3_" + numPackets + ".pcap",
                    numPackets * 3
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
