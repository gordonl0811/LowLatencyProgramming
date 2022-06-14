package PacketProcessor.QueuePacketProcessor.components;

import io.pkts.packet.Packet;
import io.pkts.packet.TransportPacket;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class PortRewriter extends Component {

    private final BlockingQueue<Packet> outputQueue;
    private final int srcPort;
    private final int destPort;

    public PortRewriter(BlockingQueue<Packet> inputQueue, BlockingQueue<Packet> outputQueue, int srcPort, int destPort) {
        super(inputQueue);
        this.outputQueue = outputQueue;
        this.srcPort = srcPort;
        this.destPort = destPort;
    }

    @Override
    public void process(Packet packet) throws IOException, InterruptedException {
        Protocol layerFourProtocol = packet.hasProtocol(Protocol.TCP) ? Protocol.TCP : Protocol.UDP;
        TransportPacket layerFourPacket = (TransportPacket) packet.getPacket(layerFourProtocol);

        // Set ports if they have been defined
        if (srcPort >= 0) {
            layerFourPacket.setSourcePort(srcPort);
        }
        if (destPort >= 0) {
            layerFourPacket.setDestinationPort(destPort);
        }

        outputQueue.put(packet);
    }
}
