package PacketProcessor.QueuePacketProcessor.components;

import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.Packet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Writer extends ProcessorComponent {

    private final PcapOutputStream output;

    public Writer(BlockingQueue<Packet> inputQueue, File dest) throws FileNotFoundException {
        super(inputQueue);
        this.output = PcapOutputStream.create(
                PcapGlobalHeader.createDefaultHeader(),
                new FileOutputStream(dest)
        );
    }

    public Writer(BlockingQueue<Packet> packetQueue, String dest) throws FileNotFoundException {
        this(packetQueue, new File(dest));
    }

    @Override
    public void process(Packet packet) throws IOException {
        output.write(packet);
    }

}
