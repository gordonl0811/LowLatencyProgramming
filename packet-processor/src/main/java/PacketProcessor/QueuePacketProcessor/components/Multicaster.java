package PacketProcessor.QueuePacketProcessor.components;

import io.pkts.packet.Packet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Multicaster extends ProcessorComponent {

    List<BlockingQueue<Packet>> outputQueues;

    public Multicaster(BlockingQueue<Packet> inputQueue, List<BlockingQueue<Packet>> outputQueues) {
        super(inputQueue);
        this.outputQueues = outputQueues;
    }

    @Override
    public void process(Packet packet) throws IOException, InterruptedException {
        for (BlockingQueue<Packet> outputQueue : outputQueues) {
            outputQueue.put(packet);
        }
    }
}
