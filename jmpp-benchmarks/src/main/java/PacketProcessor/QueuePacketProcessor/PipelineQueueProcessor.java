package PacketProcessor.QueuePacketProcessor;


import PacketProcessor.QueuePacketProcessor.components.Dropper;
import PacketProcessor.QueuePacketProcessor.components.PortRewriter;
import PacketProcessor.QueuePacketProcessor.components.ProcessorComponent;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PipelineQueueProcessor extends AbstractQueueProcessor {

    private final PcapReader reader;
    private final PortRewriter rewriter;
    private final Dropper dropper;

    private final long expectedPackets;

    public PipelineQueueProcessor(int bufferSize, String source, int srcPort, int destPort, long expectedPackets) throws IOException {

        BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(bufferSize);
        BlockingQueue<Packet> rewriterQueue = new ArrayBlockingQueue<>(bufferSize);

        this.reader = new PcapReader(source, readerQueue);
        this.rewriter = new PortRewriter(readerQueue, rewriterQueue, srcPort, destPort);
        this.dropper = new Dropper(rewriterQueue);

        this.expectedPackets = expectedPackets;
    }

    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<ProcessorComponent> setComponents() {
        return List.of(rewriter, dropper);
    }

    @Override
    public boolean shouldTerminate() {
        return dropper.getPacketCount() >= expectedPackets;
    }


}
