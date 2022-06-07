package PacketProcessorBenchmarks;

import PacketProcessor.DisruptorPacketProcessor.PipelineDisruptorProcessor;
import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.PipelineQueueProcessor;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
public class BenchmarkPipelineProcessor {

    @State(Scope.Benchmark)
    public static class DisruptorImplementationState {

        public PacketProcessor processor;

        @Param({"1", "4", "16", "64", "256", "1024", "4096", "16384"})
        public int size;

        @Param({"1", "10", "100", "1000", "10000", "1000000"})
        public int numPackets;

        private final String source = "src/main/resources/inputs/input_" + numPackets + ".pcap";

        @Setup(Level.Invocation)
        public void setup() throws IOException {
            processor = new PipelineDisruptorProcessor(size, source, 100, 200, numPackets);
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

        @Param({"1", "4", "16", "64", "256", "1024", "4096", "16384"})
        public int size;

        @Param({"1", "10", "100", "1000", "10000", "1000000"})
        public int numPackets;

        private final String source = "src/main/resources/inputs/input_" + numPackets + ".pcap";

        @Setup(Level.Invocation)
        public void setup() throws IOException {
            processor = new PipelineQueueProcessor(size, source, 100, 200, numPackets);
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