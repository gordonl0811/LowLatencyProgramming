package PacketProcessor;

import PacketProcessor.DisruptorPacketProcessor.FilterAndDropProcessor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        FilterAndDropProcessor processor = new FilterAndDropProcessor(
                1024,
                "src/main/resources/input_thousand.pcap"
        );

        processor.initialize();
        processor.start();

    }
}
