package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Reader;
import PacketProcessor.PacketProcessor;

import java.util.concurrent.TimeUnit;

public abstract class AbstractQueueProcessor implements PacketProcessor {

    @Override
    public abstract void initialize();

    @Override
    public void start() throws InterruptedException {

        releasePackets();

        while (!shouldTerminate()) {
            TimeUnit.MILLISECONDS.sleep(1);
        }

    }

    @Override
    public abstract void shutdown();

    public abstract void releasePackets();

    public abstract boolean shouldTerminate();
}
