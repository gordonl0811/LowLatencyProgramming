package PacketProcessor.QueuePacketProcessor.components;

import io.pkts.packet.Packet;

import java.util.concurrent.BlockingQueue;

public class Dropper extends Component {

    public Dropper(BlockingQueue<Packet> inputQueue) {
        super(inputQueue);
    }

    @Override
    public void process(Packet packet) {
//        System.out.println("Processing " + getPacketCount());
    }

}
