package PacketProcessor.QueuePacketProcessor.components;

import PacketProcessor.utils.PoisonPacket;
import io.pkts.packet.Packet;

import java.util.concurrent.BlockingQueue;

public class Dropper implements Runnable {
    private final BlockingQueue<Packet> packetQueue;

    public Dropper(BlockingQueue<Packet> packetQueue) {
        this.packetQueue = packetQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Packet packet = packetQueue.take();
                if (packet instanceof PoisonPacket) {
                    return;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
