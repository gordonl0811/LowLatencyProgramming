import components.PacketFilter;
import components.PacketProducer;
import components.PacketWriter;
import io.pkts.packet.Packet;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PacketProcessor {

  public static void main(String[] args) throws IOException {

    BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(100);
    BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(100);
    BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(100);

    final String source = "src/main/resources/input.pcap";
    final String tcpDest = "src/main/resources/tcp_output.pcap";
    final String udpDest = "src/main/resources/udp_output.pcap";

    PacketProducer packetProducer = new PacketProducer(source, producerQueue);
    PacketFilter packetFilter = new PacketFilter(producerQueue, tcpQueue, udpQueue);
    PacketWriter tcpWriter = new PacketWriter(tcpQueue, tcpDest);
    PacketWriter udpWriter = new PacketWriter(udpQueue, udpDest);

    new Thread(packetProducer).start();
    new Thread(packetFilter).start();
    new Thread(tcpWriter).start();
    new Thread(udpWriter).start();
  }

}
