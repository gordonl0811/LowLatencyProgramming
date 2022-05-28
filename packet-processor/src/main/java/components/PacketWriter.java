package components;

import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.Packet;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

public class PacketWriter implements Runnable {

  private final BlockingQueue<Packet> packetQueue;
  private final PcapOutputStream output;

  public PacketWriter(BlockingQueue<Packet> packetQueue, String dest) throws FileNotFoundException {
    this.packetQueue = packetQueue;
    this.output = PcapOutputStream.create(
        PcapGlobalHeader.createDefaultHeader(),
        new FileOutputStream(dest)
    );
    ;
  }

  private void writePacket(Packet packet) throws IOException {
    output.write(packet);
  }

  @Override
  public void run() {
    try {
      while (!(Thread.currentThread().isInterrupted())) {
        writePacket(packetQueue.take());
      }
      // Drain and process remaining elements on the queue
      final LinkedList<Packet> remainingPackets = new LinkedList<>();
      packetQueue.drainTo(remainingPackets);
      for (Packet complexObject : remainingPackets) {
        writePacket(complexObject);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
