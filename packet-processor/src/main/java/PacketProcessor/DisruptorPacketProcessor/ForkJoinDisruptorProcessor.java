package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.AbstractPacketProcessor;
import PacketProcessor.DisruptorPacketProcessor.components.*;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

public class ForkJoinDisruptorProcessor extends AbstractPacketProcessor {

    private final PcapReader reader;
    private final Filter filter;
    private final PortRewriter tcpRewriter;
    private final PortRewriter udpRewriter;
    private final Dropper dropper;

    private final long expectedPackets;

    public ForkJoinDisruptorProcessor(
            int bufferSize,
            String source,
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

        this.reader = new PcapReader(source, readerDisruptor);
        this.filter = new Filter(readerDisruptor, tcpDisruptor, udpDisruptor);
        this.tcpRewriter = new PortRewriter(tcpDisruptor, rewriterDisruptor, tcpSrcPort, tcpDestPort);
        this.udpRewriter = new PortRewriter(udpDisruptor, rewriterDisruptor, udpSrcPort, udpDestPort);
        this.dropper = new Dropper(rewriterDisruptor);

        this.expectedPackets = expectedPackets;
    }


    @Override
    public void initialize() {
        dropper.initialize();
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
        dropper.shutdown();
    }

    @Override
    public void releasePackets() {
        reader.start();
    }

    @Override
    public boolean shouldTerminate() {
        return dropper.getPacketCount() >= expectedPackets;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ForkJoinDisruptorProcessor processor = new ForkJoinDisruptorProcessor(
                1024,
                "src/main/resources/input_thousand.pcap",
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
