package PacketProcessorBenchmarks.QueueBenchmarks;

import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.FilterProcessor;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, time = 3)
@Measurement(iterations = 1, time = 3)
@Fork(value = 1)
public class BenchmarkFilterProcessor {

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    public PacketProcessor processor;

    @Param({"1", "8", "64", "512"})
    public int queueSize;

    @Setup(Level.Invocation)
    public void setup() throws IOException {
      processor = new FilterProcessor(
          queueSize,
          "src/main/resources/input_thousand.pcap",
          "src/main/resources/tcp_output.pcap",
          "src/main/resources/udp_output.pcap"
      );
      processor.initialize();
    }
  }

  @Benchmark
  public void benchmark(BenchmarkState state) throws InterruptedException {
    state.processor.start();
  }
}
