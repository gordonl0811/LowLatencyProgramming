package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.Packet;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PacketWriter implements PacketEventConsumer {

  private final PcapOutputStream output;

  public PacketWriter(String dest) throws FileNotFoundException {
    this.output = PcapOutputStream.create(
        PcapGlobalHeader.createDefaultHeader(),
        new FileOutputStream(dest)
    );
  }

  private void writePacket(Packet packet) throws IOException {
    output.write(packet);
  }

  @Override
  public void onEvent(PacketEvent packetEvent, long l, boolean b) throws Exception {
    writePacket(packetEvent.getValue());
  }
}
