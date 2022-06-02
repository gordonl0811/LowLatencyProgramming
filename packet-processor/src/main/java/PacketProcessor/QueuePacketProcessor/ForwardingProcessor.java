package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.components.PacketReader;
import PacketProcessor.QueuePacketProcessor.components.PacketWriter;
import io.pkts.packet.Packet;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ForwardingProcessor implements PacketProcessor {

  private final Thread producerThread;
  private final Thread writerThread;

  public ForwardingProcessor(int queueSize, String source, String dest)
      throws IOException {

    final BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(queueSize);

    final PacketReader packetReader = new PacketReader(source, readerQueue);
    final PacketWriter packetWriter = new PacketWriter(readerQueue, dest);

    this.producerThread = new Thread(packetReader);
    this.writerThread = new Thread(packetWriter);
  }

  @Override
  public void initialize() {
    writerThread.start();
  }

  @Override
  public void start() throws InterruptedException {
    producerThread.start();
    producerThread.join();
    writerThread.join();
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    ForwardingProcessor processor = new ForwardingProcessor(
        1000,
        "src/main/resources/input_thousand.pcap",
        "src/main/resources/output/forwarded.pcap"
    );

    processor.initialize();
    processor.start();
  }

}
