package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.EventHandler;
import io.pkts.packet.Packet;

import java.io.IOException;

public abstract class Component implements EventHandler<PacketEvent> {

    private long packetCount = 0;

    public long getPacketCount() {
        return packetCount;
    }

    public abstract void initialize();

    public abstract void shutdown();

    public abstract void process(Packet packet) throws IOException;

    @Override
    public final void onEvent(PacketEvent packetEvent, long l, boolean b) throws IOException {
        process(packetEvent.getValue());
        packetCount++;
    }
}
