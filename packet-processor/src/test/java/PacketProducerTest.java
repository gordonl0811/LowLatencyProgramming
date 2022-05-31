import components.PacketProducer;
import io.pkts.packet.Packet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.junit.jupiter.api.Test;
import utils.PoisonPacket;

public class PacketProducerTest {

  @Test
  void testProducerSendsPoisonPacket() throws IOException, InterruptedException {

    final String source = "src/test/resources/PacketProducerTest/input_single.pcap";
    BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
    PacketProducer packetProducer = new PacketProducer(source, producerQueue);

    Thread thread = new Thread(packetProducer);
    thread.start();
    thread.join();

    ArrayList<Packet> remainingPackets = new ArrayList<>();
    producerQueue.drainTo(remainingPackets);

    // Check for the single packet and the poison packet
    assert remainingPackets.size() == 2;
    assert remainingPackets.get(1) instanceof PoisonPacket;
  }

  @Test
  void testProducerForwardsMultiplePackets() throws IOException, InterruptedException {
    // PCAP containing 100 packets
    final String source = "src/test/resources/PacketProducerTest/input_multiple.pcap";
    BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
    PacketProducer packetProducer = new PacketProducer(source, producerQueue);

    Thread thread = new Thread(packetProducer);
    thread.start();
    thread.join();

    // Increment for the Poison Packet
    assert producerQueue.size() == 100 + 1;
  }
}
