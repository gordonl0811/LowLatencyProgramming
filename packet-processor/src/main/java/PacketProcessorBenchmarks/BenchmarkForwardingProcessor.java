package PacketProcessorBenchmarks;

import PacketProcessor.DisruptorPacketProcessor.ForwardingDisruptorProcessor;
import PacketProcessor.PacketProcessor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import PacketProcessor.QueuePacketProcessor.ForwardingQueueProcessor;
import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 1, time = 3)
@Fork(value = 1)
public class BenchmarkForwardingProcessor {

  @State(Scope.Benchmark)
  public static class DisruptorImplementationState {

    public PacketProcessor processor;

    @Param({"8", "64", "512", "4096"})
    public int bufferSize;

    @Setup(Level.Invocation)
    public void setup() throws IOException {
      processor = new ForwardingDisruptorProcessor(
          bufferSize,
          "src/main/resources/input_thousand.pcap",
          "src/main/resources/output/forwarded.pcap",
              1000);
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
      processor = new ForwardingQueueProcessor(
              queueSize,
              "src/main/resources/input_thousand.pcap",
              "src/main/resources/output/forwarded.pcap",
              1000
      );
      processor.initialize();
    }
  }

  @Benchmark
  public void benchmarkQueueImplementation(QueueImplementationState state) throws InterruptedException {
    state.processor.start();
  }

}