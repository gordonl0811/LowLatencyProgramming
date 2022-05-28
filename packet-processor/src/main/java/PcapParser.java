import io.pkts.Pcap;
import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.framer.FramingException;
import io.pkts.protocol.Protocol;
import java.io.FileOutputStream;
import java.io.IOException;

public class PcapParser {

    private static final String input_location = "src/main/resources/single_input.pcap";
    private static final String output_location = "src/main/resources/single_output.pcap";

    public static void packetProducer(String source) throws IOException {
        final Pcap pcap = Pcap.openStream(source);
    }

    public static void main(final String[] args) throws IOException, FramingException {

        final Pcap pcap = Pcap.openStream(input_location);
        PcapOutputStream outputPcap = PcapOutputStream.create(
            PcapGlobalHeader.createDefaultHeader(),
            new FileOutputStream(output_location)
        );

        pcap.loop(packet -> {
            if (packet.hasProtocol(Protocol.TCP)) {
//                System.out.println(packet.getPacket(Protocol.TCP).getPayload());
                outputPcap.write(packet);
            }
            return true;
        });

    }

}
