package PacketProcessorBenchmarks;

import PacketProcessor.DisruptorPacketProcessor.ForwardingDisruptorProcessor;
import PacketProcessor.PacketProcessor;
import java.io.IOException;

import PacketProcessor.QueuePacketProcessor.ForwardingQueueProcessor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, time = 3)
@Measurement(iterations = 1, time = 3)
@Fork(value = 1)
public class BenchmarkForwardingProcessor {

  @State(Scope.Benchmark)
  public static class DisruptorImplementationState {

    public PacketProcessor processor;

    @Param({"1", "8", "64", "512"})
    public int bufferSize;

    @Setup(Level.Invocation)
    public void setup() throws IOException {
      processor = new ForwardingDisruptorProcessor(
          bufferSize,
          "src/main/resources/input_thousand.pcap",
          "src/main/resources/output/forwarded.pcap"
      );
      processor.initialize();
    }
  }

  @Benchmark
  public void benchmarkDisruptorImplementation(DisruptorImplementationState state) throws InterruptedException {
    state.processor.start();
  }

  @State(Scope.Benchmark)
  public static class QueueImplementationState {

    public PacketProcessor processor;

    @Param({"1", "8", "64", "512"})
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