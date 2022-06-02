package PacketProcessor;

import PacketProcessor.QueuePacketProcessor.FilterProcessor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        FilterProcessor processor = new FilterProcessor(
                1024,
                "src/main/resources/input_thousand.pcap",
                "src/main/resources/output/tcp_output.pcap",
                "src/main/resources/output/udp_output.pcap"
        );

        processor.initialize();
        processor.start();

    }
}
