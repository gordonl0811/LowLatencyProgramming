package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.sources.PcapReader;
import PacketProcessor.QueuePacketProcessor.components.Writer;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ForwardingQueueProcessor extends AbstractQueueProcessor {

    private final Writer writer;
    private final long expectedPackets;

    public ForwardingQueueProcessor(int queueSize, String source, String dest, long expectedPackets)
            throws IOException {

        super();

        final BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(queueSize);

        final PcapReader reader = new PcapReader(source, readerQueue);
        this.writer = new Writer(readerQueue, dest);

        this.expectedPackets = expectedPackets;

        setReader(reader);
        addComponent(this.writer);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ForwardingQueueProcessor processor = new ForwardingQueueProcessor(
                1000,
                "src/main/resources/input_thousand.pcap",
                "src/main/resources/output/forwarded.pcap",
                1000
        );

        processor.initialize();
        processor.start();
    }

    @Override
    public boolean shouldTerminate() {
        return writer.getPacketCount() >= expectedPackets;
    }

    @Override
    public void shutdown() {

    }
}
