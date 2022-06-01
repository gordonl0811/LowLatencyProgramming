package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.Pcap;
import java.io.IOException;

public class PacketReader implements PacketEventProducer {

  private final Pcap source;
  private final Disruptor<PacketEvent> producerDisruptor;
  private RingBuffer<PacketEvent> producerRingBuffer;

  public PacketReader(String source,
      Disruptor<PacketEvent> producerDisruptor) throws IOException {
    this.source = Pcap.openStream(source);
    this.producerDisruptor = producerDisruptor;
  }

  @Override
  public void initialize() {
    producerRingBuffer = this.producerDisruptor.start();
  }

  public void start() {

    try {
      // Load the packets into the RingBuffer
      this.source.loop(packet -> {
        producerRingBuffer.publishEvent((event, sequence, buffer) -> event.setValue(packet));
        return true;
      });
      producerDisruptor.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
