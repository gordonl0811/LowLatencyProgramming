package QueuePacketProcessorTests;

import PacketProcessor.QueuePacketProcessor.components.PacketProducer;
import io.pkts.packet.Packet;
import org.junit.Test;
import PacketProcessor.utils.PoisonPacket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PacketProducerTest {

  @Test
  public void testProducerSendsPoisonPacket() throws IOException, InterruptedException {

    final String source = "src/test/resources/QueuePacketProcessorTests.PacketProducerTest/input_single.pcap";
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
  public void testProducerAcceptsFileObject() throws IOException, InterruptedException {

    final File source = new File("src/test/resources/QueuePacketProcessorTests.PacketProducerTest/input_single.pcap");
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
  public void testProducerForwardsMultiplePackets() throws IOException, InterruptedException {
    // PCAP containing 100 packets
    final String source = "src/test/resources/QueuePacketProcessorTests.PacketProducerTest/input_multiple.pcap";
    BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
    PacketProducer packetProducer = new PacketProducer(source, producerQueue);

    Thread thread = new Thread(packetProducer);
    thread.start();
    thread.join();

    // Increment for the Poison Packet
    assert producerQueue.size() == 100 + 1;
  }
}
