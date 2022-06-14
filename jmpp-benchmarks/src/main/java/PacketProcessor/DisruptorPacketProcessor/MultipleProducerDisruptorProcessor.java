package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Dropper;
import PacketProcessor.DisruptorPacketProcessor.components.Component;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MultipleProducerDisruptorProcessor extends AbstractDisruptorProcessor {

    private final PcapReader readerOne;
    private final PcapReader readerTwo;
    private final PcapReader readerThree;
    private final Dropper dropper;

    private final long expectedPackets;

    public MultipleProducerDisruptorProcessor(int bufferSize, String sourceOne, String sourceTwo, String sourceThree, int expectedPackets) throws IOException {
        Disruptor<PacketEvent> sharedDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new YieldingWaitStrategy());

        this.readerOne = new PcapReader(sourceOne, sharedDisruptor);
        this.readerTwo = new PcapReader(sourceTwo, sharedDisruptor);
        this.readerThree = new PcapReader(sourceThree, sharedDisruptor);
        this.dropper = new Dropper(sharedDisruptor);

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

    public static void main(String[] args) throws IOException, InterruptedException {
        MultipleProducerDisruptorProcessor processor = new MultipleProducerDisruptorProcessor(
                1024,
                "src/main/resources/example_one.pcap",
                "src/main/resources/example_two.pcap",
                "src/main/resources/example_three.pcap",
                3000
        );

        processor.initialize();
        processor.start();
        processor.shutdown();
    }
}
