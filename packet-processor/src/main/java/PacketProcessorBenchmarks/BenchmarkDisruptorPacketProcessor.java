package PacketProcessorBenchmarks;

import PacketProcessor.DisruptorPacketProcessor.FilterProcessor;
import PacketProcessor.PacketProcessor;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;

@Warmup(iterations = 1, time = 3)
@Measurement(iterations = 5, time = 3)
@Fork(value = 1)
public class BenchmarkDisruptorPacketProcessor {

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    public PacketProcessor processor;

    @Param({"1", "8", "64", "512", "4096"})
    public int bufferSize;

    @Setup(Level.Invocation)
    public void setup() throws IOException {
      processor = new FilterProcessor(
          bufferSize,
          "src/main/resources/input_ten_thousand.pcap",
          "src/main/resources/tcp_output.pcap",
          "src/main/resources/udp_output.pcap"
      );
      processor.initialize();
    }
  }

  @Benchmark
  public void benchmark(BenchmarkQueuePacketProcessor.BenchmarkState state) throws InterruptedException {
    state.processor.start();
  }

}
