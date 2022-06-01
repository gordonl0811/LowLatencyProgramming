package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.Pcap;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PacketReader implements PacketEventProducer {

  private final Pcap source;
  private final Disruptor<PacketEvent> readerDisruptor;
  private RingBuffer<PacketEvent> readerRingBuffer;
  private final List<Disruptor<PacketEvent>> dependants;

  public PacketReader(String source,
      Disruptor<PacketEvent> readerDisruptor, List<Disruptor<PacketEvent>> dependants)
      throws IOException {
    this.source = Pcap.openStream(source);
    this.readerDisruptor = readerDisruptor;
    this.dependants = dependants;
  }

  @Override
  public void initialize() {
    readerRingBuffer = this.readerDisruptor.start();
  }

  public void start() {

    try {

      // Load the packets into the RingBuffer
      this.source.loop(packet -> {
        readerRingBuffer.publishEvent((event, sequence, buffer) -> event.setValue(packet));
        try {
          TimeUnit.MICROSECONDS.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return true;
      });

      // Wait for this ring buffer to clear
      clearBacklog();

      // Wait for consumers to finish and shut down system
      waitForConsumers();
      readerDisruptor.shutdown();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void clearBacklog() {
    final long cursor = readerRingBuffer.getCursor();
    while (readerDisruptor.getRingBuffer().getMinimumGatingSequence() != cursor);
  }

  private void waitForConsumers() {
    for (Disruptor<PacketEvent> dependant : dependants) {
      RingBuffer<PacketEvent> dependantRingBuffer = dependant.getRingBuffer();
      final long cursor = dependantRingBuffer.getCursor();
      while (dependantRingBuffer.getMinimumGatingSequence() != cursor);
    }
  }

}
