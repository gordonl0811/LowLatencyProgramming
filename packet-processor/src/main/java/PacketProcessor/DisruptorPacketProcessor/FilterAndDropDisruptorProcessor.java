package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Dropper;
import PacketProcessor.DisruptorPacketProcessor.components.Filter;
import PacketProcessor.DisruptorPacketProcessor.components.ProcessorComponent;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FilterAndDropDisruptorProcessor extends AbstractDisruptorProcessor {

    private final PcapReader reader;
    private final Filter filter;
    private final Dropper tcpDropper;
    private final Dropper udpDropper;

    private final long expectedTcpPackets;
    private final long expectedUdpPackets;

    public FilterAndDropDisruptorProcessor(int bufferSize, String source, long expectedTcpPackets, long expectedUdpPackets) throws IOException {

        Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> tcpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> udpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        this.reader = new PcapReader(source, readerDisruptor);
        this.filter = new Filter(readerDisruptor, tcpDisruptor, udpDisruptor);
        this.tcpDropper = new Dropper(tcpDisruptor);
        this.udpDropper = new Dropper(udpDisruptor);

        this.expectedTcpPackets = expectedTcpPackets;
        this.expectedUdpPackets = expectedUdpPackets;

    }

    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<ProcessorComponent> setComponents() {
        return Arrays.asList(filter, tcpDropper, udpDropper);
    }


    @Override
    public boolean shouldTerminate() {
        return tcpDropper.getPacketCount() >= expectedTcpPackets && udpDropper.getPacketCount() >= expectedUdpPackets;
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        FilterAndDropDisruptorProcessor processor = new FilterAndDropDisruptorProcessor(1024, "src/main/resources/input_thousand.pcap", 505, 495);

        processor.initialize();
        processor.start();
        processor.shutdown();
    }
}
