import components.PacketFilter;
import components.PacketProducer;
import io.pkts.packet.Packet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PacketFilterTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testFilterThreadTerminatesWithPoisonPacket()
            throws IOException, InterruptedException, ExecutionException, TimeoutException {

        final String source = "src/test/resources/PacketProducerTest/input_single.pcap";

        BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
        BlockingQueue<Packet> dummyQueueOne = new ArrayBlockingQueue<>(1000);
        BlockingQueue<Packet> dummyQueueTwo = new ArrayBlockingQueue<>(1000);

        PacketProducer packetProducer = new PacketProducer(source, producerQueue);
        PacketFilter packetFilter = new PacketFilter(producerQueue, dummyQueueOne, dummyQueueTwo);

        new Thread(packetProducer).start();

        // Check that the thread has completed, i.e. terminated
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(packetFilter);
        future.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testFilterSplitsTcpUdpPackets() throws IOException, InterruptedException {
        BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(1000);
        BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(1000);
        BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(1000);

        final String source = "src/test/resources/PacketFilterTest/input_multiple.pcap";

        PacketProducer packetProducer = new PacketProducer(source, producerQueue);
        PacketFilter packetFilter = new PacketFilter(producerQueue, tcpQueue, udpQueue);

        Thread producerThread = new Thread(packetProducer);
        Thread filterThread = new Thread(packetFilter);

        producerThread.start();
        filterThread.start();

        producerThread.join();
        filterThread.join();

        // From WireShark, there are 52 TCP and 48 UDP packets

        List<Packet> tcpPackets = new ArrayList<>();
        tcpQueue.drainTo(tcpPackets);
        assert(tcpPackets.size() == 52);

        List<Packet> udpPackets = new ArrayList<>();
        udpQueue.drainTo(udpPackets);
        assert(udpPackets.size() == 48);

    }
}
