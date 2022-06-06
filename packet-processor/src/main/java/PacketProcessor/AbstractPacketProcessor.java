package PacketProcessor;

import java.util.concurrent.TimeUnit;

public abstract class AbstractPacketProcessor implements PacketProcessor {

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

    protected abstract void releasePackets();

    protected abstract boolean shouldTerminate();
}
