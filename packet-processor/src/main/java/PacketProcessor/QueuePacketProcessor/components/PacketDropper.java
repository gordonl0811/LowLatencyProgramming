package PacketProcessor.QueuePacketProcessor.components;

import PacketProcessor.PacketProcessor;
import PacketProcessor.utils.PoisonPacket;
import io.pkts.PcapOutputStream;
import io.pkts.frame.PcapGlobalHeader;
import io.pkts.packet.Packet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class PacketDropper implements Runnable {
    private final BlockingQueue<Packet> packetQueue;

    public PacketDropper(BlockingQueue<Packet> packetQueue) {
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
