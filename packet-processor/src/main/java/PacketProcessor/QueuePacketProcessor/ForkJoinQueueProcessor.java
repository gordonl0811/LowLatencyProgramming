package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.*;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ForkJoinQueueProcessor extends AbstractQueueProcessor {

    private final Dropper dropper;

    private final long expectedPackets;

    public ForkJoinQueueProcessor(
            int queueSize,
            String source,
            int tcpSrcPort,
            int tcpDestPort,
            int udpSrcPort,
            int udpDestPort,
            long expectedPackets

    )
            throws IOException {

        super();

        final BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> rewriterQueue = new ArrayBlockingQueue<>(queueSize);

        Filter filter = new Filter(readerQueue, tcpQueue, udpQueue);
        PortRewriter tcpRewriter = new PortRewriter(tcpQueue, rewriterQueue, tcpSrcPort, tcpDestPort);
        PortRewriter udpRewriter = new PortRewriter(udpQueue, rewriterQueue, udpSrcPort, udpDestPort);
        this.dropper = new Dropper(rewriterQueue);
        this.expectedPackets = expectedPackets;

        setReader(new Reader(source, readerQueue));
        addComponent(filter);
        addComponent(tcpRewriter);
        addComponent(udpRewriter);
        addComponent(this.dropper);

    }

    @Override
    public boolean shouldTerminate() {
        return dropper.getPacketCount() >= expectedPackets;
    }

    @Override
    public void shutdown() {

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ForkJoinQueueProcessor processor = new ForkJoinQueueProcessor(
                1000,
                "src/main/resources/input_thousand.pcap",
                12,
                34,
                56,
                78,
                1000);

        processor.initialize();
        processor.start();
        processor.shutdown();
    }
}
