package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Filter;
import PacketProcessor.DisruptorPacketProcessor.components.Reader;
import PacketProcessor.DisruptorPacketProcessor.components.Writer;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

public class FilterAndWriteDisruptorProcessor extends AbstractQueueProcessor {

    private final Reader reader;
    private final Filter filter;
    private final Writer tcpWriter;
    private final Writer udpWriter;

    private final long expectedTcpPackets;
    private final long expectedUdpPackets;

    public FilterAndWriteDisruptorProcessor(int bufferSize, String source, String tcpDest, String udpDest, long expectedTcpPackets, long expectedUdpPackets) throws IOException {

        Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> tcpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> udpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        this.reader = new Reader(source, readerDisruptor);
        this.filter = new Filter(readerDisruptor, tcpDisruptor, udpDisruptor);
        this.tcpWriter = new Writer(tcpDisruptor, tcpDest);
        this.udpWriter = new Writer(udpDisruptor, udpDest);

        this.expectedTcpPackets = expectedTcpPackets;
        this.expectedUdpPackets = expectedUdpPackets;

        setReader(this.reader);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        FilterAndWriteDisruptorProcessor processor = new FilterAndWriteDisruptorProcessor(1024, "src/main/resources/input_thousand.pcap", "src/main/resources/output/tcp_output.pcap", "src/main/resources/output/udp_output.pcap", 505, 495);

        processor.initialize();
        processor.start();

    }

    @Override
    public void initialize() {

        // Initialise writers
        tcpWriter.initialize();
        udpWriter.initialize();

        // Initialise filter
        filter.initialize();

        // Initialise reader
        reader.initialize();

    }

    @Override
    public void shutdown() {
        reader.shutdown();
        filter.shutdown();
        tcpWriter.shutdown();
        udpWriter.shutdown();
    }

    @Override
    public boolean shouldTerminate() {
        return tcpWriter.getPacketCount() >= expectedTcpPackets && udpWriter.getPacketCount() >= expectedUdpPackets;
    }
}
