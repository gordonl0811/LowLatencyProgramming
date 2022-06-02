package PacketProcessorBenchmarks.QueueBenchmarks;

import PacketProcessor.DisruptorPacketProcessor.ForwardingProcessor;
import PacketProcessor.PacketProcessor;
import java.io.IOException;
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
  public static class BenchmarkState {

    public PacketProcessor processor;

    @Param({"1", "8", "64", "512"})
    public int queueSize;

    @Setup(Level.Invocation)
    public void setup() throws IOException {
      processor = new ForwardingProcessor(
          queueSize,
          "src/main/resources/input_thousand.pcap",
          "src/main/resources/output/forwarded.pcap"
      );
      processor.initialize();
    }
  }

  @Benchmark
  public void benchmark(BenchmarkState state) throws InterruptedException {
    state.processor.start();
  }
}