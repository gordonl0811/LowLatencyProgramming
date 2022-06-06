package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.ProcessorComponent;
import PacketProcessor.QueuePacketProcessor.components.Writer;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ForwardingQueueProcessor extends AbstractQueueProcessor {

    private final PcapReader reader;
    private final Writer writer;
    private final long expectedPackets;

    public ForwardingQueueProcessor(int queueSize, String source, String dest, long expectedPackets)
            throws IOException {



        final BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(queueSize);

        this.reader = new PcapReader(source, readerQueue);
        this.writer = new Writer(readerQueue, dest);

        this.expectedPackets = expectedPackets;
    }

    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<ProcessorComponent> setComponents() {
        return List.of(writer);
    }

    @Override
    public boolean shouldTerminate() {
        return writer.getPacketCount() >= expectedPackets;
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
        processor.shutdown();
    }

}
