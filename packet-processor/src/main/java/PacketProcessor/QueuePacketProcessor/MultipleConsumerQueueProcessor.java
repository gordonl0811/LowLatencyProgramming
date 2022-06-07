package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.QueuePacketProcessor.components.Dropper;
import PacketProcessor.QueuePacketProcessor.components.Multicaster;
import PacketProcessor.QueuePacketProcessor.components.ProcessorComponent;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;
import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MultipleConsumerQueueProcessor extends AbstractQueueProcessor {

    private final PcapReader reader;
    private final Multicaster multicaster;
    private final Dropper consumerOne;
    private final Dropper consumerTwo;
    private final Dropper consumerThree;

    private final long expectedPackets;

    public MultipleConsumerQueueProcessor(int queueSize, String source, long expectedPackets) throws IOException {
        BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(queueSize);
        BlockingQueue<Packet> consumerOneQueue = new ArrayBlockingQueue<>(queueSize);
        BlockingQueue<Packet> consumerTwoQueue = new ArrayBlockingQueue<>(queueSize);
        BlockingQueue<Packet> consumerThreeQueue = new ArrayBlockingQueue<>(queueSize);


        this.reader = new PcapReader(source, readerQueue);
        this.multicaster = new Multicaster(readerQueue, List.of(consumerOneQueue, consumerTwoQueue, consumerThreeQueue));
        this.consumerOne = new Dropper(consumerOneQueue);
        this.consumerTwo = new Dropper(consumerTwoQueue);
        this.consumerThree = new Dropper(consumerThreeQueue);

        this.expectedPackets = expectedPackets;
    }


    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<ProcessorComponent> setComponents() {
        return List.of(multicaster, consumerOne, consumerTwo, consumerThree);
    }

    @Override
    protected boolean shouldTerminate() {
        return consumerOne.getPacketCount() == expectedPackets &&
                consumerTwo.getPacketCount() == expectedPackets &&
                consumerThree.getPacketCount() == expectedPackets;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        MultipleConsumerQueueProcessor processor = new MultipleConsumerQueueProcessor(
                1024,
                "src/main/resources/input_thousand.pcap",
                1000
        );

        processor.initialize();
        processor.start();
        processor.shutdown();
    }
}
