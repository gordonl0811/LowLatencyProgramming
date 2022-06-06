package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.AbstractPacketProcessor;
import PacketProcessor.QueuePacketProcessor.components.ProcessorComponent;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQueueProcessor extends AbstractPacketProcessor {

    private final List<Thread> readerThreads = new ArrayList<>();
    private final List<Thread> componentsThreads = new ArrayList<>();

    protected void addReader(PcapReader reader) {
        readerThreads.add(new Thread(reader));
    }

    protected void addComponent(ProcessorComponent component) {
        componentsThreads.add(new Thread(component));
    }

    @Override
    public final void initialize() {
        for (Thread thread : componentsThreads) {
            thread.start();
        }
    }

    @Override
    public final void shutdown() {
        for (Thread thread : readerThreads) {
            thread.interrupt();
        }
        for (Thread thread : componentsThreads) {
            thread.interrupt();
        }
    }

    @Override
    protected final void releasePackets() {
        for (Thread thread : readerThreads) {
            thread.start();
        }
    }

    @Override
    protected abstract boolean shouldTerminate();
}
