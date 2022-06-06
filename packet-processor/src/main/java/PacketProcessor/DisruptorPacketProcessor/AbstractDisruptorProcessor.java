package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.PacketProcessor;

import java.util.concurrent.TimeUnit;

public abstract class AbstractDisruptorProcessor implements PacketProcessor {

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
