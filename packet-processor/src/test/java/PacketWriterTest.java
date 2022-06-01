import components.PacketProducer;
import components.PacketWriter;
import io.pkts.packet.Packet;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PacketWriterTest {
  @Test
  public void testWriterThreadTerminatesWithPoisonPacket()
      throws IOException, InterruptedException, ExecutionException, TimeoutException {

    final String source = "src/test/resources/PacketProducerTest/input_single.pcap";
    final String dest = "TODO";
    BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);

    PacketProducer packetProducer = new PacketProducer(source, producerQueue);
    PacketWriter packetWriter = new PacketWriter(producerQueue, dest);

    new Thread(packetProducer).start();

    // Check that the thread has completed, i.e. terminated
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<?> future = executor.submit(packetWriter);
    future.get(5, TimeUnit.SECONDS);
  }

  @Test
  public void testWriterWritesMultiplePacketsToPcap() throws IOException {
    final String source = "src/test/resources/PacketProducerTest/input_single.pcap";
    final String dest = "TODO";
    BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);

    PacketProducer packetProducer = new PacketProducer(source, producerQueue);
    PacketWriter packetWriter = new PacketWriter(producerQueue, dest);

    new Thread(packetProducer).start();

  }
}
