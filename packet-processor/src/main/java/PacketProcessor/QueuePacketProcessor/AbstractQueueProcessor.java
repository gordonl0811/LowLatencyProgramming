package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.components.ProcessorComponent;
import PacketProcessor.QueuePacketProcessor.sources.PcapReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractQueueProcessor implements PacketProcessor {

    @Override
    public abstract void initialize();

    @Override
    public final void start() throws InterruptedException {

        releasePackets();

        while (!shouldTerminate()) {
            TimeUnit.MILLISECONDS.sleep(1);
        }

    }

    @Override
    public abstract void shutdown();

    public abstract boolean shouldTerminate();
}
