package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.components.ProcessorComponent;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractQueueProcessor implements PacketProcessor {

    private final List<Thread> componentsThreads;
    protected Thread readerThread;

    public AbstractQueueProcessor() {
        componentsThreads = new ArrayList<>();
    }

    public void setReader(PcapReader reader) {
        this.readerThread = new Thread(reader);
    }

    public void addComponent(ProcessorComponent component) {
        componentsThreads.add(new Thread(component));
    }

    @Override
    public final void initialize() {
        for (Thread thread : componentsThreads) {
            thread.start();
        }
    }

    @Override
    public final void start() throws InterruptedException {

        readerThread.start();

        while (!shouldTerminate()) {
            TimeUnit.MILLISECONDS.sleep(1);
        }

        readerThread.interrupt();
        for (Thread thread : componentsThreads) {
            thread.interrupt();
        }
    }

    public abstract boolean shouldTerminate();
}
