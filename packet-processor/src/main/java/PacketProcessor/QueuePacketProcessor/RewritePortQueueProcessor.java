package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.PortRewriter;
import PacketProcessor.QueuePacketProcessor.components.ProcessorComponent;
import PacketProcessor.QueuePacketProcessor.components.Writer;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RewritePortQueueProcessor extends AbstractQueueProcessor {

    private final PcapReader reader;
    private final PortRewriter rewriter;
    private final Writer writer;
    private final long expectedPackets;

    public RewritePortQueueProcessor(int queueSize, String source, String dest, int srcPort, int destPort, long expectedPackets) throws IOException {

        final BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> rewriterQueue = new ArrayBlockingQueue<>(queueSize);

        this.reader = new PcapReader(source, producerQueue);
        this.rewriter = new PortRewriter(producerQueue, rewriterQueue, srcPort, destPort);
        this.writer = new Writer(rewriterQueue, dest);

        this.expectedPackets = expectedPackets;
    }

    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<ProcessorComponent> setComponents() {
        return Arrays.asList(rewriter, writer);
    }

    @Override
    public boolean shouldTerminate() {
        return writer.getPacketCount() >= expectedPackets;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        RewritePortQueueProcessor processor = new RewritePortQueueProcessor(
                1024,
                "src/main/resources/input_thousand.pcap",
                "src/main/resources/output/rewritten.pcap",
                6969, 6969, 1000
        );

        processor.initialize();
        processor.start();
        processor.shutdown();
    }
}
