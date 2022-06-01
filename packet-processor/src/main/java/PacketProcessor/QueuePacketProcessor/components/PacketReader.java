package PacketProcessor.QueuePacketProcessor.components;

import io.pkts.Pcap;
import io.pkts.packet.Packet;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import PacketProcessor.utils.PoisonPacket;

public class PacketReader implements Runnable {

  private final Pcap source;
  private final BlockingQueue<Packet> producerQueue;

  public PacketReader(String source, BlockingQueue<Packet> producerQueue) throws IOException {
    this.source = Pcap.openStream(source);
    this.producerQueue = producerQueue;
  }

  public PacketReader(File source, BlockingQueue<Packet> producerQueue) throws IOException {
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
          TimeUnit.MICROSECONDS.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return true;
      });

      producerQueue.put(new PoisonPacket());
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

  }
}
