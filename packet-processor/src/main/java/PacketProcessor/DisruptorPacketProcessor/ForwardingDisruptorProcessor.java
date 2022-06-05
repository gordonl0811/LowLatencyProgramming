package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Reader;
import PacketProcessor.DisruptorPacketProcessor.components.Writer;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

public class ForwardingDisruptorProcessor extends AbstractQueueProcessor {

  private final Disruptor<PacketEvent> readerDisruptor;

  private final Reader reader;
  private final Writer writer;

  private final long expectedPackets;

  public ForwardingDisruptorProcessor(int bufferSize, String source, String dest, long expectedPackets) throws IOException {
    this.readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
        DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

    this.reader = new Reader(source, readerDisruptor);
    this.writer = new Writer(readerDisruptor, dest);

    this.expectedPackets = expectedPackets;

    setReader(this.reader);
  }

  @Override
  public void initialize() {
    writer.initialize();
    reader.initialize();
  }

  @Override
  public void shutdown() {
    readerDisruptor.shutdown();
  }

  @Override
  public boolean shouldTerminate() {
    return writer.getPacketCount() >= expectedPackets;
  }
}
