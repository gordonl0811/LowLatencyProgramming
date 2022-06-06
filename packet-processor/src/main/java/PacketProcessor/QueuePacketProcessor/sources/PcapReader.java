package PacketProcessor.QueuePacketProcessor.sources;

import io.pkts.Pcap;
import io.pkts.packet.Packet;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class PcapReader implements Runnable {

    private final Pcap source;
    private final BlockingQueue<Packet> outputQueue;

    public PcapReader(String source, BlockingQueue<Packet> outputQueue) throws IOException {
        this(new File(source), outputQueue);
    }

    public PcapReader(File source, BlockingQueue<Packet> outputQueue) throws IOException {
        this.source = Pcap.openStream(source);
        this.outputQueue = outputQueue;
    }

    @Override
    public void run() {
        // Load the packets into the queue
        try {
            this.source.loop(packet -> {
                try {
                    outputQueue.put(packet);
                } catch (InterruptedException ignored) { }
                return true;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        source.close();


    }
}
