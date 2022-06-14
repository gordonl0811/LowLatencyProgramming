package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.Packet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static PacketProcessor.DisruptorPacketProcessor.utils.Utils.startDisruptor;

public class Writer extends Component {

    private final Disruptor<PacketEvent> inputDisruptor;
    private final PcapOutputStream output;

    public Writer(Disruptor<PacketEvent> inputDisruptor, String dest) throws FileNotFoundException {
        this.inputDisruptor = inputDisruptor;
        inputDisruptor.handleEventsWith(this);
        this.output = PcapOutputStream.create(
                PcapGlobalHeader.createDefaultHeader(),
                new FileOutputStream(dest)
        );
    }

    @Override
    public void initialize() {
        startDisruptor(inputDisruptor);
    }

    @Override
    public void shutdown() {
        inputDisruptor.shutdown();
    }

    @Override
    public void process(Packet packet) throws IOException {
//        System.out.println("Processed " + getPacketCount());
        output.write(packet);
    }

}
