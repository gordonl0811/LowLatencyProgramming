package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Filter;
import PacketProcessor.DisruptorPacketProcessor.components.Reader;
import PacketProcessor.DisruptorPacketProcessor.components.Writer;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import PacketProcessor.PacketProcessor;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FilterAndWriteDisruptorProcessor implements PacketProcessor {

    private final Reader reader;
    private final Filter filter;
    private final Writer tcpWriter;
    private final Writer udpWriter;

    public FilterAndWriteDisruptorProcessor(int bufferSize, String source, String tcpDest, String udpDest)
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
        this.tcpWriter = new Writer(tcpDisruptor, tcpDest);
        this.udpWriter = new Writer(udpDisruptor, udpDest);

    }

    @Override
    public void initialize() {

        // Initialise writers
        tcpWriter.initialize();
        udpWriter.initialize();

        // Initialise filter
        filter.initialize();

        // Initialise reader
        reader.initialize();

    }

    @Override
    public void start() throws InterruptedException {
        reader.start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        FilterAndWriteDisruptorProcessor processor = new FilterAndWriteDisruptorProcessor(
                1024,
                "src/main/resources/input_ten_thousand.pcap",
                "src/main/resources/tcp_output.pcap",
                "src/main/resources/udp_output.pcap"
        );

        processor.initialize();
        processor.start();

    }
}
