package PacketProcessorBenchmarks;

import PacketProcessor.DisruptorPacketProcessor.FilterRewriteJoinDisruptorProcessor;
import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.FilterRewriteJoinQueueProcessor;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 3, time = 5)
@Fork(value = 1)
public class BenchmarkFilterRewriteJoinProcessor {

    private final static int BUFFER_SIZE = 1024;

    @State(Scope.Benchmark)
    public static class DisruptorImplementationState {

        public PacketProcessor processor;

        @Param({"100", "1000", "10000", "100000", "1000000"})
        public int numPackets;

        @Setup(Level.Invocation)
        public void setup() throws IOException {

            String source = "src/main/resources/inputs/input_" + numPackets + ".pcap";

            processor = new FilterRewriteJoinDisruptorProcessor(
                    BUFFER_SIZE,
                    source,
                    12,
                    34,
                    56,
                    78,
                    numPackets
            );
            processor.initialize();
        }

        @TearDown(Level.Invocation)
        public void teardown() {
            processor.shutdown();
        }
    }

//    @Benchmark
//    public void benchmarkDisruptorImplementation(DisruptorImplementationState state) throws InterruptedException {
//        state.processor.start();
//    }

    @State(Scope.Benchmark)
    public static class QueueImplementationState {

        public PacketProcessor processor;

        @Param({"100", "1000", "10000", "100000", "1000000"})
        public int numPackets;

        @Setup(Level.Invocation)
        public void setup() throws IOException {

            String source = "src/main/resources/inputs/input_" + numPackets + ".pcap";

            processor = new FilterRewriteJoinQueueProcessor(
                    BUFFER_SIZE,
                    source,
                    12,
                    34,
                    56,
                    78,
                    numPackets
            );
            processor.initialize();
        }

        @TearDown(Level.Invocation)
        public void teardown() {
            processor.shutdown();
        }
    }

//    @Benchmark
//    public void benchmarkQueueImplementation(QueueImplementationState state) throws InterruptedException {
//        state.processor.start();
//    }

}
