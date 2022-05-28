package components;

import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

public class PacketFilter implements Runnable {

  private final BlockingQueue<Packet> producerQueue;
  private final BlockingQueue<Packet> tcpQueue;
  private final BlockingQueue<Packet> udpQueue;

  public PacketFilter(BlockingQueue<Packet> producerQueue,
      BlockingQueue<Packet> tcpQueue,
      BlockingQueue<Packet> udpQueue) {
    this.producerQueue = producerQueue;
    this.tcpQueue = tcpQueue;
    this.udpQueue = udpQueue;
  }

  private void filterPacket(Packet packet) throws IOException, InterruptedException {
    if (packet.hasProtocol(Protocol.TCP)) {
      tcpQueue.put(packet);
    } else if (packet.hasProtocol(Protocol.UDP)) {
      udpQueue.put(packet);
    }
  }

  @Override
  public void run() {
    try {
      while (!(Thread.currentThread().isInterrupted())) {
        filterPacket(producerQueue.take());
      }
      // Drain and process remaining elements on the queue
      final LinkedList<Packet> remainingPackets = new LinkedList<>();
      producerQueue.drainTo(remainingPackets);
      for (Packet complexObject : remainingPackets) {
        filterPacket(complexObject);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
