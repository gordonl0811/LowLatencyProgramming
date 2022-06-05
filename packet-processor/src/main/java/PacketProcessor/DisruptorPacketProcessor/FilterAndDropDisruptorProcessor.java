package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Dropper;
import PacketProcessor.DisruptorPacketProcessor.components.Filter;
import PacketProcessor.DisruptorPacketProcessor.components.Reader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

public class FilterAndDropDisruptorProcessor extends AbstractQueueProcessor {

    private final Disruptor<PacketEvent> readerDisruptor;
    private final Disruptor<PacketEvent> tcpDisruptor;
    private final Disruptor<PacketEvent> udpDisruptor;

    private final Reader reader;
    private final Filter filter;
    private final Dropper tcpDropper;
    private final Dropper udpDropper;

    private final long expectedTcpPackets;
    private final long expectedUdpPackets;

    public FilterAndDropDisruptorProcessor(int bufferSize, String source, long expectedTcpPackets, long expectedUdpPackets)
            throws IOException {

        this.readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
                DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        this.tcpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
                DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        this.udpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
                DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        this.reader = new Reader(source, readerDisruptor);
        this.filter = new Filter(readerDisruptor, tcpDisruptor, udpDisruptor);
        this.tcpDropper = new Dropper(tcpDisruptor);
        this.udpDropper = new Dropper(udpDisruptor);

        this.expectedTcpPackets = expectedTcpPackets;
        this.expectedUdpPackets = expectedUdpPackets;

        setReader(this.reader);
    }

    @Override
    public void initialize() {

        // Initialise writers
        tcpDropper.initialize();
        udpDropper.initialize();

        // Initialise filter
        filter.initialize();

        // Initialise reader
        reader.initialize();

    }

    @Override
    public void shutdown() {
        readerDisruptor.shutdown();
        tcpDisruptor.shutdown();
        udpDisruptor.shutdown();
    }

    @Override
    public boolean shouldTerminate() {
        return tcpDropper.getPacketCount() >= expectedTcpPackets && udpDropper.getPacketCount() >= expectedUdpPackets;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        FilterAndDropDisruptorProcessor processor = new FilterAndDropDisruptorProcessor(
                1024,
                "src/main/resources/input_thousand.pcap",
                505, 495);

        processor.initialize();
        processor.start();
    }

}
