package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.packet.Packet;
import io.pkts.packet.TransportPacket;
import io.pkts.protocol.Protocol;

import java.io.IOException;

import static PacketProcessor.DisruptorPacketProcessor.utils.Utils.startDisruptor;

public class PortRewriter extends Component {

    private final Disruptor<PacketEvent> inputDisruptor;
    private final Disruptor<PacketEvent> outputDisruptor;
    private final int srcPort;
    private final int destPort;

    public PortRewriter(Disruptor<PacketEvent> inputDisruptor, Disruptor<PacketEvent> outputDisruptor, int srcPort, int destPort) {
        this.inputDisruptor = inputDisruptor;
        this.outputDisruptor = outputDisruptor;
        inputDisruptor.handleEventsWith(this);
        // Set srcPort/destPort to a negative value (i.e. -1) to retain their values
        this.srcPort = srcPort;
        this.destPort = destPort;
    }

    @Override
    public void initialize() {
        startDisruptor(inputDisruptor);
        startDisruptor(outputDisruptor);
    }

    @Override
    public void shutdown() {
        inputDisruptor.shutdown();
        outputDisruptor.shutdown();
    }

    @Override
    public void process(Packet packet) throws IOException {
        Protocol layerFourProtocol = packet.hasProtocol(Protocol.TCP) ? Protocol.TCP : Protocol.UDP;
        TransportPacket layerFourPacket = (TransportPacket) packet.getPacket(layerFourProtocol);

        // Set ports if they have been defined
        if (srcPort >= 0) {
            layerFourPacket.setSourcePort(srcPort);
        }
        if (destPort >= 0) {
            layerFourPacket.setDestinationPort(destPort);
        }

        outputDisruptor.publishEvent((event, sequence) -> event.setValue(packet));
    }
}

