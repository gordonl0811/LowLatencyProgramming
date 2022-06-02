package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.PacketFilter;
import PacketProcessor.DisruptorPacketProcessor.components.PacketReader;
import PacketProcessor.DisruptorPacketProcessor.components.PacketWriter;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import PacketProcessor.PacketProcessor;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FilterAndWriteProcessor implements PacketProcessor {

    private final PacketReader packetReader;
    private final PacketFilter packetFilter;
    private final PacketWriter tcpWriter;
    private final PacketWriter udpWriter;

    public FilterAndWriteProcessor(int bufferSize, String source, String tcpDest, String udpDest)
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
        this.tcpWriter = new PacketWriter(tcpDisruptor, tcpDest);
        this.udpWriter = new PacketWriter(udpDisruptor, udpDest);

    }

    @Override
    public void initialize() {

        // Initialise writers
        tcpWriter.initialize();
        udpWriter.initialize();

        // Initialise filter
        packetFilter.initialize();

        // Initialise reader
        packetReader.initialize();

    }

    @Override
    public void start() throws InterruptedException {
        packetReader.start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        FilterAndWriteProcessor processor = new FilterAndWriteProcessor(
                1024,
                "src/main/resources/input_ten_thousand.pcap",
                "src/main/resources/tcp_output.pcap",
                "src/main/resources/udp_output.pcap"
        );

        processor.initialize();
        processor.start();

    }
}