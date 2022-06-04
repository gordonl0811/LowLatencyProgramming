package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.dsl.Disruptor;

public class Dropper implements PacketEventConsumer {

    private final Disruptor<PacketEvent> inputDisruptor;

    public Dropper(Disruptor<PacketEvent> inputDisruptor) {
        this.inputDisruptor = inputDisruptor;
    }

    @Override
    public void initialize() {
        inputDisruptor.handleEventsWith(this);
    }

    @Override
    public void onEvent(PacketEvent packetEvent, long l, boolean b) { }
}
