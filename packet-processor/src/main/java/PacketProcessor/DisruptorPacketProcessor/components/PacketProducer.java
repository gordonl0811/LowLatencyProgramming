package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.Pcap;
import java.io.IOException;

public class PacketProducer {

  private final Pcap source;
  private final Disruptor<PacketEvent> producerDisruptor;
  private RingBuffer<PacketEvent> producerRingBuffer;

  public PacketProducer(String source,
      Disruptor<PacketEvent> producerDisruptor) throws IOException {
    this.source = Pcap.openStream(source);
    this.producerDisruptor = producerDisruptor;
  }

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
