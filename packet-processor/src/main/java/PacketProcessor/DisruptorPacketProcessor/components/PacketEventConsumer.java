package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;

public interface PacketEventConsumer extends EventHandler<PacketEvent> {

  Disruptor<PacketEvent> getInputDisruptor();

  void initialize();

}
