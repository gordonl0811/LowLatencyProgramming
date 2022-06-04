package PacketProcessor.QueuePacketProcessor;

import PacketProcessor.PacketProcessor;
import PacketProcessor.QueuePacketProcessor.components.Reader;
import PacketProcessor.QueuePacketProcessor.components.Writer;
import io.pkts.packet.Packet;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ForwardingQueueProcessor implements PacketProcessor {

  private final Thread producerThread;
  private final Thread writerThread;

  public ForwardingQueueProcessor(int queueSize, String source, String dest)
      throws IOException {

    final BlockingQueue<Packet> readerQueue = new ArrayBlockingQueue<>(queueSize);

    final Reader reader = new Reader(source, readerQueue);
    final Writer writer = new Writer(readerQueue, dest);

    this.producerThread = new Thread(reader);
    this.writerThread = new Thread(writer);
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
    ForwardingQueueProcessor processor = new ForwardingQueueProcessor(
        1000,
        "src/main/resources/input_thousand.pcap",
        "src/main/resources/output/forwarded.pcap"
    );

    processor.initialize();
    processor.start();
  }

}
