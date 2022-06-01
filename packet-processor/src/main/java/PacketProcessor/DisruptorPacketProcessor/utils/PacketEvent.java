package PacketProcessor.DisruptorPacketProcessor.utils;

import com.lmax.disruptor.EventFactory;
import io.pkts.packet.Packet;

public class PacketEvent {

  private Packet value;
  public final static EventFactory<PacketEvent> EVENT_FACTORY = PacketEvent::new;

  public Packet getValue() {
    return value;
  }

  public void setValue(Packet value) {
    this.value = value;
  }

}
