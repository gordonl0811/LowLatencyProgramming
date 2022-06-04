package PacketProcessor.QueuePacketProcessor.components;

import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.Packet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Writer implements Runnable {

  private final BlockingQueue<Packet> packetQueue;
  private final PcapOutputStream output;

  public Writer(BlockingQueue<Packet> packetQueue, File dest) throws FileNotFoundException {
    this.packetQueue = packetQueue;
    this.output = PcapOutputStream.create(
            PcapGlobalHeader.createDefaultHeader(),
            new FileOutputStream(dest)
    );
  }

  public Writer(BlockingQueue<Packet> packetQueue, String dest) throws FileNotFoundException {
    this(packetQueue, new File(dest));
  }

  private void writePacket(Packet packet) throws IOException {
    output.write(packet);
  }

  @Override
  public void run() {
    try {
      while (true) {
        writePacket(packetQueue.take());
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
