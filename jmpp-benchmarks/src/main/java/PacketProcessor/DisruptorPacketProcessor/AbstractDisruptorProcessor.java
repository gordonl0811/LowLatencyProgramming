package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.AbstractPacketProcessor;
import PacketProcessor.DisruptorPacketProcessor.components.ProcessorComponent;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;

import java.util.List;

public abstract class AbstractDisruptorProcessor extends AbstractPacketProcessor {

    private List<PcapReader> readers;
    private List<ProcessorComponent> components;

    protected abstract List<PcapReader> setReaders();

    protected abstract List<ProcessorComponent> setComponents();

    @Override
    public final void initialize() {
        readers = setReaders();
        components = setComponents();
        for (PcapReader reader : readers) {
            reader.initialize();
        }
        for (ProcessorComponent component : components) {
            component.initialize();
        }
    }

    @Override
    public final void shutdown() {
        for (PcapReader reader : readers) {
            reader.shutdown();
        }
        for (ProcessorComponent component : components) {
            component.shutdown();
        }
    }

    @Override
    protected final void releasePackets() {
        for (PcapReader reader : readers) {
            reader.start();
        }
    }

    @Override
    protected abstract boolean shouldTerminate();

}
