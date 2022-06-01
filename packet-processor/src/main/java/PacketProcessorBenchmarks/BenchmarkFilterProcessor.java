package PacketProcessorBenchmarks;

import PacketProcessor.FilterProcessor;
import PacketProcessor.PacketProcessor;
import java.io.IOException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

public class BenchmarkFilterProcessor {

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    public PacketProcessor processor;

    @Param({"1", "10", "100", "1000", "10000"})
    public int queueSize;

    @Setup
    public void setup() throws IOException {
      processor = new FilterProcessor(
          queueSize,
          "src/main/resources/input.pcap",
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
