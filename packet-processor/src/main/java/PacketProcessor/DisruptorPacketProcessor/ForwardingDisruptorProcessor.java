package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.ProcessorComponent;
import PacketProcessor.DisruptorPacketProcessor.components.Writer;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.List;

public class ForwardingDisruptorProcessor extends AbstractDisruptorProcessor {

    private final PcapReader reader;
    private final Writer writer;

    private final long expectedPackets;

    public ForwardingDisruptorProcessor(int bufferSize, String source, String dest, long expectedPackets) throws IOException {

        Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        this.reader = new PcapReader(source, readerDisruptor);
        this.writer = new Writer(readerDisruptor, dest);

        this.expectedPackets = expectedPackets;
    }

    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<ProcessorComponent> setComponents() {
        return List.of(writer);
    }

    @Override
    public boolean shouldTerminate() {
        return writer.getPacketCount() >= expectedPackets;
    }
}
