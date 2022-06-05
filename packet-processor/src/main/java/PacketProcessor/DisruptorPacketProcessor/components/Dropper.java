package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.packet.Packet;

public class Dropper extends ProcessorComponent {

    private final Disruptor<PacketEvent> inputDisruptor;

    public Dropper(Disruptor<PacketEvent> inputDisruptor) {
        this.inputDisruptor = inputDisruptor;
    }

    @Override
    public void initialize() {
        inputDisruptor.handleEventsWith(this);
    }

    @Override
    public void shutdown() {
        inputDisruptor.shutdown();
    }


    @Override
    public void process(Packet packet) {
    }
}
