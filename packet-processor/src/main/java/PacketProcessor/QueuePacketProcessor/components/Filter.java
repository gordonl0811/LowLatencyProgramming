package PacketProcessor.QueuePacketProcessor.components;

import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;
import PacketProcessor.utils.PoisonPacket;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Filter implements Runnable {

  private final BlockingQueue<Packet> producerQueue;
  private final BlockingQueue<Packet> tcpQueue;
  private final BlockingQueue<Packet> udpQueue;

  public Filter(BlockingQueue<Packet> producerQueue,
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
      while (true) {
        Packet packet = producerQueue.take();
        if (packet instanceof PoisonPacket) {
          tcpQueue.put(packet);
          udpQueue.put(packet);
          return;
        }
        filterPacket(packet);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
