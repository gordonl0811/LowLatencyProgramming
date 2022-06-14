package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.AbstractPacketProcessor;
import PacketProcessor.QueuePacketProcessor.components.Component;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractQueueProcessor extends AbstractPacketProcessor {

    private List<Thread> readerThreads;
    private List<Thread> componentsThreads;

    protected abstract List<PcapReader> setReaders();

    protected abstract List<Component> setComponents();

    @Override
    public final void initialize() {
        readerThreads = setReaders().stream().map(Thread::new).collect(Collectors.toList());
        componentsThreads = setComponents().stream().map(Thread::new).collect(Collectors.toList());
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
