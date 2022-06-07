package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Dropper;
import PacketProcessor.DisruptorPacketProcessor.components.ProcessorComponent;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.List;

public class MultipleConsumerDisruptorProcessor extends AbstractDisruptorProcessor {

    private final PcapReader reader;
    private final Dropper consumerOne;
    private final Dropper consumerTwo;
    private final Dropper consumerThree;

    private final long expectedPackets;

    public MultipleConsumerDisruptorProcessor(int bufferSize, String source, long expectedPackets) throws IOException {
        Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        this.reader = new PcapReader(source, readerDisruptor);
        this.consumerOne = new Dropper(readerDisruptor);
        this.consumerTwo = new Dropper(readerDisruptor);
        this.consumerThree = new Dropper(readerDisruptor);

        this.expectedPackets = expectedPackets;
    }

    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<ProcessorComponent> setComponents() {
        return List.of(consumerOne, consumerTwo, consumerThree);
    }

    @Override
    protected boolean shouldTerminate() {
        return consumerOne.getPacketCount() == expectedPackets &&
                consumerTwo.getPacketCount() == expectedPackets &&
                consumerThree.getPacketCount() == expectedPackets;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        MultipleConsumerDisruptorProcessor processor = new MultipleConsumerDisruptorProcessor(
                1024,
                "src/main/resources/input_thousand.pcap",
                1000
        );

        processor.initialize();
        processor.start();
        processor.shutdown();
    }
}
