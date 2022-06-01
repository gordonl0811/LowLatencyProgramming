import components.PacketFilter;
import components.PacketProducer;
import components.PacketWriter;
import io.pkts.packet.Packet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
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
}
