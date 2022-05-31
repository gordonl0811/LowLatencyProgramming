package components;

import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.Packet;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import utils.PoisonPacket;

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
      while (true) {
        Packet packet = packetQueue.take();
        if (!(packet instanceof PoisonPacket)) {
          return;
        }
        writePacket(packet);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
