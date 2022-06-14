package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.packet.IPPacket;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;

import java.io.IOException;

import static PacketProcessor.DisruptorPacketProcessor.utils.Utils.startDisruptor;

public class IPAddressRewriter extends Component {

    private final Disruptor<PacketEvent> inputDisruptor;
    private final Disruptor<PacketEvent> outputDisruptor;
    private final String srcAddr;
    private final String dstAddr;

    public IPAddressRewriter(Disruptor<PacketEvent> inputDisruptor, Disruptor<PacketEvent> outputDisruptor, String srcAddr, String dstAddr) {
        this.inputDisruptor = inputDisruptor;
        this.outputDisruptor = outputDisruptor;
        inputDisruptor.handleEventsWith(this);
        // Set srcPort/destPort to a negative value (i.e. -1) to retain their values
        this.srcAddr = srcAddr;
        this.dstAddr = dstAddr;
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
        Protocol layerThreeProtocol = packet.hasProtocol(Protocol.IPv4) ? Protocol.IPv4 : Protocol.IPv6;
        IPPacket layerThreePacket = (IPPacket) packet.getPacket(layerThreeProtocol);

        // Set addresses if they have been defined
        if (srcAddr != null) {
            layerThreePacket.setSourceIP(srcAddr);
        }
        if (dstAddr != null) {
            layerThreePacket.setDestinationIP(dstAddr);
        }

        outputDisruptor.publishEvent((event, sequence) -> event.setValue(packet));
    }
}
