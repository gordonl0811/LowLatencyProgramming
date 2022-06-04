package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.Reader;
import PacketProcessor.DisruptorPacketProcessor.components.Writer;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import PacketProcessor.PacketProcessor;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

public class ForwardingDisruptorProcessor extends AbstractQueueProcessor {

  private final Reader reader;
  private final Writer writer;

  public ForwardingDisruptorProcessor(int bufferSize, String source, String dest) throws IOException {
    Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
        DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

    this.reader = new Reader(source, readerDisruptor);
    this.writer = new Writer(readerDisruptor, dest);


  }

  @Override
  public void initialize() {
    writer.initialize();
    reader.initialize();
  }

  @Override
  public void start() throws InterruptedException {
    reader.start();
  }

  @Override
  public boolean shouldTerminate() {
    return false;
  }
}
