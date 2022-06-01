package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.components.PacketFilter;
import PacketProcessor.QueuePacketProcessor.components.PacketReader;
import PacketProcessor.QueuePacketProcessor.components.PacketWriter;
import io.pkts.packet.Packet;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FilterProcessor implements PacketProcessor {

  private final Thread producerThread;
  private final Thread filterThread;
  private final Thread tcpThread;
  private final Thread udpThread;

  public FilterProcessor(int queueSize, String source, String tcpDest, String udpDest)
      throws IOException {

    final BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(queueSize);
    final BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(queueSize);
    final BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(queueSize);

    final PacketReader packetReader = new PacketReader(source, producerQueue);
    final PacketFilter packetFilter = new PacketFilter(producerQueue, tcpQueue, udpQueue);
    final PacketWriter tcpWriter = new PacketWriter(tcpQueue, tcpDest);
    final PacketWriter udpWriter = new PacketWriter(udpQueue, udpDest);

    this.producerThread = new Thread(packetReader);
    this.filterThread = new Thread(packetFilter);
    this.tcpThread = new Thread(tcpWriter);
    this.udpThread = new Thread(udpWriter);
  }

  @Override
  public void initialize() {
    filterThread.start();
    tcpThread.start();
    udpThread.start();
  }

  @Override
  public void start() throws InterruptedException {
    producerThread.start();
    producerThread.join();
    filterThread.join();
    tcpThread.join();
    udpThread.join();
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    FilterProcessor processor = new FilterProcessor(
        1000,
        "src/main/resources/input.pcap",
        "src/main/resources/tcp_output.pcap",
        "src/main/resources/udp_output.pcap"
    );

    processor.initialize();
    processor.start();
  }
}
