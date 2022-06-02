package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.PacketDropper;
import PacketProcessor.DisruptorPacketProcessor.components.PacketFilter;
import PacketProcessor.DisruptorPacketProcessor.components.PacketReader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import PacketProcessor.PacketProcessor;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FilterAndDropProcessor implements PacketProcessor {
    private final PacketReader packetReader;
    private final PacketFilter packetFilter;
    private final PacketDropper tcpDropper;
    private final PacketDropper udpDropper;

    public FilterAndDropProcessor(int bufferSize, String source)
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

        this.packetReader = new PacketReader(source, readerDisruptor, consumers);
        this.packetFilter = new PacketFilter(readerDisruptor, tcpDisruptor, udpDisruptor);
        this.tcpDropper = new PacketDropper(tcpDisruptor);
        this.udpDropper = new PacketDropper(udpDisruptor);

    }

    @Override
    public void initialize() {

        // Initialise writers
        tcpDropper.initialize();
        udpDropper.initialize();

        // Initialise filter
        packetFilter.initialize();

        // Initialise reader
        packetReader.initialize();

    }

    @Override
    public void start() throws InterruptedException {
        packetReader.start();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        FilterAndDropProcessor processor = new FilterAndDropProcessor(
                1024,
                "src/main/resources/input_thousand.pcap"
        );

        processor.initialize();
        processor.start();
    }

}
