package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.components.Filter;
import PacketProcessor.QueuePacketProcessor.components.Reader;
import PacketProcessor.QueuePacketProcessor.components.Writer;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FilterAndWriteQueueProcessor implements PacketProcessor {

    private final Thread producerThread;
    private final Thread filterThread;
    private final Thread tcpThread;
    private final Thread udpThread;

    public FilterAndWriteQueueProcessor(int queueSize, String source, String tcpDest, String udpDest)
            throws IOException {

        final BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(queueSize);

        final Reader reader = new Reader(source, producerQueue);
        final Filter filter = new Filter(producerQueue, tcpQueue, udpQueue);
        final Writer tcpWriter = new Writer(tcpQueue, tcpDest);
        final Writer udpWriter = new Writer(udpQueue, udpDest);

        this.producerThread = new Thread(reader);
        this.filterThread = new Thread(filter);
        this.tcpThread = new Thread(tcpWriter);
        this.udpThread = new Thread(udpWriter);
    }

    @Override
    public void initialize() {
        filterThread.start();
        tcpThread.start();
        udpThread.start();
    }

    @Override
    public void start() throws InterruptedException {
        producerThread.start();
        producerThread.join();
        filterThread.join();
        tcpThread.join();
        udpThread.join();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FilterAndWriteQueueProcessor processor = new FilterAndWriteQueueProcessor(
                1000,
                "src/main/resources/input_ten_thousand.pcap",
                "src/main/resources/tcp_output.pcap",
                "src/main/resources/udp_output.pcap"
        );

        processor.initialize();
        processor.start();
    }
}
