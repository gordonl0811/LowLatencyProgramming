package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Dropper;
import PacketProcessor.DisruptorPacketProcessor.components.LayerFourPortRewriter;
import PacketProcessor.DisruptorPacketProcessor.components.ProcessorComponent;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.List;

public class PipelineDisruptorProcessor extends AbstractDisruptorProcessor {

    private final PcapReader reader;
    private final LayerFourPortRewriter rewriter;
    private final Dropper dropper;

    private final long expectedPackets;

    public PipelineDisruptorProcessor(int bufferSize, String source, int srcPort, int destPort, long expectedPackets) throws IOException {

        Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());
        Disruptor<PacketEvent> rewriterDisruptor = new Disruptor<>(PacketEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

        this.reader = new PcapReader(source, readerDisruptor);
        this.rewriter = new LayerFourPortRewriter(readerDisruptor, rewriterDisruptor, srcPort, destPort);
        this.dropper = new Dropper(rewriterDisruptor);

        this.expectedPackets = expectedPackets;
    }

    @Override
    protected List<PcapReader> setReaders() {
        return List.of(reader);
    }

    @Override
    protected List<ProcessorComponent> setComponents() {
        return List.of(rewriter, dropper);
    }

    @Override
    public boolean shouldTerminate() {
        return dropper.getPacketCount() >= expectedPackets;
    }
}
