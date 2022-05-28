package components;

import io.pkts.Pcap;
import io.pkts.packet.Packet;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class PacketProducer implements Runnable {

  private final Pcap source;
  private final BlockingQueue<Packet> producerQueue;

  public PacketProducer(String source, BlockingQueue<Packet> producerQueue) throws IOException {
    this.source = Pcap.openStream(source);
    this.producerQueue = producerQueue;
  }

  @Override
  public void run() {
    try {
      // Load the packets into the queue
      this.source.loop(packet -> {
        try {
          producerQueue.put(packet);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return true;
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
