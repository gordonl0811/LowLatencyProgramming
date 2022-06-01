package PacketProcessor.utils;

import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.PacketParseException;
import io.pkts.packet.impl.UnknownApplicationPacket;
import io.pkts.protocol.Protocol;
import java.io.IOException;
import java.io.OutputStream;

public class PoisonPacket implements UnknownApplicationPacket {

  @Override
  public long getArrivalTime() {
    return -1;
  }

  @Override
  public void verify() {

  }

  @Override
  public void write(OutputStream outputStream) throws IOException {

  }

  @Override
  public void write(OutputStream outputStream, Buffer buffer) throws IOException {

  }

  @Override
  public Packet clone() {
    return null;
  }

  @Override
  public boolean hasProtocol(Protocol protocol) throws IOException {
    return false;
  }

  @Override
  public Protocol getProtocol() {
    return null;
  }

  @Override
  public Packet getPacket(Protocol protocol) throws IOException, PacketParseException {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Packet getNextPacket() throws IOException, PacketParseException {
    return null;
  }

  @Override
  public Packet getParentPacket() {
    return null;
  }

  @Override
  public Buffer getPayload() {
    return null;
  }
}
