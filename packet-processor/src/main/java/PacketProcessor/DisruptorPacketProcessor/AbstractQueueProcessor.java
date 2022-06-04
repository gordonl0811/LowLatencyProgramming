package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Reader;
import PacketProcessor.PacketProcessor;

import java.util.concurrent.TimeUnit;

public abstract class AbstractQueueProcessor implements PacketProcessor {

    private Reader reader;

    public void setReader(Reader reader) { this.reader = reader; }

    @Override
    public abstract void initialize();

    @Override
    public void start() throws InterruptedException {

        reader.start();

        while (!shouldTerminate()) {
            TimeUnit.MILLISECONDS.sleep(1);
        }

    }

    public abstract boolean shouldTerminate();
}
