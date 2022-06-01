package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.PacketReader;
import PacketProcessor.DisruptorPacketProcessor.components.PacketWriter;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.io.IOException;

public class FilterProcessor {

  public static void main(String[] args) throws IOException {

    int bufferSize = 1024;
    Disruptor<PacketEvent> producerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
        DaemonThreadFactory.INSTANCE);

    PacketReader packetReader = new PacketReader("src/main/resources/input.pcap",
        producerDisruptor);
    EventHandler<PacketEvent> packetWriter = new PacketWriter("src/main/resources/output.pcap");

    // Connect the consumer
    producerDisruptor.handleEventsWith(packetWriter);

    // Start producing packets
    packetReader.initialize();
    packetReader.start();

  }
}
