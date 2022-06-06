package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.PortRewriter;
import PacketProcessor.DisruptorPacketProcessor.components.ProcessorComponent;
import PacketProcessor.DisruptorPacketProcessor.components.Writer;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RewritePortDisruptorProcessor extends AbstractDisruptorProcessor {

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

        RewritePortDisruptorProcessor processor = new RewritePortDisruptorProcessor(1024, "src/main/resources/input_thousand.pcap", "src/main/resources/output/rewritten.pcap", 6969, 6969, 1000);

        processor.initialize();
        processor.start();

        processor.shutdown();

    }
}
