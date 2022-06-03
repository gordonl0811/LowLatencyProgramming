package PacketProcessor.DisruptorPacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.components.PacketReader;
import PacketProcessor.DisruptorPacketProcessor.components.PacketWriter;
import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import PacketProcessor.PacketProcessor;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.io.IOException;
import java.util.LinkedList;

public class ForwardingDisruptorProcessor implements PacketProcessor {

  private final PacketReader packetReader;
  private final PacketWriter packetWriter;

  public ForwardingDisruptorProcessor(int bufferSize, String source, String dest) throws IOException {
    Disruptor<PacketEvent> readerDisruptor = new Disruptor<>(PacketEvent::new, bufferSize,
        DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new YieldingWaitStrategy());

    this.packetReader = new PacketReader(source, readerDisruptor, new LinkedList<>());
    this.packetWriter = new PacketWriter(readerDisruptor, dest);


  }

  @Override
  public void initialize() {
    packetWriter.initialize();
    packetReader.initialize();
  }

  @Override
  public void start() throws InterruptedException {
    packetReader.start();
  }
}
