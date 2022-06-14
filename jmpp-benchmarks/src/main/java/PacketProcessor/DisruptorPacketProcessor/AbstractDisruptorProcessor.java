package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.AbstractPacketProcessor;
import PacketProcessor.DisruptorPacketProcessor.components.Component;
import PacketProcessor.DisruptorPacketProcessor.sources.PcapReader;

import java.util.List;

public abstract class AbstractDisruptorProcessor extends AbstractPacketProcessor {

    private List<PcapReader> readers;
    private List<Component> components;

    protected abstract List<PcapReader> setReaders();

    protected abstract List<Component> setComponents();

    @Override
    public final void initialize() {
        readers = setReaders();
        components = setComponents();
        for (PcapReader reader : readers) {
            reader.initialize();
        }
        for (Component component : components) {
            component.initialize();
        }
    }

    @Override
    public final void shutdown() {
        for (PcapReader reader : readers) {
            reader.shutdown();
        }
        for (Component component : components) {
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
