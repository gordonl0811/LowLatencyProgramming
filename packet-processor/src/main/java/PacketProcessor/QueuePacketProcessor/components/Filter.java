package PacketProcessor.QueuePacketProcessor.components;

import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Filter extends ProcessorComponent {

  private final BlockingQueue<Packet> tcpQueue;
  private final BlockingQueue<Packet> udpQueue;

  public Filter(BlockingQueue<Packet> inputQueue,
                BlockingQueue<Packet> tcpQueue,
                BlockingQueue<Packet> udpQueue) {
    super(inputQueue);
    this.tcpQueue = tcpQueue;
    this.udpQueue = udpQueue;
  }

  @Override
  public void process(Packet packet) throws IOException, InterruptedException {
    if (packet.hasProtocol(Protocol.TCP)) {
      tcpQueue.put(packet);
    } else if (packet.hasProtocol(Protocol.UDP)) {
      udpQueue.put(packet);
    }
  }

}
