package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.AbstractPacketProcessor;
import PacketProcessor.DisruptorPacketProcessor.components.PortRewriter;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;
import PacketProcessor.DisruptorPacketProcessor.components.Writer;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

public class RewritePortDisruptorProcessor extends AbstractPacketProcessor {

    private final PcapReader reader;
    private final PortRewriter rewriter;
    private final Writer writer;

    private final long expectedPackets;

    public RewritePortDisruptorProcessor(int bufferSize, String source, String dest, int srcPort, int destPort, long expectedPackets) throws IOException {

        Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> rewriteDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        this.reader = new PcapReader(source, readerDisruptor);
        this.rewriter = new PortRewriter(readerDisruptor, rewriteDisruptor, srcPort, destPort);
        this.writer = new Writer(rewriteDisruptor, dest);

        this.expectedPackets = expectedPackets;

    }

    @Override
    public void initialize() {

        // Initialise writers
        writer.initialize();
        rewriter.initialize();

        // Initialise reader
        reader.initialize();

    }

    @Override
    public void shutdown() {
        reader.shutdown();
        rewriter.shutdown();
        writer.shutdown();
    }

    @Override
    public void releasePackets() {
        reader.start();
    }

    @Override
    public boolean shouldTerminate() {
        return writer.getPacketCount() >= expectedPackets;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        RewritePortDisruptorProcessor processor = new RewritePortDisruptorProcessor(1024, "src/main/resources/input_thousand.pcap", "src/main/resources/output/rewritten.pcap", 6969, 6969, 1000);

        processor.initialize();
        processor.start();

        processor.shutdown();

    }
}
