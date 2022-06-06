package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.PortRewriter;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;
import PacketProcessor.QueuePacketProcessor.components.Writer;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RewritePortQueueProcessor extends AbstractQueueProcessor {

    private final Writer packetWriter;
    private final long expectedPackets;

    public RewritePortQueueProcessor(int queueSize, String source, String dest, int srcPort, int destPort, long expectedPackets) throws IOException {
        super();

        final BlockingQueue<Packet> producerQueue = new ArrayBlockingQueue<>(queueSize);
        final BlockingQueue<Packet> rewriterQueue = new ArrayBlockingQueue<>(queueSize);

        PortRewriter portRewriter = new PortRewriter(producerQueue, rewriterQueue, srcPort, destPort);
        this.packetWriter = new Writer(rewriterQueue, dest);

        this.expectedPackets = expectedPackets;

        setReader(new PcapReader(source, producerQueue));
        addComponent(portRewriter);
        addComponent(this.packetWriter);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean shouldTerminate() {
        return packetWriter.getPacketCount() >= expectedPackets;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        RewritePortQueueProcessor processor = new RewritePortQueueProcessor(
                1024,
                "src/main/resources/input_thousand.pcap",
                "src/main/resources/output/rewritten.pcap",
                6969, 6969, 1000
        );

        processor.initialize();
        processor.start();
    }
}
