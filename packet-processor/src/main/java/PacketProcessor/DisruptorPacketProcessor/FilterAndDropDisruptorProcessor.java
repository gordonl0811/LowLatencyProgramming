package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Dropper;
import PacketProcessor.DisruptorPacketProcessor.components.Filter;
import PacketProcessor.DisruptorPacketProcessor.components.Reader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import PacketProcessor.PacketProcessor;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FilterAndDropDisruptorProcessor implements PacketProcessor {
    private final Reader reader;
    private final Filter filter;
    private final Dropper tcpDropper;
    private final Dropper udpDropper;

    public FilterAndDropDisruptorProcessor(int bufferSize, String source)
            throws IOException {

        Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
                DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> tcpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
                DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> udpDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
                DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        List<Disruptor<PacketEvent>> consumers = new LinkedList<>();
        consumers.add(tcpDisruptor);
        consumers.add(udpDisruptor);

        this.reader = new Reader(source, readerDisruptor, consumers);
        this.filter = new Filter(readerDisruptor, tcpDisruptor, udpDisruptor);
        this.tcpDropper = new Dropper(tcpDisruptor);
        this.udpDropper = new Dropper(udpDisruptor);

    }

    @Override
    public void initialize() {

        // Initialise writers
        tcpDropper.initialize();
        udpDropper.initialize();

        // Initialise filter
        filter.initialize();

        // Initialise reader
        reader.initialize();

    }

    @Override
    public void start() throws InterruptedException {
        reader.start();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        FilterAndDropDisruptorProcessor processor = new FilterAndDropDisruptorProcessor(
                1024,
                "src/main/resources/input_thousand.pcap"
        );

        processor.initialize();
        processor.start();
    }

}
