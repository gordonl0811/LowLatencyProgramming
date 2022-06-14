package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.Dropper;
import PacketProcessor.QueuePacketProcessor.components.Filter;
import PacketProcessor.QueuePacketProcessor.components.PortRewriter;
import PacketProcessor.QueuePacketProcessor.components.Component;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FilterRewriteJoinQueueProcessor extends AbstractQueueProcessor {

    private final PcapReader reader;
    private final Filter filter;
    private final PortRewriter tcpRewriter;
    private final PortRewriter udpRewriter;
    private final Dropper dropper;

    private final long expectedPackets;

    public FilterRewriteJoinQueueProcessor(int queueSize, String source, int tcpSrcPort, int tcpDestPort, int udpSrcPort, int udpDestPort, long expectedPackets) throws IOException {

        final BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> tcpQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> udpQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> rewriterQueue = new ArrayBlockingQueue<>(queueSize);

        this.reader = new PcapReader(source, readerQueue);
        this.filter = new Filter(readerQueue, tcpQueue, udpQueue);
        this.tcpRewriter = new PortRewriter(tcpQueue, rewriterQueue, tcpSrcPort, tcpDestPort);
        this.udpRewriter = new PortRewriter(udpQueue, rewriterQueue, udpSrcPort, udpDestPort);
        this.dropper = new Dropper(rewriterQueue);

        this.expectedPackets = expectedPackets;

    }

    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<Component> setComponents() {
        return Arrays.asList(filter, tcpRewriter, udpRewriter, dropper);
    }

    @Override
    public boolean shouldTerminate() {
        return dropper.getPacketCount() >= expectedPackets;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FilterRewriteJoinQueueProcessor processor = new FilterRewriteJoinQueueProcessor(1000, "src/main/resources/input_thousand.pcap", 12, 34, 56, 78, 1000);

        processor.initialize();
        processor.start();
        processor.shutdown();
    }
}
