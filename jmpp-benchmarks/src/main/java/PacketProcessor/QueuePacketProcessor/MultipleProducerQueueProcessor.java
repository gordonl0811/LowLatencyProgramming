package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.Dropper;
import PacketProcessor.QueuePacketProcessor.components.Component;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MultipleProducerQueueProcessor extends AbstractQueueProcessor {

    private final PcapReader readerOne;
    private final PcapReader readerTwo;
    private final PcapReader readerThree;
    private final Dropper dropper;

    private final long expectedPackets;

    public MultipleProducerQueueProcessor(int queueSize, String sourceOne, String sourceTwo, String sourceThree, int expectedPackets) throws IOException {
        BlockingQueue<Packet> sharedQueue = new ArrayBlockingQueue<>(queueSize);

        this.readerOne = new PcapReader(sourceOne, sharedQueue);
        this.readerTwo = new PcapReader(sourceTwo, sharedQueue);
        this.readerThree = new PcapReader(sourceThree, sharedQueue);
        this.dropper = new Dropper(sharedQueue);

        this.expectedPackets = expectedPackets;
    }

    @Override
    protected List<PcapReader> setReaders() {
        return Arrays.asList(readerOne, readerTwo, readerThree);
    }

    @Override
    protected List<Component> setComponents() {
        return List.of(dropper);
    }

    @Override
    protected boolean shouldTerminate() {
        return dropper.getPacketCount() >= expectedPackets;
    }
}
