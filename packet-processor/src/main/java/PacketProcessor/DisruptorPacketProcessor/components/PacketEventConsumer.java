package PacketProcessor.DisruptorPacketProcessor.components;

import PacketProcessor.DisruptorPacketProcessor.utils.PacketEvent;
import com.lmax.disruptor.EventHandler;

public interface PacketEventConsumer extends EventHandler<PacketEvent> {}
