import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.framer.FramingException;
import io.pkts.packet.Packet;
import io.pkts.protocol.Protocol;

import java.io.IOException;

public class PcapParser {

    private static final String pcap_location = "src/main/resources/single_input.pcap";

    public static void main(final String[] args) throws IOException, FramingException {

        final Pcap pcap = Pcap.openStream(pcap_location);

        pcap.loop(packet -> {
            if (packet.hasProtocol(Protocol.UDP)) {
                System.out.println(packet.getPacket(Protocol.UDP).getPayload());
            } else if (packet.hasProtocol(Protocol.TCP)) {
                System.out.println(packet.getPacket(Protocol.TCP).getPayload());
            }
            return true;
        });

    }

}
