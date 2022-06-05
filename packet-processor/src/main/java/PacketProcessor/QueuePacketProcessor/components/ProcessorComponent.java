package PacketProcessor.QueuePacketProcessor.components;

import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public abstract class ProcessorComponent implements Runnable {

    private final BlockingQueue<Packet> inputQueue;
    private long packetCount = 0;

    public ProcessorComponent(BlockingQueue<Packet> inputQueue) {
        this.inputQueue = inputQueue;
    }

    public long getPacketCount() {
        return packetCount;
    }

    public abstract void process(Packet packet) throws IOException, InterruptedException;

    @Override
    public void run() {
        try {
            while (true) {
                process(inputQueue.take());
                packetCount++;
            }
        } catch (InterruptedException | IOException ignored) {
        }
    }

}
