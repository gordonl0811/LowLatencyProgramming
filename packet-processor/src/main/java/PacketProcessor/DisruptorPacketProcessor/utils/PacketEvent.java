package PacketProcessor.DisruptorPacketProcessor.utils;

import com.lmax.disruptor.EventFactory;
import io.pkts.packet.Packet;

public class PacketEvent {

    public final static EventFactory<PacketEvent> EVENT_FACTORY = PacketEvent::new;
    private Packet value;

    public Packet getValue() {
        return value;
    }

    public void setValue(Packet value) {
        this.value = value;
    }

}
