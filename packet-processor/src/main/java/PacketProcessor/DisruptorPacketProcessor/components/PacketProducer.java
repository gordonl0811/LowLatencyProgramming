package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.Pcap;
import java.io.IOException;

public class PacketProducer {

  private final Pcap source;
  private final Disruptor<PacketEvent> producerDisruptor;

  public PacketProducer(String source,
      Disruptor<PacketEvent> producerDisruptor) throws IOException {
    this.source = Pcap.openStream(source);
    this.producerDisruptor = producerDisruptor;
  }

  public void start() {

    RingBuffer<PacketEvent> producerRingBuffer = this.producerDisruptor.start();
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
