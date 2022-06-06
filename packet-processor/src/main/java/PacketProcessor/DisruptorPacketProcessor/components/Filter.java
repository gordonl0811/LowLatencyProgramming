package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;

import java.io.IOException;

import static PacketProcessor.DisruptorPacketProcessor.utils.Utils.startDisruptor;

public class Filter extends ProcessorComponent {

    private final Disruptor<PacketEvent> inputDisruptor;
    private final Disruptor<PacketEvent> tcpDisruptor;
    private final Disruptor<PacketEvent> udpDisruptor;

    public Filter(
            Disruptor<PacketEvent> inputDisruptor,
            Disruptor<PacketEvent> tcpDisruptor,
            Disruptor<PacketEvent> udpDisruptor) {
        this.inputDisruptor = inputDisruptor;
        this.tcpDisruptor = tcpDisruptor;
        this.udpDisruptor = udpDisruptor;
    }

    @Override
    public void initialize() {
        inputDisruptor.handleEventsWith(this);
        startDisruptor(inputDisruptor);
        startDisruptor(tcpDisruptor);
        startDisruptor(udpDisruptor);
    }

    @Override
    public void shutdown() {
        inputDisruptor.shutdown();
        tcpDisruptor.shutdown();
        udpDisruptor.shutdown();
    }

    @Override
    public void process(Packet packet) throws IOException {
        if (packet.hasProtocol(Protocol.TCP)) {
            tcpDisruptor.publishEvent((event, sequence) -> event.setValue(packet));
        } else if (packet.hasProtocol(Protocol.UDP)) {
            udpDisruptor.publishEvent((event, sequence) -> event.setValue(packet));
        }
    }

}
