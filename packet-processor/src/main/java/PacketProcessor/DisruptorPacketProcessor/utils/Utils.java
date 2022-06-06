package PacketProcessor.DisruptorPacketProcessor.utils;

import com.lmax.disruptor.dsl.Disruptor;

public class Utils {

    static public void startDisruptor(Disruptor<PacketEvent> disruptor) {
        if (!disruptor.hasStarted()) {
            disruptor.start();
        }
    }
}
