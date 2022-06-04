package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.Filter;
import PacketProcessor.QueuePacketProcessor.components.Reader;
import PacketProcessor.QueuePacketProcessor.components.Writer;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FilterAndWriteQueueProcessor extends AbstractQueueProcessor {

    private final Writer tcpWriter;
    private final Writer udpWriter;

    private final long expectedTcpPackets;
    private final long expectedUdpPackets;

    public FilterAndWriteQueueProcessor(int queueSize, String source, String tcpDest, String udpDest, long expectedTcpPackets, long expectedUdpPackets)
            throws IOException {

        super();

        final BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(queueSize);

        Filter filter = new Filter(readerQueue, tcpQueue, udpQueue);
        this.tcpWriter = new Writer(tcpQueue, tcpDest);
        this.udpWriter = new Writer(udpQueue, udpDest);

        this.expectedTcpPackets = expectedTcpPackets;
        this.expectedUdpPackets = expectedUdpPackets;

        setReader(new Reader(source, readerQueue));
        addComponent(filter);
        addComponent(this.tcpWriter);
        addComponent(this.udpWriter);

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
    }
}
