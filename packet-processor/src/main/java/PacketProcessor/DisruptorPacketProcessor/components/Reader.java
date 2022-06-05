package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import io.pkts.Pcap;

import java.io.IOException;

public class Reader {

    private final Pcap source;
    private final Disruptor<PacketEvent> readerDisruptor;

    public Reader(String source, Disruptor<PacketEvent> readerDisruptor)
            throws IOException {
        this.source = Pcap.openStream(source);
        this.readerDisruptor = readerDisruptor;
    }

    public Disruptor<PacketEvent> getReaderDisruptor() {
        return readerDisruptor;
    }

    public void initialize() {
        readerDisruptor.start();
    }

    public void start() {

        try {
            // Load the packets into the RingBuffer
            this.source.loop(packet -> {
                readerDisruptor.publishEvent((event, sequence) -> event.setValue(packet));
                return true;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        source.close();

    }

}
