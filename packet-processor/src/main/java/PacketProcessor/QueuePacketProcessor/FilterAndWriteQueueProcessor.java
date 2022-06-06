package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.Filter;
import PacketProcessor.QueuePacketProcessor.components.ProcessorComponent;
import PacketProcessor.QueuePacketProcessor.components.Writer;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FilterAndWriteQueueProcessor extends AbstractQueueProcessor {

    private final PcapReader reader;
    private final Filter filter;
    private final Writer tcpWriter;
    private final Writer udpWriter;

    private final long expectedTcpPackets;
    private final long expectedUdpPackets;

    public FilterAndWriteQueueProcessor(int queueSize, String source, String tcpDest, String udpDest, long expectedTcpPackets, long expectedUdpPackets)
            throws IOException {

        final BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(queueSize);

        this.reader = new PcapReader(source, readerQueue);
        this.filter = new Filter(readerQueue, tcpQueue, udpQueue);
        this.tcpWriter = new Writer(tcpQueue, tcpDest);
        this.udpWriter = new Writer(udpQueue, udpDest);

        this.expectedTcpPackets = expectedTcpPackets;
        this.expectedUdpPackets = expectedUdpPackets;
    }

    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<ProcessorComponent> setComponents() {
        return Arrays.asList(filter, tcpWriter, udpWriter);
    }

    @Override
    public boolean shouldTerminate() {
        return tcpWriter.getPacketCount() >= expectedTcpPackets && udpWriter.getPacketCount() >= expectedUdpPackets;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FilterAndWriteQueueProcessor processor = new FilterAndWriteQueueProcessor(
                1000,
                "src/main/resources/input_thousand.pcap",
                "src/main/resources/output/tcp_output.pcap",
                "src/main/resources/output/udp_output.pcap",
                505, 495);

        processor.initialize();
        processor.start();
        processor.shutdown();
    }
}
