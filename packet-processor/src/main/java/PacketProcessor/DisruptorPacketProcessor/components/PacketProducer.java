package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import PacketProcessor.utils.PoisonPacket;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.Pcap;
import java.io.IOException;

public class PacketProducer {

  private final Pcap source;
  private final Disruptor<PacketEvent> producerDisruptor;
  private final RingBuffer<PacketEvent> producerRingBuffer;

  public PacketProducer(String source,
      Disruptor<PacketEvent> producerDisruptor) throws IOException {
    this.source = Pcap.openStream(source);
    this.producerDisruptor = producerDisruptor;
    this.producerRingBuffer = this.producerDisruptor.start();
  }

  public void producePackets() {

    try {

      // Load the packets into the RingBuffer
      this.source.loop(packet -> {
        long sequenceId = producerRingBuffer.next();
        PacketEvent packetEvent = producerRingBuffer.get(sequenceId);
        packetEvent.setValue(packet);
        producerRingBuffer.publish(sequenceId);
        return true;
      });

      long sequenceId = producerRingBuffer.next();
      PacketEvent packetEvent = producerRingBuffer.get(sequenceId);
      packetEvent.setValue(new PoisonPacket());
      producerRingBuffer.publish(sequenceId);

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
