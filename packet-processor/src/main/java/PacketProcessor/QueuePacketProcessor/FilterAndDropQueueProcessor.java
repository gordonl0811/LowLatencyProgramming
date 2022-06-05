package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.Dropper;
import PacketProcessor.QueuePacketProcessor.components.Filter;
import PacketProcessor.QueuePacketProcessor.components.Reader;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FilterAndDropQueueProcessor extends AbstractQueueProcessor {

    private final Dropper tcpDropper;
    private final Dropper udpDropper;
    private final long expectedTcpPackets;
    private final long expectedUdpPackets;

    public FilterAndDropQueueProcessor(int queueSize, String source, long expectedTcpPackets, long expectedUdpPackets)
            throws IOException {

        super();

        final BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(queueSize);

        Filter filter = new Filter(producerQueue, tcpQueue, udpQueue);
        this.tcpDropper = new Dropper(tcpQueue);
        this.udpDropper =new Dropper(udpQueue);

        this.expectedTcpPackets = expectedTcpPackets;
        this.expectedUdpPackets = expectedUdpPackets;

        setReader(new Reader(source, producerQueue));
        addComponent(filter);
        addComponent(this.tcpDropper);
        addComponent(this.udpDropper);
    }

    @Override
    public boolean shouldTerminate() {
        return tcpDropper.getPacketCount() >= expectedTcpPackets && udpDropper.getPacketCount() >= expectedUdpPackets;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FilterAndDropQueueProcessor processor = new FilterAndDropQueueProcessor(
                1024,
                "src/main/resources/input_thousand.pcap",
                505, 495
        );

        processor.initialize();
        processor.start();
    }

    @Override
    public void shutdown() {

    }
}
