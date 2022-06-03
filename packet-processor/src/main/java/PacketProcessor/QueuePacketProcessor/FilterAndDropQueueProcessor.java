package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.components.PacketDropper;
import PacketProcessor.QueuePacketProcessor.components.PacketFilter;
import PacketProcessor.QueuePacketProcessor.components.PacketReader;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FilterAndDropQueueProcessor implements PacketProcessor {
    private final Thread producerThread;
    private final Thread filterThread;
    private final Thread tcpDropperThread;
    private final Thread udpDropperThread;

    public FilterAndDropQueueProcessor(int queueSize, String source)
            throws IOException {

        final BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(queueSize);

        final PacketReader packetReader = new PacketReader(source, producerQueue);
        final PacketFilter packetFilter = new PacketFilter(producerQueue, tcpQueue, udpQueue);
        final PacketDropper tcpDropper = new PacketDropper(tcpQueue);
        final PacketDropper udpDropper = new PacketDropper(udpQueue);

        this.producerThread = new Thread(packetReader);
        this.filterThread = new Thread(packetFilter);
        this.tcpDropperThread = new Thread(tcpDropper);
        this.udpDropperThread = new Thread(udpDropper);
    }

    @Override
    public void initialize() {
        filterThread.start();
        tcpDropperThread.start();
        udpDropperThread.start();
    }

    @Override
    public void start() throws InterruptedException {
        producerThread.start();
        producerThread.join();
        filterThread.join();
        tcpDropperThread.join();
        udpDropperThread.join();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FilterAndDropQueueProcessor processor = new FilterAndDropQueueProcessor(
                1024,
                "src/main/resources/input_thousand.pcap"
        );

        processor.initialize();
        processor.start();
    }
}
