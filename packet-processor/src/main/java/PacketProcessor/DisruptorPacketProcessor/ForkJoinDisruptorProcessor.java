package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Filter;
import PacketProcessor.DisruptorPacketProcessor.components.PortRewriter;
import PacketProcessor.DisruptorPacketProcessor.components.Reader;
import PacketProcessor.DisruptorPacketProcessor.components.Writer;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

public class ForkJoinDisruptorProcessor extends AbstractDisruptorProcessor {

    private final Reader reader;
    private final Filter filter;
    private final PortRewriter tcpRewriter;
    private final PortRewriter udpRewriter;
    private final Writer writer;

    private final long expectedPackets;

    public ForkJoinDisruptorProcessor(
            int bufferSize,
            String source,
            String dest,
            int tcpSrcPort,
            int tcpDestPort,
            int udpSrcPort,
            int udpDestPort,
            long expectedPackets
    ) throws IOException {
        Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> tcpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> udpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> rewriterDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new YieldingWaitStrategy());

        this.reader = new Reader(source, readerDisruptor);
        this.filter = new Filter(readerDisruptor, tcpDisruptor, udpDisruptor);
        this.tcpRewriter = new PortRewriter(tcpDisruptor, rewriterDisruptor, tcpSrcPort, tcpDestPort);
        this.udpRewriter = new PortRewriter(udpDisruptor, rewriterDisruptor, udpSrcPort, udpDestPort);
        this.writer = new Writer(rewriterDisruptor, dest);

        this.expectedPackets = expectedPackets;
    }


    @Override
    public void initialize() {
        writer.initialize();
        tcpRewriter.initialize();
        udpRewriter.initialize();
        filter.initialize();
        reader.initialize();
    }

    @Override
    public void shutdown() {
        reader.shutdown();
        filter.shutdown();
        tcpRewriter.shutdown();
        udpRewriter.shutdown();
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
        ForkJoinDisruptorProcessor processor = new ForkJoinDisruptorProcessor(
                1024,
                "src/main/resources/input_thousand.pcap",
                "src/main/resources/output/rewritten.pcap",
                12,
                34,
                56,
                78,
                1000
                );

        processor.initialize();
        processor.start();
        processor.shutdown();
    }
}
