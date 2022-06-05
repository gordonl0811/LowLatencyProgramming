package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.Packet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Writer extends ProcessorComponent {

    private final Disruptor<PacketEvent> inputDisruptor;
    private final PcapOutputStream output;

    public Writer(Disruptor<PacketEvent> inputDisruptor, String dest) throws FileNotFoundException {
        this.inputDisruptor = inputDisruptor;
        this.output = PcapOutputStream.create(
                PcapGlobalHeader.createDefaultHeader(),
                new FileOutputStream(dest)
        );
    }

    @Override
    public void initialize() {
        inputDisruptor.handleEventsWith(this);
    }

    @Override
    public void process(Packet packet) throws IOException {
        output.write(packet);
    }

}
